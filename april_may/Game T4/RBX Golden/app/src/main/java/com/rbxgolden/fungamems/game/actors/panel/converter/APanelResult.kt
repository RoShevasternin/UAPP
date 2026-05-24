package com.rbxgolden.fungamems.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.NumberFormatter
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame

class APanelResult(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter48 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setBorder(2f, GameColor.orange_FE)
        .setSize(48)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg  = Image(gdxGame.assetsAll.PANEL_RESULT)
    private val aResultLbl = Label("0.00", FontFactory.create(screen, parameter48, screen.fontGenerator_Bold, GameColor.yellow_FF))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }
        addResultLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addResultLbl() {
        aResultLbl.setSize(97f, 72f)
        add(aResultLbl) { endToEnd(margin = 90f); topToTop(margin = 50f) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun calculate(value: Int) {
        val result = value * GLOBAL_SELECTED_CONVERTER_TYPE.coff
        aResultLbl.setText(NumberFormatter.formatDollars(result))
    }

}