package com.driftglass.home.game.platform

// ═════════════════════════════════════════════════════════════════════════════
//  PlatformBridge — ЄДИНИЙ контракт між LibGDX-частиною і Android.
//
//  GDX-код (екрани, актори) НЕ імпортує android.*; лише цей інтерфейс.
//  Реалізація — AndroidBridge (MainActivity створює і віддає в GDXGame).
//
//  ПОТОКИ:
//    • методи викликаються з GL-потоку; важке (іконки, WallpaperManager) реалізація
//      сама переносить у фон;
//    • колбеки onResult реалізація ЗАВЖДИ повертає в GL-потік (runGDX);
//    • «синхронні» геттери (isDefaultHome, battery) — дешеві, можна раз на секунду.
// ═════════════════════════════════════════════════════════════════════════════
interface PlatformBridge {

    // ── Система ──────────────────────────────────────────────────────────────
    /** Висоти системних барів у px (WindowInsets) — для safe area, як у T35. */
    val statusBarPx: Int
    val navBarPx: Int
    fun showToast(text: String)
    fun vibrate(ms: Long = 12)
    fun openUrl(url: String)
    /** Лаунчер не можна закривати: «вихід» = moveTaskToBack(true), а не Gdx.app.exit(). */
    fun moveToBack()
    val appVersion: String
    fun isOnline(): Boolean

    // ── Конфіг з нашого сервера (businesModule, ServerConfig) ───────────────
    /** Режим з картки `home`: app_open = custom* → «наша реклама»; офлайн / na / admob — DEFAULT. Не щокадру. */
    fun appFlags(): com.driftglass.home.core.config.AppFlags
    /** Чи є конфіг (хоч з кешу минулого запуску). Ні — Splash чекає сервер. */
    fun hasConfig(): Boolean
    /** /appconfig; onDone у GL-потоці рівно один раз — по відповіді або по таймауту. */
    fun loadConfig(timeoutMs: Long, onDone: () -> Unit)
    /** Settings → Debug: звідки конфіг, atk, тип юзера, режим. */
    fun configDebug(): String
    /** Debug AD_MODE: «наша реклама» примусово (вкладки на «Додому», роль обов'язкова, без Uninstall). */
    fun isAdMode(): Boolean
    /** Увімкнути / вимкнути AD_MODE і перезапустити апку (лише debug-збірка). */
    fun setAdMode(on: Boolean)

    // ── Бізнес-логіка UAPP: черга старту, реклама (adsmodule), події ─────────
    /** Черга старту UAPP (Biz.runStartupFlow): опт-ін → стартовий app_open → onDone у GL-потоці рівно один раз. */
    fun runStartupFlow(onDone: () -> Unit)
    /** app_open при поверненні в застосунок (AppOpenManager.onReturnToApp). */
    fun appOpenOnReturn()
    /** Інтерстішал на переходах (AdManager): частоту front/back рахує картка сервера. */
    fun onFrontNavigation()
    fun onBackNavigation()
    /** Застосували / зберегли шпалери — лічильник для showInterstitial (кожні 2). */
    fun onWallpaperAction()
    fun isInterstitialDue(): Boolean
    /** showInterstitial (pl=interstitial / AdMob), скидає лічильник. */
    fun showInterstitial()
    /** Settings → Debug: провайдер інтерстішала, лічильники. */
    fun adsDebug(): String
    /** TikTok UNLOCK_ACHIEVEMENT (AnalyticsManager.openHomeScreen) — вхід у застосунок, як MainScreen еталона. */
    fun openHomeScreen()
    /** Подія screen_view (Events): bt — спільний словник парку, block — snake_case, не перейменовувати. */
    fun screenView(bt: com.driftglass.home.businesModule.backend.Bt, block: String)

    // ── Debug: чистий режим для скрінів Google Play (лише debug-збірка) ──────
    /** Лаунчер показує вигадані апки з нашими іконками (src/debug/assets/demo_icons), годинник 9:41. */
    val isStoreDemo: Boolean
    /** Заряд 0..100 і чи на зарядці — для режиму економії. */
    fun battery(): BatteryState

