package com.rbxgolden.fungamems.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.rbxgolden.fungamems.MainActivity
import com.rbxgolden.fungamems.game.dataStore.DS_Player
import com.rbxgolden.fungamems.game.manager.MusicManager
import com.rbxgolden.fungamems.game.manager.NavigationManager
import com.rbxgolden.fungamems.game.manager.ParticleEffectManager
import com.rbxgolden.fungamems.game.manager.SoundManager
import com.rbxgolden.fungamems.game.manager.SpriteManager
import com.rbxgolden.fungamems.game.manager.util.MusicUtil
import com.rbxgolden.fungamems.game.manager.util.ParticleEffectUtil
import com.rbxgolden.fungamems.game.manager.util.SoundUtil
import com.rbxgolden.fungamems.game.manager.util.SpriteUtil
import com.rbxgolden.fungamems.game.manager.util.VibroUtil
import com.rbxgolden.fungamems.game.model.PlayerModel
import com.rbxgolden.fungamems.game.screens.LoaderScreen
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.ShaderClock
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGame
import com.rbxgolden.fungamems.game.utils.disposeAll
import com.rbxgolden.fungamems.game.utils.vfx.Blit
import com.rbxgolden.fungamems.game.utils.vfx.VfxShaderCache
import com.rbxgolden.fungamems.util.currentClassName
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class GDXGame(val activity: MainActivity) : AdvancedGame() {

    // ------------------------------------------------------------------------
    // Managers
    // ------------------------------------------------------------------------
    lateinit var assetManager     : AssetManager      private set
    lateinit var navigationManager: NavigationManager private set
    lateinit var spriteManager    : SpriteManager     private set
    lateinit var musicManager     : MusicManager      private set
    lateinit var soundManager     : SoundManager      private set
    lateinit var particleEffectManager: ParticleEffectManager private set

    val assetsLoader by lazy { SpriteUtil.Loader() }
    val assetsAll    by lazy { SpriteUtil.All() }

    val musicUtil by lazy { MusicUtil() }
    val soundUtil by lazy { SoundUtil() }
    val vibroUtil by lazy { VibroUtil() }

    val particleEffectLoader by lazy { ParticleEffectUtil.Loader() }
    val particleEffectAll    by lazy { ParticleEffectUtil.All() }

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------
    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    // ------------------------------------------------------------------------
    // Coroutine
    // ------------------------------------------------------------------------
    val coroutine = CoroutineScope(Dispatchers.Default)

    private val dsPlayer = DS_Player(coroutine)

    // ------------------------------------------------------------------------
    // Models
    // ------------------------------------------------------------------------
    val modelPlayer = PlayerModel(dsPlayer, coroutine)

    override fun create() {
        navigationManager = NavigationManager(this)
        assetManager      = AssetManager()
        spriteManager     = SpriteManager(assetManager)

        musicManager      = MusicManager(assetManager)
        soundManager      = SoundManager(assetManager)

        particleEffectManager = ParticleEffectManager(assetManager)

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
    }

    override fun resume() {
        super.resume()
        log("resume")
        Blit.dispose()
    }

    override fun dispose() {
        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(
                assetManager, musicUtil,
                VfxShaderCache, Blit,
            )

            log("dispose $currentClassName")
            super.dispose()
        } catch (e: Exception) { log("exception: ${e.message}") }
    }

}