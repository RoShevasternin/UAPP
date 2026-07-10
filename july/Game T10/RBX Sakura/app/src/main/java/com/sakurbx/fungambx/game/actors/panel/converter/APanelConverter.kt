package com.sakurbx.fungambx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.NumberFormatter
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    enum class Type { CONVERT_NOW, CLEAR }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterAmount = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    private val parameterResult = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ",")
        .setSize(32)

    private val lsAmount = FontFactory.create(screen, parameterAmount, screen.fontGenerator_Laila_Bold)
    private val lsResult = FontFactory.create(screen, parameterResult, screen.fontGenerator_Laila_Bold)

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
    private val aFromCurrencyLbl = Label(currentType.fromCurrency, lsAmount)
    private val aToCurrencyLbl   = Label(currentType.toCurrency, lsAmount)
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
        aFromCurrencyLbl.setSize(106f, 15f)
        add(aFromCurrencyLbl) { centerX(); topToTop(margin = 8f) }
        aFromCurrencyLbl.setEllipsis(true)
        aFromCurrencyLbl.setAlignment(Align.center)

        aToCurrencyLbl.setSize(106f, 15f)
        add(aToCurrencyLbl) { centerX(); bottomToBottom(margin = 109f) }
        aToCurrencyLbl.setEllipsis(true)
        aToCurrencyLbl.setAlignment(Align.center)
    }

    private fun addResultLbl() {
        aResultLbl.setSize(332f, 31f)
        add(aResultLbl) { centerX(); bottomToBottom(margin = 36f) }
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setEllipsis(true)
    }

    private fun addInput() {
        aInput.setSize(332f, 31f)
        add(aInput) { centerX(); topToTop(margin = 46f) }

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