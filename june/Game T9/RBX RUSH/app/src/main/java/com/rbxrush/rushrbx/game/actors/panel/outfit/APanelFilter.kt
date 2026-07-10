package com.rbxrush.rushrbx.game.actors.panel.outfit

import com.rbxrush.rushrbx.game.actors.AScrollPane
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen

class APanelFilter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aHorizontal = AAutoLayout(
        screen       = screen,
        direction    = AAutoLayout.Direction.HORIZONTAL,
        alignCross   = AAutoLayout.AlignCross.CENTER,
        sizingW      = AAutoLayout.Sizing.HUG,
        gapMain      = 4f,
        paddingStart = 16f,
        paddingEnd   = 16f,
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

    fun setListFilterTab(items: List<AFilterTab>) {
        items.forEach {
            //it.setSize(1f, 33f)
            aHorizontal.add(it)
            it.setCheckBoxGroup(cbg)
        }
    }

}