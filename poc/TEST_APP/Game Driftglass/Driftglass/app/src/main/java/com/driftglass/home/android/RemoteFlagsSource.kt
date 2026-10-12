package com.driftglass.home.android

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import com.driftglass.home.BuildConfig
import com.driftglass.home.core.config.AppFlags
import com.driftglass.home.util.log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

// ═════════════════════════════════════════════════════════════════════════════
//  RemoteFlagsSource — Firebase Remote Config → AppFlags. Порт Redwave RemoteFlagsSource 1:1
//  (рішення VELDAN 12.10.2026), параметр — AppFlags.REMOTE_KEY ("driftglass_config").
//
//  • Немає інтернету → AppFlags.DEFAULT: реклами немає, «Maybe later» є, видаляти можна —
//    навіть якщо в кеші Firebase інші значення.
//  • Онлайн: активовані значення з кешу Firebase одразу; перший запуск без відповіді → DEFAULT.
//  • fetchAndActivate на кожному вході (MainActivity.onResume); частоту ріже minimumFetchInterval
//    (debug 0 с, release 5 хв) + real-time listener, поки апка відкрита.
//  • Немає app/google-services.json (Firebase ще не створено) — getInstance() падає, ловимо → DEFAULT.
//  • Debug-збірка: підміна (AD_MODE у Settings → Debug або JSON з adb, extra driftglass.debug_flags).
// ═════════════════════════════════════════════════════════════════════════════
object RemoteFlagsSource {

    private const val PREFS = "remote_flags_debug"
    private val main = Handler(Looper.getMainLooper())

    private var rc: FirebaseRemoteConfig? = null
    private lateinit var ctx: Context

    /** Останні активовані (кеш Firebase / debug). Для рішень — [effective]. */
    @Volatile var current: AppFlags = AppFlags.DEFAULT
        private set

    /** Що діє зараз: офлайн — DEFAULT (крім debug-підміни), інакше [current]. */
    val effective: AppFlags
        get() = if (debugOverride == null && !isOnline()) AppFlags.DEFAULT else current
    /** remote / default / debug — для Settings → Debug і логу. */
    @Volatile var origin: String = "default"
        private set
    @Volatile private var debugOverride: AppFlags? = null

    /** Прапорці змінились (fetch / real-time / debug). Main-потік. */
    var onChanged: ((AppFlags) -> Unit)? = null

    fun init(context: Context) {
        ctx = context.applicationContext
        if (BuildConfig.DEBUG) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("json", null)
                ?.let { debugOverride = AppFlags.parse(it) }
        }
        rc = runCatching {
            FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(
                    FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0L else 300L)
                        .setFetchTimeoutInSeconds(10L)
                        .build()
                )
                addOnConfigUpdateListener(object : ConfigUpdateListener {
                    override fun onUpdate(configUpdate: ConfigUpdate) {
                        if (AppFlags.REMOTE_KEY !in configUpdate.updatedKeys) return
                        activate().addOnCompleteListener { publish("real-time") }
                    }
                    override fun onError(error: FirebaseRemoteConfigException) {
                        log("remote config real-time: ${error.code}")
                    }
                })
            }
        }.onFailure { log("remote config init: ${it.message}") }.getOrNull()
        read()
        log("remote flags on start [$origin]: $current")
    }

    /** Чи є значення з сервера (хоч з кешу минулого запуску). Ні — Splash чекає fetch. */
    fun hasRemote(): Boolean = debugOverride != null ||
        rc?.getValue(AppFlags.REMOTE_KEY)?.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE

    /** fetchAndActivate; onDone рівно один раз — по відповіді або по таймауту (тоді кеш / DEFAULT). */
    fun refresh(timeoutMs: Long, onDone: (AppFlags) -> Unit) {
        var done = false
        fun finish(why: String) {
            if (done) return
            done = true
            publish(why)
            onDone(effective)
        }
        val r = rc ?: return finish("no firebase")
        main.postDelayed({ finish("timeout ${timeoutMs}ms") }, timeoutMs)
        r.fetchAndActivate().addOnCompleteListener { t ->
            main.post { finish(if (t.isSuccessful) "fetch" else "fetch failed: ${t.exception?.message}") }
        }
    }

    /** Debug: JSON з adb; порожній рядок — прибрати підміну. */
    fun setDebugOverride(json: String) {
        if (!BuildConfig.DEBUG) return
        debugOverride = if (json.isBlank()) null else AppFlags.parse(json)
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            if (json.isBlank()) remove("json") else putString("json", json)
        }
        publish("debug override")
    }

    // ── Debug AD_MODE (Settings → Debug) ──────────────────────────────────────
    fun isAdMode(): Boolean = debugOverride?.isAdMode == true

    /**
     * Увімкнути — підміна поверх Remote Config: вкладка на «Додому» / після «Недавніх» (url з Remote
     * Config, якщо там https, інакше google.com) і AdMob, роль обов'язкова, видаляти з лаунчера не можна.
     * Вимкнути — підміну прибрано, діє Remote Config. Синхронний commit: далі процес перезапускаємо.
     */
    fun setAdMode(on: Boolean) {
        if (!BuildConfig.DEBUG) return
        val flags = if (on) {
            val remoteUrl = rc?.getValue(AppFlags.REMOTE_KEY)
                ?.takeIf { it.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE }
                ?.let { AppFlags.parse(it.asString()).url }
                ?.takeIf { it.startsWith("https://") }
            AppFlags.AD_MODE.copy(url = remoteUrl ?: AppFlags.AD_URL)
        } else null
        debugOverride = flags
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) {
            if (flags == null) remove("json") else putString("json", AppFlags.encode(flags))
        }
        log("AD_MODE ${if (on) "on" else "off"}: $flags")
    }

    fun debugLine(): String {
        val f = effective
        val src = when {
            rc == null && debugOverride == null -> "no firebase → default"
            debugOverride == null && !isOnline() -> "offline → default"
            else -> origin
        }
        return "[$src] ${AppFlags.REMOTE_KEY} = enabled_url_ad=${f.enabledUrlAd} home_required=${f.homeRequired} " +
            "is_uninstall=${f.isUninstall} is_enable_admob=${f.isEnableAdmob}" + (if (f.url.isNotBlank()) " url=${f.url}" else "")
    }

    fun isOnline(): Boolean {
        if (!::ctx.isInitialized) return false
        val cm = ctx.getSystemService(android.net.ConnectivityManager::class.java) ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ------------------------------------------------------------------------
    private fun read() {
        val o = debugOverride
        if (o != null) { current = o; origin = "debug"; return }
        val v = rc?.getValue(AppFlags.REMOTE_KEY)
        if (v == null || v.source != FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
            current = AppFlags.DEFAULT; origin = "default"
        } else {
            current = AppFlags.parse(v.asString()); origin = "remote"
        }
    }

    private fun publish(why: String) {
        val before = current
        read()
        if (current == before) return
        log("remote flags ($why) [$origin]: $current")
        val now = current
        main.post { onChanged?.invoke(now) }
    }
}
