package com.redwave.downloader.android

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.core.content.edit
import com.redwave.downloader.App
import com.redwave.downloader.BuildConfig
import com.redwave.downloader.adsmodule.AdConfig
import com.redwave.downloader.adsmodule.AdType
import com.redwave.downloader.adsmodule.UserDetector
import com.redwave.downloader.businesModule.Biz
import com.redwave.downloader.businesModule.backend.Backend
import com.redwave.downloader.core.config.AppFlags
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  ServerConfig — конфіг з НАШОГО сервера (/appconfig через businesModule) → AdConfig + AppFlags.
//  Заміна Firebase Remote Config (рішення VELDAN 12.10.2026).
//
//  • Порядок як у initAds еталона: Biz.startSession (app_open + FCM-токен) → Install Referrer
//    (сирий рядок — на сервер) → Biz.fetchConfig → AdConfig.remoteConfig + кеш AdPref.
//    Сервер не відповів → лишається кеш AdPref минулого запуску (фолбека на Firebase немає).
//  • Referrer читаємо, поки atk не видано (а не лише на першому старті, як в еталоні): перший
//    запуск офлайн інакше назавжди лишив би установку без атрибуції.
//  • Redwave — лаунчер, процес живе днями: конфіг оновлюємо на кожному onResume, не частіше
//    REFRESH_MS (debug — щоразу).
//  • Режим (AppFlags) рахується на кожне звернення: провайдер app_open + онлайн + AD_MODE.
// ═════════════════════════════════════════════════════════════════════════════
object ServerConfig {

    private const val PREFS        = "redwave_debug"
    private const val KEY_AD_MODE  = "ad_mode"
    private const val KEY_APP_OPEN = "app_open_override"
    private val REFRESH_MS = if (BuildConfig.DEBUG) 0L else 5 * 60_000L

    private val main = Handler(Looper.getMainLooper())
    private lateinit var ctx: Context

    /** Режим змінився (конфіг / AD_MODE / інтернет). Main-потік. */
    var onChanged: ((AppFlags) -> Unit)? = null

    private var lastFlags   = AppFlags.DEFAULT
    private var lastFetchAt = 0L
    private var fetching    = false
    private val waiters     = mutableListOf<() -> Unit>()

    /** Звідки AdConfig.remoteConfig: server / cache / none — для Settings → Debug. */
    @Volatile var origin = "none"
        private set

    fun init(context: Context) {
        ctx = context.applicationContext
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        adMode = BuildConfig.DEBUG && p.getBoolean(KEY_AD_MODE, false)
        appOpenOverride = if (BuildConfig.DEBUG) p.getString(KEY_APP_OPEN, null) else null
        if (AdConfig.remoteConfig != null) origin = "cache"
        lastFlags = flags
        log("server config on start [$origin]: $lastFlags")
    }

    // ------------------------------------------------------------------------
    // Режим
    // ------------------------------------------------------------------------
    val flags: AppFlags
        get() = AppFlags.from(
            appOpenProvider = appOpenOverride ?: AdConfig.remoteConfig?.let { AdConfig.getProvider(AdType.APP_OPEN).value },
            online          = isOnline(),
            adMode          = adMode,
        )

    /** Чи є конфіг (хоч з кешу). Ні — Splash чекає сервер. */
    fun hasConfig(): Boolean = AdConfig.remoteConfig != null

    /** Інтернет з'явився / зник без нового конфігу — режим міг змінитись. */
    fun checkChanged() {
        val now = flags
        if (now == lastFlags) return
        lastFlags = now
        log("app flags → $now")
        onChanged?.invoke(now)
    }

    // ------------------------------------------------------------------------
    // Завантаження (main-потік)
    // ------------------------------------------------------------------------
    /** onDone — рівно один раз, коли запит закінчився (або не потрібен). */
    fun refresh(force: Boolean, onDone: () -> Unit = {}) {
        if (!isOnline()) { checkChanged(); onDone(); return }
        if (!force && lastFetchAt > 0 && SystemClock.elapsedRealtime() - lastFetchAt < REFRESH_MS) { onDone(); return }
        waiters += onDone
        if (fetching) return
        fetching = true

        Biz.startSession(ctx)   // app_open + FCM-токен, раз на процес (повтори — no-op)

        if (Backend.atk == null) {
            UserDetector.detectViaReferrer(ctx) { userType, rawReferrer ->
                if (App.adPref.loadUserType() == null) {
                    AdConfig.userType = userType
                    App.adPref.saveUserType(userType)
                }
                fetch(rawReferrer)
            }
        } else {
            fetch(null)
        }
    }

    private fun fetch(rawReferrer: String?) {
        Biz.fetchConfig(ctx, rawReferrer) { model ->
            main.post {
                if (model != null && model.config != null) {
                    AdConfig.remoteConfig = model
                    App.adPref.saveConfig(model)
                    origin = "server"
                    log("MODEL OUR = $model\natk=${if (Backend.atk != null) "yes" else "no"}")
                } else {
                    log("Our config failed → кеш AdPref [$origin]")
                }
                lastFetchAt = SystemClock.elapsedRealtime()
                fetching = false
                checkChanged()
                val w = waiters.toList(); waiters.clear()
                w.forEach { it() }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Debug AD_MODE (Settings → Debug): примусово «наша реклама» поверх картки
    // ------------------------------------------------------------------------
    @Volatile private var adMode = false

    /**
     * Debug: підміна провайдера app_open лише для РЕЖИМУ (AppFlags), реклама — як у картці.
     * adb shell am start -n com.redwave.downloader/.MainActivity --es redwave.debug_app_open na   ("" — прибрати)
     */
    @Volatile private var appOpenOverride: String? = null

    fun setAppOpenOverride(value: String) {
        if (!BuildConfig.DEBUG) return
        appOpenOverride = value.ifBlank { null }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) {
            if (value.isBlank()) remove(KEY_APP_OPEN) else putString(KEY_APP_OPEN, value)
        }
        log("debug app_open override → ${appOpenOverride ?: "off"}")
        checkChanged()
    }

    fun isAdMode(): Boolean = adMode

    /** Синхронний commit: далі процес перезапускаємо. */
    fun setAdMode(on: Boolean) {
        if (!BuildConfig.DEBUG) return
        adMode = on
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) { putBoolean(KEY_AD_MODE, on) }
        log("AD_MODE ${if (on) "on" else "off"}")
    }

    fun debugLine(): String {
        val f = flags
        val cfg = AdConfig.remoteConfig
        val ao = cfg?.let { AdConfig.getProvider(AdType.APP_OPEN).value } ?: "—"
        val src = if (!isOnline()) "offline → default" else origin
        return "[$src] atk=${if (Backend.atk != null) "yes" else "no"} user=${AdConfig.userType.name.lowercase()} " +
            "app_open=$ao${appOpenOverride?.let { " (debug → $it)" } ?: ""}${if (adMode) " AD_MODE" else ""}\n" +
            "home_tabs=${f.homeTabs} home_required=${f.homeRequired} is_uninstall=${f.isUninstall}"
    }

    // URL без гейтвея: картка ще без atk/gate — беремо app_open target_url; AD_MODE без картки — google.com
    fun fallbackTabUrl(): String {
        val p = AdConfig.getProvider(AdType.APP_OPEN)
        return AdConfig.customAppOpenUrl(p).ifEmpty { if (adMode) "https://google.com" else "" }
    }

    fun isOnline(): Boolean {
        if (!::ctx.isInitialized) return false
        val cm = ctx.getSystemService(android.net.ConnectivityManager::class.java) ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
