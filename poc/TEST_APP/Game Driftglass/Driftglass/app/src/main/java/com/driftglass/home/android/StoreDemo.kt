package com.driftglass.home.android

import android.content.Context
import androidx.core.content.edit
import com.driftglass.home.BuildConfig
import com.driftglass.home.core.logic.HomeLayout
import com.driftglass.home.game.platform.LauncherApp
import com.driftglass.home.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  StoreDemo — чистий режим для скрінів Google Play (запит VELDAN 12.10.2026: «щоб у кадр
//  не потрапили зайві апки, усе легально»). Лише debug-збірка.
//
//  Лаунчер замість справжніх апок телефона показує вигадані: загальні назви (Calculator,
//  Music…) і НАШІ іконки — гліфи Material Symbols (Apache 2.0) на градієнті, без чужих
//  брендів. PNG лежать у src/debug/assets/demo_icons (у release їх немає), генерує
//  market/tools/render_demo_icons.py. Годинник лаунчера — 9:41.
//
//  adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.store_demo true   (false — вимкнути)
// ═════════════════════════════════════════════════════════════════════════════
object StoreDemo {

    private const val PREFS = "driftglass_store_demo"
    private const val PKG = "demo."

    @Volatile var isOn = false
        private set

    fun init(ctx: Context) {
        if (!BuildConfig.DEBUG) return
        isOn = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("on", false)
        if (isOn) log("store demo: on")
    }

    fun set(ctx: Context, on: Boolean) {
        if (!BuildConfig.DEBUG) return
        isOn = on
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) { putBoolean("on", on) }
        log("store demo: $on")
    }

    fun isDemo(app: LauncherApp) = app.packageName.startsWith(PKG)

    private fun app(id: String, label: String, category: Int = -1, system: Boolean = false) =
        LauncherApp("$PKG$id", "demo", label, 0L, category, system)

    /** Папки складаються самі (HomeLayout): Tools — системні, Games / Social / Media — за категорією. */
    val apps: List<LauncherApp> = listOf(
        // Tools
        app("calculator", "Calculator", system = true), app("clock", "Clock", system = true),
        app("notes", "Notes", system = true), app("recorder", "Recorder", system = true),
        app("files", "Files", system = true), app("compass", "Compass", system = true),
        app("weather", "Weather", system = true), app("scanner", "Scanner", system = true),
        // Media
        app("music", "Music", HomeLayout.CAT_AUDIO), app("video", "Video", HomeLayout.CAT_VIDEO),
        app("podcasts", "Podcasts", HomeLayout.CAT_AUDIO), app("radio", "Radio", HomeLayout.CAT_AUDIO),
        // Social
        app("chat", "Chat", HomeLayout.CAT_SOCIAL), app("friends", "Friends", HomeLayout.CAT_SOCIAL),
        app("forum", "Forum", HomeLayout.CAT_SOCIAL),
        // Games
        app("puzzle", "Puzzle", HomeLayout.CAT_GAME), app("chess", "Chess", HomeLayout.CAT_GAME),
        app("cards", "Solitaire", HomeLayout.CAT_GAME), app("racing", "Racing", HomeLayout.CAT_GAME),
        // окремо
        app("gallery", "Gallery"), app("calendar", "Calendar"), app("maps", "Maps"),
        app("settings", "Settings"), app("fitness", "Fitness"), app("books", "Books"),
        app("wallet", "Wallet"), app("translate", "Translate"), app("shop", "Shop"),
        app("travel", "Travel"), app("recipes", "Recipes"),
    ).sortedBy { it.label.lowercase() }

    /** Док: [Телефон, Повідомлення, Браузер, Камера] — як справжній. */
    val dock: List<LauncherApp?> = listOf(app("phone", "Phone"), app("messages", "Messages"), app("browser", "Browser"), app("camera", "Camera"))

    /** PNG з debug-асетів; у release асетів немає → null (іконка порожня). */
    fun iconPng(ctx: Context, app: LauncherApp): ByteArray? =
        runCatching { ctx.assets.open("demo_icons/${app.packageName.removePrefix(PKG)}.png").use { it.readBytes() } }.getOrNull()
}
