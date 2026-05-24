package com.rsbuxs.rcounbux.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.rsbuxs.rcounbux.MainActivity
import com.rsbuxs.rcounbux.game.dataStore.DS_Player
import com.rsbuxs.rcounbux.game.manager.MusicManager
import com.rsbuxs.rcounbux.game.manager.NavigationManager
import com.rsbuxs.rcounbux.game.manager.ParticleEffectManager
import com.rsbuxs.rcounbux.game.manager.SoundManager
import com.rsbuxs.rcounbux.game.manager.SpriteManager
import com.rsbuxs.rcounbux.game.manager.util.MusicUtil
import com.rsbuxs.rcounbux.game.manager.util.ParticleEffectUtil
import com.rsbuxs.rcounbux.game.manager.util.SoundUtil
import com.rsbuxs.rcounbux.game.manager.util.SpriteUtil
import com.rsbuxs.rcounbux.game.manager.util.VibroUtil
import com.rsbuxs.rcounbux.game.model.PlayerModel
import com.rsbuxs.rcounbux.game.screens.LoaderScreen
import com.rsbuxs.rcounbux.game.utils.GameColor
import com.rsbuxs.rcounbux.game.utils.ShaderClock
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGame
import com.rsbuxs.rcounbux.game.utils.disposeAll
import com.rsbuxs.rcounbux.game.utils.vfx.Blit
import com.rsbuxs.rcounbux.game.utils.vfx.VfxShaderCache
import com.rsbuxs.rcounbux.util.currentClassName
import com.rsbuxs.rcounbux.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class GDXGame(val activity: MainActivity) : AdvancedGame() {

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

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    val coroutine = CoroutineScope(Dispatchers.Default)

    val dsPlayer    = DS_Player(coroutine)
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