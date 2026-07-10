package com.rbuxrds.counter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.rbuxrds.counter.databinding.ActivityMainBinding
import com.rbuxrds.counter.game.actors.panel.APanelMemesForFun
import com.rbuxrds.counter.util.OneTime
import com.rbuxrds.counter.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import androidx.core.net.toUri

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            onceSystemBarHeight.use {
                statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                navBarHeight    = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

                log("statusBarHeight = $statusBarHeight | navBarHeight = $navBarHeight")

                // hide Status or Nav bar (після встановлення їх розмірів)
                //windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                //windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

//    fun openEmail(to: String, subject: String = "") {
//        runOnUiThread {
//            val uri = "mailto:$to?subject=${Uri.encode(subject)}".toUri()
//
//            val intent = Intent(Intent.ACTION_SENDTO, uri)
//            startActivity(Intent.createChooser(intent, "Send email"))
//        }
//    }

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
            val url = "https://your-privacy-policy-url.com"
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

}