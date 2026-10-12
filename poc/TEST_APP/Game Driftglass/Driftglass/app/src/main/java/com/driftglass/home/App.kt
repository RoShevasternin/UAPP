package com.driftglass.home

import android.app.Application
import android.content.Context

/** Контекст для DataStore з GDX-коду, як у T35. Firebase — лише Remote Config (RemoteFlagsSource), реклама — AdMob (AdsManager). */
lateinit var appContext: Context
    private set

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        com.driftglass.home.android.RemoteFlagsSource.init(this)
        com.driftglass.home.android.AdsManager.init(this)
        com.driftglass.home.android.StoreDemo.init(this)
    }
}
