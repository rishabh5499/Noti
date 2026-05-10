package `in`.vyomsoft.noti

import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN

object SessionManager {
    fun isLoggedIn(): Boolean {
        val token = UserCacheManager.get(AUTH_TOKEN)
        return !token.isNullOrBlank() && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        // Optional: Add logic to decode JWT and check 'exp'
        return false
    }
}