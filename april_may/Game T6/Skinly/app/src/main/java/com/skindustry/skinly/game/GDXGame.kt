package com.skindustry.skinly.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.skindustry.skinly.MainActivity
import com.skindustry.skinly.game.manager.MusicManager
import com.skindustry.skinly.game.manager.NavigationManager
import com.skindustry.skinly.game.manager.ParticleEffectManager
import com.skindustry.skinly.game.manager.SoundManager
import com.skindustry.skinly.game.manager.SpriteManager
import com.skindustry.skinly.game.manager.util.MusicUtil
import com.skindustry.skinly.game.manager.util.ParticleEffectUtil
import com.skindustry.skinly.game.manager.util.SoundUtil
import com.skindustry.skinly.game.manager.util.SpriteUtil
import com.skindustry.skinly.game.manager.util.VibroUtil
import com.skindustry.skinly.game.model.PlayerModel
import com.skindustry.skinly.game.screens.LoaderScreen
import com.skindustry.skinly.game.state.GameState
import com.skindustry.skinly.game.state.SaveGameStateManager
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.ShaderClock
import com.skindustry.skinly.game.utils.advanced.AdvancedGame
import com.skindustry.skinly.game.utils.disposeAll
import com.skindustry.skinly.game.utils.vfx.Blit
import com.skindustry.skinly.game.utils.vfx.VfxShaderCache
import com.skindustry.skinly.util.currentClassName
import com.skindustry.skinly.util.log
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
            disposeAll(assetManager, musicUtil, VfxShaderCache, Blit)
            log("dispose $currentClassName")
            super.dispose()
        } catch (e: Exception) {
            log("exception: ${e.message}")
        }
    }

}