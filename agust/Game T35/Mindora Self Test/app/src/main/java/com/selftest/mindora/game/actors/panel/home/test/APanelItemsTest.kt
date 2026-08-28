package com.selftest.mindora.game.actors.panel.home.test

import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APanelItemsTest(screen: AdvancedScreen): AAutoLayout(
    screen        = screen,
    direction     = Direction.VERTICAL,
    gapMain       = 16f,
    sizingH       = Sizing.HUG,
    alignCross    = AlignCross.CENTER,
) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 14f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTitleLbl = AMsdfLabel("Tests", styleDef)

    val aPanelCardsTest = APanelCardsTest(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTitleLbl()
        addPanelCardsTest()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTitleLbl() {
        aTitleLbl.setSize(344f, 17f)
        add(aTitleLbl)
    }

    private fun addPanelCardsTest() {
        aPanelCardsTest.setSize(344f, 1f)
        add(aPanelCardsTest)
    }

}