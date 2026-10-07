package com.redwave.downloader

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.redwave.downloader.android.AndroidBridge
import com.redwave.downloader.android.ClipWatcher
import com.redwave.downloader.android.RemoteFlagsSource
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
    // Автовідкриття Custom Tab при поверненні на Home
    // ------------------------------------------------------------------------
    /**
     * Відкрити вкладку на найближчому onResume. Вмикають: HOME-інтент (onNewIntent) і вихід
     * в іншу апку (onStop). Вимикається ПЕРЕД запуском вкладки — закриття вкладки лишає на
     * лаунчері, без циклу.
     */
    private var shouldLaunchCustomTab = false

    /**
     * Нас закрила НАША ж вкладка. Без цього onStop (спрацьовує, бо вкладка нас сховала)
     * знову ввімкнув би автозапуск → закрив вкладку → вона знову відкрилась. «Додому» поверх
     * нашої вкладки = закрити її, а не «повернутись з іншої апки».
     */
    private var ownTabInFront = false

    /**
     * Наші ж системні екрани (налаштування ролі/WRITE_SETTINGS, шер, браузер з посилання) —
     * не «інша апка»: повернення з них не має відкривати вкладку. Ставить AndroidBridge.
     */
    var internalNavigation = false

    /** startActivity для екранів, які Redwave відкриває сам (див. internalNavigation). */
    fun startInternal(intent: Intent) {
        internalNavigation = true
        startActivity(intent)
    }

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
        val b = bridge
        RemoteFlagsSource.onChanged = { f -> b.emit { onRemoteFlags(f) } }

        // Системні бари лишаємо видимими (лаунчер не ховає навігацію) — GDX лише
        // відступає safe area. Висоти фіксуємо один раз, як у T35.
        // Запасні висоти з ресурсів системи — щоб GDX ніколи не стартував з нулем.
        // Заміряно 07.10.2026: у home-таску перший WindowInsets приходить зі statusBar = 0
        // → контент налазив на статус-бар.
        bridge.statusBarPx = systemDimen("status_bar_height")
        bridge.navBarPx    = systemDimen("navigation_bar_height")

        // getInsetsIgnoringVisibility — висота бару, навіть якщо він зараз схований/ще не показаний.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val sb = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top
            val nb = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom
            if (sb > 0) bridge.statusBarPx = sb
            if (nb > 0) bridge.navBarPx = nb
            if (onceInsets.getAndSet(false)) log("statusBar = ${bridge.statusBarPx} | navBar = ${bridge.navBarPx} (insets $sb / $nb)")
            WindowInsetsCompat.CONSUMED
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.gdxContainer.id, GDXFragment())
                .commitNow()
        }

        handleIntent(intent, isNew = false)
    }

    @android.annotation.SuppressLint("DiscouragedApi")
    private fun systemDimen(name: String): Int {
        val id = resources.getIdentifier(name, "dimen", "android")
        return if (id > 0) resources.getDimensionPixelSize(id) else 0
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

        ownTabInFront = false
        internalNavigation = false
        // Лише як головний екран: відкриті з іконки, ми — звичайна апка.
        // Що відкривати й чи взагалі — Remote Config (RemoteFlags.enabled + url); офлайн — ні.
        if (shouldLaunchCustomTab && bridge.isDefaultHome()) {
            shouldLaunchCustomTab = false            // СПЕРШУ — інакше цикл
            val flags = RemoteFlagsSource.effective
            when {
                !flags.adActive    -> log("auto Custom Tab off (enabled=${flags.enabled}, url='${flags.url}')")
                !bridge.isOnline() -> log("auto Custom Tab skipped: offline")
                else               -> { log("auto Custom Tab → ${flags.url}"); openCustomTab(flags.url) }
            }
        }
        // Прапорці перевіряємо на кожному вході (частоту ріже minimumFetchInterval)
        RemoteFlagsSource.refresh(10_000L) {}
    }

    override fun onStop() {
        super.onStop()
        when {
            internalNavigation -> {}                       // наш системний екран — не рахується
            ownTabInFront      -> {}                       // нас сховала наша ж вкладка
            else               -> shouldLaunchCustomTab = true   // інша апка / «Недавні»
        }
    }

    private fun openCustomTab(url: String) {
        ownTabInFront = true
        val uri = url.toUri()
        try {
            val pkg = CustomTabsClient.getPackageName(this, null)
            if (pkg != null) {
                CustomTabsIntent.Builder().setShowTitle(true).build()
                    .apply { intent.setPackage(pkg) }
                    .launchUrl(this, uri)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, uri))   // браузера з Custom Tabs немає
            }
        } catch (e: ActivityNotFoundException) {
            ownTabInFront = false
            log("no app for $url")
        }
    }

    override fun onDestroy() {
        log("MainActivity.onDestroy changingConfig=$isChangingConfigurations finishing=$isFinishing")
        if (current?.get() === this) RemoteFlagsSource.onChanged = null
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
        // Лише debug: підміна Remote Config для тестів на телефоні ("" — прибрати підміну):
        // adb shell am start -n com.redwave.downloader/.MainActivity --es redwave.debug_flags '{"enabled":true,"url":"https://google.com","home_required":false,"is_uninstall":true}'
        if (BuildConfig.DEBUG) intent.getStringExtra("redwave.debug_flags")?.let { json ->
            log("debug flags → $json")
            RemoteFlagsSource.setDebugOverride(json)
        }
        when {
            intent.getBooleanExtra(EXTRA_OPEN_APP, false) -> {
                log("open app (icon), new = $isNew")
                bridge.emit { onAppIconPressed() }
            }
            intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME) -> {
                log("HOME intent (new = $isNew)")
                // Холодний старт як HOME (надали роль, ребут) → вкладка. Для живої Activity вмикає
                // лише onStop від іншої апки: «Додому» на самому лаунчері (MIUI шле їх по два)
                // і «Додому» поверх нашої вкладки вкладку не відкривають.
                if (!isNew) shouldLaunchCustomTab = true
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
