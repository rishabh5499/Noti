package `in`.vyomsoft.noti

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import `in`.vyomsoft.noti.responses.UserDetailsResponse

object UserCacheManager {

    private lateinit var prefs: SharedPreferences
    private const val USER_DATA = "user_data"

    fun init(context: Context) {
        prefs = context.getSharedPreferences("user_cache", Context.MODE_PRIVATE)
    }

    fun put(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun get(key: String): String? = prefs.getString(key, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun saveUserDetails(details: UserDetailsResponse) {
        val gson = Gson()
        val json = gson.toJson(details)
        prefs.edit().putString(USER_DATA, json).apply()
    }

    fun getUserDetails(): UserDetailsResponse? {
        val json = prefs.getString(USER_DATA, null) ?: return null
        return Gson().fromJson(json, UserDetailsResponse::class.java)
    }
}