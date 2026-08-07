package com.fimer.skintool.game.screens.home.calculator

import com.fimer.skintool.adsmodule.AdSizeManager
import com.fimer.skintool.game.actors.button.AYellowButton
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelTop
import com.fimer.skintool.game.actors.panel.calculator.APanelCalculator
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_CALCULATOR_INDEX
import com.fimer.skintool.game.utils.runGDX
import kotlinx.coroutines.launch

class CalculatorScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop    by lazy { APanelTop(this) }
    private val aPanel  by lazy { APanelCalculator(this) }
    private val aBtn    by lazy { AYellowButton(this, "CALCULATE") }

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
        aPanel.setSize(344f, 463f)
        add(aPanel) { centerX(); topToBottom(aTop, 24f); }
    }

    private fun AConstraintLayout.addBtn() {
        aBtn.setSize(344f, 50f)
        add(aBtn) { centerX(); bottomToBottom(margin = 32f); }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect {
            runGDX { update(aBtn) { marginBottom = screen.adBottomUI + 30f } }
        } }

        aBtn.setOnClickListener { aPanel.calculate() }
    }

}