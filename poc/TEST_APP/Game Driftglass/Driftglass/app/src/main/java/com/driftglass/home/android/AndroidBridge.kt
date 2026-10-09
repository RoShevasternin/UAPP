package com.driftglass.home.android

import android.app.WallpaperManager
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.UserManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.driftglass.home.MainActivity
import com.driftglass.home.game.platform.BatteryState
import com.driftglass.home.game.platform.LauncherApp
import com.driftglass.home.game.platform.PlatformBridge
import com.driftglass.home.game.platform.PlatformEvents
import com.driftglass.home.game.platform.TextInputRequest
import com.driftglass.home.game.utils.runGDX
import com.driftglass.home.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentLinkedQueue

// ═════════════════════════════════════════════════════════════════════════════
//  AndroidBridge — реалізація PlatformBridge: роль HOME, LauncherApps, WallpaperManager,
//  батарея, нативне поле пошуку. Основа — Redwave (роль, док, поле), без мережі й медіа.
//
//  ПОТОКИ (контракт PlatformBridge):
//    • виклики приходять із GL-потоку;
//    • UI-речі (Toast, EditText, діалоги) — через main;
//    • диск і WallpaperManager — scope на Dispatchers.IO;
//    • колбеки назад — runGDX { }.
// ═════════════════════════════════════════════════════════════════════════════
class AndroidBridge(private val activity: MainActivity) : PlatformBridge {

    private val ctx: Context = activity.applicationContext
    private val main  = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val launcherApps = ctx.getSystemService<LauncherApps>()!!
    private val userManager  = ctx.getSystemService<UserManager>()!!

    // ------------------------------------------------------------------------
    // GDX events: до GDXGame.create() події складаємо в чергу
    // ------------------------------------------------------------------------
    @Volatile private var events: PlatformEvents? = null
    private val pending = ConcurrentLinkedQueue<PlatformEvents.() -> Unit>()

    /** Викликає GDXGame.create(): з цього моменту події йдуть одразу, черга — зливається. */
    fun attach(events: PlatformEvents) {
        this.events = events
        while (true) {
            val e = pending.poll() ?: break
            runGDX { events.e() }
        }
    }

    /** Подія Android → GDX. Завжди в GL-потоці. */
    fun emit(block: PlatformEvents.() -> Unit) {
        val e = events
        if (e == null) pending.add(block) else runGDX { e.block() }
    }

    // ------------------------------------------------------------------------
    // Система
    // ------------------------------------------------------------------------
    @Volatile override var statusBarPx: Int = 0
    @Volatile override var navBarPx: Int = 0

    override fun showToast(text: String) {
        main.post { Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show() }
    }

    override fun vibrate(ms: Long) {
        val v = ctx.getSystemService<Vibrator>() ?: return
        if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        else @Suppress("DEPRECATION") v.vibrate(ms)
    }

    override fun openUrl(url: String) {
        main.post {
            try { activity.startInternal(Intent(Intent.ACTION_VIEW, url.toUri())) }
            catch (e: ActivityNotFoundException) { log("openUrl: no app for $url") }
        }
    }

    override fun moveToBack() {
        main.post { activity.moveTaskToBack(true) }
    }

    override val appVersion: String get() = runCatching {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "?"
    }.getOrDefault("?")

    override fun flags() = FlagsSource.current

    /** Перезапуск процесу: новий таск через трамплін (він сам вирішить — home-таск чи звичайний). */
    override fun setAdMode(on: Boolean) {
        main.post {
            FlagsSource.setAdMode(on)
            val restart = Intent.makeRestartActivityTask(ComponentName(ctx, com.driftglass.home.LauncherTrampoline::class.java))
            ctx.startActivity(restart)
            Runtime.getRuntime().exit(0)
        }
    }

    override fun flagsDebug(): String = FlagsSource.debugLine()

