package com.fimer.skintool.game.actors.panel.calculator

import com.badlogic.gdx.utils.Align
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.GameColor
import com.fimer.skintool.game.utils.NumberFormatter
import com.fimer.skintool.game.utils.actor.setOnClickListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame

class AInput(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoSans_Bold, 14f, GameColor.black_0F0F0F)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLbl = AMsdfLabel("0", styleDef)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput: (Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLbl()

        setOnClickListener {
            gdxGame.activity.showInput { value ->
                val text = NumberFormatter.format(value)
                aLbl.setText(text)

                onInput(value)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLbl() {
        add(aLbl) { fillParent() }
        aLbl.setAlignment(Align.center)
    }

}