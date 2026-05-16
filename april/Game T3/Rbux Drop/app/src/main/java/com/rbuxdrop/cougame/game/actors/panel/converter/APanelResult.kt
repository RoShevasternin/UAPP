package com.rbuxdrop.cougame.game.actors.panel.converter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbuxdrop.cougame.game.utils.NumberFormatter
import com.rbuxdrop.cougame.game.utils.actor.animShowAndEnable
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame

class APanelResult(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val parameter36 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(36)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg    = Image(gdxGame.assetsAll.PANEL_RESULT)
    private val aResultLbl   = ALabel(screen, "0.00", Color.WHITE, parameter36, screen.fontGenerator_Bold)
    private val aCurrencyLbl = ALabel(screen, "Dollars", Color.WHITE, parameter14, screen.fontGenerator_Medium)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }
        addResultLbl()
        addCurrencyLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addResultLbl() {
        aResultLbl.setSize(80f, 44f)
        add(aResultLbl) { centerX(); topToTop(margin = 12f) }
        aResultLbl.setAlignment(Align.center)
    }

    private fun addCurrencyLbl() {
        aCurrencyLbl.setSize(312f, 22f)
        add(aCurrencyLbl) { centerX(); bottomToBottom(margin = 12f) }
        aCurrencyLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun calculate(value: Int) {
        val result = value * GLOBAL_SELECTED_CONVERTER_TYPE.coff
        aResultLbl.setText(NumberFormatter.formatDollars(result))

        aCurrencyLbl.setText(GLOBAL_SELECTED_CONVERTER_TYPE.currency)
    }

}