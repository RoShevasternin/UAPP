package com.rsbuxs.rcounbux.game.actors.panel.n_to_rbx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.screens.main.NtoRBXScreen
import com.rsbuxs.rcounbux.game.utils.GLOBAL_SELECTED_RBX_CALCULATOR_TITLE
import com.rsbuxs.rcounbux.game.utils.GameColor
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import com.rsbuxs.rcounbux.game.utils.gdxGame

class AFieldEdit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aFieldImg  = Image(gdxGame.assetsAll.field_edit)
    private val aLbl       = ALabel(screen, "Enter here", GameColor.white_25, parameter, screen.fontGenerator_Medium)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput: (Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addFieldImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addFieldImg() {
        add(aFieldImg) { fillParent() }
    }

    private fun addLbl() {
        add(aLbl) { fillParent() }

        aLbl.setAlignment(Align.center)
        aLbl.setOnClickListener {
            gdxGame.activity.showInput { value ->
                val text = value.toString()
                aLbl.setText(text)
                aLbl.setLabelColor(Color.WHITE)

                onInput(value)
            }
        }
    }

}