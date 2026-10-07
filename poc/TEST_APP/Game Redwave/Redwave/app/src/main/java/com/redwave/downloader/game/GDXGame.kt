package com.redwave.downloader.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.link.sourceUrl
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
    val downloads = DownloadController(model, bridge) { toast(it) }
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
        // Роль HOME могли забрати в налаштуваннях, поки ми були у фоні → назад на екран-вимогу
        if (isReady && model.state.onboarded && currentScreen !is OnboardingScreen && currentScreen !is SplashScreen && !bridge.isDefaultHome()) {
            log("HOME role lost → gate")
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
        downloads.resumeAfterStart()
        player.applyEq()
    }

    /**
     * Роль HOME обов'язкова (рішення VELDAN 07.10.2026): без неї апкою користуватись не можна,
     * лише онбординг / екран-вимога (OnboardingScreen з KEY_GATE).
     */
    val isUnlocked: Boolean get() = model.state.onboarded && bridge.isDefaultHome()

    /** Перший екран після Splash / після надання ролі. */
    fun navigateFirst() {
        val nav = navigationManager
        when {
            !model.state.onboarded   -> nav.navigateRoot(OnboardingScreen::class.java.name)
            !bridge.isDefaultHome()  -> nav.navigateRoot(OnboardingScreen::class.java.name, OnboardingScreen.KEY_GATE)
            pendingHome              -> nav.navigateRoot(LauncherScreen::class.java.name)
            else                     -> nav.navigateRoot(AppScreen::class.java.name)
        }
        pendingHome = false
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

    fun dismissClip() {
        clipLink?.sourceUrl?.let { bridge.dismissClipLink(it) }
        clipLink = null
        clipVersion++
    }
}
