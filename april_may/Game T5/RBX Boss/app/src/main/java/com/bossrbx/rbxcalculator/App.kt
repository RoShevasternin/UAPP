package com.bossrbx.rbxcalculator

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.bossrbx.rbxcalculator.adsmodule.AdConfig
import com.bossrbx.rbxcalculator.adsmodule.AdPref
import com.bossrbx.rbxcalculator.adsmodule.NavigationCounter
import com.bossrbx.rbxcalculator.util.NetworkUtils
import com.bossrbx.rbxcalculator.util.log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

lateinit var appContext: Context private set

class App: Application() {

    companion object {
        lateinit var adPref           : AdPref
        lateinit var navigationCounter: NavigationCounter
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        checkVpnAndToggleAnalytics()

        // ── 1. Ініціалізуємо AdPref ───────────────────────────────────────────
        // Завантажуємо збережений конфіг і тип юзера з минулого запуску
        // Це потрібно щоб реклама одразу працювала без очікування Firebase
        initAdPref()

        // ── 2. Ініціалізуємо лічильник навігації ─────────────────────────────
        initNavigationCounter()

        // ── 3. Ініціалізуємо AdMob SDK ────────────────────────────────────────
        MobileAds.initialize(this)
    }

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    private fun initAdPref() {
        adPref = AdPref(this)
        adPref.loadConfig()?.let { AdConfig.remoteConfig = it }
        adPref.loadUserType()?.let { AdConfig.userType = it }
    }

    private fun initNavigationCounter() {
        navigationCounter = NavigationCounter(adPref)
        navigationCounter.applyRestartReset()
    }

    // ------------------------------------------------------------------------
    // Firebase
    // ------------------------------------------------------------------------

    private fun checkVpnAndToggleAnalytics() {
        if (!NetworkUtils.isVpnConnected()) {
            // VPN вимкнено -> Усе чисто, вмикаємо аналітику.
            // Якщо це перший "чистий" запуск, Firebase сам зафіксує first_open.
            log("VPN ------------------ false")
            Firebase.analytics.setAnalyticsCollectionEnabled(true)
        } else log("VPN ------------------ true")
    }

}