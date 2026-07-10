package com.sakurbx.fungambx.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.game.actors.layout.AlignH
import com.sakurbx.fungambx.game.actors.layout.AlignV
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.loader.ALoaderGroup
import com.sakurbx.fungambx.game.manager.MusicManager
import com.sakurbx.fungambx.game.manager.ParticleEffectManager
import com.sakurbx.fungambx.game.manager.SoundManager
import com.sakurbx.fungambx.game.manager.SpriteManager
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.HEIGHT_UI
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.WIDTH_UI
import com.sakurbx.fungambx.game.utils.actor.addActorAligned
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.currentTimeMinus
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.runGDX
import com.sakurbx.fungambx.util.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toDuration

class LoaderScreen : AdvancedScreen() {

    private val progressFlow     = MutableStateFlow(0f)
    private var isFinishLoading  = false
    private var isFinishProgress = false

    private var startTime = 0L

    private val aMain by lazy { ALoaderGroup(this) }

    override fun show() {
        rootConstraintLayout.color.a = 0f

        loadLoaderAssets()
        setBackground(gdxGame.assetsLoader.BACKGROUND_LOADER)
        super.show()
        animShowScreen { startTime = System.currentTimeMillis() }

        loadAssets()
        collectProgress()
    }

    override fun render(delta: Float) {
        super.render(delta)
        loadingAssets()
        isFinish()
    }

    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // Actors ------------------------------------------------------------------------
    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        aMain.setSize(WIDTH_UI, HEIGHT_UI)
        addActorAligned(aMain, AlignH.CENTER, AlignV.CENTER)

        val aBottomTextImg = Image(gdxGame.assetsLoader.bottom_title)
        aBottomTextImg.setSize(344f, 40f)
        addActorAligned(aBottomTextImg, AlignH.CENTER, AlignV.BOTTOM)
        aBottomTextImg.y += 44f
    }

    // Logic ------------------------------------------------------------------------

    private fun loadLoaderAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = mutableListOf(SpriteManager.EnumAtlas.LOADER.data)
            loadAtlas()
            loadableTexturesList = mutableListOf(
                SpriteManager.EnumTexture.BACKGROUND_LOADER.data,
                SpriteManager.EnumTexture.STAR.data,
            )
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

            // мінімум 2 секунди показу лоадера
            if (currentTimeMinus(startTime) < 3000L) return

            isFinishProgress = false

            gdxGame.musicUtil.apply { currentMusic = MAIN.apply {
                isLooping = true
                coff      = 0.17f
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
        animHideScreen { gdxGame.navigationManager.navigate(OnboardingScreen::class.java.name) }
    }

}