    // ── Текстове поле: нативний EditText поверх GL (пошук апок) ──────────────
    fun beginTextInput(req: TextInputRequest, onChange: (String) -> Unit, onDone: (text: String, submitted: Boolean) -> Unit)
    fun endTextInput()

    // ── Роль HOME ────────────────────────────────────────────────────────────
    fun isDefaultHome(): Boolean
    /** RoleManager.createRequestRoleIntent(ROLE_HOME) на Q+, ACTION_HOME_SETTINGS на старших. */
    fun requestDefaultHome(onResult: (isHome: Boolean) -> Unit)
    /** Системний екран вибору головного застосунку (Settings → вимкнути «Use as Home screen»). */
    fun openHomeAppSettings()

    // ── Шпалери системи ──────────────────────────────────────────────────────
    /**
     * Нерухомий кадр (PNG) у WallpaperManager. system — головний екран системного лаунчера
     * (коли Driftglass не HOME), lock — екран блокування. onDone(ok) — у GL-потоці.
     */
    fun setStillWallpaper(png: ByteArray, system: Boolean, lock: Boolean, onDone: (ok: Boolean) -> Unit)

    // ── Лаунчер ──────────────────────────────────────────────────────────────
    /** LauncherApps.getActivityList для всіх профілів (робочий профіль теж), без самої Driftglass. */
    fun listApps(): List<LauncherApp>
    fun appIconPng(app: LauncherApp, sizePx: Int, onResult: (ByteArray?) -> Unit)
    fun launchApp(app: LauncherApp)
    /** Док: дефолтні апки ролей [Телефон, Повідомлення, Браузер, Камера]; null — немає дефолту. */
    fun dockApps(): List<LauncherApp?>
    /** app = null — сама Driftglass. Системні й робочого профілю — лише «Про застосунок». */
    fun canUninstall(app: LauncherApp?): Boolean
    fun openAppInfo(app: LauncherApp?)
    fun uninstallApp(app: LauncherApp?)
}

/** Android → GDX. Реалізує GDXGame; Android кличе вже через runGDX (GL-потік). */
interface PlatformEvents {
    /** onNewIntent з CATEGORY_HOME: показати LauncherScreen. */
    fun onHomePressed()
    /** Іконку Driftglass натиснули (трамплін), коли апка вже жива. */
    fun onAppIconPressed()
    fun onAppsChanged()
    /** ACTION_USER_PRESENT — людина розблокувала екран (Auto-shuffle «кожне розблокування»). */
    fun onUnlocked()
    /** onResume Activity: перевірити розклад Auto-shuffle, роль, мову. */
    fun onAppResumed()
    /**
     * Людина повернулась у Driftglass з іншої апки / «Недавніх» / вимкненого екрана (не через «Додому»,
     * не з нашої реклами, вкладки чи системного екрана): awayMs — скільки її не було (-1 — не йшли).
     * fromIcon = true — тапнули іконку Driftglass (відкриття апки, а не повернення).
     */
    fun onAppForeground(awayMs: Long, fromIcon: Boolean)
    /** Режим змінився (новий конфіг сервера / інтернет / AD_MODE). */
    fun onFlagsChanged(flags: com.driftglass.home.core.config.AppFlags)
}

data class BatteryState(val pct: Int, val charging: Boolean)

data class TextInputRequest(
    val xPx: Int, val yPx: Int, val wPx: Int, val hPx: Int,
    val text: String,
    val hint: String,
    val textSizePx: Float,
)

data class LauncherApp(
    val packageName: String,
    val activityName: String,
    val label: String,
    /** UserManager.getSerialNumberForUser — розрізняє особистий і робочий профіль. */
    val userSerial: Long,
    /** ApplicationInfo.category (API 26+), -1 — не вказано. Для авто-папок (core/logic/HomeLayout). */
    val category: Int = -1,
    /** Системна (передвстановлена) — йде в папку «Tools», як у MIUI. */
    val system: Boolean = false,
)
