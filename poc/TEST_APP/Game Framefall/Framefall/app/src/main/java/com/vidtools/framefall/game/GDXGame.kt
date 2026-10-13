package com.vidtools.framefall.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.vidtools.framefall.core.link.ResolvedLink
import com.vidtools.framefall.core.link.chipLabel
import com.vidtools.framefall.core.link.sourceUrl
import com.vidtools.framefall.core.model.withEvent
import com.vidtools.framefall.game.controller.DiscoverController
import com.vidtools.framefall.game.controller.DownloadController
import com.vidtools.framefall.game.manager.NavigationManager
import com.vidtools.framefall.game.manager.SoundManager
import com.vidtools.framefall.game.manager.SpriteManager
import com.vidtools.framefall.game.manager.util.SoundUtil
import com.vidtools.framefall.game.manager.util.SpriteUtil
import com.vidtools.framefall.game.manager.util.VibroUtil
import com.vidtools.framefall.game.model.AppModel
import com.vidtools.framefall.game.platform.PlatformBridge
import com.vidtools.framefall.game.platform.PlatformEvents
import com.vidtools.framefall.game.screens.AppScreen
import com.vidtools.framefall.game.screens.LauncherScreen
import com.vidtools.framefall.game.screens.OnboardingScreen
import com.vidtools.framefall.game.screens.PlayerScreen
import com.vidtools.framefall.game.screens.SplashScreen
import com.vidtools.framefall.game.screens.StudioScreen
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.ShaderClock
import com.vidtools.framefall.game.utils.advanced.AdvancedGame
import com.vidtools.framefall.game.utils.disposeAll
import com.vidtools.framefall.game.utils.font.msdf.MsdfManager
import com.vidtools.framefall.game.utils.vfx.Blit
import com.vidtools.framefall.game.utils.vfx.VfxShaderCache
import com.vidtools.framefall.android.AndroidBridge
import com.vidtools.framefall.util.currentClassName
import com.vidtools.framefall.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

// ═════════════════════════════════════════════════════════════════════════════
//  GDXGame — корінь GDX-частини. Android знає лише через PlatformBridge,
//  події Android → GDX приходять сюди (PlatformEvents) вже в GL-потоці.
// ═════════════════════════════════════════════════════════════════════════════
class GDXGame(val bridge: PlatformBridge) : AdvancedGame(), PlatformEvents {

    // ------------------------------------------------------------------------
    // Assets
    // ------------------------------------------------------------------------
    val assetsAll by lazy { SpriteUtil.All() }

    // ------------------------------------------------------------------------
    // Audio (лише кліки; відео — ExoVideoPlayer)
    // ------------------------------------------------------------------------
    val soundUtil by lazy { SoundUtil() }
    val vibroUtil by lazy { VibroUtil() }

    // ------------------------------------------------------------------------
    // Managers
    // ------------------------------------------------------------------------
    lateinit var assetManager     : AssetManager      private set
    lateinit var navigationManager: NavigationManager private set
    lateinit var spriteManager    : SpriteManager     private set
    lateinit var soundManager     : SoundManager      private set
    lateinit var msdfManager      : MsdfManager       private set

    // ------------------------------------------------------------------------
    // Model / controllers
    // ------------------------------------------------------------------------
    val coroutine = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val model     = AppModel(coroutine)
    val downloads = DownloadController(model, bridge) { toast(it) }.apply { onVideoAdded = { afterVideoSaved() } }
    val discover  = DiscoverController(model, downloads)

    /** Асети й стан готові (SplashScreen). До цього події Android лише запам'ятовуємо. */
    var isReady = false
        private set

    // ── Події Android, що чекають на свій екран ──────────────────────────────
    /** Лінк з буфера для картки на лаунчері (ClipGate вже відфільтрував). */
    var clipLink: ResolvedLink? = null
    var clipVersion = 0
        private set
    /** «Поділитися → Framefall»: текст для поля на Save. */
    var sharedText: String? = null
    /** Що відкрити в PlayerScreen (NavigationManager.key — лише Int). */
    var playerVideoId: String? = null
    /** Інструмент для StudioScreen (Copy.Studio.TOOLS[].id). */
    var studioTool: String? = null
    var appsVersion = 0
        private set
    private var pendingHome = false
    /** Події з буфера, що прийшли до завантаження стану (див. onClipboardLink). */
    private val pendingEvents = mutableListOf<com.vidtools.framefall.core.model.AppEvent>()

