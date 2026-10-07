package com.redwave.downloader.android

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.redwave.downloader.BuildConfig
import com.redwave.downloader.core.config.RemoteFlags
import com.redwave.downloader.core.model.RedwaveJson
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  RemoteFlagsSource — Firebase Remote Config → RemoteFlags.
//
//  • Немає інтернету → RemoteFlags.DEFAULT (рішення VELDAN 07.10.2026): реклами немає,
//    «Maybe later» є, видаляти можна — навіть якщо в кеші Firebase інші значення.
//  • Онлайн: активовані значення з кешу Firebase одразу; перший запуск без відповіді
//    сервера → теж DEFAULT.
//  • fetchAndActivate на кожному вході (MainActivity.onResume); частоту ріже
//    minimumFetchInterval (debug 0 с, release 5 хв) + real-time listener, поки апка відкрита.
//  • Debug-збірка: підміна з adb (extra redwave.debug_flags) — щоб перевірити всі комбінації
//    на телефоні без Firebase Console. Живе в SharedPreferences до очищення порожнім рядком.
// ═════════════════════════════════════════════════════════════════════════════
object RemoteFlagsSource {

    private const val PREFS = "remote_flags_debug"
    private val main = Handler(Looper.getMainLooper())

    private var rc: FirebaseRemoteConfig? = null
    private lateinit var ctx: Context

    /** Останні активовані (кеш Firebase / debug). Для рішень — [effective]. */
    @Volatile var current: RemoteFlags = RemoteFlags.DEFAULT
        private set

    /** Що діє зараз: офлайн — DEFAULT (крім debug-підміни), інакше [current]. */
    val effective: RemoteFlags
        get() = if (debugOverride == null && !isOnline()) RemoteFlags.DEFAULT else current
    /** remote / default / debug — для Settings → Debug і логу. */
    @Volatile var origin: String = "default"
        private set
    @Volatile private var debugOverride: RemoteFlags? = null

    /** Прапорці змінились (fetch / real-time / debug). Main-потік. */
    var onChanged: ((RemoteFlags) -> Unit)? = null

    fun init(context: Context) {
        ctx = context.applicationContext
        if (BuildConfig.DEBUG) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("json", null)
                ?.let { debugOverride = RemoteFlags.parse(it) }
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
                        if (RemoteFlags.REMOTE_KEY !in configUpdate.updatedKeys) return
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
        rc?.getValue(RemoteFlags.REMOTE_KEY)?.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE

    /** fetchAndActivate; onDone рівно один раз — по відповіді або по таймауту (тоді кеш / DEFAULT). */
    fun refresh(timeoutMs: Long, onDone: (RemoteFlags) -> Unit) {
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
        debugOverride = if (json.isBlank()) null else RemoteFlags.parse(json)
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            if (json.isBlank()) remove("json") else putString("json", json)
        }
        publish("debug override")
    }

    // ── Debug AD_MODE (Settings → Debug) ──────────────────────────────────────
    /** AD_MODE увімкнено: debug-підміна з рекламою, обов'язковою роллю і без Uninstall. */
    fun isAdMode(): Boolean = debugOverride?.let { it.enabled && it.homeRequired && !it.isUninstall } == true

    /**
     * Увімкнути — підміна поверх Remote Config: реклама на «Додому» / після «Недавніх» (url з Remote
     * Config, якщо там https, інакше google.com), роль HOME обов'язкова, видаляти з лаунчера не можна.
     * Вимкнути — підміну прибрано, діє Remote Config. Синхронний commit: далі процес перезапускаємо.
     */
    fun setAdMode(on: Boolean) {
        if (!BuildConfig.DEBUG) return
        val flags = if (on) {
            val remoteUrl = rc?.getValue(RemoteFlags.REMOTE_KEY)
                ?.takeIf { it.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE }
                ?.let { RemoteFlags.parse(it.asString()).url }
                ?.takeIf { it.startsWith("https://") }
            RemoteFlags(enabled = true, url = remoteUrl ?: "https://google.com", homeRequired = true, isUninstall = false)
        } else null
        debugOverride = flags
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) {
            if (flags == null) remove("json")
            else putString("json", RedwaveJson.encodeToString(RemoteFlags.serializer(), flags))
        }
        log("AD_MODE ${if (on) "on" else "off"}: $flags")
    }

    fun debugLine(): String {
        val f = effective
        val src = if (debugOverride == null && !isOnline()) "offline → default" else origin
        return "[$src] ${RemoteFlags.REMOTE_KEY} = enabled=${f.enabled} home_required=${f.homeRequired} " +
            "is_uninstall=${f.isUninstall}" + (if (f.url.isNotBlank()) " url=${f.url}" else "")
    }

    private fun isOnline(): Boolean {
        if (!::ctx.isInitialized) return false
        val cm = ctx.getSystemService(android.net.ConnectivityManager::class.java) ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ------------------------------------------------------------------------
    private fun read() {
        val o = debugOverride
        if (o != null) { current = o; origin = "debug"; return }
        val v = rc?.getValue(RemoteFlags.REMOTE_KEY)
        if (v == null || v.source != FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
            current = RemoteFlags.DEFAULT; origin = "default"
        } else {
            current = RemoteFlags.parse(v.asString()); origin = "remote"
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
