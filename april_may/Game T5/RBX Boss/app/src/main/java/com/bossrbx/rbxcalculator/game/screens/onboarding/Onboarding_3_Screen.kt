package com.bossrbx.rbxcalculator.game.screens.onboarding

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.adsmodule.AdSizeManager
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.screens.MainScreen
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.runGDX
import kotlinx.coroutines.launch

class Onboarding_3_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aContentImg by lazy { Image(gdxGame.assetsAll.listOnb[2]) }
    private val aNextBtn    by lazy { ABlueButton(this, "NEXT") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.listOnboarding[2])

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

    private fun AConstraintLayout.addNextBtn() {
        aNextBtn.setSize(344f, 64f)
        add(aNextBtn) { centerX(); bottomToBottom(margin = 24f) }

        aNextBtn.setOnClickListener {
            animHideScreen {
                gdxGame.navigationManager.navigate(MainScreen::class.java.name, Onboarding_3_Screen::class.java.name)
            }
        }

        coroutine?.launch {
            AdSizeManager.bannerFlow.collect {
                runGDX {
                    if (safeBannerUI >= 0f) update(aNextBtn) {
                        marginBottom += screen.safeBannerUI
                    }
                }
            }
        }
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(344f, 236f)
        add(aContentImg) { centerX(); bottomToTop(aNextBtn, 24f) }
    }

}