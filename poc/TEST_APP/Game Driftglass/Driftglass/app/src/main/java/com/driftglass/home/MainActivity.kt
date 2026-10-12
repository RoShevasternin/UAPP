package com.driftglass.home

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.driftglass.home.android.AndroidBridge
import com.driftglass.home.android.AdsManager
import com.driftglass.home.android.RemoteFlagsSource
import com.driftglass.home.android.StoreDemo
import com.driftglass.home.databinding.ActivityMainBinding
import com.driftglass.home.game.GDXFragment
import com.driftglass.home.util.log
import java.util.concurrent.atomic.AtomicBoolean

// ═════════════════════════════════════════════════════════════════════════════
//  MainActivity — ЄДИНА Activity: головний екран (HOME) і застосунок (через трамплін).
//
//  singleTask: повторні HOME приходять в onNewIntent, перший — в onCreate.
//  Розблокування (ACTION_USER_PRESENT) ловимо ресивером у рантаймі — для
//  Auto-shuffle «кожне розблокування»; маніфест-ресивер на нього не працює з API 26.
//
//  AD_MODE (AppFlags.adActive): Custom Tab на «Додому» з іншої апки, після «Недавніх»
//  (і «Очистити все») та після вимкненого екрана — логіка 1:1 з Redwave MainActivity.
// ═════════════════════════════════════════════════════════════════════════════
class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        /** Від LauncherTrampoline: тапнули іконку — показати застосунок, а не лаунчер. */
        const val EXTRA_OPEN_APP = "driftglass.open_app"
        private var current: java.lang.ref.WeakReference<MainActivity>? = null
    }

    lateinit var binding: ActivityMainBinding
        private set

    lateinit var bridge: AndroidBridge
        private set

    private val onceInsets = AtomicBoolean(true)

    // ------------------------------------------------------------------------
    // Автовідкриття Custom Tab при поверненні на Home (AD_MODE)
    // ------------------------------------------------------------------------
    /**
     * Відкрити вкладку на найближчому onResume. Вмикають: холодний старт як HOME і вихід
     * в іншу апку / «Недавні» / вимкнений екран (onStop). Вимикається ПЕРЕД запуском вкладки —
     * закриття вкладки лишає на лаунчері, без циклу.
     */
    private var shouldLaunchCustomTab = false

    /**
     * Нас закрила НАША ж вкладка. Без цього onStop (спрацьовує, бо вкладка нас сховала)
     * знову ввімкнув би автозапуск → закрив вкладку → вона знову відкрилась.
     */
    private var ownTabInFront = false

    /** Наші ж системні екрани (діалог ролі, App info, видалення) — не «інша апка». Ставить AndroidBridge. */
    var internalNavigation = false

    // ------------------------------------------------------------------------
    // Повернення в апку — для App Open (AdsManager / AdPolicy), як у Redwave
    // ------------------------------------------------------------------------
    /** Коли пішли в іншу апку / «Недавні» / вимкнули екран (elapsedRealtime); 0 — не йшли. */
    private var leftAt = 0L
    /** Повернулись кнопкою «Додому» — це лаунчер, а не застосунок: App Open не показуємо. */
    private var returnViaHome = false
    /** Тапнули іконку Driftglass, коли процес живий (холодний старт веде Splash). */
    private var openedFromIcon = false

    fun startInternal(intent: Intent) {
        internalNavigation = true
        startActivity(intent)
    }

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            log("ACTION_USER_PRESENT")
            bridge.emit { onUnlocked() }
        }
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

        bridge = AndroidBridge(this)
        val b = bridge
        RemoteFlagsSource.onChanged = { f -> AdsManager.onFlags(); b.emit { onRemoteFlags(f) } }

        // Системні бари видимі (лаунчер не ховає навігацію) — GDX лише відступає safe area.
        // Запасні висоти з ресурсів: у home-таску перший WindowInsets приходить зі statusBar = 0 (Redwave, 07.10.2026).
        bridge.statusBarPx = systemDimen("status_bar_height")
        bridge.navBarPx    = systemDimen("navigation_bar_height")
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val sb = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top
            val nb = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom
            if (sb > 0) bridge.statusBarPx = sb
            if (nb > 0) bridge.navBarPx = nb
            if (onceInsets.getAndSet(false)) log("statusBar = ${bridge.statusBarPx} | navBar = ${bridge.navBarPx} (insets $sb / $nb)")
            WindowInsetsCompat.CONSUMED
        }

        ContextCompat.registerReceiver(this, unlockReceiver, IntentFilter(Intent.ACTION_USER_PRESENT), ContextCompat.RECEIVER_NOT_EXPORTED)

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

    override fun onResume() {
        super.onResume()
        // Апку могли запустити офлайн (AdMob = false), а тепер інтернет є — прапорці ті самі, onChanged не прийде
        AdsManager.onFlags()
        ownTabInFront = false
        internalNavigation = false
        bridge.emit { onAppResumed() }
        // App Open: повернення (не довше години — вирішує AdPolicy) або тап по іконці
        val away = if (leftAt > 0) android.os.SystemClock.elapsedRealtime() - leftAt else -1L
        leftAt = 0L
        if (!returnViaHome && (away >= 0 || openedFromIcon)) {
            val fromIcon = openedFromIcon
            bridge.emit { onAppForeground(away, fromIcon) }
        }
        returnViaHome = false
        openedFromIcon = false
        // Лише як головний екран: відкриті з іконки, ми — звичайна апка.
        if (shouldLaunchCustomTab && bridge.isDefaultHome()) {
            shouldLaunchCustomTab = false            // СПЕРШУ — інакше цикл
            val flags = RemoteFlagsSource.effective
            when {
                !flags.adActive    -> log("auto Custom Tab off (enabled_url_ad=${flags.enabledUrlAd}, url='${flags.url}')")
                !bridge.isOnline() -> log("auto Custom Tab skipped: offline")
                else               -> { log("auto Custom Tab → ${flags.url}"); openCustomTab(flags.url) }
            }
        }
        // Прапорці перевіряємо на кожному вході (частоту ріже minimumFetchInterval)
        RemoteFlagsSource.refresh(10_000L) {}
    }

    override fun onStop() {
        super.onStop()
        // Прапорці стосуються наступного повернення (MIUI шле HOME по два — без onResume з'їв би наступне)
        returnViaHome = false
        openedFromIcon = false
        when {
            internalNavigation   -> {}                    // наш системний екран — не рахується
            ownTabInFront        -> {}                    // нас сховала наша ж вкладка
            AdsManager.isShowing -> {}                    // нас сховала наша ж реклама AdMob
            else                 -> {                     // інша апка / «Недавні» / вимкнений екран
                shouldLaunchCustomTab = true
                leftAt = android.os.SystemClock.elapsedRealtime()
            }
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
        runCatching { unregisterReceiver(unlockReceiver) }
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
        // Лише debug: підміна прапорців ("" — прибрати):
        // adb shell am start -n com.driftglass.home/.MainActivity --es driftglass.debug_flags '{"enabled_url_ad":true,"url":"https://google.com","home_required":true,"is_uninstall":false}'
        if (BuildConfig.DEBUG) intent.getStringExtra("driftglass.debug_flags")?.let { json ->
            log("debug flags → $json")
            RemoteFlagsSource.setDebugOverride(json)
        }
        // Лише debug: забути останній показ реклами й лічильник дій (не чекати 3 хв):
        // adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.debug_ads_reset true
        if (BuildConfig.DEBUG && intent.getBooleanExtra("driftglass.debug_ads_reset", false)) AdsManager.debugReset()
        // Лише debug: чистий режим для скрінів Google Play (вигадані апки, годинник 9:41), перезапуск апки:
        // adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.store_demo true
        if (BuildConfig.DEBUG && intent.hasExtra("driftglass.store_demo")) {
            StoreDemo.set(this, intent.getBooleanExtra("driftglass.store_demo", false))
            bridge.appsChangedForDemo()
        }
        when {
            intent.getBooleanExtra(EXTRA_OPEN_APP, false) -> {
                log("open app (icon), new = $isNew")
                if (isNew) openedFromIcon = true
                bridge.emit { onAppIconPressed() }
            }
            intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME) -> {
                log("HOME intent (new = $isNew)")
                // Холодний старт як HOME (надали роль, ребут, процес вбили «Очистити все») → вкладка.
                // Для живої Activity вмикає лише onStop від іншої апки: «Додому» на самому лаунчері
                // (MIUI шле їх по два) і «Додому» поверх нашої вкладки вкладку не відкривають.
                if (!isNew) shouldLaunchCustomTab = true
                if (isNew) returnViaHome = true
                bridge.emit { onHomePressed() }
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
