package com.selftest.mindora

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.selftest.mindora.adsmodule.AdConfig
import com.selftest.mindora.adsmodule.AdManager
import com.selftest.mindora.adsmodule.AdSizeManager
import com.selftest.mindora.adsmodule.AppOpenManager
import com.selftest.mindora.adsmodule.RemoteConfigModel
import com.selftest.mindora.adsmodule.UserDetector
import com.selftest.mindora.config.AppConfig
import com.selftest.mindora.databinding.ActivityMainBinding
import com.selftest.mindora.game.utils.runGDX
import com.selftest.mindora.services.tiktok.TikTokManager
import com.selftest.mindora.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        var statusBarHeight = 0
        var navBarHeight    = 0
    }

    private val coroutine  = CoroutineScope(Dispatchers.Default)
    private val onceExit   = AtomicBoolean(true)

    private val onceSystemBarHeight = AtomicBoolean(true)

    private lateinit var binding : ActivityMainBinding

    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    // ------------------------------------------------------------------------
    // Ad система
    // ------------------------------------------------------------------------
    // Створюємо один раз — LibGDX звертається через game.activity.adManager
    lateinit var adManager     : AdManager
    lateinit var appOpenManager: AppOpenManager

    // ------------------------------------------------------------------------
    // AppConfig
    // ------------------------------------------------------------------------
    var appConfig: AppConfig = AppConfig()
        private set

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        requestNotificationPermission()
        initialize()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            if (onceSystemBarHeight.getAndSet(false)) {
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

    override fun exit() {
        if (onceExit.getAndSet(false)) {
            log("exit")
            finish()
        }
    }

    private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // AppOpenManager — слідкує за lifecycle сам (показує рекламу при поверненні з фону)
        appOpenManager = AppOpenManager(application)

        // AdManager — використовується для Banner / Native / Interstitial
        // Створюємо після того, як конфіг буде завантажений в initAds()
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
                setTextColor(Color.WHITE)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                hint = "0"
                setHintTextColor(0xFF5C6070.toInt())
            }
            val container = FrameLayout(this).apply {
                val p = (24 * resources.displayMetrics.density).toInt()
                setPadding(p, p, p, 0)
                addView(editText)
            }
            androidx.appcompat.app.AlertDialog
                .Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setView(container)
                .setPositiveButton("OK") { _, _ ->
                    val value = editText.text.toString().toIntOrNull() ?: 0
                    val clamped = value.coerceIn(0, 1_000_000)
                    runGDX { onResult(clamped) }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    runGDX { onResult(0) }
                }
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
            val url = "https://doc-hosting.flycricket.io/ff-miner-skin-tool-privacy-policy/fc166b36-262e-478f-92b3-097222b8385f/privacy"
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

    /**
     * Короткий системний тост. Зветься з GDX-потоку, тому runOnUiThread
     * обов'язковий — Toast.show() з чужого потоку кидає RuntimeException.
     */
    fun showToast(text: String) {
        runOnUiThread {
            android.widget.Toast.makeText(this, text, android.widget.Toast.LENGTH_SHORT).show()
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

        // ── Крок 1: Визначаємо тип юзера ─────────────────────────────────────
        // Тільки якщо ще не визначено (щоб Retry не перевизначав)
        if (App.adPref.loadUserType() == null) {
            UserDetector.detectViaReferrer(this) { userType ->
                AdConfig.userType = userType
                App.adPref.saveUserType(userType)
                fetchRemoteConfig(onComplete)
            }
        } else {
            // Тип юзера вже збережений — одразу йдемо до конфігу
            fetchRemoteConfig(onComplete)
        }

        fetchRemoteAppConfig()
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
                    val json = remoteConfig.getString("ad_config")
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

    private fun fetchRemoteAppConfig() {
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
                    val model = Gson().fromJson(json, AppConfig::class.java)

                    appConfig = model
                    log("MODEL FRC AppConfig = $model")
                }.onFailure {
                    log("Parse failed FRC: $it")
                }
            } else {
                log("Fetch failed FRC: ${task.exception}")
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

    // ------------------------------------------------------------------------
    // Push permission (Android 13+)
    // ------------------------------------------------------------------------
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        log("POST_NOTIFICATIONS granted = $granted")
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

}