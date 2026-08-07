package com.diam.ondbit.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.NumberFormatter
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class APanelConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    //private val styleAmount = MsdfStyle(msdf, msdf.fontPoppins_Medium, 14f, GameColor.white_77)
    private val styleResult = MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 24f)

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
    //private val aFromCurrencyLbl   = AMsdfLabel("Enter yor " + currentTypeFlow.value.fromCurrency, styleAmount)
    private val aInput             = AInput(screen)
    //private val aToCurrencyLbl     = AMsdfLabel("Conversion " + currentTypeFlow.value.toCurrency, styleAmount)
    private val aResultLbl         = AMsdfLabel("0", styleResult)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelConverterImg()
        addCurrencyLbl()
        addResultLbl()
        addInput()

        collectConverterType()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelConverterImg() {
        add(aPanelConverterImg) { fillParent() }
    }

    private fun addCurrencyLbl() {
//        aFromCurrencyLbl.setSize(344f, 20f)
//        add(aFromCurrencyLbl) { centerX(); topToTop() }
//        aFromCurrencyLbl.setAlignment(Align.center)
//
//        aToCurrencyLbl.setSize(344f, 20f)
//        add(aToCurrencyLbl) { centerX(); topToTop(margin = 169f) }
//        aToCurrencyLbl.setAlignment(Align.center)
    }

    private fun addResultLbl() {
        aResultLbl.setSize(65f, 24f)
        add(aResultLbl) { centerX(); bottomToBottom(margin = 85f) }
        aResultLbl.setAlignment(Align.center)
    }

    private fun addInput() {
        aInput.setSize(274f, 42f)
        add(aInput) { centerX(); topToTop(margin = 40f) }

        aInput.onInput = { value ->
            inputValue = value
            onInput()
        }
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    private fun collectConverterType() {
        coroutine?.launch {
            currentTypeFlow.collect { type ->
                runGDX {
                    //aFromCurrencyLbl.setText("Enter yor " + type.fromCurrency)
                    //aToCurrencyLbl.setText("Conversion " + type.toCurrency)
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