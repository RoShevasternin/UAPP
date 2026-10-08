package com.redwave.downloader.android

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import androidx.core.content.edit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.redwave.downloader.core.ads.AdPolicy
import com.redwave.downloader.core.ads.AdState
import com.redwave.downloader.game.platform.AdStatus
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  AdsManager — AdMob: App Open + Interstitial (рішення VELDAN 07.10.2026).
//
//  Зараз ЛИШЕ тестові блоки Google (ca-app-pub-3940256099942544/…): справжніх показів
//  і грошей немає, банити нема за що. Перед релізом — свої ID тут і в маніфесті
//  (com.google.android.gms.ads.APPLICATION_ID), згода UMP для ЄЕЗ і Privacy Policy з AdMob.
//
//  Вмикач — Remote Config is_enable_admob (рішення VELDAN 08.10.2026): false (і офлайн / без
//  відповіді Firebase) — SDK навіть не ініціалізуємо, нічого не вантажимо й не показуємо.
//  Частота — AdPolicy (чиста логіка + тест), стан — SharedPreferences, щоб 3 хв між
//  показами трималися й після перезапуску процесу. ДЕ показувати — вирішує GDXGame.
//
//  Потоки: усе тут — у main (AndroidBridge переносить виклики з GL-потоку).
// ═════════════════════════════════════════════════════════════════════════════
object AdsManager {

    // ── Тестові блоки Google (не міняти на свої до релізу) ───────────────────
    private const val APP_OPEN_UNIT     = "ca-app-pub-3940256099942544/9257395921"
    private const val INTERSTITIAL_UNIT = "ca-app-pub-3940256099942544/1033173712"

    private const val PREFS = "redwave_ads"
    /** App Open, завантажений давніше, AdMob уже не покаже (термін — 4 год). */
    private const val APP_OPEN_TTL_MS = 4 * 60 * 60_000L
    /** Не довбати мережу після помилки завантаження. */
    private const val RETRY_MS = 30_000L

    private lateinit var ctx: Context
    /** Контекст є (App.onCreate) — можна читати стан. */
    private var initialized = false
    /** MobileAds.initialize викликано — лише коли is_enable_admob хоч раз був true. */
    private var started = false

    /** Remote Config is_enable_admob (офлайн — false, як решта реклами). */
    private val enabled: Boolean get() = RemoteFlagsSource.effective.isEnableAdmob

    private var appOpen: AppOpenAd? = null
    private var appOpenLoadedAt = 0L
    private var appOpenLoading = false
    private var appOpenFailedAt = 0L

    private var interstitial: InterstitialAd? = null
    private var interstitialLoading = false
    private var interstitialFailedAt = 0L

    /** Реклама зараз на екрані: її Activity ховає MainActivity (onStop) — це не «вийшли з апки». */
    @Volatile var isShowing = false
        private set

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun init(context: Context) {
        if (initialized) return
        initialized = true
        ctx = context.applicationContext
        onFlags()
    }

    /** Прапорці змінились (старт, fetch, real-time, debug): увімкнули AdMob → стартуємо SDK і вантажимо. */
    fun onFlags() {
        if (!initialized || !enabled) return
        if (started) { loadAppOpen(); loadInterstitial(); return }
        started = true
        log("ads: is_enable_admob = true → MobileAds.initialize")
        // initialize — важкий (WebView, адаптери): у фоні, як радить Google для SDK 24+
        Thread {
            MobileAds.initialize(ctx) { status ->
                log("ads: MobileAds ready ${status.adapterStatusMap.keys}")
                android.os.Handler(android.os.Looper.getMainLooper()).post { loadAppOpen(); loadInterstitial() }
            }
        }.start()
    }

    // ------------------------------------------------------------------------
    // Стан (AdPolicy)
    // ------------------------------------------------------------------------
    private fun prefs() = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var state: AdState
        get() = if (!initialized) AdState() else prefs().let {
            AdState(it.getLong("last_fullscreen_at", 0L), it.getInt("downloads_since_interstitial", 0))
        }
        private set(s) = prefs().edit {
            putLong("last_fullscreen_at", s.lastFullscreenAt)
            putInt("downloads_since_interstitial", s.downloadsSinceInterstitial)
        }

    private fun now() = System.currentTimeMillis()

    fun onTrackDownloaded() {
        if (!enabled) return                       // AdMob вимкнено — треки не рахуємо
        state = AdPolicy.onTrackDownloaded(state)
        log("ads: track downloaded → ${state.downloadsSinceInterstitial}/${AdPolicy.INTERSTITIAL_EVERY}")
        loadInterstitial()
    }

    // ------------------------------------------------------------------------
    // App Open
    // ------------------------------------------------------------------------
    private fun appOpenFresh() = appOpen != null && SystemClock.elapsedRealtime() - appOpenLoadedAt < APP_OPEN_TTL_MS

    val isAppOpenReady: Boolean get() = appOpenFresh()

    /** Хоч одна спроба завантаження була: до неї (SDK ще ініціалізується) Splash є сенс чекати. */
    private var appOpenAttempted = false

