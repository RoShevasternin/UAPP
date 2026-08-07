package com.racing.funtols.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.NumberFormatter
import com.racing.funtols.game.utils.actor.animHideAndDisable
import com.racing.funtols.game.utils.actor.animShowAndEnable
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.racing.funtols.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleAmount = MsdfStyle(msdf, msdf.fontPoppins_Medium, 14f, GameColor.white_77)
    private val styleResult = MsdfStyle(msdf, msdf.fontPoppins_Medium, 24f)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val currentTypeFlow = MutableStateFlow(GLOBAL_SELECTED_CONVERTER_TYPE)

    var inputValue  = 0
        private set

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput = {}

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelConverterImg = Image(gdxGame.assetsAll.PANEL_CONVERTER)
    private val aFromCurrencyLbl   = AMsdfLabel("Enter yor " + currentTypeFlow.value.fromCurrency, styleAmount)
    private val aInput             = AInput(screen)
    private val aToCurrencyLbl     = AMsdfLabel("Conversion " + currentTypeFlow.value.toCurrency, styleAmount)
    private val aResultLbl         = AMsdfLabel("0", styleResult)
    private val aSelectType        = APanelSelectConverter(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelCurrencyImg()
        addCurrencyLbl()
        addResultLbl()
        addInput()
        addSelect()

        collectConverterType()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelCurrencyImg() {
        aPanelConverterImg.setSize(344f, 262f)
        add(aPanelConverterImg) { centerX(); topToTop() }
    }

    private fun addCurrencyLbl() {
        aFromCurrencyLbl.setSize(344f, 20f)
        add(aFromCurrencyLbl) { centerX(); topToTop() }
        aFromCurrencyLbl.setAlignment(Align.center)

        aToCurrencyLbl.setSize(344f, 20f)
        add(aToCurrencyLbl) { centerX(); topToTop(margin = 169f) }
        aToCurrencyLbl.setAlignment(Align.center)
    }

    private fun addResultLbl() {
        aResultLbl.setSize(50f, 20f)
        add(aResultLbl) { centerX(); topToTop(margin = 219f) }
        aResultLbl.setAlignment(Align.center)
    }

    private fun addInput() {
        aInput.setSize(344f, 65f)
        add(aInput) { centerX(); topToTop(margin = 28f) }

        aInput.onInput = { value ->
            inputValue = value
            onInput()
        }
    }

    private fun addSelect() {
        aSelectType.setSize(344f, 241f)
        add(aSelectType) { centerX(); bottomToBottom() }

        aSelectType.onSelectType = { type -> currentTypeFlow.value = type }
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    private fun collectConverterType() {
        coroutine?.launch {
            currentTypeFlow.collect { type ->
                runGDX {
                    aFromCurrencyLbl.setText("Enter yor " + type.fromCurrency)
                    aToCurrencyLbl.setText("Conversion " + type.toCurrency)
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun calculate() {
        val result = (inputValue * GLOBAL_SELECTED_CONVERTER_TYPE.coff).toLong()
        aResultLbl.setText(NumberFormatter.format(result))
    }

}