package com.diam.ondbit.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.diam.ondbit.MainActivity
import com.diam.ondbit.game.manager.MusicManager
import com.diam.ondbit.game.manager.NavigationManager
import com.diam.ondbit.game.manager.ParticleEffectManager
import com.diam.ondbit.game.manager.SoundManager
import com.diam.ondbit.game.manager.SpriteManager
import com.diam.ondbit.game.manager.util.MusicUtil
import com.diam.ondbit.game.manager.util.ParticleEffectUtil
import com.diam.ondbit.game.manager.util.SoundUtil
import com.diam.ondbit.game.manager.util.SpriteUtil
import com.diam.ondbit.game.manager.util.VibroUtil
import com.diam.ondbit.game.model.PlayerModel
import com.diam.ondbit.game.screens.LoaderScreen
import com.diam.ondbit.game.state.GameState
import com.diam.ondbit.game.state.SaveGameStateManager
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.ShaderClock
import com.diam.ondbit.game.utils.advanced.AdvancedGame
import com.diam.ondbit.game.utils.disposeAll
import com.diam.ondbit.game.utils.font.msdf.MsdfManager
import com.diam.ondbit.game.utils.vfx.Blit
import com.diam.ondbit.game.utils.vfx.VfxShaderCache
import com.diam.ondbit.util.currentClassName
import com.diam.ondbit.util.log
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