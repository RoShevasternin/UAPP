package com.driftglass.home.adsmodule

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.driftglass.home.App

// Головний клас для показу реклами
//
// Провайдер визначається через AdConfig.getProvider(adType) на основі типу юзера:
//   ADMOB / CUSTOM / CUSTOM_GOOGLE / CUSTOM_TIKTOK / CUSTOM_FACEBOOK / NA
//
// Усі CUSTOM* обробляються однаково — різниця тільки в тому з якої секції
// конфігу беруться юніти (AdConfig сам це вирішує за provider).
//
// Driftglass: копія еталона ЛИШЕ з Interstitial (front / back / interstitial).
// Банера й нативки в Driftglass немає (рішення VELDAN 12.10.2026) — разом з ними
// прибрано Glide і layout-и a_ad_unified_big / a_custom_native_big.

class AdManager(private val activity: Activity) {

    private var admobInterstitial: InterstitialAd? = null
    private val counter get() = App.navigationCounter

    private var lastAdShownMs: Long = 0
    private fun isDebouncing() = System.currentTimeMillis() - lastAdShownMs < 500L
    private fun markAdShown()  { lastAdShownMs = System.currentTimeMillis() }

    init {
        preloadAdmobInterstitial()
    }

    // Чи це один з кастомних провайдерів
    private fun AdProvider.isCustom() = this == AdProvider.CUSTOM ||
            this == AdProvider.CUSTOM_GOOGLE ||
            this == AdProvider.CUSTOM_TIKTOK ||
            this == AdProvider.CUSTOM_FACEBOOK

    // ── Interstitial ──────────────────────────────────────────────────────────

    fun onFrontNavigation(onComplete: () -> Unit = {}) {
        if (isDebouncing()) { onComplete(); return }

        val provider = AdConfig.getProvider(AdType.INTERSTITIAL)
        when {
            provider == AdProvider.ADMOB -> showAdmobInterstitial(onComplete)
            provider.isCustom()          -> handleCustomFrontNav(onComplete, provider)
            else                         -> onComplete()
        }
    }

    fun onBackNavigation(onComplete: () -> Unit = {}) {
        if (isDebouncing()) { onComplete(); return }

        val provider = AdConfig.getProvider(AdType.INTERSTITIAL)
        when {
            provider == AdProvider.ADMOB -> showAdmobInterstitial(onComplete)
            provider.isCustom()          -> handleCustomBackNav(onComplete, provider)
            else                         -> onComplete()
        }
    }

    fun showInterstitial(onComplete: () -> Unit = {}) {
        val provider = AdConfig.getProvider(AdType.INTERSTITIAL)
        when {
            provider == AdProvider.ADMOB -> showAdmobInterstitial(onComplete)
            provider.isCustom()          -> showCustomInterstitial(onComplete, provider)
            else                         -> onComplete()
        }
    }

    // ── Custom Interstitial логіка ────────────────────────────────────────────

    private fun handleCustomFrontNav(onComplete: () -> Unit, provider: AdProvider) {
        val cfg = AdConfig.customInterstitial(provider)
        if (cfg == null || !cfg.frontNavigation.enabled) { onComplete(); return }

        counter.incrementFront()

        if (counter.frontCount >= cfg.frontNavigation.frequency) {
            counter.resetFront()
            markAdShown()
            AdConfig.isFullscreenAdShowing = true
            BrowserUtil.openAd(activity, cfg.targetUrl, "front")
        }
        onComplete()
    }

    private fun handleCustomBackNav(onComplete: () -> Unit, provider: AdProvider) {
        val cfg = AdConfig.customInterstitial(provider)
        if (cfg == null || !cfg.backNavigation.enabled) { onComplete(); return }

        counter.incrementBack()

        if (counter.backCount >= cfg.backNavigation.frequency) {
            counter.resetBack()
            markAdShown()
            AdConfig.isFullscreenAdShowing = true
            BrowserUtil.openAd(activity, cfg.targetUrl, "back")
        }
        onComplete()
    }

    private fun showCustomInterstitial(onComplete: () -> Unit, provider: AdProvider) {
        val cfg = AdConfig.customInterstitial(provider)
        if (cfg != null) {
            AdConfig.isFullscreenAdShowing = true
            BrowserUtil.openAd(activity, cfg.targetUrl, "interstitial")
        }
        onComplete()
    }

    // ── AdMob Interstitial ────────────────────────────────────────────────────

    /** Конфіг міг приїхати вже після створення AdManager — догружаємо. */
    fun preloadAdmobInterstitial() {
        if (admobInterstitial != null) return
        if (AdConfig.getProvider(AdType.INTERSTITIAL) != AdProvider.ADMOB) return
        val unitId = AdConfig.admobInterstitialId().takeIf { it.isNotEmpty() } ?: return

        InterstitialAd.load(
            activity,
            unitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd)   { admobInterstitial = ad }
                override fun onAdFailedToLoad(e: LoadAdError) { admobInterstitial = null }
            }
        )
    }

    private fun showAdmobInterstitial(onComplete: () -> Unit) {
        val ad = admobInterstitial
        if (ad == null) { onComplete(); preloadAdmobInterstitial(); return }

        AdConfig.isFullscreenAdShowing = true

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                AdConfig.isFullscreenAdShowing = false
                admobInterstitial = null
                markAdShown()
                onComplete()
                preloadAdmobInterstitial()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                AdConfig.isFullscreenAdShowing = false
                onComplete()
            }
        }
        ad.show(activity)
    }

    fun debugLine(): String = "interstitial=${AdConfig.getProvider(AdType.INTERSTITIAL).value}" +
        (if (admobInterstitial != null) " (admob ready)" else "") +
        " front=${counter.frontCount} back=${counter.backCount}"
}
