package com.selftest.mindora.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.Actor
import com.selftest.mindora.game.actors.AScrollPane
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVertical = AAutoLayout(
        screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        gapMain       = 24f,
        sizingH       = AAutoLayout.Sizing.HUG,
        alignCross    = AAutoLayout.AlignCross.CENTER,
        paddingBottom = 24f,
    )
    private val aScrollPane = AScrollPane(aVertical)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        setupVerticalGroup()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aVertical.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun setupVerticalGroup() {
        aVertical.width = width
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun addItem(item: Actor) {
        aVertical.add(item)
    }

}