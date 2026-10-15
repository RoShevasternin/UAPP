package com.driftglass.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.driftglass.home.adsmodule.AdConfig
import com.driftglass.home.adsmodule.AdManager
import com.driftglass.home.adsmodule.AppOpenManager
import com.driftglass.home.adsmodule.BrowserUtil
import com.driftglass.home.android.AndroidBridge
import com.driftglass.home.android.ServerConfig
import com.driftglass.home.android.StoreDemo
import com.driftglass.home.businesModule.Biz
import com.driftglass.home.businesModule.backend.Backend
import com.driftglass.home.businesModule.backend.Events
import com.driftglass.home.businesModule.push.PushOptIn
import com.driftglass.home.core.config.AppFlags
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
//  Бізнес-логіка UAPP — як у Redwave / Framefall (рішення VELDAN 15.10.2026): черга старту,
//  app_open / front / back / interstitial (adsmodule), вкладка на «Додому» і після «Недавніх»
//  (pl=app_open, лише режим «наша реклама» з картки сервера), диплінк home:// і тап по пушу.
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
    // Реклама (adsmodule, як в UAPP): app_open, front / back / interstitial
    // ------------------------------------------------------------------------
    lateinit var appOpenManager: AppOpenManager
        private set
    lateinit var adManager: AdManager
        private set

    // ── Дозвіл на пуші (businesModule, правка 6.1) ───────────────────────────
    // Launcher ОБОВ'ЯЗКОВО реєструється до onStart — тому property, а не виклик у методі.
    private var onPushPermissionResult: ((Boolean) -> Unit)? = null
    private val pushPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        internalNavigation = false
        onPushPermissionResult?.invoke(granted)
        onPushPermissionResult = null
    }

    /** Черга старту (опт-ін) і повернення з лендінга просять системний запит через активіті. */
    fun requestPushPermission(onResult: (Boolean) -> Unit) {
        if (android.os.Build.VERSION.SDK_INT < 33) { onResult(true); return }
        onPushPermissionResult = onResult
        internalNavigation = true            // системний діалог — не «інша апка»
        pushPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    // ------------------------------------------------------------------------
    // Вкладка на «Додому» / після «Недавніх» — плейсмент app_open (як Redwave, рішення VELDAN 13.10.2026)
    // ------------------------------------------------------------------------
    /**
     * Відкрити вкладку на найближчому onResume. Вмикають: вихід в іншу апку / «Недавні» / вимкнений
     * екран (onStop) і холодний старт як HOME. Вимикається ПЕРЕД запуском вкладки — закриття вкладки
     * лишає на лаунчері, без циклу.
     */
    private var shouldLaunchCustomTab = false

    /**
     * Нас сховала НАША вкладка / реклама — фіксуємо в onStop, а не вгадуємо в onResume: стартова
     * вкладка відкривається між onStart і onResume (одразу після системного дозволу на пуші), і
     * onResume, скинувши AdConfig.isFullscreenAdShowing, зарахував би її закриття як вихід → друга вкладка.
     */
    private var stoppedByOwnAd = false

    /** Холодний старт замінив живу MainActivity (Android перезапускає її, коли надали роль HOME) — не «Додому». */
    private var replacedPrevious = false

    /** Наші ж системні екрани (діалог ролі, App info, видалення, дозвіл на пуші) — не «інша апка». */
    var internalNavigation = false

    // ------------------------------------------------------------------------
    // Повернення в апку — для app_open (AppOpenManager, рішення в GDXGame)
    // ------------------------------------------------------------------------
    /** Коли пішли в іншу апку / «Недавні» / вимкнули екран (elapsedRealtime); 0 — не йшли. */
    private var leftAt = 0L
    /** Повернулись кнопкою «Додому» — це лаунчер, а не застосунок: app_open не показуємо. */
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
        current?.get()?.takeIf { it !== this && !it.isFinishing }?.let { log("finish previous MainActivity"); replacedPrevious = true; it.finish() }
        current = java.lang.ref.WeakReference(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bridge = AndroidBridge(this)
        val b = bridge
        ServerConfig.onChanged = { f -> adManager.preloadAdmobInterstitial(); b.emit { onFlagsChanged(f) } }

        appOpenManager = AppOpenManager(application)
        adManager      = AdManager(this)

        // Повернення з веба з монетами (home://reward): монет у Driftglass немає —
        // Wallet нарахує сам (події для сервера), свого діалогу немає.
        Biz.onWebReward = { coins -> log("web reward +$coins (UI немає)") }
        // З лендінга за дозволом (home://optin): системний запит тут, нагорода — токеном назад у вкладку
        Biz.onWebOptIn = { act ->
            PushOptIn.requestFromWeb(
                act,
                requestPermission = { onResult -> requestPushPermission(onResult) },
                reopenTab = { token ->
                    // gate_open руками: URL будуємо самі (&granted=), BrowserUtil.openAd так не вміє
                    Events.gateOpen("optin_return")
                    val base = Backend.gateUrl("optin_return")
                    if (base != null) BrowserUtil.open(act, if (token != null) "$base&granted=$token" else base)
                },
            )
        }

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

        Biz.onActivityIntent(this, intent)   // диплінк з лендінга + тап по пушу: холодний старт
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
        Biz.onActivityIntent(this, intent)   // живий процес (singleTask)
        handleIntent(intent, isNew = true)
    }

    override fun onStart() { super.onStart(); Biz.onStart(this) }

    override fun onResume() {
        super.onResume()

        internalNavigation = false
        bridge.emit { onAppResumed() }
        // Повернулись з НАШОЇ вкладки / реклами — не «повернення в апку» і не привід для нової вкладки
        val fromOwnAd = stoppedByOwnAd
        if (fromOwnAd) { stoppedByOwnAd = false; AdConfig.isFullscreenAdShowing = false; shouldLaunchCustomTab = false }

        // app_open: повернення в застосунок або тап по іконці (GDXGame вирішує, чи ми в застосунку)
        val away = if (leftAt > 0) SystemClock.elapsedRealtime() - leftAt else -1L
        leftAt = 0L
        if (!fromOwnAd && !returnViaHome && (away >= 0 || openedFromIcon)) {
            val fromIcon = openedFromIcon
            bridge.emit { onAppForeground(away, fromIcon) }
        }
        returnViaHome = false
        openedFromIcon = false

        // Лише як головний екран: відкриті з іконки, ми — звичайна апка.
        // Чи відкривати — картка сервера (app_open = custom*, AppFlags.homeTabs), офлайн — ні.
        // Інтент щойно сам відкрив вкладку (тап по пушу з gate_pl, повернення з лендінга) — другу не відкриваємо
        if (shouldLaunchCustomTab && AdConfig.isFullscreenAdShowing) { shouldLaunchCustomTab = false; log("home tab skipped: intent already opened a tab") }
        if (shouldLaunchCustomTab && bridge.isDefaultHome()) {
            shouldLaunchCustomTab = false            // СПЕРШУ — інакше цикл
            val pl = AppFlags.PL_HOME_TAB
            val flags = ServerConfig.flags
            when {
                !flags.homeTabs    -> log("home tab off [$pl] (${ServerConfig.debugLine().replace('\n', ' ')})")
                !bridge.isOnline() -> log("home tab skipped [$pl]: offline")
                else               -> { log("home tab → pl=$pl"); BrowserUtil.openAd(this, ServerConfig.fallbackTabUrl(), pl) }
            }
        }
        // Конфіг сервера перевіряємо на кожному вході (частоту ріже ServerConfig.REFRESH_MS)
        ServerConfig.refresh(force = false)
    }

    override fun onStop() {
        super.onStop()
        Biz.onStop(this)   // плануємо локальні пуші за правилами картки
        // Прапорці стосуються наступного повернення. Зайвий HOME на живій Activity (MIUI шле по два)
        // без onResume лишив би returnViaHome = true і з'їв би наступне справжнє повернення.
        returnViaHome = false
        openedFromIcon = false
        when {
            internalNavigation                -> {}        // наш системний екран — не рахується
            AdConfig.isFullscreenAdShowing ||
            appOpenManager.isShowingAd        -> stoppedByOwnAd = true   // наша вкладка / інтерстішал / App Open
            else                              -> {         // інша апка / «Недавні» / вимкнений екран
                shouldLaunchCustomTab = true
                leftAt = SystemClock.elapsedRealtime()
            }
        }
    }

    override fun onDestroy() {
        log("MainActivity.onDestroy changingConfig=$isChangingConfigurations finishing=$isFinishing")
        runCatching { unregisterReceiver(unlockReceiver) }
        if (current?.get() === this) ServerConfig.onChanged = null
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
        // Лише debug: режим як при іншому провайдері app_open у картці (na / admob / custom_google; "" — прибрати):
        // adb shell am start -n com.driftglass.home/.MainActivity --es driftglass.debug_app_open na
        if (BuildConfig.DEBUG) intent.getStringExtra("driftglass.debug_app_open")?.let { ServerConfig.setAppOpenOverride(it) }
        // Лише debug: чистий режим для скрінів Google Play (вигадані апки, годинник 9:41):
        // adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.store_demo true
        if (BuildConfig.DEBUG && intent.hasExtra("driftglass.store_demo")) {
            StoreDemo.set(this, intent.getBooleanExtra("driftglass.store_demo", false))
            bridge.appsChangedForDemo()
        }
        when {
            intent.getBooleanExtra(EXTRA_OPEN_APP, false) -> {
                log("open app (icon), new = $isNew")
                // Відкриття застосунку (іконка, пуш, диплінк через трамплін) — не повернення на лаунчер:
                // інакше після виходу в іншу апку onResume відкрив би ще й вкладку «Додому» (Redwave, 13.10.2026)
                shouldLaunchCustomTab = false
                if (isNew) openedFromIcon = true
                bridge.emit { onAppIconPressed() }
            }
            intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME) -> {
                log("HOME intent (new = $isNew)")
                // Холодний старт як HOME (ребут, MIUI вбив процес очищенням «Недавніх») → вкладка. Для живої Activity
                // вмикає лише onStop від іншої апки: «Додому» на самому лаунчері (MIUI шле їх по два)
                // і «Додому» поверх нашої вкладки вкладку не відкривають.
                if (!isNew && !replacedPrevious) shouldLaunchCustomTab = true
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
