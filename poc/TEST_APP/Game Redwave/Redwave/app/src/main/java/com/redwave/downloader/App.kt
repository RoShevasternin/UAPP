package com.redwave.downloader

import android.app.Application
import android.content.Context

/** Контекст для DataStore/AudioManager з GDX-коду, як у T35. Firebase — лише Remote Config (RemoteFlagsSource), реклама — AdMob (AdsManager). */
lateinit var appContext: Context
    private set

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        com.redwave.downloader.android.RemoteFlagsSource.init(this)
        com.redwave.downloader.android.AdsManager.init(this)
    }
}
