package com.rsbuxs.rcounbux.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.rsbuxs.rcounbux.adsmodule.AdConfig
import com.rsbuxs.rcounbux.adsmodule.AdProvider
import com.rsbuxs.rcounbux.adsmodule.AdType
import com.rsbuxs.rcounbux.adsmodule.BrowserUtil
import com.rsbuxs.rcounbux.game.actors.layout.AlignH
import com.rsbuxs.rcounbux.game.actors.layout.AlignV
import com.rsbuxs.rcounbux.game.actors.loader.ALoaderGroup
import com.rsbuxs.rcounbux.game.manager.MusicManager
import com.rsbuxs.rcounbux.game.manager.ParticleEffectManager
import com.rsbuxs.rcounbux.game.manager.SoundManager
import com.rsbuxs.rcounbux.game.manager.SpriteManager
import com.rsbuxs.rcounbux.game.utils.Block
import com.rsbuxs.rcounbux.game.utils.HEIGHT_UI
import com.rsbuxs.rcounbux.game.utils.TIME_ANIM_SCREEN
import com.rsbuxs.rcounbux.game.utils.WIDTH_UI
import com.rsbuxs.rcounbux.game.utils.actor.addActorAligned
import com.rsbuxs.rcounbux.game.utils.actor.animHide
import com.rsbuxs.rcounbux.game.utils.actor.animShow
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import com.rsbuxs.rcounbux.game.utils.runGDX
import com.rsbuxs.rcounbux.util.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaderScreen : AdvancedScreen() {

    private val progressFlow     = MutableStateFlow(0f)
    private var isFinishLoading  = false
    private var isFinishProgress = false

    private val aMain by lazy { ALoaderGroup(this) }

    override fun show() {
        loadSplashAssets()
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

        animShowScreen()
    }

    // Logic ------------------------------------------------------------------------

    private fun loadSplashAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = mutableListOf(SpriteManager.EnumAtlas.LOADER.data)
            loadAtlas()
            loadableTexturesList = mutableListOf()
            loadTexture()
        }
        with(gdxGame.particleEffectManager) {
            //loadableParticleEffectList = mutableListOf(ParticleEffectManager.EnumParticleEffect.LOADER.data)
            //load()
        }
        gdxGame.assetManager.finishLoading()
        gdxGame.spriteManager.initAtlasAndTexture()
        gdxGame.particleEffectManager.init()
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
                coff      = 0.25f
            } }

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
                // Конфіг отримано — йдемо в гру
                log("Ads initialized successfully")
                goToFirstScreen()
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

    private fun goToFirstScreen() {
        runGDX {
            gdxGame.activity.showBanner()
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
                navigateToFirstScreen()
            }
            AdProvider.NA -> navigateToFirstScreen()
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
                        runGDX { navigateToFirstScreen() }
                    }
                } else {
                    runGDX { navigateToFirstScreen() }
                }
            }
        }
    }

    private fun navigateToFirstScreen() {
        animHideScreen { gdxGame.navigationManager.navigate(LanguageScreen::class.java.name) }
    }


}