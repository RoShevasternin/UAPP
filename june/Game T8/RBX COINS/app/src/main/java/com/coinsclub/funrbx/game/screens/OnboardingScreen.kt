package com.coinsclub.funrbx.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.button.AYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.screens.selector.Selector_1_Screen
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.WIDTH_UI
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.runGDX
import com.coinsclub.funrbx.util.log
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
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    //private val aBottomImg   by lazy { Image(gdxGame.assetsAll.BOTTOM_BROWN) }
    private val aContinueBtn by lazy { AYellowButton(this, "CONTINUE") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ONBOARDING)
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addContentImg()
        //addBottomImg()
        addContinueBtn()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(WIDTH, 738f)
        add(aContentImg) { centerX(); bottomToBottom() }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContentImg) { marginBottom += screen.adBottomUI }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

//    private fun AConstraintLayout.addBottomImg() {
//        aBottomImg.height = 146f
//        add(aBottomImg) { centerX(); bottomToBottom(); matchWidth() }
//    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(340f, 56f)
        add(aContinueBtn) { centerX(aContentImg); bottomToBottom(aContentImg, margin = 40f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
            if (currentIndex == maxIndex) aContinueBtn.label.setText("START EARNING")
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------

    private fun animateTransition(newIndex: Int) {

        isAnimating = true

        val duration = 0.45f

        val startX = aContentImg.x

        aContentImg.addAction(

            Actions.sequence(

                // невеликий замах вправо
                Actions.moveBy(
                    30f,
                    0f,
                    0.08f,
                    Interpolation.sineOut
                ),

                // виліт вліво
                Actions.parallel(

                    Actions.moveBy(
                        -500f,
                        0f,
                        duration,
                        Interpolation.exp10In
                    ),

                    Actions.fadeOut(
                        duration * 1.5f,
                        Interpolation.fade
                    )
                ),

                Actions.run {

                    aContentImg.drawable =
                        TextureRegionDrawable(
                            gdxGame.assetsAll.listOnboarding[newIndex]
                        )

                    aContentImg.color.a = 0f
                    aContentImg.x = startX + 500f
                },

                // вліт справа
                Actions.parallel(

                    Actions.moveTo(
                        startX,
                        aContentImg.y,
                        duration,
                        Interpolation.swingOut
                    ),

                    Actions.fadeIn(
                        duration * 0.9f,
                        Interpolation.fade
                    )
                ),

                Actions.run {
                    isAnimating = false
                }
            )
        )
    }

}