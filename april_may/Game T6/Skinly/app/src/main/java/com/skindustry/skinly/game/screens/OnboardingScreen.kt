package com.skindustry.skinly.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.button.AOrangeButton
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.screens.selector.Selector_1_Screen
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import com.skindustry.skinly.util.log
import kotlinx.coroutines.launch
import kotlin.math.max

class OnboardingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var maxIndex = gdxGame.assetsAll.listOnboarding.lastIndex

    private var currentIndex = 0
        set(value) {
            gdxGame.activity.onFrontNavigation()
            aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[value])
            field = value
        }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    private val aNextBtn    by lazy { AOrangeButton(this, "Next") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addNextBtn()
        addContentImg()
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
        aContentImg.setSize(WIDTH, 560f)
        add(aContentImg) { centerX(); bottomToTop(aNextBtn, 24f) }
    }

    private fun AConstraintLayout.addNextBtn() {
        aNextBtn.setSize(344f, 56f)
        add(aNextBtn) { centerX(); bottomToBottom(margin = 24f) }

        aNextBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen {
                    gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name)
                }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aNextBtn) { marginBottom += screen.adBottomUI }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

}