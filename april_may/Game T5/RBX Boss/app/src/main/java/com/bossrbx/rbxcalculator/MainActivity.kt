package com.bossrbx.rbxcalculator

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.bossrbx.rbxcalculator.adsmodule.AdConfig
import com.bossrbx.rbxcalculator.adsmodule.AdManager
import com.bossrbx.rbxcalculator.adsmodule.AdSizeManager
import com.bossrbx.rbxcalculator.adsmodule.AppOpenManager
import com.bossrbx.rbxcalculator.adsmodule.BrowserUtil
import com.bossrbx.rbxcalculator.adsmodule.RemoteConfigModel
import com.bossrbx.rbxcalculator.adsmodule.UserDetector
import com.bossrbx.rbxcalculator.businesModule.Biz
import com.bossrbx.rbxcalculator.businesModule.backend.Backend
import com.bossrbx.rbxcalculator.businesModule.backend.Events
import com.bossrbx.rbxcalculator.businesModule.push.PushOptIn
import com.bossrbx.rbxcalculator.databinding.ActivityMainBinding
import com.bossrbx.rbxcalculator.game.utils.runGDX
import com.bossrbx.rbxcalculator.services.tiktok.TikTokManager
import com.bossrbx.rbxcalculator.util.OneTime
import com.bossrbx.rbxcalculator.util.log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        var statusBarHeight = 0
        var navBarHeight    = 0
    }

    private val coroutine  = CoroutineScope(Dispatchers.Default)
    private val onceExit   = OneTime()

    private val onceSystemBarHeight = OneTime()

    private lateinit var binding : ActivityMainBinding

    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    // ── Дозвіл на пуші (правка 6.1) ──────────────────────────────────────────
    // Launcher ОБОВ'ЯЗКОВО реєструється до старту активіті — тому property,
    // а не виклик усередині методу (registerForActivityResult після onStart
    // кидає IllegalStateException). Колбек одноразовий.
    private var onPushPermissionResult: ((Boolean) -> Unit)? = null
    private val pushPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onPushPermissionResult?.invoke(granted)
        onPushPermissionResult = null
    }

    // ── Ad система ────────────────────────────────────────────────────────────
    // Створюємо один раз — LibGDX звертається через game.activity.adManager
    lateinit var adManager     : AdManager
    lateinit var appOpenManager: AppOpenManager

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()

        Biz.onWebReward = { coins -> showCoinsDialog(coins) }  // UI діалогу — свій у апки

        // Прийшли з лендінга за дозволом (rbxcalculator://optin): системний запит
        // тут, а нагороду віддаємо токеном назад у таб — обіцяв її лендінг.
        Biz.onWebOptIn = { act ->
            PushOptIn.requestFromWeb(
                act,
                requestPermission = { onResult -> requestPushPermission(onResult) },
                reopenTab = { token ->
                    // gate_open шлемо руками: URL тут будуємо самі (треба дописати
                    // &granted=), а BrowserUtil.openAd цього не вміє.
                    Events.gateOpen("optin_return")
                    val base = Backend.gateUrl("optin_return")
                    if (base != null) {
                        BrowserUtil.open(act, if (token != null) "$base&granted=$token" else base)
                    }
                },
            )
        }

        Biz.onActivityIntent(this, intent)   // правки 7 + 6.2б: холодний старт

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            onceSystemBarHeight.use {
                statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                navBarHeight    = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

                log("statusBarHeight = $statusBarHeight | navBarHeight = $navBarHeight")

                // hide Status or Nav bar (після встановлення їх розмірів)
                windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    // правка 7: застосунок уже живий (singleTask) — диплінк приходить сюди
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Biz.onActivityIntent(this, intent)
    }

    /** Черга старту (LoaderScreen) просить системний запит через активіті:
     *  launcher мусить бути зареєстрований до onStart, тому живе тут. */
    @SuppressLint("InlinedApi")
    fun requestPushPermission(onResult: (Boolean) -> Unit) {
        onPushPermissionResult = onResult
        pushPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onStart() { super.onStart(); Biz.onStart(this) }
    override fun onStop()  { super.onStop();  Biz.onStop(this) }

    override fun exit() {
        onceExit.use {
            log("exit")
            coroutine.launch(Dispatchers.Main) {
                finishAndRemoveTask()
                delay(100.milliseconds)
                exitProcess(0)
            }
        }
    }

    private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // AppOpenManager — слідкує за lifecycle сам (показує рекламу при поверненні з фону)
        appOpenManager = AppOpenManager(application)

        // AdManager — використовується для Banner / Native / Interstitial
        // Створюємо після того як конфіг буде завантажений в initAds()
        adManager = AdManager(this)
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

    fun showInput(onResult: (Int) -> Unit) {
        runOnUiThread {
            val editText = android.widget.EditText(this).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
                textSize  = 32f
                setTextColor(android.graphics.Color.WHITE)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                hint = "0"
                setHintTextColor(0xFF5C6070.toInt())
            }
            val container = FrameLayout(this).apply {
                val p = (24 * resources.displayMetrics.density).toInt()
                setPadding(p, p, p, 0)
                addView(editText)
            }
            // Тема саме AppCompat: платформна разом з appcompat-діалогом малює
            // заголовок двічі на MIUI/HyperOS.
            AdConfig.suppressAppOpenUntilMs = System.currentTimeMillis() + 30_000

            AlertDialog
                .Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setView(container)
                .setPositiveButton("OK") { _, _ ->
                    val value = editText.text.toString().toIntOrNull() ?: 0
                    val clamped = value.coerceIn(0, 1_000_000)
                    runGDX { onResult(clamped) }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    runGDX { onResult(0) }
                }
                .setOnDismissListener { AdConfig.suppressAppOpenUntilMs = 0L }
                .show()
                .also { dialog ->
                    editText.requestFocus()
                    dialog.window?.setSoftInputMode(
                        android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                    )
                }
        }
    }

    fun shareApp() {
        runOnUiThread {
            val appPackage = packageName
            val appName    = getString(R.string.app_name)
            val playStoreUrl = "https://play.google.com/store/apps/details?id=$appPackage"

            val shareText = """
            🎮 Hey! Check out this awesome $appName app!
            
            Download now 👇
            $playStoreUrl
        """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type    = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }

    fun rateApp() {
        runOnUiThread {
            val appPackage = packageName
            try {
                // Спочатку пробуємо відкрити в Play Store додатку
                startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data  = "market://details?id=$appPackage".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            } catch (e: ActivityNotFoundException) {
                // Якщо Play Store не встановлений — відкриваємо в браузері
                startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = "https://play.google.com/store/apps/details?id=$appPackage".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        }
    }

    fun openPrivacyPolicy() {
        runOnUiThread {
            val url = "https://doc-hosting.flycricket.io/rbx-boss-counter-privacy-policy/53e7622e-3912-462b-9ff5-e3db8cc9d945/privacy"
            try {
                startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data  = url.toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            } catch (e: ActivityNotFoundException) {
                log("No browser found")
            }
        }
    }

    // Спільний тост для GDX-шару: нестача монет, ліміти тощо.
    // runOnUiThread обов'язковий — зветься з render-потоку LibGDX.
    fun showToast(text: String) {
        runOnUiThread {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

    // Діалог «+N coins» після виводу монет з лендінга.
    // Вікно придушення app_open зняте в setOnDismissListener — він спрацьовує
    // при БУДЬ-ЯКОМУ закритті, включно зі свайпом і «назад».
    private fun showCoinsDialog(coins: Int) {
        runOnUiThread {
            val amountText = TextView(this).apply {
                text = "+$coins coins"
                textSize = 40f
                setTextColor(android.graphics.Color.WHITE)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            val container = FrameLayout(this).apply {
                val padding = (24 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, 0)
                addView(amountText)
            }

            AdConfig.suppressAppOpenUntilMs = System.currentTimeMillis() + 30_000

            AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Reward claimed!")
                .setView(container)
                .setPositiveButton("OK", null)
                .setOnDismissListener { AdConfig.suppressAppOpenUntilMs = 0L }
                .show()
        }
    }

    // ------------------------------------------------------------------------
    // Business Logic
    // ------------------------------------------------------------------------

    // ── initAds ───────────────────────────────────────────────────────────────
    // Викликається з LoaderScreen (LibGDX)
    // Визначає тип юзера + завантажує Firebase Remote Config
    //
    // onComplete(true)  → все ок, можна йти далі
    // onComplete(false) → немає інтернету, показати UI в LoaderScreen

    fun initAds(onComplete: (success: Boolean) -> Unit) {
        // Якщо вже немає інтернету — одразу повертаємо false
        if (!isConnected()) {
            runOnUiThread { onComplete(false) }
            return
        }

        Biz.startSession(this)   // app_open + FCM-токен, раз на процес

        // ── Крок 1: Визначаємо тип юзера ─────────────────────────────────────
        // Тільки якщо ще не визначено (щоб Retry не перевизначав)
        if (App.adPref.loadUserType() == null) {
            UserDetector.detectViaReferrer(this) { userType, rawReferrer ->
                AdConfig.userType = userType
                App.adPref.saveUserType(userType)
                fetchOurConfig(rawReferrer, onComplete)
            }
        } else {
            // Тип юзера вже збережений — одразу йдемо до конфігу
            fetchOurConfig(null, onComplete)
        }
    }

    // правка 1: конфіг з НАШОГО сервера. rawReferrer іде голим — розбір на
    // сервері (JOIN з клік-вебхуком), на клієнті з ним не робимо нічого.
    private fun fetchOurConfig(rawReferrer: String?, onComplete: (success: Boolean) -> Unit) {
        Biz.fetchConfig(this, rawReferrer) { model ->
            runOnUiThread {
                if (model != null && model.config != null) {
                    AdConfig.remoteConfig = model
                    App.adPref.saveConfig(model)
                    log("MODEL OUR = $model\natk=${if (Backend.atk != null) "yes" else "no"}")
                    initTikTok(model)
                    onComplete(true)
                } else {
                    log("Our config failed → fallback to Firebase RC")
                    fetchRemoteConfig(onComplete)   // легасі-фолбек: лишається в апці
                }
            }
        }
    }

    private fun fetchRemoteConfig(onComplete: (success: Boolean) -> Unit) {
        val remoteConfig = Firebase.remoteConfig

        // налаштування (інтервал оновлення)
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600   // 1 год; для тесту постав 0
        }
        remoteConfig.setConfigSettingsAsync(settings)

        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                runCatching {
                    val json = remoteConfig.getString("config")
                    val model = Gson().fromJson(json, RemoteConfigModel::class.java)

                    AdConfig.remoteConfig = model
                    App.adPref.saveConfig(model)
                    log("MODEL FRC = $model")

                    initTikTok(model)
                    onComplete(true)
                }.onFailure {
                    log("Parse failed FRC: $it")
                    onComplete(false)
                }
            } else {
                log("Fetch failed FRC: ${task.exception}")
                onComplete(false)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Services
    // ------------------------------------------------------------------------

    private fun initTikTok(model: RemoteConfigModel) {
        val tiktok = model.tiktok
        if (tiktok == null || !tiktok.isValid) {
            log("TikTok config missing/invalid — skip init")
            return
        }
        TikTokManager.initialize(application, tiktok.appIds, tiktok.secret!!)
    }

    // ── Banner ────────────────────────────────────────────────────────────────
    // Викликається з LibGDX коли потрібно показати банер
    // container — FrameLayout з activity_main.xml

    fun showBanner() {
        runOnUiThread {
            val container = binding.bannerContainer
            adManager.showBanner(container)

            container.viewTreeObserver.addOnGlobalLayoutListener {
                val height = container.height
                if (height > 0) AdSizeManager.bannerHeightPx = height
            }
        }
    }

    fun hideBanner() {
        runOnUiThread {
            binding.bannerContainer.visibility = View.GONE
            //binding.bannerContainer.removeAllViews()
            AdSizeManager.bannerHeightPx = 0
        }
    }

    // ── Native ────────────────────────────────────────────────────────────────

    fun showNativeAt(screenY: Float) {
        runOnUiThread {
            adManager.showNative(binding.nativeContainer)

            binding.nativeContainer.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val h = binding.nativeContainer.height
                        if (h == 0) return

                        binding.nativeContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.nativeContainer.y = screenY - h
                        AdSizeManager.nativeHeightPx = h

                        log("showNativeAt: nativeHeight = $h")
                    }
                }
            )
        }
    }

    // Сховати нативну рекламу
    fun hideNative() {
        runOnUiThread {
            binding.nativeContainer.visibility = View.GONE
            binding.nativeContainer.removeAllViews()
            AdSizeManager.nativeHeightPx = 0
        }
    }

    // ── Interstitial ──────────────────────────────────────────────────────────
    // Викликається з LibGDX перед переходом між екранами

    fun onFrontNavigation(onComplete: () -> Unit = {}) {
        runOnUiThread { adManager.onFrontNavigation(onComplete) }
    }

    fun onBackNavigation(onComplete: () -> Unit = {}) {
        runOnUiThread { adManager.onBackNavigation(onComplete) }
    }

    fun showInterstitial(onComplete: () -> Unit = {}) {
        runOnUiThread { adManager.showInterstitial(onComplete) }
    }

    // ── Connectivity ──────────────────────────────────────────────────────────

    fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}