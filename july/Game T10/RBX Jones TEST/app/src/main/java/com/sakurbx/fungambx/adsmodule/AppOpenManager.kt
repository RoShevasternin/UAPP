package com.sakurbx.fungambx.adsmodule

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
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

    private var lastShownMs: Long = 0
    private val COOLDOWN_MS = 3000L

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

    // ── App Open логіка (повернення з фону) ─────────────────────────────────────

    private fun onAppForeground() {
        isInBackground = false
        val activity = currentActivity ?: return
        if (isShowingAd) return

        // Повернулись після custom interstitial/app_open — скидаємо прапорець, пропускаємо
        if (AdConfig.isFullscreenAdShowing) {
            AdConfig.isFullscreenAdShowing = false
            return
        }

        // Cooldown — щоб не зациклитись при поверненні з Custom Tabs
        if (System.currentTimeMillis() - lastShownMs < COOLDOWN_MS) return

        val provider = AdConfig.getProvider(AdType.APP_OPEN)
        when {
            provider == AdProvider.ADMOB -> {
                if (isAdReady()) showAdmobAppOpen(activity) else loadAdmobAppOpen()
            }
            provider.isCustomProvider() -> {
                val url = AdConfig.customAppOpenUrl(provider)
                if (url.isNotEmpty()) {
                    markShown()
                    BrowserUtil.open(activity, url)
                }
            }
            else -> { /* NA — нічого */ }
        }
    }

    // ── App Open на LoaderScreen ────────────────────────────────────────────────
    // Викликається один раз при старті гри (з LoaderScreen).
    // Сам визначає провайдера, чекає admob до timeout, показує і викликає onDone.
    // onDone ГАРАНТОВАНО викликається рівно один раз — можна навігувати далі.

    fun showOnLoader(activity: Activity, timeoutMs: Int = 3000, onDone: () -> Unit) {
        val provider = AdConfig.getProvider(AdType.APP_OPEN)
        when {
            provider == AdProvider.ADMOB -> {
                loadAdmobAppOpen()
                waitAdmobThenShow(activity, timeoutMs, onDone)
            }
            provider.isCustomProvider() -> {
                val url = AdConfig.customAppOpenUrl(provider)
                if (url.isNotEmpty()) {
                    AdConfig.isFullscreenAdShowing = true
                    markShown()
                    BrowserUtil.open(activity, url)
                }
                onDone()
            }
            else -> onDone()
        }
    }

    // Чекає готовності admob app_open (poll кожні 100мс) до timeout, потім показує
    private fun waitAdmobThenShow(activity: Activity, timeoutMs: Int, onDone: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        var waited = 0
        var finished = false

        // Гарантуємо що onDone викликається лише раз
        val done = {
            if (!finished) {
                finished = true
                onDone()
            }
        }

        val poll = object : Runnable {
            override fun run() {
                when {
                    isAdReady() -> showAdmobAppOpen(activity) { markShown(); done() }
                    waited >= timeoutMs -> done()
                    else -> {
                        waited += 100
                        handler.postDelayed(this, 100)
                    }
                }
            }
        }
        handler.post(poll)
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
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                isShowingAd = false
                onComplete?.invoke()
            }
        }
        ad.show(activity)
    }

    fun isAdReady(): Boolean {
        val fourHours = 4 * 60 * 60 * 1000L
        return appOpenAd != null && (System.currentTimeMillis() - loadTimeMs) < fourHours
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