package com.rbuxdrop.cougame.game.actors.panel.converter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.NumberFormatter
import com.rbuxdrop.cougame.game.utils.actor.setOnClickListener
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame

class AInput(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aInputImg   = Image(gdxGame.assetsAll.input)
    private val aLbl        = ALabel(screen, "Enter here", GameColor.white_25, parameter, screen.fontGenerator_Medium)
    private val aRestartBtn = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput: (Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addInputImg()
        addLbl()
        addRestartBtn()

        setOnClickListener {
            gdxGame.activity.showInput { value ->
                val text = NumberFormatter.formatDollars(value.toDouble())
                aLbl.setText(text)
                aLbl.setLabelColor(Color.WHITE)

                onInput(value)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addInputImg() {
        add(aInputImg) { fillParent() }
    }

    private fun addLbl() {
        aLbl.setSize(104f, 22f)
        add(aLbl) { startToStart(margin = 12f); centerY() }
    }

    private fun addRestartBtn() {
        aRestartBtn.setSize(42f, 42f)
        add(aRestartBtn) { endToEnd(margin = 11f); centerY() }
        aRestartBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(screen::class.java.name) }
        }
    }

}