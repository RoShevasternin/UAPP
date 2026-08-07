package com.diam.ondbit.game.actors.panel.outfit

import com.diam.ondbit.game.actors.AScrollPane
import com.diam.ondbit.game.actors.layout.autoLayout.AAutoLayout
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.advanced.AdvancedScreen

class APanelOutfit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    private val aTable = AAutoLayout(
        screen,
        direction     = AAutoLayout.Direction.HORIZONTAL,
        wrap          = true,
        sizingH       = AAutoLayout.Sizing.HUG,
        gapMain       = 8f,
        gapCross      = 8f,
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
            item.setSize(168f, 206f)
            aTable.add(item)
        }
    }
}