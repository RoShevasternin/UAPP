package com.selftest.mindora.game.actors.panel.home.main

import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen

class APanelItemsMain(screen: AdvancedScreen): AAutoLayout(
    screen        = screen,
    direction     = Direction.VERTICAL,
    gapMain       = 8f,
    sizingH       = Sizing.HUG,
    alignCross    = AlignCross.CENTER,
) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val aPanelItemPortrait = APanelItemPortrait(screen)
    val aPanelItemDaily    = APanelItemDaily(screen)
    val aPanelItemInsight  = APanelItemInsight(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelItemPortrait()
        addPanelItemDaily()
        addPanelItemInsight()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelItemPortrait() {
        aPanelItemPortrait.setSize(344f, 103f)
        add(aPanelItemPortrait)
    }

    private fun addPanelItemDaily() {
        aPanelItemDaily.setSize(344f, 138f)
        add(aPanelItemDaily)
    }

    private fun addPanelItemInsight() {
        aPanelItemInsight.setSize(344f, 88f)
        add(aPanelItemInsight)
    }

}