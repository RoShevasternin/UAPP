package com.driftglass.home

import android.app.Application
import android.content.Context

/** Контекст для DataStore з GDX-коду, як у T35. Без Firebase; прапорці AD_MODE — FlagsSource. */
lateinit var appContext: Context
    private set

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        com.driftglass.home.android.FlagsSource.init(this)
    }
}
