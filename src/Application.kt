package me.freedom4live.ktor

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Sessions) {
        cookie<UserIdPrincipal>(
            Cookies.AUTH_COOKIE,
            storage = SessionStorageMemory()
        ) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
        //Configure Authentication with cookies
        session<UserIdPrincipal>(AuthName.SESSION) {
            challenge {
                // What to do if the user isn't authenticated
                throw AuthenticationException()
            }
            validate { session: UserIdPrincipal ->
                // If you need to do additional validation on session data, you can do so here.
                session
            }
        }

        //Configure Authentication with login data
        form(AuthName.FORM) {
            userParamName = FormFields.USERNAME
            passwordParamName = FormFields.PASSWORD
            challenge {
                throw AuthenticationException()
            }
            validate { cred: UserPasswordCredential ->
                AuthProvider.tryAuth(cred.name, cred.password)
            }
        }
    }

    routing {
        install(StatusPages) {
            //Here you can specify responses on exceptions
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        route("/login") {
            authenticate(AuthName.FORM) {
                post {
                    //Principal must not be null as we are authenticated
                    val principal = call.principal<UserIdPrincipal>()!!

                    // Set the cookie to make session auth working
                    call.sessions.set(principal)
                    call.respond(HttpStatusCode.OK, "OK")
                }
            }
        }

        route("/user_info") {
            authenticate(AuthName.SESSION) {
                get {
                    //Principal must not be null as we are authenticated
                    val principal = call.principal<UserIdPrincipal>()!!
                    call.respond(HttpStatusCode.OK, principal)
                }
            }
        }

    }
}
