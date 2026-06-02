package `in`.vyomsoft.noti.GA4

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.Firebase
import `in`.vyomsoft.noti.GA4.FirebaseConstants.ERROR_DETAILS
import `in`.vyomsoft.noti.GA4.FirebaseConstants.ERROR_TYPE
import `in`.vyomsoft.noti.GA4.FirebaseConstants.USER_ID
import `in`.vyomsoft.noti.GA4.FirebaseConstants.USER_NAME
import `in`.vyomsoft.noti.UserCacheManager

object AppAnalytics {
    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(com.google.firebase.FirebaseApp.getInstance().applicationContext)
    }

    fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            putString(USER_ID, UserCacheManager.getUserDetails()?.id.toString())
            putString(USER_NAME, UserCacheManager.getUserDetails()?.username.toString())
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logEvent(eventName: String, params: Bundle? = null) {
        params?.putString(USER_ID, UserCacheManager.getUserDetails()?.id.toString())
        params?.putString(USER_NAME, UserCacheManager.getUserDetails()?.username.toString())
        analytics.logEvent(eventName, params)
    }

    fun logError(errorType: String, details: String) {
        val bundle = Bundle().apply {
            putString(ERROR_TYPE, errorType)
            putString(ERROR_DETAILS, details)
            putString(USER_ID, UserCacheManager.getUserDetails()?.id.toString())
            putString(USER_NAME, UserCacheManager.getUserDetails()?.username.toString())
        }
        analytics.logEvent("app_error_triggered", bundle)
    }
}