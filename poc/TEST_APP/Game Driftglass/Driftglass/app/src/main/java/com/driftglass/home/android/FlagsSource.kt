package com.driftglass.home.android

import android.content.Context
import androidx.core.content.edit
import com.driftglass.home.BuildConfig
import com.driftglass.home.core.config.AppFlags
import com.driftglass.home.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  FlagsSource — звідки беруться AppFlags. Сервера (Remote Config) у Driftglass немає
//  (poc/TEST_APP: сервер — лише коли VELDAN скаже), тому:
//    • release — завжди AppFlags.DEFAULT;
//    • debug — підміна в SharedPreferences: AD_MODE з Settings → Debug або JSON з adb
//      (extra driftglass.debug_flags). Живе до вимкнення / очищення порожнім рядком.
// ═════════════════════════════════════════════════════════════════════════════
object FlagsSource {

    private const val PREFS = "driftglass_flags_debug"
    private lateinit var ctx: Context

    @Volatile private var debugOverride: AppFlags? = null

    /** Що діє зараз. */
    val current: AppFlags get() = debugOverride ?: AppFlags.DEFAULT

    fun init(context: Context) {
        ctx = context.applicationContext
        if (BuildConfig.DEBUG) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("json", null)
                ?.let { debugOverride = AppFlags.parse(it) }
        }
        log("flags on start: ${debugLine()}")
    }

    fun isAdMode(): Boolean = current.isAdMode

    /** Debug: JSON з adb; порожній рядок — прибрати підміну. */
    fun setDebugOverride(json: String) {
        if (!BuildConfig.DEBUG) return
        debugOverride = if (json.isBlank()) null else AppFlags.parse(json)
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            if (json.isBlank()) remove("json") else putString("json", json)
        }
        log("flags (debug override): ${debugLine()}")
    }

    /**
     * Увімкнути — реклама на «Додому» / після «Недавніх», роль HOME обов'язкова, видаляти з
     * лаунчера не можна. Вимкнути — DEFAULT. Синхронний commit: далі процес перезапускаємо.
     */
    fun setAdMode(on: Boolean) {
        if (!BuildConfig.DEBUG) return
        val flags = if (on) AppFlags.AD_MODE else null
        debugOverride = flags
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) {
            if (flags == null) remove("json") else putString("json", AppFlags.encode(flags))
        }
        log("AD_MODE ${if (on) "on" else "off"}: $flags")
    }

    fun debugLine(): String {
        val f = current
        val src = if (debugOverride != null) "debug" else "default"
        return "[$src] enabled_url_ad=${f.enabledUrlAd} home_required=${f.homeRequired} is_uninstall=${f.isUninstall}" +
            (if (f.url.isNotBlank()) " url=${f.url}" else "")
    }
}
