package com.bossrbx.rbxcalculator.game.actors.panel.converter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.NumberFormatter
import com.bossrbx.rbxcalculator.game.utils.actor.setFontColor
import com.bossrbx.rbxcalculator.game.utils.actor.setOnClickListener
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

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
    private val aInputImg   = Image(gdxGame.assetsAll.INPUT)
    private val aLbl        = Label("Enter Amount", FontFactory.create(screen, parameter, screen.fontGenerator_Medium, GameColor.white_25))
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
                val text = NumberFormatter.format(value)
                aLbl.setText(text)
                aLbl.setFontColor(Color.WHITE)

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
        aRestartBtn.setSize(60f, 60f)
        add(aRestartBtn) { endToEnd(margin = 2f); centerY() }
        aRestartBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(screen::class.java.name) }
        }
    }

}