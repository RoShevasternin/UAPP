package com.rbuxrds.counterds.adsmodule

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenManager(
    private val application: Application
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var loadTimeMs: Long = 0
    private var currentActivity: Activity? = null
    private var isInBackground = false

    // Час останнього показу — щоб не показувати одразу після повернення з Custom Tabs
    private var lastShownMs: Long = 0
    private val COOLDOWN_MS = 3000L  // 3 секунди між показами

    var isShowingAd = false
        private set

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onStart(owner: LifecycleOwner) {
        if (isInBackground) onAppForeground()
    }

    override fun onStop(owner: LifecycleOwner) {
        isInBackground = true
    }

    // ── App Open логіка ───────────────────────────────────────────────────────

    private fun onAppForeground() {
        isInBackground = false
        val activity = currentActivity ?: return
        if (isShowingAd) return

        // Якщо повернулись після custom interstitial/app_open — скидаємо прапорець
        // і не показуємо app_open цього разу
        if (AdConfig.isFullscreenAdShowing) {
            AdConfig.isFullscreenAdShowing = false  // ← скидаємо
            return                                  // ← пропускаємо цей раз
        }

        // Cooldown — не показуємо якщо тільки що показали
        // Це запобігає нескінченному циклу при поверненні з Custom Tabs
        if (System.currentTimeMillis() - lastShownMs < COOLDOWN_MS) return

        when (AdConfig.getProvider(AdType.APP_OPEN)) {
            AdProvider.ADMOB -> {
                if (isAdReady()) showAdmobAppOpen(activity)
                else loadAdmobAppOpen()
            }
            AdProvider.CUSTOM -> {
                val url = AdConfig.customAppOpenUrl()
                if (url.isNotEmpty()) {
                    markShown()
                    BrowserUtil.open(activity, url)
                }
            }
            AdProvider.NA -> { /* нічого не робимо */ }
        }
    }

    // ── AdMob App Open ────────────────────────────────────────────────────────

    fun loadAdmobAppOpen() {
        val unitId = AdConfig.admobAppOpenId()
        if (unitId.isEmpty() || isLoadingAd || isAdReady()) return
        isLoadingAd = true

        AppOpenAd.load(
            application,
            unitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd   = ad
                    loadTimeMs  = System.currentTimeMillis()
                    isLoadingAd = false
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingAd = false
                }
            }
        )
    }

    fun showAdmobAppOpen(activity: Activity, onComplete: (() -> Unit)? = null) {
        val ad = appOpenAd
        if (ad == null || !isAdReady()) {
            onComplete?.invoke()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                markShown()
            }
            override fun onAdDismissedFullScreenContent() {
                appOpenAd   = null
                isShowingAd = false
                onComplete?.invoke()
                loadAdmobAppOpen()
            }
            override fun onAdFailedToShowFullScreenContent(
                error: com.google.android.gms.ads.AdError
            ) {
                isShowingAd = false
                onComplete?.invoke()
            }
        }
        ad.show(activity)
    }

    fun isAdReady(): Boolean {
        val fourHours = 4 * 60 * 60 * 1000L
        return appOpenAd != null &&
                (System.currentTimeMillis() - loadTimeMs) < fourHours
    }

    fun markShown() {
        lastShownMs = System.currentTimeMillis()
    }

    // ── ActivityLifecycleCallbacks ────────────────────────────────────────────

    override fun onActivityCreated(activity: Activity, bundle: Bundle?)          { currentActivity = activity }
    override fun onActivityStarted(activity: Activity)                           { currentActivity = activity }
    override fun onActivityResumed(activity: Activity)                           { currentActivity = activity }
    override fun onActivityPaused(activity: Activity)                            {}
    override fun onActivityStopped(activity: Activity)                           {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) currentActivity = null
    }
}