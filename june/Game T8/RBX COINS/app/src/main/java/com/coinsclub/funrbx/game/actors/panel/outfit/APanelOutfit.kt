package com.coinsclub.funrbx.game.actors.panel.outfit

import com.coinsclub.funrbx.game.actors.ACard
import com.coinsclub.funrbx.game.actors.AScrollPane
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen

class APanelOutfit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    private val aTable = AAutoLayout(screen,
        direction     = AAutoLayout.Direction.HORIZONTAL,
        wrap          = true,
        gapMain       = 6f,
        gapCross      = 6f,
        sizingH       = AAutoLayout.Sizing.HUG,
        paddingBottom = 20f,
    )
    private val scrollPane = AScrollPane(aTable)

    override fun addActorsOnGroup() {
        add(scrollPane) { fillParent() }

        aTable.width = width
        aTable.minH  = height
    }

    // показати лише передані картки
    fun showItems(items: List<ACard>) {
        aTable.clearChildren()
        items.forEach { item ->
            item.setSize(170f, 165f)
            aTable.add(item)
        }
    }
}