    /** Текстури мініатюр на всю сесію. */
    val covers = com.vidtools.framefall.game.utils.CoverCache()

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun create() {
        ShaderProgram.pedantic = false
        // object-кеші живуть довше за GL-контекст: друга MainActivity / перестворення
        VfxShaderCache.reset()
        Blit.reset()

        assetManager      = AssetManager()
        spriteManager     = SpriteManager(assetManager)
        soundManager      = SoundManager(assetManager)
        msdfManager       = MsdfManager()
        navigationManager = NavigationManager(this)

        (bridge as? AndroidBridge)?.attach(this)

        navigationManager.navigate(SplashScreen::class.java.name)
    }

    override fun render() {
        ShaderClock.update()
        if (isReady) downloads.tick(Gdx.graphics.deltaTime)
        ScreenUtils.clear(backgroundColor)
        super.render()
    }

    /** Activity у фоні: GL-контекст може зникнути — відео звільняють OES-текстури (перестворять на resume). */
    override fun pause() {
        (bridge as? AndroidBridge)?.onGlPause()
        super.pause()
    }

    override fun resume() {
        super.resume()
        Blit.dispose()
        // Роль HOME могли забрати в налаштуваннях, поки ми були у фоні → назад на екран-вимогу.
        // Лише коли роль обов'язкова (картка сервера: app_open = custom*) — перевіряємо на кожному вході.
        // Інтернет міг зникнути / з'явитись → режим інший (офлайн — DEFAULT): оновити 3-й слайд
        (currentScreen as? OnboardingScreen)?.onFlagsChanged()
        if (isReady && model.state.onboarded && currentScreen !is OnboardingScreen && currentScreen !is SplashScreen && !isUnlocked) {
            log("HOME role required but not held → gate")
            navigationManager.navigateRoot(OnboardingScreen::class.java.name, OnboardingScreen.KEY_GATE)
        }
    }

