package `in`.vyomsoft.noti.auth

import android.util.Base64
import org.json.JSONObject

class LoginUtils {
    companion object {
        fun isJwtExpired(token: String?): Boolean {
            val parts = token?.split(".")
            if (parts?.size != 3) return true // malformed

            val payloadJson = try {
                val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP)
                String(payloadBytes, Charsets.UTF_8)
            } catch (e: Exception) {
                return true
            }

            val payload = JSONObject(payloadJson)
            if (!payload.has("exp")) return true

            val expSeconds = payload.getLong("exp")
            val expMillis = expSeconds * 1000

            return System.currentTimeMillis() > expMillis
        }
    }
}