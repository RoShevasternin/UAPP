package com.racing.funtols.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.racing.funtols.MainActivity
import com.racing.funtols.game.manager.MusicManager
import com.racing.funtols.game.manager.NavigationManager
import com.racing.funtols.game.manager.ParticleEffectManager
import com.racing.funtols.game.manager.SoundManager
import com.racing.funtols.game.manager.SpriteManager
import com.racing.funtols.game.manager.util.MusicUtil
import com.racing.funtols.game.manager.util.ParticleEffectUtil
import com.racing.funtols.game.manager.util.SoundUtil
import com.racing.funtols.game.manager.util.SpriteUtil
import com.racing.funtols.game.manager.util.VibroUtil
import com.racing.funtols.game.model.PlayerModel
import com.racing.funtols.game.screens.LoaderScreen
import com.racing.funtols.game.state.GameState
import com.racing.funtols.game.state.SaveGameStateManager
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.ShaderClock
import com.racing.funtols.game.utils.advanced.AdvancedGame
import com.racing.funtols.game.utils.disposeAll
import com.racing.funtols.game.utils.font.msdf.MsdfManager
import com.racing.funtols.game.utils.vfx.Blit
import com.racing.funtols.game.utils.vfx.VfxShaderCache
import com.racing.funtols.util.currentClassName
import com.racing.funtols.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class GDXGame(val activity: MainActivity) : AdvancedGame() {

    // ------------------------------------------------------------------------
    // Assets
    // ------------------------------------------------------------------------

    val assetsLoader by lazy { SpriteUtil.Loader() }
    val assetsAll    by lazy { SpriteUtil.All() }

    val particleEffectLoader by lazy { ParticleEffectUtil.Loader() }
    val particleEffectAll    by lazy { ParticleEffectUtil.All() }

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

        navigationManager.navigate(LoaderScreen::class.java.name)

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
        saveManager.save()
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
            log("dispose $currentClassName")
            super.dispose()
        } catch (e: Exception) {
            log("exception: ${e.message}")
        }
    }

}