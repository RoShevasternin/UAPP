package com.racing.funtols.game.actors.panel.outfit

import com.racing.funtols.game.actors.AScrollPane
import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.advanced.AdvancedScreen

class APanelOutfit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    private val aTable = AAutoLayout(
        screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        gapMain       = 8f,
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
            item.setSize(344f, 80f)
            aTable.add(item)
        }
    }
}