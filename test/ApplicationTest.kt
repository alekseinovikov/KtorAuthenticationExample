package me.freedom4live.ktor

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {

    @Test
    fun loginTest() {
        withTestApplication({ module(testing = true) }) {
            loginTestAccount()
        }
    }

    @Test
    fun meData_noAuth_Unauthorized() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/user_info").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun meData_hasAuth_ReturnsOkAndCurrentUserData() {
        withTestApplication({ module(testing = true) }) {
            runWithTestUser {
                handleRequest(HttpMethod.Get, "/user_info").apply {
                    assertEquals(HttpStatusCode.OK, response.status())

                    val expectedContent = "{\n" +
                            "  \"name\" : \"test_user_name\"\n" +
                            "}"
                    assertEquals(expectedContent, response.content)
                }
            }
        }
    }

    private fun TestApplicationEngine.loginTestAccount() {
        handleRequest(HttpMethod.Post, "/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(
                listOf(
                    FormFields.USERNAME to AuthProvider.TEST_USER_NAME,
                    FormFields.PASSWORD to AuthProvider.TEST_USER_PASSWORD
                ).formUrlEncode()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("OK", response.content)
        }
    }

    private fun TestApplicationEngine.runWithTestUser(test: TestApplicationEngine.() -> Unit) =
        cookiesSession {
            loginTestAccount()

            test()
        }

}
