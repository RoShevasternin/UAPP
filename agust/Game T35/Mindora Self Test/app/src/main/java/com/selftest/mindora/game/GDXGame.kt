package com.selftest.mindora.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.selftest.mindora.MainActivity
import com.selftest.mindora.game.manager.MusicManager
import com.selftest.mindora.game.manager.NavigationManager
import com.selftest.mindora.game.manager.ParticleEffectManager
import com.selftest.mindora.game.manager.SoundManager
import com.selftest.mindora.game.manager.SpriteManager
import com.selftest.mindora.game.manager.util.MusicUtil
import com.selftest.mindora.game.manager.util.ParticleEffectUtil
import com.selftest.mindora.game.manager.util.SoundUtil
import com.selftest.mindora.game.manager.util.SpriteUtil
import com.selftest.mindora.game.manager.util.VibroUtil
import com.selftest.mindora.game.model.PlayerModel
import com.selftest.mindora.game.screens.LoaderScreen
import com.selftest.mindora.game.state.GameState
import com.selftest.mindora.game.state.SaveGameStateManager
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.Settings
import com.selftest.mindora.game.utils.ShaderClock
import com.selftest.mindora.game.utils.advanced.AdvancedGame
import com.selftest.mindora.game.utils.disposeAll
import com.selftest.mindora.game.utils.font.msdf.MsdfManager
import com.selftest.mindora.game.utils.vfx.Blit
import com.selftest.mindora.game.utils.vfx.VfxShaderCache
import com.selftest.mindora.services.analytics.AnalyticsManager
import com.selftest.mindora.util.currentClassName
import com.selftest.mindora.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GDXGame(val activity: MainActivity) : AdvancedGame() {

    // ------------------------------------------------------------------------
    // Assets
    // ------------------------------------------------------------------------

    val assetsLoader by lazy { SpriteUtil.Loader() }
    val assetsAll    by lazy { SpriteUtil.All() }

    //val particleEffectLoader by lazy { ParticleEffectUtil.Loader() }
    val particleEffectAll by lazy { ParticleEffectUtil.All() }

    // ------------------------------------------------------------------------
    // Audio
    // ------------------------------------------------------------------------

    val musicUtil by lazy { MusicUtil() }
    val soundUtil by lazy { SoundUtil() }
    val vibroUtil by lazy { VibroUtil() }

    // ------------------------------------------------------------------------
    // Managers
    // ------------------------------------------------------------------------

    lateinit var assetManager         : AssetManager          private set
    lateinit var navigationManager    : NavigationManager     private set
    lateinit var spriteManager        : SpriteManager         private set
    lateinit var musicManager         : MusicManager          private set
    lateinit var soundManager         : SoundManager          private set
    lateinit var particleEffectManager: ParticleEffectManager private set
    lateinit var msdfManager          : MsdfManager           private set

    // ------------------------------------------------------------------------
    // Coroutine
    // ------------------------------------------------------------------------

    val coroutine = CoroutineScope(Dispatchers.Default)

    // ------------------------------------------------------------------------
    // GameState
    // ------------------------------------------------------------------------

    private val gameState   = GameState()
    private val saveManager = SaveGameStateManager(gameState, coroutine)

    // ------------------------------------------------------------------------
    // Models
    // ------------------------------------------------------------------------

    val modelPlayer = PlayerModel(gameState, coroutine)

    // ------------------------------------------------------------------------
    // Services
    // ------------------------------------------------------------------------

    val settings  by lazy { Settings() }
    val analytics get() = AnalyticsManager

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun create() {
        assetManager          = AssetManager()
        spriteManager         = SpriteManager(assetManager)
        musicManager          = MusicManager(assetManager)
        soundManager          = SoundManager(assetManager)
        particleEffectManager = ParticleEffectManager(assetManager)
        msdfManager           = MsdfManager()
        navigationManager     = NavigationManager(this)

        saveManager.load()
        saveManager.startAutoSave(intervalSec = 30)

        collectModelPlayer()

        val firstScreenName = LoaderScreen::class.java.name
        navigationManager.navigate(firstScreenName)

        ShaderProgram.pedantic = false
    }

    override fun render() {
        ShaderClock.update()
        ScreenUtils.clear(backgroundColor)
        super.render()
    }

    override fun pause() {
        super.pause()
        log("pause")
        saveManager.save() // потім зберігаємо вже свіжий стан
    }

    override fun resume() {
        super.resume()
        log("resume")
        Blit.dispose()
    }

    override fun dispose() {
        saveManager.stopAutoSave()
        saveManager.save()

        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(assetManager, musicUtil, soundUtil, VfxShaderCache, Blit, msdfManager)
            super.dispose()
            log("dispose $currentClassName")
        } catch (e: Exception) {
            log("exception: ${e.message}")
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private fun collectModelPlayer() {
        coroutine.launch {
            modelPlayer.isLoadedFlow.first { it }
        }
    }

}