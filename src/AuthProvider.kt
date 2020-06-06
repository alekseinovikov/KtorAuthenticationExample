package me.freedom4live.ktor

import io.ktor.auth.UserIdPrincipal

object AuthProvider {

    const val TEST_USER_NAME = "test_user_name"
    const val TEST_USER_PASSWORD = "test_user_password"

    fun tryAuth(userName: String, password: String): UserIdPrincipal? {

        //Here you can use DB or other ways to check user and create a Principal
        if (userName == TEST_USER_NAME && password == TEST_USER_PASSWORD) {
            return UserIdPrincipal(TEST_USER_NAME)
        }

        return null
    }
}