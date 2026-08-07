package com.diam.ondbit.game.actors.panel.converter

import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.NumberFormatter
import com.diam.ondbit.game.utils.actor.setOnClickListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame

class AInput(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 18f)

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

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun clearInput() {
        aLbl.setText("0")
    }

}