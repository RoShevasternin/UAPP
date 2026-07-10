package com.sakurbx.fungambx.game.actors.panel.outfit

import com.sakurbx.fungambx.game.actors.AScrollPane
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen

class APanelOutfit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    private val aTable = AAutoLayout(screen,
        direction     = AAutoLayout.Direction.HORIZONTAL,
        wrap          = true,
        gapMain       = 8f,
        gapCross      = 8f,
        sizingH       = AAutoLayout.Sizing.HUG,
        paddingBottom = 20f,
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
            item.setSize(168f, 168f)
            aTable.add(item)
        }
    }
}