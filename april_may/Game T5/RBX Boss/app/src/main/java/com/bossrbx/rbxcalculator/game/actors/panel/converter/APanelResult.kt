package com.bossrbx.rbxcalculator.game.actors.panel.converter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.NumberFormatter
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APanelResult(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter12 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg        = Image(gdxGame.assetsAll.PANEL_CONVERTER_RESULT)
    private val aFromCurrencyLbl = Label("RBX", FontFactory.create(screen, parameter12, screen.fontGenerator_Light, GameColor.white_50))
    private val aToCurrencyLbl   = Label("Dollars", FontFactory.create(screen, parameter12, screen.fontGenerator_Light, GameColor.white_50))
    private val aFromValueLbl    = Label("1000", FontFactory.create(screen, parameter24, screen.fontGenerator_FIRENIGHT, Color.WHITE))
    private val aToValueLbl      = Label("0.73", FontFactory.create(screen, parameter24, screen.fontGenerator_FIRENIGHT, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }

        addCurrencyLbl()
        addValueLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCurrencyLbl() {
        addActor(aFromCurrencyLbl)
        aFromCurrencyLbl.setBounds(12f, 44f, 132f, 20f)

        addActor(aToCurrencyLbl)
        aToCurrencyLbl.setBounds(200f, 44f, 132f, 20f)
    }

    private fun addValueLbl() {
        addActor(aFromValueLbl)
        aFromValueLbl.setBounds(12f, 12f, 132f, 24f)

        addActor(aToValueLbl)
        aToValueLbl.setBounds(200f, 12f, 132f, 24f)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun calculate(value: Int) {
        val result = (value * GLOBAL_SELECTED_CONVERTER_TYPE.coff).toLong()

        aFromCurrencyLbl.setText(GLOBAL_SELECTED_CONVERTER_TYPE.fromCurrency)
        aToCurrencyLbl.setText(GLOBAL_SELECTED_CONVERTER_TYPE.toCurrency)

        aFromValueLbl.setText(NumberFormatter.format(value))
        aToValueLbl.setText(NumberFormatter.format(result))
    }

}