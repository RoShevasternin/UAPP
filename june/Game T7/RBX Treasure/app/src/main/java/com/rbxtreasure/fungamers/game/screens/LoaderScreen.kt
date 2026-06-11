package com.rbxtreasure.fungamers.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.adsmodule.AdConfig
import com.rbxtreasure.fungamers.adsmodule.AdProvider
import com.rbxtreasure.fungamers.adsmodule.AdType
import com.rbxtreasure.fungamers.adsmodule.BrowserUtil
import com.rbxtreasure.fungamers.game.actors.layout.AlignH
import com.rbxtreasure.fungamers.game.actors.layout.AlignV
import com.rbxtreasure.fungamers.game.actors.loader.ALoaderGroup
import com.rbxtreasure.fungamers.game.manager.MusicManager
import com.rbxtreasure.fungamers.game.manager.ParticleEffectManager
import com.rbxtreasure.fungamers.game.manager.SoundManager
import com.rbxtreasure.fungamers.game.manager.SpriteManager
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.HEIGHT_UI
import com.rbxtreasure.fungamers.game.utils.TIME_ANIM_SCREEN
import com.rbxtreasure.fungamers.game.utils.WIDTH_UI
import com.rbxtreasure.fungamers.game.utils.actor.addActorAligned
import com.rbxtreasure.fungamers.game.utils.actor.animHide
import com.rbxtreasure.fungamers.game.utils.actor.animShow
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.runGDX
import com.rbxtreasure.fungamers.util.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaderScreen : AdvancedScreen() {

    private val progressFlow     = MutableStateFlow(0f)
    private var isFinishLoading  = false
    private var isFinishProgress = false

    private val aMain by lazy { ALoaderGroup(this) }

    override fun show() {
        loadLoaderAssets()
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
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

        aMain.setSize(WIDTH_UI, HEIGHT_UI)
        addActorAligned(aMain, AlignH.CENTER, AlignV.CENTER)

        val aBottomTextImg = Image(gdxGame.assetsLoader.bottom_title)
        aBottomTextImg.setSize(344f, 38f)
        addActorAligned(aBottomTextImg, AlignH.CENTER, AlignV.TOP)
        aBottomTextImg.y -= 24f

        animShowScreen()
    }

    // Logic ------------------------------------------------------------------------

    private fun loadLoaderAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = mutableListOf(SpriteManager.EnumAtlas.LOADER.data)
            loadAtlas()
            loadableTexturesList = mutableListOf(SpriteManager.EnumTexture.BACKGROUND.data)
            loadTexture()
        }
//        with(gdxGame.particleEffectManager) {
//            loadableParticleEffectList = mutableListOf(ParticleEffectManager.EnumParticleEffect.LOADER.data)
//            load()
//        }
        gdxGame.assetManager.finishLoading()
        gdxGame.spriteManager.initAll()
//        gdxGame.particleEffectManager.init()
    }

    private fun loadAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = SpriteManager.EnumAtlas.entries.map { it.data }.toMutableList()
            loadAtlas()
            loadableTexturesList = SpriteManager.EnumTexture.entries.map { it.data }.toMutableList()
            loadTexture()
            loadableGroupList = SpriteManager.EnumTextureGroup.entries.map { it.data }.toMutableList()
            loadGroups()
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
        gdxGame.spriteManager.initAll()
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

                    if (progress % 50 == 0) log("progress = $progress%")
                    if (progress == 100) isFinishProgress = true

                    //delay((25..35).shuffled().first().toLong())
                }
            }
        }
    }

    private fun isFinish() {
        if (isFinishLoading && isFinishProgress) {
            isFinishProgress = false

            gdxGame.musicUtil.apply { currentMusic = MAIN.apply {
                isLooping = true
                coff      = 0.15f
            } }

            // поки тестим потім розкоментуй initAds()
            //goToFirstScreen()

            // ── Завантажуємо рекламну конфігурацію ────────────────────────────
            // initAds перевіряє інтернет + завантажує Config
            initAds()
        }
    }

    // ------------------------------------------------------------------------
    // Ads Logic
    // ------------------------------------------------------------------------

    private fun initAds() {
        gdxGame.activity.initAds { success ->
            if (success) {
                log("Ads initialized successfully")
                goToFirstScreen()
            } else {
                log("No internet connection")
                showNoWifi()
            }
        }
    }

    private fun showNoWifi() {
        runGDX {
            aMain.showNoWifi()
            aMain.onRetry = {
                log("Retry initAds")
                initAds()
            }
        }
    }

    private fun goToFirstScreen() {
        runGDX {
            gdxGame.activity.showBanner()

            // App Open показ повністю в AppOpenManager — чекаємо onDone і навігуємо
            gdxGame.activity.appOpenManager.showOnLoader(gdxGame.activity) {
                runGDX { navigateToFirstScreen() }
            }
        }
    }

    private fun navigateToFirstScreen() {
        animHideScreen {
            gdxGame.navigationManager.navigate(OnboardingScreen::class.java.name)
        }
    }

}