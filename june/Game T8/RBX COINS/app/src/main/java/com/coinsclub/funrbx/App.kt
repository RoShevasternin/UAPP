package com.coinsclub.funrbx

import android.app.Application
import android.content.Context
import com.coinsclub.funrbx.adsmodule.AdConfig
import com.coinsclub.funrbx.adsmodule.AdPref
import com.coinsclub.funrbx.adsmodule.NavigationCounter
import com.coinsclub.funrbx.util.NetworkUtils
import com.coinsclub.funrbx.util.log
import com.google.android.gms.ads.MobileAds
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

        enableAnalyticsIfNoVpn()

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
    // Firebase | VPN
    // ------------------------------------------------------------------------

    private fun enableAnalyticsIfNoVpn() {
        val vpn = NetworkUtils.isVpnConnected()
        log("VPN --- $vpn")
        if (!vpn) Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }

}