    override fun dispose() {
        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(covers, assetManager, soundUtil, VfxShaderCache, Blit, msdfManager)
            super.dispose()
            log("dispose $currentClassName")
        } catch (e: Exception) {
            log("exception: ${e.message}")
        }
    }

    // ------------------------------------------------------------------------
    // Старт після Splash
    // ------------------------------------------------------------------------
    /** Splash: асети й стан завантажено → куди йти. */
    fun onReady() {
        isReady = true
        if (pendingEvents.isNotEmpty()) {
            val evs = pendingEvents.toList(); pendingEvents.clear()
            model.update { s -> evs.fold(s) { acc, e -> acc.withEvent(e) } }
        }
        downloads.resumeAfterStart()
    }

    /** Режим з картки нашого сервера (кеш AdPref або DEFAULT), див. AppFlags. */
    val flags get() = bridge.appFlags()

    /**
     * Чи можна в апку. Роль HOME обов'язкова лише в режимі «наша реклама» (app_open = custom*,
     * рішення VELDAN 12.10.2026): тоді без ролі — тільки екран-вимога (OnboardingScreen з KEY_GATE).
     * Інакше роль бажана: «Maybe later» на 3-му слайді, перемикач у Settings.
     */
    val isUnlocked: Boolean get() = model.state.onboarded && (bridge.isDefaultHome() || !flags.homeRequired)

    /** Перший екран після Splash / після надання ролі. */
    fun navigateFirst() {
        val nav = navigationManager
        // Роль надали, але онбординг не позначено: Android при наданні ролі закриває стару
        // MainActivity (з онбордингом) і стартує нову як HOME — колбек ролі гине разом зі старою
        // (заміряно 07.10.2026). Роль наша = онбординг пройдено → одразу в апку.
        if (!model.state.onboarded && bridge.isDefaultHome()) {
            log("HOME role already granted → onboarded")
            model.update { it.copy(onboarded = true) }
            pendingHome = false
            nav.navigateRoot(AppScreen::class.java.name)
            runStartupFlow()
            return
        }
        when {
            !model.state.onboarded   -> nav.navigateRoot(OnboardingScreen::class.java.name)
            !isUnlocked              -> nav.navigateRoot(OnboardingScreen::class.java.name, OnboardingScreen.KEY_GATE)
            pendingHome              -> nav.navigateRoot(LauncherScreen::class.java.name)
            else                     -> { nav.navigateRoot(AppScreen::class.java.name); runStartupFlow() }
        }
        pendingHome = false
    }

    // ------------------------------------------------------------------------
    // Бізнес-логіка UAPP: черга старту, app_open, інтерстішал (adsmodule вирішує провайдера)
    // ------------------------------------------------------------------------
    /**
     * Лише всередині застосунку: вкладки, плеєр, Studio. На лаунчері (роль HOME) app_open / інтерстішалів
     * немає — там своя вкладка на «Додому» / «Недавні» (MainActivity, pl=app_open); на онбордингу й
     * екрані-вимозі — теж ні.
     */
    private val isInApp: Boolean get() = currentScreen is AppScreen || currentScreen is PlayerScreen || currentScreen is StudioScreen

    /** Черга старту (опт-ін → app_open) уже була в цьому процесі. */
    var startupFlowDone = false
        private set

    /** Splash: чи поведе перший екран у застосунок (тоді — черга старту). Перший запуск — ні. */
    fun firstScreenIsApp(): Boolean = model.state.onboarded && isUnlocked && !pendingHome

    /**
     * Черга старту UAPP: опт-ін на пуші → стартовий app_open → onDone. Раз на процес: зі Splash,
     * а якщо процес стартував як лаунчер або з онбордингу — на першому вході в застосунок.
     */
    fun runStartupFlow(onDone: () -> Unit = {}) {
        if (startupFlowDone) { onDone(); return }
        startupFlowDone = true
        bridge.runStartupFlow(onDone)
    }

    /**
     * Повернулись у Framefall / тапнули іконку. Навігація з onAppIconPressed ще в черзі GL —
     * екран перевіряємо наступним проходом черги, коли AppScreen уже стоїть.
     */
    override fun onAppForeground(awayMs: Long, fromIcon: Boolean) {
        if (!isReady || !isUnlocked) return
        com.vidtools.framefall.game.utils.runGDX {
            if (!isInApp) return@runGDX
            if (!startupFlowDone) runStartupFlow() else bridge.appOpenOnReturn()
        }
    }

    /** Іконка Framefall на НАШОМУ лаунчері — таке саме відкриття апки, як тап у системному (app_open). */
    fun openAppFromLauncher() {
        navigationManager.navigate(AppScreen::class.java.name, LauncherScreen::class.java.name)
        onAppForeground(awayMs = -1L, fromIcon = true)
    }

    /** Перехід уперед у застосунку (вкладка, плеєр, Studio) — front-інтерстішал за частотою з картки. */
    fun adFront() {
        if (!isReady || !isInApp) return
        if (maybeInterstitial()) return
        bridge.onFrontNavigation()
    }

    /** «Назад» з плеєра / Studio в застосунок — back-інтерстішал. */
    fun adBack() {
        if (!isReady) return
        bridge.onBackNavigation()
    }

    /** Відео в бібліотеці → лічильник; інтерстішал — коли людина побачила тост «Saved». */
    private fun afterVideoSaved() {
        bridge.onVideoSaved()
        com.badlogic.gdx.utils.Timer.schedule(object : com.badlogic.gdx.utils.Timer.Task() {
            override fun run() { maybeInterstitial() }
        }, 1.5f)
    }

    /**
     * showInterstitial (pl=interstitial) кожні 2 відео — у природній паузі: після тосту «Saved»
     * або на наступному переході. Не поверх шторки; не настав час — нічого (лічильник чекає).
     */
    fun maybeInterstitial(): Boolean {
        if (!isReady || !isInApp) return false
        if ((currentScreen as? FramefallScreen)?.sheet != null) return false
        if (!bridge.isInterstitialDue()) return false
        bridge.showInterstitial()
        return true
    }

    fun toast(text: String) {
        val s = currentScreen as? FramefallScreen
        if (s != null) s.showToast(text) else bridge.showToast(text)
    }

    // ------------------------------------------------------------------------
    // PlatformEvents
    // ------------------------------------------------------------------------
    override fun onHomePressed() {
        if (!isReady || !isUnlocked) { pendingHome = true; return }
        val s = currentScreen
        if (s is LauncherScreen) s.onHomeAgain()
        else navigationManager.navigateRoot(LauncherScreen::class.java.name)
    }

    override fun onAppIconPressed() {
        pendingHome = false                      // перший екран — AppScreen, навіть у home-таску
        if (!isReady || !isUnlocked) return
        if (currentScreen is LauncherScreen) navigationManager.navigate(AppScreen::class.java.name, LauncherScreen::class.java.name)
    }

    override fun onClipboardLink(link: ResolvedLink) {
        clipLink = link
        clipVersion++
        // У центр подій: лінк лишається там, навіть якщо картку на лаунчері закрили
        val url = link.sourceUrl ?: return
        val now = System.currentTimeMillis()
        // Заголовок — ім'я файлу (ed_hd.mp4), без нього — хост
        val title = runCatching {
            val u = java.net.URI(url)
            u.path?.substringAfterLast('/')?.takeIf { it.isNotBlank() }?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: u.host
        }.getOrNull() ?: url
        val ev = com.vidtools.framefall.core.model.AppEvent("clip-$now", com.vidtools.framefall.core.model.EventKind.CLIP_LINK, title, link.chipLabel, url = url, at = now)
        // Холодний старт (апку щойно відкрили «Додому»): стан ще вантажиться — додамо в onReady
        if (model.isLoaded) model.update { it.withEvent(ev) } else pendingEvents += ev
    }

    override fun onSharedText(text: String) {
        sharedText = text
        if (!isReady || !isUnlocked) return
        val s = currentScreen
        if (s is AppScreen) s.consumeSharedText()
        else navigationManager.navigateRoot(AppScreen::class.java.name)
    }

    override fun onDownloadFinished(id: Long, success: Boolean, localUri: String?) {
        if (model.isLoaded) downloads.onFinished(id, success, localUri)
    }

    override fun onAppsChanged() { appsVersion++ }

    /**
     * Режим змінився на льоту (новий конфіг / інтернет). Послаблення діє одразу: роль стала
     * необов'язковою, а людина стоїть на екрані-вимозі → в апку. Посилення (роль стала
     * обов'язковою) — на наступному вході (resume / старт), щоб не викидати посеред дії.
     */
    override fun onFlagsChanged(flags: com.vidtools.framefall.core.config.AppFlags) {
        if (!isReady) return
        val s = currentScreen
        if (s is OnboardingScreen) {
            if (s.isGate && isUnlocked) navigationManager.navigateRoot(AppScreen::class.java.name)
            else s.onFlagsChanged()
        }
    }

    // ------------------------------------------------------------------------
    // Відкрити відео / Studio
    // ------------------------------------------------------------------------
    /**
     * Плеєр. Кадр і GIF наш плеєр не грає — системний переглядач. Файлу вже немає (видалили з
     * Галереї) — тост, а запис лишається, щоб можна було видалити з бібліотеки.
     */
    fun openVideo(v: com.vidtools.framefall.core.model.Video) {
        if (!bridge.fileExists(v.localUri)) { toast(com.vidtools.framefall.core.copy.Copy.Player.MISSING); return }
        if (v.kind == com.vidtools.framefall.core.model.MediaKind.IMAGE) { bridge.openExternally(v); return }
        playerVideoId = v.id
        val from = navigationManager.currentScreenName
        if (currentScreen is AppScreen || currentScreen is StudioScreen) adFront()
        navigationManager.navigate(PlayerScreen::class.java.name, from)
    }

    /** Studio з інструментом; відео — вибране (model.studioVideo) або [v]. */
    fun openStudio(tool: String, v: com.vidtools.framefall.core.model.Video? = null) {
        // Фіксуємо, з чим працюємо: інакше результат Studio (найновіший) став би «поточним» відео
        val target = v ?: model.studioVideo
        target?.let { vid -> if (model.state.studioVideoId != vid.id) model.update { it.copy(studioVideoId = vid.id) } }
        if (model.studioVideo == null) { toast(com.vidtools.framefall.core.copy.Copy.Studio.NO_VIDEOS); return }
        studioTool = tool
        val from = navigationManager.currentScreenName
        if (currentScreen is AppScreen || currentScreen is PlayerScreen) adFront()
        navigationManager.navigate(StudioScreen::class.java.name, from)
    }

    fun dismissClip() {
        clipLink?.sourceUrl?.let { bridge.dismissClipLink(it) }
        clipLink = null
        clipVersion++
    }
}
