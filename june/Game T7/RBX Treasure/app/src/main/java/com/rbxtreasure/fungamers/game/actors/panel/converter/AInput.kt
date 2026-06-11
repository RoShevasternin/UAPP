package com.rbxtreasure.fungamers.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.NumberFormatter
import com.rbxtreasure.fungamers.game.utils.actor.setFontColor
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame

class AInput(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLbl = Label("0", FontFactory.create(screen, parameter, screen.fontGenerator_AlanSans_Bold, GameColor.beige_E2CEAA))

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