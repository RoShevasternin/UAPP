package com.rbxtreasure.fungamers.game.actors.panel.outfit

import com.rbxtreasure.fungamers.game.actors.ACard
import com.rbxtreasure.fungamers.game.actors.layout.AScrollLayout
import com.rbxtreasure.fungamers.game.actors.layout.autoLayout.AAutoLayout
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen

class APanelOutfit(override val screen: AdvancedScreen): AScrollLayout(screen, 8f, 20f) {

    override fun AAutoLayout.addContent() {}

    // показати лише передані картки
    fun showItems(items: List<ACard>) {
        verticalGroup.clearChildren()
        items.forEach { item ->
            item.setSize(344f, 94f)
            verticalGroup.add(item)
        }
    }
}