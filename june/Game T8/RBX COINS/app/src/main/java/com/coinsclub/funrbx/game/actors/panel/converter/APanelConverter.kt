package com.coinsclub.funrbx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.NumberFormatter
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    enum class Type { CONVERT_NOW, CLEAR }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterAmount = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
        .setBorderAndShadow()

    private val parameterResult = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ",")
        .setSize(54)
        .setBorderAndShadow()

    private val lsAmount = FontFactory.create(screen, parameterAmount, screen.fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3)
    private val lsResult = FontFactory.create(screen, parameterResult, screen.fontGenerator_LuckiestGuy_Regular, GameColor.yellow_DFA008)

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
    private val aFromCurrencyLbl = Label(currentType.fromCurrency + " AMOUNT", lsAmount)
    private val aToCurrencyLbl   = Label(currentType.toCurrency + " AMOUNT", lsAmount)
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
        aFromCurrencyLbl.setSize(100f, 14f)
        add(aFromCurrencyLbl) { startToStart(margin = 10f); topToTop(margin = 8f) }
        aFromCurrencyLbl.setEllipsis(true)

        aToCurrencyLbl.setSize(100f, 14f)
        add(aToCurrencyLbl) { startToStart(margin = 10f); topToTop(margin = 150f) }
        aToCurrencyLbl.setEllipsis(true)
    }

    private fun addResultLbl() {
        aResultLbl.setSize(115f, 29f)
        add(aResultLbl) { centerX(); bottomToBottom(margin = 78f) }
        aResultLbl.setAlignment(Align.center)
    }

    private fun addInput() {
        aInput.setSize(250f, 28f)
        add(aInput) { centerX(); topToTop(margin = 52f) }

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