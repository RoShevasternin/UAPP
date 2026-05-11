package com.rsbuxs.rcounbux

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds

lateinit var appContext: Context private set

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

}