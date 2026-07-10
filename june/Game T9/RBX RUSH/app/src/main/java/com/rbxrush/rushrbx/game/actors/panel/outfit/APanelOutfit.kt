package com.rbxrush.rushrbx.game.actors.panel.outfit

import com.rbxrush.rushrbx.game.actors.AScrollPane
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen

class APanelOutfit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    private val aTable = AAutoLayout(screen,
        direction     = AAutoLayout.Direction.HORIZONTAL,
        wrap          = true,
        gapMain       = 8f,
        gapCross      = 8f,
        sizingH       = AAutoLayout.Sizing.HUG,
        paddingBottom = 40f,
    )
    private val scrollPane = AScrollPane(aTable)

    override fun addActorsOnGroup() {
        add(scrollPane) { fillParent() }

        aTable.width = width
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aTable.minH = height
    }

    // показати лише передані картки
    fun showItems(items: List<ACard>) {
        aTable.clearChildren()
        items.forEach { item ->
            item.setSize(109f, 137f)
            aTable.add(item)
        }
    }
}