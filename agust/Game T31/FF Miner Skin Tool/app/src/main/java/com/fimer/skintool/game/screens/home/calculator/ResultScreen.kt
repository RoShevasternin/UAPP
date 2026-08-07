package com.fimer.skintool.game.screens.home.calculator

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.fimer.skintool.adsmodule.AdSizeManager
import com.fimer.skintool.game.actors.button.AYellowButton
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelTop
import com.fimer.skintool.game.actors.panel.calculator.APanelCalculator
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.actor.setSize
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_CALCULATOR_INDEX
import com.fimer.skintool.game.utils.runGDX
import kotlinx.coroutines.launch

class ResultScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop    by lazy { APanelTop(this) }
    private val aPanel  by lazy { Image(gdxGame.assetsAll.RESULT) }
    private val aLbl    by lazy { AMsdfLabel(gdxGame.msdfManager, gdxGame.msdfManager.fontNunitoSans_Black, APanelCalculator.GLOBAL_RESULT.toString(), 32f) }
    private val aBtn    by lazy { AYellowButton(this, "AGAIN") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addPanelCalculator()
        addBtn()
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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(344f, 32f)
        add(aTop) { centerX(); topToTop(margin = 16f) }

        val title = listOf(
            "BASIC",
            "NORMAL",
            "ADVANCE",
        )[GLOBAL_CALCULATOR_INDEX]

        aTop.setTitle("$title CALCULATOR")
    }

    private fun AConstraintLayout.addPanelCalculator() {
        aPanel.setSize(344f, 128f)
        add(aPanel) { center() }

        aLbl.setSize(85f, 48f)
        add(aLbl) { centerX(aPanel); bottomToBottom(aPanel, 24f) }
        aLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addBtn() {
        aBtn.setSize(344f, 50f)
        add(aBtn) { centerX(); bottomToBottom(margin = 32f); }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect {
            runGDX { update(aBtn) { marginBottom = screen.adBottomUI + 30f } }
        } }

        aBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.back() } }
    }

}