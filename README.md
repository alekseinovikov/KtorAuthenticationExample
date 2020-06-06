# KtorAuthenticationExample

![BUILD (Ubuntu)](https://github.com/freedom4live/KtorAuthenticationExample/workflows/BUILD%20(Ubuntu)/badge.svg)

Simple example how to implement Authentication using login form
and sessions based on cookies.

Application.kt contains all the configuration:
* Session configuration in  install(Sessions) section
* Two types of auth in install(Authentication) section
    * session - needed to auth a user using his or her cookies
    * form - needed to auth a user on login endpoint using username and password saving his session using cookies
* route("/login") - auth a user via auth section, takes his or her principal and puts it into the session to make possible to auth via the session and cookies
* route("/user_info") - just takes the principal and returns it to the user

