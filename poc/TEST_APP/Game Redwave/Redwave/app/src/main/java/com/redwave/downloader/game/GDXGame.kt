package com.redwave.downloader.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.link.chipLabel
import com.redwave.downloader.core.link.sourceUrl
import com.redwave.downloader.core.model.withEvent
import com.redwave.downloader.game.controller.DiscoverController
import com.redwave.downloader.game.controller.DownloadController
import com.redwave.downloader.game.controller.PlayerController
import com.redwave.downloader.game.manager.NavigationManager
import com.redwave.downloader.game.manager.SoundManager
import com.redwave.downloader.game.manager.SpriteManager
import com.redwave.downloader.game.manager.util.SoundUtil
import com.redwave.downloader.game.manager.util.SpriteUtil
import com.redwave.downloader.game.manager.util.VibroUtil
import com.redwave.downloader.game.model.AppModel
import com.redwave.downloader.game.platform.PlatformBridge
import com.redwave.downloader.game.platform.PlatformEvents
import com.redwave.downloader.game.screens.AppScreen
import com.redwave.downloader.game.screens.LauncherScreen
import com.redwave.downloader.game.screens.OnboardingScreen
import com.redwave.downloader.game.screens.PlayerScreen
import com.redwave.downloader.game.screens.SplashScreen
import com.redwave.downloader.game.screens.base.RedwaveScreen
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.ShaderClock
import com.redwave.downloader.game.utils.advanced.AdvancedGame
import com.redwave.downloader.game.utils.disposeAll
import com.redwave.downloader.game.utils.font.msdf.MsdfManager
import com.redwave.downloader.game.utils.vfx.Blit
import com.redwave.downloader.game.utils.vfx.VfxShaderCache
import com.redwave.downloader.android.AndroidBridge
import com.redwave.downloader.util.currentClassName
import com.redwave.downloader.util.log
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
    // Audio (лише кліки; музика — Media3 у PlaybackService)
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
    val downloads = DownloadController(model, bridge) { toast(it) }.apply { onTrackAdded = { afterTrackDownloaded() } }
    val player    = PlayerController(model, bridge) { toast(it) }
    val discover  = DiscoverController(model, bridge, downloads)

    /** Асети й стан готові (SplashScreen). До цього події Android лише запам'ятовуємо. */
    var isReady = false
        private set

    // ── Події Android, що чекають на свій екран ──────────────────────────────
    /** Лінк з буфера для картки на лаунчері (ClipGate вже відфільтрував). */
    var clipLink: ResolvedLink? = null
    var clipVersion = 0
        private set
    /** «Поділитися → Redwave»: текст для поля на Home. */
    var sharedText: String? = null
    /** Трек для вкладки Ringtone (з плеєра / меню треку). */
    var ringtoneTrackId: String? = null
    var appsVersion = 0
        private set
    private var pendingHome = false
    /** Події з буфера, що прийшли до завантаження стану (див. onClipboardLink). */
    private val pendingEvents = mutableListOf<com.redwave.downloader.core.model.AppEvent>()

    /** Текстури обкладинок на всю сесію. */
    val covers = com.redwave.downloader.game.utils.CoverCache()

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
        if (isReady) {
            player.tick()
            downloads.tick(Gdx.graphics.deltaTime)
        }
        ScreenUtils.clear(backgroundColor)
        super.render()
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
        player.applyEq()
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
     * Лише всередині застосунку: вкладки й плеєр. На лаунчері (роль HOME) app_open / інтерстішалів
     * немає — там свої плейсменти click_home / clear_history (MainActivity); на онбордингу й
     * екрані-вимозі — теж ні.
     */
    private val isInApp: Boolean get() = currentScreen is AppScreen || currentScreen is PlayerScreen

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
     * Повернулись у Redwave / тапнули іконку. Навігація з onAppIconPressed ще в черзі GL —
     * екран перевіряємо наступним проходом черги, коли AppScreen уже стоїть.
     */
    override fun onAppForeground(awayMs: Long, fromIcon: Boolean) {
        if (!isReady || !isUnlocked) return
        com.redwave.downloader.game.utils.runGDX {
            if (!isInApp) return@runGDX
            if (!startupFlowDone) runStartupFlow() else bridge.appOpenOnReturn()
        }
    }

    /** Іконка Redwave на НАШОМУ лаунчері — таке саме відкриття апки, як тап у системному (app_open). */
    fun openAppFromLauncher() {
        navigationManager.navigate(AppScreen::class.java.name, LauncherScreen::class.java.name)
        onAppForeground(awayMs = -1L, fromIcon = true)
    }

    /** Перехід уперед у застосунку (вкладка, плеєр) — front-інтерстішал за частотою з картки. */
    fun adFront() {
        if (!isReady || !isInApp) return
        if (maybeInterstitial()) return
        bridge.onFrontNavigation()
    }

    /** «Назад» з плеєра в застосунок — back-інтерстішал. */
    fun adBack() {
        if (!isReady) return
        bridge.onBackNavigation()
    }

    /** Трек у бібліотеці → лічильник; інтерстішал — коли людина побачила тост «Downloaded». */
    private fun afterTrackDownloaded() {
        bridge.onTrackDownloaded()
        com.badlogic.gdx.utils.Timer.schedule(object : com.badlogic.gdx.utils.Timer.Task() {
            override fun run() { maybeInterstitial() }
        }, 1.5f)
    }

    /**
     * showInterstitial (pl=interstitial) кожні 2 треки — у природній паузі: після тосту «Downloaded»
     * або на наступному переході. Не поверх шторки; не настав час — нічого (лічильник чекає).
     */
    fun maybeInterstitial(): Boolean {
        if (!isReady || !isInApp) return false
        if ((currentScreen as? RedwaveScreen)?.sheet != null) return false
        if (!bridge.isInterstitialDue()) return false
        bridge.showInterstitial()
        return true
    }

    fun toast(text: String) {
        val s = currentScreen as? RedwaveScreen
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
        // Заголовок — ім'я файлу (blue-room.m4a), без нього — хост
        val title = runCatching {
            val u = java.net.URI(url)
            u.path?.substringAfterLast('/')?.takeIf { it.isNotBlank() }?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: u.host
        }.getOrNull() ?: url
        val ev = com.redwave.downloader.core.model.AppEvent("clip-$now", com.redwave.downloader.core.model.EventKind.CLIP_LINK, title, link.chipLabel, url = url, at = now)
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

    override fun onPlaybackChanged() {}

    /**
     * Режим змінився на льоту (новий конфіг / інтернет). Послаблення діє одразу: роль стала
     * необов'язковою, а людина стоїть на екрані-вимозі → в апку. Посилення (роль стала
     * обов'язковою) — на наступному вході (resume / старт), щоб не викидати посеред дії.
     */
    override fun onFlagsChanged(flags: com.redwave.downloader.core.config.AppFlags) {
        if (!isReady) return
        val s = currentScreen
        if (s is OnboardingScreen) {
            if (s.isGate && isUnlocked) navigationManager.navigateRoot(AppScreen::class.java.name)
            else s.onFlagsChanged()
        }
    }

    fun dismissClip() {
        clipLink?.sourceUrl?.let { bridge.dismissClipLink(it) }
        clipLink = null
        clipVersion++
    }
}
