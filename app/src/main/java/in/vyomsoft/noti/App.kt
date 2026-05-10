package `in`.vyomsoft.noti

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        UserCacheManager.init(this)
    }
}