package com.rbxtreasure.fungamers.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxtreasure.fungamers.adsmodule.AdSizeManager
import com.rbxtreasure.fungamers.game.actors.button.AYellowButton
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.screens.selector.Selector_1_Screen
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.TIME_ANIM_SCREEN
import com.rbxtreasure.fungamers.game.utils.actor.animDelay
import com.rbxtreasure.fungamers.game.utils.actor.animHide
import com.rbxtreasure.fungamers.game.utils.actor.animShow
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.runGDX
import com.rbxtreasure.fungamers.util.log
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
    private val aBottomImg   by lazy { Image(gdxGame.assetsAll.BOTTOM_BROWN) }
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
        addBottomImg()
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
        aContentImg.setSize(456f, 747f)
        add(aContentImg) { centerX(); topToTop(margin = 12f) }
    }

    private fun AConstraintLayout.addBottomImg() {
        aBottomImg.height = 146f
        add(aBottomImg) { centerX(); bottomToBottom(); matchWidth() }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomImg) { marginBottom += screen.adBottomUI }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 51f)
        add(aContinueBtn) { centerX(); bottomToBottom(aBottomImg, margin = 41f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
            if (currentIndex == maxIndex) aContinueBtn.label.setText("START ADVENTURE")
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------

    private fun animateTransition(newIndex: Int) {
        isAnimating = true
        val duration = 0.35f

        // Стара картинка виїжджає вліво + зникає
        aContentImg.addAction(Actions.parallel(
            Actions.moveBy(-120f, 0f, duration, Interpolation.smooth),
            Actions.fadeOut(duration, Interpolation.smooth),
        ))

        // Після завершення — міняємо текстуру і заїжджаємо справа
        aContentImg.addAction(Actions.sequence(
            Actions.delay(duration),
            Actions.run {
                aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])
                aContentImg.color.a = 0f
                aContentImg.x += 240f  // справа від оригінальної позиції
            },
            Actions.parallel(
                Actions.moveBy(-120f, 0f, duration, Interpolation.smooth),
                Actions.fadeIn(duration, Interpolation.smooth),
            ),
            Actions.run { isAnimating = false }
        ))
    }

}