package com.bossrbx.rbxcalculator

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.bossrbx.rbxcalculator.adsmodule.AdConfig
import com.bossrbx.rbxcalculator.adsmodule.AdPref
import com.bossrbx.rbxcalculator.adsmodule.NavigationCounter
import com.bossrbx.rbxcalculator.businesModule.Biz
import com.bossrbx.rbxcalculator.services.meta.MetaManager
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

        // ── 0. businesModule ─────────────────────────────────────────────────
        // САМЕ тут, а не в MainActivity: PushWorker може підняти процес без
        // жодної активіті, і Biz.config мусить уже існувати.
        Biz.install(this, Biz.Config(
            mainActivityClass   = MainActivity::class.java,
            notificationIconRes = R.drawable.ic_notification,
            appVersion          = BuildConfig.VERSION_NAME,
        ))

        enableAnalyticsIfNoVpn()

        // ── 1. Ініціалізуємо AdPref ───────────────────────────────────────────
        // Завантажуємо збережений конфіг і тип юзера з минулого запуску
        // Це потрібно щоб реклама одразу працювала без очікування Firebase
        initAdPref()

        // ── 1a. Meta SDK з кешованого конфігу ─────────────────────────────────
        // Старт до першого екрана — інакше Meta не бачить запуск сесії.
        // Найперший запуск (кешу ще немає) — старт у MainActivity після конфігу
        initMetaFromCache()

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

    private fun initMetaFromCache() {
        val meta = AdConfig.remoteConfig?.meta ?: return
        if (meta.isValid) MetaManager.initialize(this, meta.appId!!, meta.clientToken!!)
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