package com.rbuxrds.counterds.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counterds.App
import com.rbuxrds.counterds.adsmodule.AdConfig
import com.rbuxrds.counterds.adsmodule.AdPref
import com.rbuxrds.counterds.adsmodule.AdProvider
import com.rbuxrds.counterds.adsmodule.AdType
import com.rbuxrds.counterds.adsmodule.BrowserUtil
import com.rbuxrds.counterds.game.actors.loader.ALoaderGroup
import com.rbuxrds.counterds.game.actors.layout.AlignH
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.manager.MusicManager
import com.rbuxrds.counterds.game.manager.ParticleEffectManager
import com.rbuxrds.counterds.game.manager.SoundManager
import com.rbuxrds.counterds.game.manager.SpriteManager
import com.rbuxrds.counterds.game.utils.Block
import com.rbuxrds.counterds.game.utils.HEIGHT_UI
import com.rbuxrds.counterds.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counterds.game.utils.WIDTH_UI
import com.rbuxrds.counterds.game.utils.actor.addActorAligned
import com.rbuxrds.counterds.game.utils.actor.animDelay
import com.rbuxrds.counterds.game.utils.actor.animHide
import com.rbuxrds.counterds.game.utils.actor.animShow
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.runGDX
import com.rbuxrds.counterds.util.log
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

        aMain.setSize(WIDTH_UI, HEIGHT_UI)
        addActorAligned(aMain, AlignH.CENTER, AlignV.CENTER)

        animShowScreen()
    }

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
            //animHideScreen { gdxGame.navigationManager.navigate(Select_1_Screen::class.java.name) }

            // ── Завантажуємо рекламну конфігурацію ────────────────────────────
            // initAds перевіряє інтернет + завантажує Firebase Remote Config
            initAds()
        }
    }

    // ------------------------------------------------------------------------
    // Ads Logic
    // ------------------------------------------------------------------------

    private fun initAds() {
        gdxGame.activity.initAds { success ->
            if (success) {
                // Конфіг отримано — йдемо в гру
                log("Ads initialized successfully")
                goToMenu()
            } else {
                // Немає інтернету — показуємо UI
                log("No internet connection")
                showNoWifi()
            }
        }
    }

    private fun showNoWifi() {
        runGDX {
            aMain.showNoWifi()
            aMain.onRetry = {
                // Юзер натиснув Retry — пробуємо знову
                log("Retry initAds")
                initAds()
            }
        }
    }

    private fun goToMenu() {
        runGDX {
            gdxGame.activity.showBanner(gdxGame.activity.binding.bannerContainer)
            showAppOpenThenGoToMenu()
        }
    }

    private fun showAppOpenThenGoToMenu() {
        when (AdConfig.getProvider(AdType.APP_OPEN)) {
            AdProvider.ADMOB -> waitAndShowAdmobAppOpen()
            AdProvider.CUSTOM -> {
                val url = AdConfig.customAppOpenUrl()
                if (url.isNotEmpty()) {
                    AdConfig.isFullscreenAdShowing = true
                    gdxGame.activity.runOnUiThread {
                        BrowserUtil.open(gdxGame.activity, url)
                    }
                }
                navigateToMenu()
            }
            AdProvider.NA -> navigateToMenu()
        }
    }

    private fun waitAndShowAdmobAppOpen() {
        val appOpenManager = gdxGame.activity.appOpenManager

        gdxGame.activity.runOnUiThread {
            appOpenManager.loadAdmobAppOpen()
        }

        coroutine?.launch {
            var waited = 0
            while (!appOpenManager.isAdReady() && waited < 3000) {
                kotlinx.coroutines.delay(100)
                waited += 100
            }

            gdxGame.activity.runOnUiThread {
                if (appOpenManager.isAdReady()) {
                    appOpenManager.showAdmobAppOpen(gdxGame.activity) {
                        // Після показу — встановлюємо cooldown
                        // щоб onAppForeground не показав ще раз одразу
                        appOpenManager.markShown()  // ← додамо цей метод
                        runGDX { navigateToMenu() }
                    }
                } else {
                    runGDX { navigateToMenu() }
                }
            }
        }
    }

    private fun navigateToMenu() {
        animHideScreen { gdxGame.navigationManager.navigate(Select_1_Screen::class.java.name) }
    }


}