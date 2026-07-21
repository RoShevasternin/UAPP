package com.mon.sterbx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.NumberFormatter
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelConverterResult(
    override val screen: AdvancedScreen,
    lsAmount: Label.LabelStyle,
    lsResult: Label.LabelStyle,
    toCurrency: String,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg         = Image(gdxGame.assetsAll.PANEL_CONVERTER_2)
    private val aToCurrencyLbl = Label(toCurrency, lsAmount)

    val aResultLbl = Label("0", lsResult)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addCurrencyLbl()
        addResultLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCurrencyLbl() {
        aToCurrencyLbl.setSize(89f, 15f)
        add(aToCurrencyLbl) { startToStart(margin = 17f); bottomToBottom(margin = 110f) }
    }

    private fun addResultLbl() {
        aResultLbl.setSize(332f, 31f)
        add(aResultLbl) { centerX(); bottomToBottom(margin = 37f) }
        aResultLbl.setAlignment(Align.center)
    }

}