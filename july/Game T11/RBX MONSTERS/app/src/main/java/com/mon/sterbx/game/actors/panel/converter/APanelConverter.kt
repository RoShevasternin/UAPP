package com.mon.sterbx.game.actors.panel.converter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.NumberFormatter
import com.mon.sterbx.game.utils.actor.animHideAndDisable
import com.mon.sterbx.game.utils.actor.animShowAndEnable
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    enum class Type { CONVERT_NOW, CLEAR }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterAmount = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val parameterResult = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + " ")
        .setSize(32)

    private val lsAmount = FontFactory.create(screen, parameterAmount, screen.fontGenerator_BeVietnamPro_BlackItalic, Color.BLACK)
    private val lsResult = FontFactory.create(screen, parameterResult, screen.fontGenerator_BeVietnamPro_BlackItalic, Color.BLACK)

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
    private val aFromCurrencyLbl = Label("YOU PAY: " + currentType.fromCurrency, lsAmount)
    private val aInput           = AInput(screen)
    private val aPanelResult     = APanelConverterResult(screen, lsAmount, lsResult, "YOU GET: " + currentType.toCurrency)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addCurrencyLbl()
        addPanelResult()
        addInput()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCurrencyLbl() {
        aFromCurrencyLbl.setSize(89f, 15f)
        add(aFromCurrencyLbl) { startToStart(margin = 17f); topToTop(margin = 10f) }
    }

    private fun addPanelResult() {
        aPanelResult.setSize(344f, 206f)
        add(aPanelResult) { centerX(); topToBottom(margin = 24f) }
        aPanelResult.animHideAndDisable()
    }

    private fun addInput() {
        aInput.setSize(315f, 44f)
        add(aInput) { centerX(); topToTop(margin = 42f) }

        aInput.onInput = { value ->
            inputValue = value
            aPanelResult.animShowAndEnable(0.25f)
            onInput()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun clearInput() {
        aPanelResult.aResultLbl.setText("0")
        aInput.clearInput()
    }

    fun calculate() {
        val result = (inputValue * GLOBAL_SELECTED_CONVERTER_TYPE.coff).toLong()
        aPanelResult.aResultLbl.setText(NumberFormatter.format(result))
    }

}