package com.rbxrush.rushrbx.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.adsmodule.AdSizeManager
import com.rbxrush.rushrbx.game.actors.button.AYellowButton
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.screens.selector.Selector_1_Screen
import com.rbxrush.rushrbx.game.utils.Block
import com.rbxrush.rushrbx.game.utils.TIME_ANIM_SCREEN
import com.rbxrush.rushrbx.game.utils.WIDTH_UI
import com.rbxrush.rushrbx.game.utils.actor.animDelay
import com.rbxrush.rushrbx.game.utils.actor.animHide
import com.rbxrush.rushrbx.game.utils.actor.animShow
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.runGDX
import com.rbxrush.rushrbx.util.log
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
    private val aContinueBtn by lazy { AYellowButton(this, "CONTINUE") }
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    //private val aBottomImg   by lazy { Image(gdxGame.assetsAll.BOTTOM_BROWN) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addContinueBtn()
        addContentImg()
        //addBottomImg()
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

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 56f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 12f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
            if (currentIndex == maxIndex) aContinueBtn.label.setText("START")
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom += screen.adBottomUI }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(417f, 602f)
        add(aContentImg) {
            centerX(); topToTop(); bottomToTop(aContinueBtn)
            horizontalBias = 0.1f; verticalBias = 0.35f
        }
    }

//    private fun AConstraintLayout.addBottomImg() {
//        aBottomImg.height = 146f
//        add(aBottomImg) { centerX(); bottomToBottom(); matchWidth() }
//    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------

    private fun animateTransition(newIndex: Int) {

        isAnimating = true

        val duration = 0.40f

        val startX = aContentImg.x
        val startY = aContentImg.y

        // центр для масштабування "в глибину"
        aContentImg.setOrigin(Align.center)

        aContentImg.addAction(
            Actions.sequence(

                // ── відліт вдаль: зменшується + блідне + трохи вгору ──
                Actions.parallel(
                    Actions.scaleTo(0.3f, 0.3f, duration, Interpolation.exp5In),
                    Actions.fadeOut(duration, Interpolation.fade),
                    Actions.moveBy(0f, 40f, duration, Interpolation.sineIn)
                ),

                // ── підміна картинки + підготовка нової "далеко" ──
                Actions.run {
                    aContentImg.drawable = TextureRegionDrawable(
                        gdxGame.assetsAll.listOnboarding[newIndex]
                    )
                    aContentImg.color.a = 0f
                    aContentImg.setScale(0.3f)
                    aContentImg.setPosition(startX, startY + 40f)
                },

                // ── приліт з глибини: збільшується + проявляється + опускається ──
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, duration, Interpolation.swingOut),
                    Actions.fadeIn(duration * 0.8f, Interpolation.fade),
                    Actions.moveTo(startX, startY, duration, Interpolation.sineOut)
                ),

                Actions.run { isAnimating = false }
            )
        )
    }

}