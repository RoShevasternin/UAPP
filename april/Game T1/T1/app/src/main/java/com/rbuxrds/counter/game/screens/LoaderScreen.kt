package com.rbuxrds.counter.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.ALoaderGroup
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.manager.MusicManager
import com.rbuxrds.counter.game.manager.ParticleEffectManager
import com.rbuxrds.counter.game.manager.SoundManager
import com.rbuxrds.counter.game.manager.SpriteManager
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.HEIGHT_UI
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.WIDTH_UI
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.util.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaderScreen : AdvancedScreen() {

    private val progressFlow     = MutableStateFlow(0f)
    private var isFinishLoading  = false
    private var isFinishProgress = false

    //private val parameter = FontParameter().setCharacters(textBranding).setSize(40)
    //private val font      = fontGenerator_Nunito_SemiBold.generateFont(parameter)

    private val aMain by lazy { ALoaderGroup(this) }

    //private val brandingLbl = Label(textBranding, Label.LabelStyle(font, GameColor.white_55))


    override fun show() {
        loadSplashAssets()
        //setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()

        loadAssets()
        collectProgress()
    }

    override fun render(delta: Float) {
        super.render(delta)
        loadingAssets()
        isFinish()
    }

    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // Actors ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        //aMain.debug()
        aMain.setSize(WIDTH_UI, HEIGHT_UI)
        addActorAligned(aMain, AlignH.CENTER, AlignV.CENTER)

        //addBrandingLbl()

        animShowScreen()
    }

//    private fun Group.addBrandingLbl() {
//        brandingLbl.setSize(443f, 165f)
//        addActorAligned(brandingLbl, AlignH.CENTER)
//        brandingLbl.setAlignment(Align.center)
//        brandingLbl.y = 120f
//    }

    // Logic ------------------------------------------------------------------------

    private fun loadSplashAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = mutableListOf(SpriteManager.EnumAtlas.LOADER.data)
            loadAtlas()
            loadableTexturesList = mutableListOf(
                //SpriteManager.EnumTexture.BACKGROUND.data,
                //SpriteManager.EnumTexture.MASK.data,
            )
            loadTexture()
        }
//        with(gdxGame.particleEffectManager) {
//            loadableParticleEffectList = mutableListOf(ParticleEffectManager.EnumParticleEffect.LOADER.data)
//            load()
//        }
        gdxGame.assetManager.finishLoading()
        gdxGame.spriteManager.initAtlasAndTexture()
//        gdxGame.particleEffectManager.init()
    }

    private fun loadAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = SpriteManager.EnumAtlas.entries.map { it.data }.toMutableList()
            loadAtlas()
            loadableTexturesList = SpriteManager.EnumTexture.entries.map { it.data }.toMutableList()
            loadTexture()
        }
        with(gdxGame.musicManager) {
            loadableMusicList = MusicManager.EnumMusic.entries.map { it.data }.toMutableList()
            load()
        }
        with(gdxGame.soundManager) {
            loadableSoundList = SoundManager.EnumSound.entries.map { it.data }.toMutableList()
            load()
        }
        with(gdxGame.particleEffectManager) {
            loadableParticleEffectList = ParticleEffectManager.EnumParticleEffect.entries.map { it.data }.toMutableList()
            load()
        }
    }

    private fun initAssets() {
        gdxGame.spriteManager.initAtlasAndTexture()
        gdxGame.musicManager.init()
        gdxGame.soundManager.init()
        gdxGame.particleEffectManager.init()
    }

    private fun loadingAssets() {
        if (isFinishLoading.not()) {
            if (gdxGame.assetManager.update(16)) {
                isFinishLoading = true
                initAssets()
            }
            progressFlow.value = gdxGame.assetManager.progress
        }
    }

    private fun collectProgress() {
        coroutine?.launch {
            var progress = 0
            progressFlow.collect { p ->
                while (progress < (p * 100)) {
                    progress += 1

//                    runGDX {
//                        aMain.aLoading.setProgressPercent(progress.toFloat())
//                    }

                    if (progress % 50 == 0) log("progress = $progress%")
                    if (progress == 100) isFinishProgress = true

                    //delay((25..35).shuffled().first().toLong())
                }
            }
        }
    }

    private fun isFinish() {
        if (isFinishProgress) {
            isFinishProgress = false

            gdxGame.musicUtil.apply { currentMusic = MAIN.apply {
                isLooping = true
                coff      = 0.15f
            } }

            //stageUI.root.animDelay(0f) {}
            animHideScreen { gdxGame.navigationManager.navigate(Select_1_Screen::class.java.name) }
        }
    }


}