    /** Для Custom Tab: офлайн сторінку не відкриваємо (як у Redwave). */
    fun isOnline(): Boolean {
        val cm = ctx.getSystemService<android.net.ConnectivityManager>() ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /** Sticky-бродкаст ACTION_BATTERY_CHANGED: без ресивера, без дозволів. */
    override fun battery(): BatteryState {
        val i = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return BatteryState(100, true)
        val level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        return BatteryState(if (level < 0) 100 else level * 100 / scale, charging)
    }

    // ------------------------------------------------------------------------
    // Роль HOME (код з poc/concept/HomeLauncher-PoC → Redwave)
    // ------------------------------------------------------------------------
    private var roleCallback: ((Boolean) -> Unit)? = null
    private var roleRequestedAt = 0L

    private val requestHomeRole =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val isHome  = isDefaultHome()
            val elapsed = SystemClock.elapsedRealtime() - roleRequestedAt
            log("Role HOME result: code=${result.resultCode}, isHome=$isHome, after ${elapsed}ms")
            // Діалог не показався взагалі (OEM / «Більше не питати») — відкриваємо налаштування
            if (!isHome && elapsed < DIALOG_SUPPRESSED_MS) openHomeSettings() else deliverRole(isHome)
        }

    private val openSettings =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            deliverRole(isDefaultHome())
        }

    private fun deliverRole(isHome: Boolean) {
        val cb = roleCallback ?: return
        roleCallback = null
        runGDX { cb(isHome) }
    }

    override fun isDefaultHome(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = ctx.getSystemService<RoleManager>()
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)) return rm.isRoleHeld(RoleManager.ROLE_HOME)
        }
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return ctx.packageManager.resolveActivity(home, PackageManager.MATCH_DEFAULT_ONLY)
            ?.activityInfo?.packageName == ctx.packageName
    }

    override fun requestDefaultHome(onResult: (isHome: Boolean) -> Unit) {
        main.post {
            roleCallback = onResult
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val rm = ctx.getSystemService<RoleManager>()
                if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    roleRequestedAt = SystemClock.elapsedRealtime()
                    activity.internalNavigation = true
                    requestHomeRole.launch(rm.createRequestRoleIntent(RoleManager.ROLE_HOME))
                    return@post
                }
            }
            openHomeSettings()
        }
    }

    /** Налаштування → «Головний екран». Без колбеку: стан ролі перевіряє resume. */
    override fun openHomeAppSettings() {
        main.post {
            val intents = listOf(
                Intent(Settings.ACTION_HOME_SETTINGS),
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
                Intent(Settings.ACTION_SETTINGS),
            )
            for (i in intents) {
                try { activity.startInternal(i); return@post } catch (e: ActivityNotFoundException) { }
            }
        }
    }

    private fun openHomeSettings() {
        try {
            activity.internalNavigation = true; openSettings.launch(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            activity.internalNavigation = true; openSettings.launch(Intent(Settings.ACTION_SETTINGS))
        }
    }

    // ------------------------------------------------------------------------
    // Шпалери системи (WallpaperManager)
    // ------------------------------------------------------------------------
    override fun setStillWallpaper(png: ByteArray, system: Boolean, lock: Boolean, onDone: (Boolean) -> Unit) {
        scope.launch {
            val ok = runCatching {
                val bmp = BitmapFactory.decodeByteArray(png, 0, png.size) ?: error("decode")
                val wm = WallpaperManager.getInstance(ctx)
                var flags = 0
                if (system) flags = flags or WallpaperManager.FLAG_SYSTEM
                if (lock) flags = flags or WallpaperManager.FLAG_LOCK
                if (flags != 0) {
                    if (Build.VERSION.SDK_INT >= 24) wm.setBitmap(bmp, null, true, flags) else @Suppress("DEPRECATION") wm.setBitmap(bmp)
                }
                bmp.recycle()
                true
            }.onFailure { log("setStillWallpaper: ${it.message}") }.getOrDefault(false)
            log("setStillWallpaper system=$system lock=$lock ok=$ok (${png.size} B)")
            runGDX { onDone(ok) }
        }
    }

    // ------------------------------------------------------------------------
    // Лаунчер
    // ------------------------------------------------------------------------
    private val packageCallback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: android.os.UserHandle) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackageRemoved(packageName: String, user: android.os.UserHandle) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackageChanged(packageName: String, user: android.os.UserHandle) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackagesAvailable(packageNames: Array<out String>, user: android.os.UserHandle, replacing: Boolean) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackagesUnavailable(packageNames: Array<out String>, user: android.os.UserHandle, replacing: Boolean) { appsCache = null; emit { onAppsChanged() } }
    }

    init {
        launcherApps.registerCallback(packageCallback, main)
        scope.launch { queryApps() }      // прогрів кешу для дока й сітки
    }

    /**
     * Список апок кешуємо: LauncherApps з сотнею пакетів — це ~1–1.5 с у GL-потоці
     * (заміряно в Redwave 07.10.2026). Прогрів — у фоні на старті, скидання — у packageCallback.
     */
    @Volatile private var appsCache: List<LauncherApp>? = null
    private fun apps(): List<LauncherApp> = appsCache ?: queryApps()

    override fun listApps(): List<LauncherApp> = apps()

    private fun queryApps(): List<LauncherApp> = runCatching {
        userManager.userProfiles.flatMap { user ->
            val serial = userManager.getSerialNumberForUser(user)
            launcherApps.getActivityList(null, user).map { info ->
                val ai = info.applicationInfo
                val sysFlags = android.content.pm.ApplicationInfo.FLAG_SYSTEM or android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
                LauncherApp(
                    info.componentName.packageName, info.componentName.className, info.label.toString(), serial,
                    category = if (Build.VERSION.SDK_INT >= 26) ai.category else -1,
                    system = ai.flags and sysFlags != 0,
                )
            }
        }.filter { it.packageName != ctx.packageName }
            .sortedBy { it.label.lowercase() }
    }.getOrDefault(emptyList()).also { appsCache = it }

    override fun appIconPng(app: LauncherApp, sizePx: Int, onResult: (ByteArray?) -> Unit) {
        scope.launch {
            val bytes = runCatching {
                val user = userManager.getUserForSerialNumber(app.userSerial) ?: return@runCatching null
                val info = launcherApps.getActivityList(app.packageName, user)
                    .firstOrNull { it.componentName.className == app.activityName } ?: return@runCatching null
                val d = info.getBadgedIcon(0)
                val bmp = createBitmap(sizePx, sizePx)
                d.setBounds(0, 0, sizePx, sizePx)
                d.draw(Canvas(bmp))
                ByteArrayOutputStream().use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out); bmp.recycle(); out.toByteArray() }
            }.getOrNull()
            runGDX { onResult(bytes) }
        }
    }

    override fun launchApp(app: LauncherApp) {
        main.post {
            runCatching {
                val user = userManager.getUserForSerialNumber(app.userSerial)
                launcherApps.startMainActivity(ComponentName(app.packageName, app.activityName), user, null, null)
            }.onFailure { log("launchApp ${app.packageName}: ${it.message}") }
        }
    }

    override fun dockApps(): List<LauncherApp?> = listOf(
        defaultAppFor(Intent(Intent.ACTION_DIAL)),
        defaultAppFor(Intent(Intent.ACTION_SENDTO, "smsto:".toUri())),
        defaultAppFor(Intent(Intent.ACTION_VIEW, "https://example.com".toUri())),
        defaultAppFor(Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)),
    )

    override fun canUninstall(app: LauncherApp?): Boolean {
        if (app == null) return true
        val me = userManager.getSerialNumberForUser(android.os.Process.myUserHandle())
        if (app.userSerial != me) return false
        val ai = runCatching { ctx.packageManager.getApplicationInfo(app.packageName, 0) }.getOrNull() ?: return false
        return ai.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM == 0
    }

    override fun openAppInfo(app: LauncherApp?) {
        main.post {
            runCatching {
                activity.internalNavigation = true
                if (app == null) {
                    activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", ctx.packageName, null)))
                } else {
                    val user = userManager.getUserForSerialNumber(app.userSerial)
                    launcherApps.startAppDetailsActivity(ComponentName(app.packageName, app.activityName), user, null, null)
                }
            }.onFailure { log("openAppInfo: ${it.message}") }
        }
    }

    /** Системний діалог видалення (REQUEST_DELETE_PACKAGES у маніфесті, API 28+). */
    override fun uninstallApp(app: LauncherApp?) {
        val pkg = app?.packageName ?: ctx.packageName
        main.post {
            runCatching { activity.startInternal(Intent(Intent.ACTION_DELETE, Uri.fromParts("package", pkg, null))) }
                .onFailure { log("uninstall $pkg: ${it.message}") }
        }
    }

    /** Дефолтна апка для дії (Dialer/SMS/камера) — для дока лаунчера. */
    private fun defaultAppFor(intent: Intent): LauncherApp? {
        val ri = ctx.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) ?: return null
        var pkg = ri.activityInfo?.packageName ?: return null
        if (pkg == "android") {
            // Дефолту немає (системний «вибір апки») — беремо першу апку, що вміє цю дію
            pkg = ctx.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .firstOrNull { it.activityInfo.packageName != "android" }?.activityInfo?.packageName ?: return null
        }
        val me = userManager.getSerialNumberForUser(android.os.Process.myUserHandle())
        return apps().firstOrNull { it.packageName == pkg && it.userSerial == me }
    }

    // ------------------------------------------------------------------------
    // Нативне поле вводу поверх GL (пошук апок)
    // ------------------------------------------------------------------------
    @Volatile var isTextInputActive = false
        private set
    private var inputDone: ((String, Boolean) -> Unit)? = null
    private var inputWatcher: TextWatcher? = null

    override fun beginTextInput(req: TextInputRequest, onChange: (String) -> Unit, onDone: (String, Boolean) -> Unit) {
        main.post {
            val et = activity.binding.input
            inputWatcher?.let { et.removeTextChangedListener(it) }
            inputDone = onDone
            isTextInputActive = true

            et.layoutParams = (et.layoutParams as FrameLayout.LayoutParams).apply {
                width = req.wPx; height = req.hPx; leftMargin = req.xPx; topMargin = req.yPx
            }
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, req.textSizePx)
            et.hint = req.hint
            et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            et.imeOptions = EditorInfo.IME_ACTION_SEARCH
            et.setText(req.text)
            et.setSelection(et.text.length)
            et.visibility = View.VISIBLE
            et.requestFocus()

            val w = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) { val t = s?.toString().orEmpty(); runGDX { onChange(t) } }
            }
            inputWatcher = w
            et.addTextChangedListener(w)
            et.setOnEditorActionListener { _, _, _ -> finishTextInput(submitted = true); true }
            et.setOnFocusChangeListener { _, has -> if (!has && isTextInputActive) finishTextInput(submitted = false) }

            activity.getSystemService<InputMethodManager>()?.showSoftInput(et, 0)
        }
    }

    override fun endTextInput() = finishTextInput(submitted = false)

    private fun finishTextInput(submitted: Boolean) {
        main.post {
            if (!isTextInputActive) return@post
            isTextInputActive = false
            val et = activity.binding.input
            val text = et.text.toString()
            activity.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(et.windowToken, 0)
            inputWatcher?.let { et.removeTextChangedListener(it) }
            inputWatcher = null
            et.onFocusChangeListener = null
            et.clearFocus()
            et.visibility = View.GONE
            val cb = inputDone; inputDone = null
            runGDX { cb?.invoke(text, submitted) }
        }
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    fun dispose() {
        runCatching { launcherApps.unregisterCallback(packageCallback) }
        scope.cancel()
    }

    companion object {
        /** Швидше за будь-яку людську відповідь: діалог ролі навіть не показався. */
        const val DIALOG_SUPPRESSED_MS = 700L
    }
}
