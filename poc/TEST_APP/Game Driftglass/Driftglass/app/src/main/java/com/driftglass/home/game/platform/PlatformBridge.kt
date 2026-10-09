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
    /** Прапорці AD_MODE (core/config/AppFlags, джерело — android/FlagsSource). Дешево, без IO. */
    fun flags(): com.driftglass.home.core.config.AppFlags
    /** Увімкнути / вимкнути AD_MODE і перезапустити апку (лише debug-збірка). */
    fun setAdMode(on: Boolean)
    fun flagsDebug(): String
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
