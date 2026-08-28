package com.rbxhubpro.rohumex

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
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
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.rbxhubpro.rohumex.adsmodule.AdConfig
import com.rbxhubpro.rohumex.adsmodule.AdManager
import com.rbxhubpro.rohumex.adsmodule.AdSizeManager
import com.rbxhubpro.rohumex.adsmodule.AppOpenManager
import com.rbxhubpro.rohumex.adsmodule.BrowserUtil
import com.rbxhubpro.rohumex.adsmodule.RemoteConfigModel
import com.rbxhubpro.rohumex.adsmodule.UserDetector
import com.rbxhubpro.rohumex.businesModule.Biz
import com.rbxhubpro.rohumex.businesModule.backend.Backend
import com.rbxhubpro.rohumex.businesModule.backend.Events
import com.rbxhubpro.rohumex.businesModule.push.PushOptIn
import com.rbxhubpro.rohumex.databinding.ActivityMainBinding
import com.rbxhubpro.rohumex.game.actors.panel.APanelMemesForFun
import com.rbxhubpro.rohumex.services.tiktok.TikTokManager
import com.rbxhubpro.rohumex.util.OneTime
import com.rbxhubpro.rohumex.util.log
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

    private val coroutine           = CoroutineScope(Dispatchers.Default)
    private val onceExit            = OneTime()
    private val onceSystemBarHeight = OneTime()

    lateinit var binding : ActivityMainBinding

    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    // ── Разрешение на пуши (правка 6.1) ──────────────────────────────────────
    // Launcher ОБЯЗАН регистрироваться до старта activity — поэтому property,
    // а не вызов внутри метода (registerForActivityResult после onStart кидает
    // IllegalStateException). Колбэк одноразовый: выставляется диалогом опт-ина
    // перед launch() и сбрасывается после срабатывания.
    private var onPushPermissionResult: ((Boolean) -> Unit)? = null
    private val pushPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onPushPermissionResult?.invoke(granted)
        onPushPermissionResult = null
    }

    // ── Ad система ────────────────────────────────────────────────────────────
    // Створюємо один раз — LibGDX звертається через game.activity.adManager
    lateinit var adManager     : AdManager
    lateinit var appOpenManager: AppOpenManager

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()

        Biz.onWebReward = { coins -> showCoinsDialog(coins) }  // UI свій у апки
        // Пришли с лендинга за разрешением (rohumexapp://optin): системный запрос
        // здесь, а награду отдаём токеном обратно в таб — обещали её на лендинге.
        Biz.onWebOptIn = { act ->
            PushOptIn.requestFromWeb(
                act,
                requestPermission = { onResult ->
                    onPushPermissionResult = onResult
                    pushPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                },
                reopenTab = { token ->
                    // gate_open шлём руками: URL здесь строим сами (нужно дописать
                    // &granted=), а BrowserUtil.openAd этого не умеет. Без события
                    // плейсмент появился бы в доходе, но не в воронке показов.
                    Events.gateOpen("optin_return")
                    val base = Backend.gateUrl("optin_return")
                    if (base != null) {
                        val url = if (token != null) "$base&granted=$token" else base
                        BrowserUtil.open(act, url)
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

    // правка 7: приложение уже живо (singleTask) — диплинк приходит сюда
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Biz.onActivityIntent(this, intent)   // живий процес (singleTask)
    }

    /** Очередь старта (LoaderScreen) просит системный запрос через активити:
     *  launcher обязан быть зарегистрирован до onStart, поэтому живёт здесь. */
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
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

    fun showInput(
        hint    : String = "",
        onResult: (String) -> Unit,
    ) {
        runOnUiThread {
            val editText = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                textSize  = 32f
                setTextColor(android.graphics.Color.WHITE)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                this.hint = "0"
                setHintTextColor(0xFF5C6070.toInt())
            }

            // Обгортка з відступами
            val container = FrameLayout(this).apply {
                val padding = (24 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, 0)
                addView(editText)
            }

            AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle(hint)
                .setView(container)
                .setPositiveButton("OK") { _, _ ->
                    val text = editText.text.toString()
                    if (text.isNotEmpty()) onResult(text)
                }
                .setNegativeButton("Cancel", null)
                .show()
                .also { dialog ->
                    editText.requestFocus()
                    dialog.window?.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                    )
                }
        }
    }

    fun shareMeme(meme: APanelMemesForFun.Meme) {
        runOnUiThread {
            val text = "${meme.title}\n\n${meme.text}"

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }

            val chooser = Intent.createChooser(intent, "Share meme")
            startActivity(chooser)
        }
    }

    fun showCopiedMemeToast() {
        runOnUiThread {
            Toast.makeText(this, "Copy Meme", Toast.LENGTH_SHORT).show()
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
            val url = "https://vadopruk.github.io/RBX-HUB-PRO/"
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

    // Общий тост для GDX-слоя: нехватка монет, лимиты и т.п.
    // runOnUiThread обязателен — зовётся из render-потока LibGDX.
    fun showToast(text: String) {
        runOnUiThread {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

    // Диалог «+N coins» — та же тёмная система, что showInput: сумма крупно,
    // одна кнопка OK. Показывается только на положительный разбор токена.
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

            AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Reward claimed!")
                .setView(container)
                .setPositiveButton("OK", null)
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
        if (!isConnected()) { runOnUiThread { onComplete(false) }; return }

        Biz.startSession(this)   // app_open + FCM-токен, раз на процес

        if (App.adPref.loadUserType() == null) {
            UserDetector.detectViaReferrer(this) { userType, rawReferrer ->
                AdConfig.userType = userType
                App.adPref.saveUserType(userType)
                fetchOurConfig(rawReferrer, onComplete)
            }
        } else {
            fetchOurConfig(null, onComplete)
        }
    }

    @SuppressLint("InlinedApi")
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

    // ЛЕГАСИ-ФОЛБЭК (правка 1): вызывается только когда наш сервер недоступен.
    // Не удалять до полного переезда парка — это страховка раскатки.
    @SuppressLint("InlinedApi")
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