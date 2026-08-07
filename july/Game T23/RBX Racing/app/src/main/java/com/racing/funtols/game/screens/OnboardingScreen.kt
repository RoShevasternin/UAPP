package com.racing.funtols.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.video.VideoPlayer
import com.badlogic.gdx.video.VideoPlayerCreator
import com.racing.funtols.adsmodule.AdSizeManager
import com.racing.funtols.game.actors.AVideoActor
import com.racing.funtols.game.actors.button.ARedButton
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.screens.selector.Selector_1_Screen
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.runGDX
import com.racing.funtols.util.log
import kotlinx.coroutines.launch

class OnboardingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var maxIndex    = gdxGame.assetsAll.listOnboarding.lastIndex
    private var isAnimating = false

    private var currentIndex = 0
        set(value) {
            if (isAnimating) return
            gdxGame.activity.onFrontNavigation()
            animateTransition(value)
            field = value
        }

    // ------------------------------------------------------------------------
    // Video
    // ------------------------------------------------------------------------
    private val listOnboardingVideo = List<VideoPlayer?>(3) {
        runCatching {
            val videoPlayer = VideoPlayerCreator.createVideoPlayer().apply {
                isLooping = true   // зациклити фон
                load(Gdx.files.internal("video/onboarding_${it.inc()}.mp4"))
                play()
            }
            disposableSet.add(videoPlayer)
            videoPlayer
        }.getOrNull()
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVideoActor  by lazy { AVideoActor() }
    private val aContinueBtn by lazy { ARedButton(this, "CONTINUE") }
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addContentVideoActor()
        addContentImg()
        addContinueBtn()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addContentVideoActor() {
        add(aVideoActor) { center(); marginTop = -safeStatusBarUI; matchConstraint() }
        aVideoActor.setPlayer(listOnboardingVideo[currentIndex])
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(344f, 261f)
        add(aContentImg) { centerX(); bottomToBottom(margin = 42f) }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContentImg) { marginBottom = screen.adBottomUI + 42f }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(312f, 52f)
        add(aContinueBtn) { centerX(aContentImg); bottomToBottom(aContentImg, 24f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun animateTransition(newIndex: Int) {
        isAnimating = true

        val dur = 0.28f
        val rise = 24f   // наскільки "випливає" при появі

        aContentImg.setOrigin(Align.center)
        aVideoActor.setOrigin(Align.center)

        val startImgY   = aContentImg.y
        val startVideoY = aVideoActor.y

        // ── фаза 1: згасання + легкий зсув униз ──
        aContentImg.addAction(Actions.parallel(
            Actions.fadeOut(dur, Interpolation.pow2In),
            Actions.moveBy(0f, -rise, dur, Interpolation.pow2In),
        ))
        aVideoActor.addAction(Actions.parallel(
            Actions.fadeOut(dur, Interpolation.pow2In),
            Actions.moveBy(0f, -rise, dur, Interpolation.pow2In),
        ))

        // ── диригент послідовності ──
        aContentImg.addAction(Actions.sequence(
            Actions.delay(dur),

            Actions.run {
                // підміна контенту
                aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])
                aVideoActor.setPlayer(listOnboardingVideo[newIndex])

                // нове ставимо трохи нижче й прозоре — і випливає вгору на місце
                aContentImg.y = startImgY - rise
                aVideoActor.y = startVideoY - rise

                aContentImg.addAction(Actions.parallel(
                    Actions.fadeIn(dur, Interpolation.pow2Out),
                    Actions.moveTo(aContentImg.x, startImgY, dur, Interpolation.pow2Out),
                ))
                aVideoActor.addAction(Actions.parallel(
                    Actions.fadeIn(dur, Interpolation.pow2Out),
                    Actions.moveTo(aVideoActor.x, startVideoY, dur, Interpolation.pow2Out),
                ))
            },

            Actions.delay(dur),
            Actions.run { isAnimating = false }
        ))
    }

}