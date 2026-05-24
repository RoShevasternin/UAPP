package com.rbuxrds.counterds

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.InputType
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.gson.Gson
import com.rbuxrds.counterds.adsmodule.AdConfig
import com.rbuxrds.counterds.adsmodule.AdManager
import com.rbuxrds.counterds.adsmodule.AppOpenManager
import com.rbuxrds.counterds.adsmodule.RemoteConfigModel
import com.rbuxrds.counterds.adsmodule.UserDetector
import com.rbuxrds.counterds.databinding.ActivityMainBinding
import com.rbuxrds.counterds.game.actors.panel.APanelMemesForFun
import com.rbuxrds.counterds.util.OneTime
import com.rbuxrds.counterds.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        var statusBarHeight = 0
        var navBarHeight    = 0
        var bannerHeight    = 0
    }

    private val coroutine           = CoroutineScope(Dispatchers.Default)
    private val onceExit            = OneTime()
    private val onceSystemBarHeight = OneTime()

    lateinit var binding : ActivityMainBinding

    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    // ── Ad система ────────────────────────────────────────────────────────────
    // Створюємо один раз — LibGDX звертається через game.activity.adManager
    lateinit var adManager     : AdManager
    lateinit var appOpenManager: AppOpenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()

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

    override fun exit() {
        onceExit.use {
            log("exit")
            coroutine.launch(Dispatchers.Main) {
                finishAndRemoveTask()
                delay(100)
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
                textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
                this.hint = "0"
                setHintTextColor(0xFF5C6070.toInt())
            }

            // Обгортка з відступами
            val container = android.widget.FrameLayout(this).apply {
                val padding = (24 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, 0)
                addView(editText)
            }

            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
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
            val url = "https://doc-hosting.flycricket.io/rbux-counter-privacy-policy/9519f55e-e70e-4e3d-b19c-712a92f77a7c/privacy"
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
    }

    // ── Firebase Remote Config ────────────────────────────────────────────────

    /*private fun fetchRemoteConfig(onComplete: (success: Boolean) -> Unit) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0) // TODO: змінити на 3600 для production
                .build()
        )

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val json = remoteConfig.getString(RC_KEY)
                log("RAW JSON = '$json'")

                runCatching {
                    val model = Gson().fromJson(json, RemoteConfigModel::class.java)
                    AdConfig.remoteConfig = model
                    App.adPref.saveConfig(model)
                    log("model = $model")
                    log("Config applied. UserType=${AdConfig.userType}")
                }.onFailure {
                    log("Failed to parse remote config $it")
                }
                runOnUiThread { onComplete(true) }
            } else {
                log("Remote config fetch failed")
                // Немає інтернету або Firebase недоступний
                runOnUiThread { onComplete(false) }
            }
        }
    }*/

    private fun fetchRemoteConfig(onComplete: (success: Boolean) -> Unit) {
        // Простий HTTP запит замість Firebase
        Thread {
            runCatching {
                val url = java.net.URL("https://api.bebekoyunu.com.tr/app_001.json")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout    = 5000
                connection.requestMethod  = "GET"

                val json = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val model = Gson().fromJson(json, RemoteConfigModel::class.java)
                AdConfig.remoteConfig = model
                App.adPref.saveConfig(model)

                //log("model = ${Gson().newBuilder().setPrettyPrinting().create().toJson(model)}")
                log("RAW JSON = $json")
                log("Config applied. UserType=${AdConfig.userType}")

                runOnUiThread { onComplete(true) }
            }.onFailure {
                log("Failed to fetch config: $it")
                runOnUiThread { onComplete(false) }
            }
        }.start()
    }

    // ── Banner ────────────────────────────────────────────────────────────────
    // Викликається з LibGDX коли потрібно показати банер
    // container — FrameLayout з activity_main.xml

    fun showBanner(container: FrameLayout) {
        runOnUiThread {
            adManager.showBanner(container)

            container.viewTreeObserver.addOnGlobalLayoutListener {
                val height = container.height
                if (height > 0) bannerHeight = height
            }
        }
    }

    // ── Native ────────────────────────────────────────────────────────────────

    fun showNativeAt(screenY: Float) {
        runOnUiThread {
            adManager.showNative(binding.nativeContainer)

            binding.nativeContainer.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val nativeHeight = binding.nativeContainer.height

                        // Чекаємо поки реклама завантажиться і висота стане > 0
                        if (nativeHeight == 0) return

                        binding.nativeContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.nativeContainer.y = screenY - nativeHeight
                        log("showNativeAt: nativeHeight = $nativeHeight")
                    }
                }
            )
        }
    }

    // Сховати нативну рекламу
    fun hideNative() {
        runOnUiThread {
            binding.nativeContainer.visibility = android.view.View.GONE
            binding.nativeContainer.removeAllViews()
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

    // ── Connectivity ──────────────────────────────────────────────────────────

    fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}