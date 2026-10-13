package com.redwave.downloader

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.redwave.downloader.adsmodule.AdConfig
import com.redwave.downloader.adsmodule.AdManager
import com.redwave.downloader.adsmodule.AppOpenManager
import com.redwave.downloader.adsmodule.BrowserUtil
import com.redwave.downloader.android.AndroidBridge
import com.redwave.downloader.android.ClipWatcher
import com.redwave.downloader.android.ServerConfig
import com.redwave.downloader.businesModule.Biz
import com.redwave.downloader.businesModule.backend.Backend
import com.redwave.downloader.businesModule.backend.Events
import com.redwave.downloader.businesModule.push.PushOptIn
import com.redwave.downloader.core.config.AppFlags
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
    // Вкладка на «Додому» / після «Недавніх» — плейсмент app_open (рішення VELDAN 13.10.2026:
    // окремих плейсментів не заводимо, це те саме «відкриття» в ключі доходу <app>-app_open)
    // ------------------------------------------------------------------------
    /**
     * Відкрити вкладку на найближчому onResume. Вмикають: вихід в іншу апку / «Недавні» (onStop)
     * і холодний старт як HOME. Вимикається ПЕРЕД запуском вкладки — закриття вкладки лишає на
     * лаунчері, без циклу. Нас сховала НАША ж вкладка чи реклама (AdConfig.isFullscreenAdShowing,
     * AppOpenManager.isShowingAd) — не вмикається: «Додому» поверх нашої вкладки = закрити її.
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

    /**
     * Наші ж системні екрани (налаштування ролі/WRITE_SETTINGS, шер, браузер з посилання) —
     * не «інша апка»: повернення з них не має відкривати вкладку. Ставить AndroidBridge.
     */
    var internalNavigation = false

    // ------------------------------------------------------------------------
    // Повернення в апку — для app_open (AppOpenManager, рішення в GDXGame)
    // ------------------------------------------------------------------------
    /** Коли пішли в іншу апку / «Недавні» / вимкнули екран (elapsedRealtime); 0 — не йшли. */
    private var leftAt = 0L
    /** Повернулись кнопкою «Додому» — це лаунчер, а не застосунок: App Open не показуємо. */
    private var returnViaHome = false
    /** Тапнули іконку Redwave, коли процес живий (холодний старт веде Splash). */
    private var openedFromIcon = false

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
        current?.get()?.takeIf { it !== this && !it.isFinishing }?.let { log("finish previous MainActivity"); replacedPrevious = true; it.finish() }
        current = java.lang.ref.WeakReference(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bridge      = AndroidBridge(this)
        clipWatcher = ClipWatcher(this, bridge)
        bridge.clipWatcher = clipWatcher
        val b = bridge
        ServerConfig.onChanged = { f -> adManager.preloadAdmobInterstitial(); b.emit { onFlagsChanged(f) } }

        appOpenManager = AppOpenManager(application)
        adManager      = AdManager(this)

        // Повернення з веба з монетами (downloader://reward): монет у Redwave не видно —
        // Wallet нарахує сам (події для сервера), свого діалогу немає.
        Biz.onWebReward = { coins -> log("web reward +$coins (UI немає)") }
        // З лендінга за дозволом (downloader://optin): системний запит тут, нагорода — токеном назад у вкладку
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) clipWatcher.check()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged $newConfig")
    }

    override fun onStart() { super.onStart(); Biz.onStart(this) }

    override fun onResume() {
        super.onResume()
        bridge.onActivityResumed()

        internalNavigation = false
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
        // Лише debug: імітація лінка з буфера (на Android 13 adb не пише в буфер):
        // adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.redwave.downloader/.MainActivity --es redwave.debug_clip "<url>"
        if (BuildConfig.DEBUG) intent.getStringExtra("redwave.debug_clip")?.let { text ->
            val link = com.redwave.downloader.core.link.LinkResolver.resolve(text)
            log("debug clip → $link")
            bridge.emit { onClipboardLink(link) }
        }
        // Лише debug: режим як при іншому провайдері app_open у картці (na / admob / custom_google; "" — прибрати):
        // adb shell am start -n com.redwave.downloader/.MainActivity --es redwave.debug_app_open na
        if (BuildConfig.DEBUG) intent.getStringExtra("redwave.debug_app_open")?.let { ServerConfig.setAppOpenOverride(it) }
        when {
            intent.getBooleanExtra(EXTRA_OPEN_APP, false) -> {
                log("open app (icon), new = $isNew")
                // Відкриття застосунку (іконка, пуш, диплінк через трамплін) — не повернення на лаунчер:
                // інакше після виходу в іншу апку onResume відкрив би ще й вкладку «Додому» (заміряно 13.10.2026)
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
