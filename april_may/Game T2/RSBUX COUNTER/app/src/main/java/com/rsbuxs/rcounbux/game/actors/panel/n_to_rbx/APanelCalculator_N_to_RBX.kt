package com.rsbuxs.rcounbux.game.actors.panel.n_to_rbx

import com.rsbuxs.rcounbux.game.actors.button.AGreenGrayButton
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.actor.animHideAndDisable
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen

class APanelCalculator_N_to_RBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aFieldEdit    = AFieldEdit(screen)
    private val aGreenGrayBtn = AGreenGrayButton(screen)
    private val aPanelResult  = APanelResult(screen)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var inputValue = 0

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addFieldEdit()
        addGreenGrayBtn()
        addPanelResult()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addFieldEdit() {
        aFieldEdit.setSize(344f, 60f)
        add(aFieldEdit) {
            centerX()
            topToTop()
        }

        aFieldEdit.onInput = {
            inputValue = it
            aGreenGrayBtn.enable()
        }
    }

    private fun addGreenGrayBtn() {
        aGreenGrayBtn.setSize(344f, 60f)
        add(aGreenGrayBtn) {
            centerX()
            topToBottom(aFieldEdit, 16f)
        }

        aGreenGrayBtn.disable()

        aGreenGrayBtn.setOnClickListener {
            aGreenGrayBtn.disable()
            aPanelResult.calculateAndShow(inputValue)
        }
    }

    private fun addPanelResult() {
        aPanelResult.animHideAndDisable()
        aPanelResult.setSize(346f, 179f)
        add(aPanelResult) {
            centerX()
            topToBottom(aGreenGrayBtn, 16f)
        }
    }

}