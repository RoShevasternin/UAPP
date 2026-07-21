package com.mon.sterbx.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.video.VideoPlayer
import com.badlogic.gdx.video.VideoPlayerCreator
import com.badlogic.gdx.video.scenes.scene2d.VideoActor
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.AVideoActor
import com.mon.sterbx.game.actors.button.AOrangeButton
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.screens.selector.Selector_1_Screen
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.actor.setSize
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.runGDX
import com.mon.sterbx.util.log
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
                load(Gdx.files.internal("video/onb_${it.inc()}.mp4"))
                play()
            }
            disposableSet.add(videoPlayer)
            videoPlayer
        }.getOrNull()
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContinueBtn    by lazy { AOrangeButton(this, "CONTINUE") }
    private val aContentImg     by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    private val aVideoActor     by lazy { AVideoActor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_YELLOW)
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addContinueBtn()
        addContentImg()
        addContentVideoActor()
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

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 64f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 36f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom = screen.adBottomUI + 36f }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(351f, 504f)
        add(aContentImg) { centerX(); topToTop(); bottomToTop(aContinueBtn) }
    }

    private fun AConstraintLayout.addContentVideoActor() {
        aVideoActor.setSize(312f, 312f)
        add(aVideoActor) { centerX(aContentImg); bottomToBottom(aContentImg, 80f) }

        aVideoActor.setPlayer(listOnboardingVideo[currentIndex])
    }

    private fun animateTransition(newIndex: Int) {

        isAnimating = true

        val dur = 0.3f

        aContentImg.setOrigin(Align.center)
        aVideoActor.setOrigin(Align.center)

        // ── фаза 1: обидва згасають ──
        val fadeOut = Actions.parallel(
            Actions.fadeOut(dur, Interpolation.sine),
            Actions.scaleTo(0.94f, 0.94f, dur, Interpolation.sine),
        )
        aContentImg.addAction(fadeOut)
        aVideoActor.addAction(Actions.fadeOut(dur, Interpolation.sine))

        // ── керуємо послідовністю через один актор (aContentImg) ──
        aContentImg.addAction(
            Actions.sequence(
                Actions.delay(dur),   // чекаємо поки згасли

                // підміна обох
                Actions.run {
                    aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])
                    aVideoActor.setPlayer(listOnboardingVideo[newIndex])

                    // повертаємо обидва до появи
                    aContentImg.addAction(Actions.parallel(
                        Actions.fadeIn(dur, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, dur, Interpolation.sine),
                    ))
                    aVideoActor.addAction(Actions.fadeIn(dur, Interpolation.sine))
                },

                Actions.delay(dur),
                Actions.run { isAnimating = false }
            )
        )
    }

}