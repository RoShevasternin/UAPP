package com.rbuxrds.counter.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.rbuxrds.counter.MainActivity
import com.rbuxrds.counter.game.manager.MusicManager
import com.rbuxrds.counter.game.manager.NavigationManager
import com.rbuxrds.counter.game.manager.ParticleEffectManager
import com.rbuxrds.counter.game.manager.SoundManager
import com.rbuxrds.counter.game.manager.SpriteManager
import com.rbuxrds.counter.game.manager.util.MusicUtil
import com.rbuxrds.counter.game.manager.util.SoundUtil
import com.rbuxrds.counter.game.manager.util.SpriteUtil
import com.rbuxrds.counter.game.screens.LoaderScreen
import com.rbuxrds.counter.game.utils.GameColor
import com.rbuxrds.counter.game.utils.ShaderClock
import com.rbuxrds.counter.game.utils.advanced.AdvancedGame
import com.rbuxrds.counter.game.utils.disposeAll
import com.rbuxrds.counter.game.utils.vfx.Blit
import com.rbuxrds.counter.game.utils.vfx.VfxShaderCache
import com.rbuxrds.counter.util.currentClassName
import com.rbuxrds.counter.util.log
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

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    val coroutine = CoroutineScope(Dispatchers.Default)

    //val ds_Player = DS_Player(coroutine)
    //val modelPlayer = PlayerModel(ds_Player, coroutine)

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