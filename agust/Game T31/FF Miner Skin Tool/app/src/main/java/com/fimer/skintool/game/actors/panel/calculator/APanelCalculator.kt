package com.fimer.skintool.game.actors.panel.calculator

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.screens.home.calculator.ResultScreen
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_CALCULATOR_INDEX

class APanelCalculator(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    companion object {
        var GLOBAL_RESULT = 0L
            private set
    }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    var inputValue  = 0
        private set

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.INPUT)
    private val aInput = AInput(screen)


    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addInput()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addInput() {
        aInput.setSize(296f, 40f)
        add(aInput) { centerX(); bottomToBottom(margin = 24f) }

        aInput.onInput = { value ->
            inputValue = value
            onInput()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun calculate() {
        GLOBAL_RESULT = (inputValue * GLOBAL_CALCULATOR_INDEX.inc() * 1.235f).toLong()
        screen.animHideScreen { gdxGame.navigationManager.navigate(ResultScreen::class.java.name, screen::class.java.name) }
    }

}