package com.rbxhubpro.rohumex.adsmodule

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.rbxhubpro.rohumex.businesModule.backend.Events
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

        // Идёт наш собственный сценарий (запрос разрешения на пуши) — молчим
        if (AdConfig.appOpenSuppressed) return

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
                    BrowserUtil.openAd(activity, url, "app_open") // правка 3: через гейтвей
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
        // ⚠️ ДИАГНОСТИКА (25.08): стартовый показ — главный монетизационный момент,
        // первый сеанс это почти вся воронка. Раньше ВСЕ ветки «не показали» молчали:
        // таб не открывался, навигация шла дальше, и в данных не оставалось следа.
        // Теперь каждый пропуск уходит событием gate_skip с причиной — иначе такие
        // потери невидимы и ищутся сутками.
        fun skip(reason: String) {
            Events.track("gate_skip", slot = "app_open", block = reason)
            onDone()
        }

        // Стартовый показ тоже уважает окно подавления, иначе таб открывается
        // поверх диалога разрешения. onDone обязателен: навигация не должна встать.
        if (AdConfig.appOpenSuppressed) { skip("suppressed"); return }
        if (AdConfig.remoteConfig == null) { skip("no_config"); return }

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
                    BrowserUtil.openAd(activity, url, "app_open") // правка 3: через гейтвей
                    onDone()
                } else {
                    skip("no_url_" + provider.name.lowercase())
                }
            }
            else -> skip("provider_" + provider.name.lowercase() + "_" + AdConfig.userType.name.lowercase())
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