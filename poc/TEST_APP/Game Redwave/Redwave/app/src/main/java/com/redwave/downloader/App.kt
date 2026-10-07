package com.redwave.downloader

import android.app.Application
import android.content.Context

/** Контекст для DataStore/AudioManager з GDX-коду, як у T35 — без Firebase і реклами. */
lateinit var appContext: Context
    private set

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
