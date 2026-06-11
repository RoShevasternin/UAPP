package com.skindustry.skinly.game.actors.panel.personalization

import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxGroup
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen

class APanelFilter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aHorizontal = AAutoLayout(
        screen          = screen,
        direction       = AAutoLayout.Direction.HORIZONTAL,
        alignMain       = AAutoLayout.AlignMain.CENTER,
        alignCross      = AAutoLayout.AlignCross.CENTER,
        sizingW         = AAutoLayout.Sizing.HUG,
        paddingStart    = 16f,
        paddingEnd      = 16f,
        gapMain         = 12f
    )
    private val aScroll = AScrollPane(aHorizontal, scrollX = true, scrollY = false)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val cbg = ACheckBoxGroup()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScroll()
        setUpHorizontal()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScroll() {
        add(aScroll) { fillParent() }
    }

    private fun setUpHorizontal() {
        aHorizontal.height = height
        aHorizontal.minW   = width
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private lateinit var aFirstFilterItem: AFilterItem

    fun setFilterItems(items: List<AFilterItem>) {
        items.forEach {
            it.setSize(1f, 36f)
            aHorizontal.add(it)

            it.setCheckBoxGroup(cbg)
        }

        aFirstFilterItem = items.first()
        aFirstFilterItem.check()

    }

    fun checkFirst() {
        aFirstFilterItem.check()
    }

}