    /** Splash: READY — показуємо; LOADING — є сенс чекати (до таймауту); NONE — не чекати. */
    fun appOpenStatus(): AdStatus = when {
        !enabled                           -> AdStatus.NONE
        !AdPolicy.canAppOpen(state, now()) -> AdStatus.NONE
        appOpenFresh()                     -> AdStatus.READY
        !appOpenAttempted || appOpenLoading -> AdStatus.LOADING
        else                               -> AdStatus.NONE
    }

    fun loadAppOpen() {
        if (!started || !enabled || appOpenLoading || appOpenFresh()) return
        if (appOpenFailedAt > 0 && SystemClock.elapsedRealtime() - appOpenFailedAt < RETRY_MS) return
        appOpenLoading = true
        appOpenAttempted = true
        AppOpenAd.load(ctx, APP_OPEN_UNIT, AdRequest.Builder().build(), object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpen = ad; appOpenLoadedAt = SystemClock.elapsedRealtime()
                appOpenLoading = false; appOpenFailedAt = 0
                log("ads: app open loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                appOpenLoading = false; appOpenFailedAt = SystemClock.elapsedRealtime()
                log("ads: app open failed to load: ${error.code} ${error.message}")
            }
        })
    }

    /**
     * Показати App Open. [onReturn] = true — повернення в апку (не довше [awayMs]), інакше відкриття.
     * Колбек — завжди рівно один раз: true — показали й закрили, false — не показали.
     */
    fun showAppOpen(activity: Activity, onReturn: Boolean, awayMs: Long, onDone: (Boolean) -> Unit) {
        val s = state
        val allowed = if (onReturn) AdPolicy.canAppOpenOnReturn(s, now(), awayMs) else AdPolicy.canAppOpen(s, now())
        val ad = appOpen
        when {
            isShowing || !enabled -> { onDone(false); return }
            !allowed -> { log("ads: app open skipped by policy (return=$onReturn, away=${awayMs / 1000}s)"); onDone(false); return }
            ad == null || !appOpenFresh() -> { log("ads: app open not ready"); appOpen = null; loadAppOpen(); onDone(false); return }
        }
        appOpen = null
        isShowing = true
        ad!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() { log("ads: app open shown") }
            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                state = AdPolicy.onAppOpenShown(state, now())
                loadAppOpen()
                onDone(true)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                isShowing = false
                log("ads: app open failed to show: ${error.message}")
                loadAppOpen()
                onDone(false)
            }
        }
        ad.show(activity)
    }

    // ------------------------------------------------------------------------
    // Interstitial
    // ------------------------------------------------------------------------
    fun loadInterstitial() {
        if (!started || !enabled || interstitialLoading || interstitial != null) return
        if (interstitialFailedAt > 0 && SystemClock.elapsedRealtime() - interstitialFailedAt < RETRY_MS) return
        interstitialLoading = true
        InterstitialAd.load(ctx, INTERSTITIAL_UNIT, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitial = ad; interstitialLoading = false; interstitialFailedAt = 0
                log("ads: interstitial loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialLoading = false; interstitialFailedAt = SystemClock.elapsedRealtime()
                log("ads: interstitial failed to load: ${error.code} ${error.message}")
            }
        })
    }

    /** Чи настав час інтерстішала (кожні 2 треки + пауза після попередньої реклами). */
    fun isInterstitialDue(): Boolean = enabled && AdPolicy.canInterstitial(state, now())

    /** Колбек рівно один раз: true — показали й закрили. Не готовий — лишається в черзі (лічильник не скидаємо). */
    fun showInterstitial(activity: Activity, onDone: (Boolean) -> Unit) {
        val ad = interstitial
        when {
            isShowing -> { onDone(false); return }
            !isInterstitialDue() -> { onDone(false); return }
            ad == null -> { log("ads: interstitial due but not ready"); loadInterstitial(); onDone(false); return }
        }
        interstitial = null
        isShowing = true
        ad!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() { log("ads: interstitial shown") }
            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                state = AdPolicy.onInterstitialShown(state, now())
                loadInterstitial()
                onDone(true)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                isShowing = false
                log("ads: interstitial failed to show: ${error.message}")
                loadInterstitial()
                onDone(false)
            }
        }
        ad.show(activity)
    }

    // ------------------------------------------------------------------------
    // Debug
    // ------------------------------------------------------------------------
    /** Settings → Debug: «ads(test) · dl 1/2 · last 42s ago · AO ok · IS ok» (ASCII: ✓/✗ у моно-шрифті немає). */
    fun debugLine(): String {
        if (!initialized || !enabled) return "ads: off (is_enable_admob = false)"
        val s = state
        val ago = if (s.lastFullscreenAt <= 0) "never" else "${(now() - s.lastFullscreenAt) / 1000}s ago"
        val ao = if (appOpenFresh()) "ok" else if (appOpenLoading) "..." else "no"
        val ist = if (interstitial != null) "ok" else if (interstitialLoading) "..." else "no"
        return "ads(test) · dl ${s.downloadsSinceInterstitial}/${AdPolicy.INTERSTITIAL_EVERY} · last $ago · AO $ao · IS $ist"
    }

    /** adb … --ez redwave.debug_ads_reset true — забути останній показ і лічильник треків. */
    fun debugReset() {
        state = AdState()
        log("ads: debug reset")
    }
}
