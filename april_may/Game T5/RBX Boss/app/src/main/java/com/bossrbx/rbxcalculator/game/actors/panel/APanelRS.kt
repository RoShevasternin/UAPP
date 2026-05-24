package com.bossrbx.rbxcalculator.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.NumberFormatter
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRS(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aRSImg = Image(gdxGame.assetsAll.rs)
    private val aLbl   = Label("0", FontFactory.create(screen, parameter, screen.fontGenerator_FIRENIGHT, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addRSImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRSImg() {
        aRSImg.setSize(24f, 24f)
        add(aRSImg) { startToStart(); centerY() }
    }

    private fun addLbl() {
        aLbl.setSize(32f, 32f)
        add(aLbl) { startToEnd(aRSImg, margin = 8f); centerY() }

        collectRS()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun collectRS() {
        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rs ->
                runGDX { aLbl.setText(NumberFormatter.format(rs)) }
            }
        }
    }

}