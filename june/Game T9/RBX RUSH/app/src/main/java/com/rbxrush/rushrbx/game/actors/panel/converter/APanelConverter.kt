package com.rbxrush.rushrbx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.NumberFormatter
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    enum class Type { CONVERT_NOW, CONVERT_AGAIN }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterAmount = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val parameterResult = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ",")
        .setSize(56)

    private val lsAmount = FontFactory.create(screen, parameterAmount, screen.fontGenerator_Fredoka_Bold)
    private val lsResult = FontFactory.create(screen, parameterResult, screen.fontGenerator_Fredoka_Bold, GameColor.yellow_FACA4F)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val currentType = GLOBAL_SELECTED_CONVERTER_TYPE

    var inputValue  = 0
        private set

    var state = Type.CONVERT_NOW

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput = {}

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg           = Image(gdxGame.assetsAll.PANEL_CONVERTER)
    private val aFromCurrencyLbl = Label(currentType.fromCurrency + " amount", lsAmount)
    private val aToCurrencyLbl   = Label(currentType.toCurrency + " amount", lsAmount)
    private val aResultLbl       = Label("0", lsResult)
    private val aInput           = AInput(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addCurrencyLbl()
        addResultLbl()
        addInput()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCurrencyLbl() {
        aFromCurrencyLbl.setSize(344f, 15f)
        add(aFromCurrencyLbl) { startToStart(); topToTop() }
        aFromCurrencyLbl.setEllipsis(true)

        aToCurrencyLbl.setSize(344f, 15f)
        add(aToCurrencyLbl) { startToStart(); topToTop(margin = 95f) }
        aToCurrencyLbl.setEllipsis(true)
    }

    private fun addResultLbl() {
        aResultLbl.setSize(344f, 62f)
        add(aResultLbl) { centerX(); bottomToBottom(margin = 54f) }
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setEllipsis(true)
    }

    private fun addInput() {
        aInput.setSize(312f, 26f)
        add(aInput) { centerX(); topToTop(margin = 38f) }

        aInput.onInput = { value ->
            inputValue = value
            onInput()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun clearInput() {
        aResultLbl.setText("0")
        aInput.clearInput()
    }

    fun calculate() {
        val result = (inputValue * GLOBAL_SELECTED_CONVERTER_TYPE.coff).toLong()
        aResultLbl.setText(NumberFormatter.format(result))
    }

}