package com.treprosure.starbxup.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.treprosure.starbxup.game.actors.button.AYellowButton
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.NumberFormatter
import com.treprosure.starbxup.game.utils.actor.setOnTouchListener
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame
import com.treprosure.starbxup.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    enum class Type { COUNT_NOW, CLEAR }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter12 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)
    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val currentType = GLOBAL_SELECTED_CONVERTER_TYPE
    private var inputValue  = 0

    private var state = Type.COUNT_NOW

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg           = Image(gdxGame.assetsAll.PANEL_CONVERTER)
    private val aFromCurrencyLbl = Label(currentType.fromCurrency + " AMOUNT", FontFactory.create(screen, parameter12, screen.fontGenerator_AlanSans_Bold, GameColor.yellow_DDA334))
    private val aToCurrencyLbl   = Label(currentType.toCurrency + " AMOUNT", FontFactory.create(screen, parameter12, screen.fontGenerator_AlanSans_Bold, GameColor.yellow_DDA334))
    private val aToValueLbl      = Label("", FontFactory.create(screen, parameter24, screen.fontGenerator_AlanSans_Bold, GameColor.yellow_DDA334))
    private val aInput           = AInput(screen)
    private val aBtn             = AYellowButton(screen, "COUNT NOW")

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addCurrencyLbl()
        addValueLbl()
        addInput()
        addBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCurrencyLbl() {
        aFromCurrencyLbl.setSize(74f, 14f)
        add(aFromCurrencyLbl) { centerX(); topToTop(margin = 32f) }

        aToCurrencyLbl.setSize(74f, 14f)
        add(aToCurrencyLbl) { centerX(); topToTop(margin = 191f) }
    }

    private fun addValueLbl() {
        aToValueLbl.setSize(115f, 29f)
        add(aToValueLbl) { centerX(); topToTop(margin = 217f) }
        aToValueLbl.setAlignment(Align.center)
    }

    private fun addInput() {
        aInput.setSize(312f, 29f)
        add(aInput) { centerX(); topToTop(margin = 58f) }

        aInput.onInput = { value ->
            inputValue = value
            aBtn.enable()
        }
    }

    private fun addBtn() {
        aBtn.setSize(312f, 51f)
        add(aBtn) { centerX(); bottomToBottom(margin = 16f) }
        aBtn.disable()

        aBtn.setOnTouchListener {
            state = when(state) {
                Type.COUNT_NOW -> {
                    aBtn.label.setText("CLEAR")
                    Type.CLEAR
                }
                Type.CLEAR -> {
                    aBtn.label.setText("COUNT NOW")
                    aBtn.disable()

                    aInput.clearInput()
                    aToValueLbl.setText("0")

                    Type.COUNT_NOW
                }
            }
            calculate(inputValue)
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    private fun calculate(value: Int) {
        val result = (value * GLOBAL_SELECTED_CONVERTER_TYPE.coff).toLong()

        aToValueLbl.setText(NumberFormatter.format(result))
    }

}