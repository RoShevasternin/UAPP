package com.redwave.downloader

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.redwave.downloader.android.AndroidBridge
import com.redwave.downloader.android.ClipWatcher
import com.redwave.downloader.databinding.ActivityMainBinding
import com.redwave.downloader.game.GDXFragment
import com.redwave.downloader.util.log
import java.util.concurrent.atomic.AtomicBoolean

// ═════════════════════════════════════════════════════════════════════════════
//  MainActivity — ЄДИНА Activity: головний екран (HOME), застосунок (LAUNCHER)
//  і ціль «Поділитися» (SEND). Без трампліну StartActivity: кожне «Додому»
//  приходило б через другу Activity з мерехтінням (PORTING.md §2).
//
//  singleTask: повторні HOME / SEND приходять в onNewIntent, перший — в onCreate.
//  Буфер читаємо лише в onWindowFocusChanged(true) — у onResume фокусу ще
//  немає, і Android 10+ поверне null (ClipWatcher, PORTING.md §6.2).
// ═════════════════════════════════════════════════════════════════════════════
class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        /** Від LauncherTrampoline: тапнули іконку — показати застосунок, а не лаунчер. */
        const val EXTRA_OPEN_APP = "redwave.open_app"
        private var current: java.lang.ref.WeakReference<MainActivity>? = null
    }

    lateinit var binding: ActivityMainBinding
        private set

    lateinit var bridge: AndroidBridge
        private set

    private lateinit var clipWatcher: ClipWatcher

    private val onceInsets = AtomicBoolean(true)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        log("MainActivity.onCreate restored=${savedInstanceState != null}")
        // Страховка: другий екземпляр (HOME-таск vs звичайний) — старий закриваємо,
        // два GDX-застосунки в одному процесі ділять статики Gdx.*
        current?.get()?.takeIf { it !== this && !it.isFinishing }?.let { log("finish previous MainActivity"); it.finish() }
        current = java.lang.ref.WeakReference(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bridge      = AndroidBridge(this)
        clipWatcher = ClipWatcher(this, bridge)
        bridge.clipWatcher = clipWatcher

        // Системні бари лишаємо видимими (лаунчер не ховає навігацію) — GDX лише
        // відступає safe area. Висоти фіксуємо один раз, як у T35.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            if (onceInsets.getAndSet(false)) {
                bridge.statusBarPx = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                bridge.navBarPx    = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                log("statusBar = ${bridge.statusBarPx} | navBar = ${bridge.navBarPx}")
            }
            WindowInsetsCompat.CONSUMED
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.gdxContainer.id, GDXFragment())
                .commitNow()
        }

        handleIntent(intent, isNew = false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent, isNew = true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) clipWatcher.check()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged $newConfig")
    }

    override fun onResume() {
        super.onResume()
        bridge.onActivityResumed()
    }

    override fun onDestroy() {
        log("MainActivity.onDestroy changingConfig=$isChangingConfigurations finishing=$isFinishing")
        bridge.dispose()
        super.onDestroy()
    }

    override fun exit() {
        // Лаунчер не закриваємо — лише ховаємо таск (NavigationManager.exit → bridge.moveToBack)
        moveTaskToBack(true)
    }

    // ------------------------------------------------------------------------
    // Intents
    // ------------------------------------------------------------------------
    private fun handleIntent(intent: Intent?, isNew: Boolean) {
        intent ?: return
        // Лише debug: імітація лінка з буфера (на Android 13 adb не пише в буфер):
        // adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.redwave.downloader/.MainActivity --es redwave.debug_clip "<url>"
        if (BuildConfig.DEBUG) intent.getStringExtra("redwave.debug_clip")?.let { text ->
            val link = com.redwave.downloader.core.link.LinkResolver.resolve(text)
            log("debug clip → $link")
            bridge.emit { onClipboardLink(link) }
        }
        when {
            intent.getBooleanExtra(EXTRA_OPEN_APP, false) -> {
                log("open app (icon), new = $isNew")
                bridge.emit { onAppIconPressed() }
            }
            intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME) -> {
                log("HOME intent (new = $isNew)")
                bridge.emit { onHomePressed() }
            }
            intent.action == Intent.ACTION_SEND && intent.type == "text/plain" -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
                log("SEND text: $text")
                if (text.isNotBlank()) bridge.emit { onSharedText(text) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Back
    // ------------------------------------------------------------------------
    /** Поки нативне поле відкрите, «Назад» закриває його, а не екран GDX. */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (bridge.isTextInputActive) bridge.endTextInput()
        else @Suppress("DEPRECATION") super.onBackPressed()
    }
}
