package com.diam.ondbit.game.actors.panel.outfit

import com.badlogic.gdx.math.Vector2
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle

class OutfitController<C>(
    private val screen     : AdvancedScreen,
    private val filterTabs : Map<C, AFilterTab>,
    private val panel      : APanelOutfit,
    private val labelStyleT: MsdfStyle,
    private val textureSize: Vector2,
    private val items      : List<OutfitItem<C>>,
    private val allCategory: C,                       // яка категорія = "показати все"
) {

    private data class Entry<C>(val data: OutfitItem<C>, val actor: ACard)
    private var entries = listOf<Entry<C>>()

    fun initialize() {
        entries = items.map { data ->
            Entry(data, ACard(screen, data.name, labelStyleT, data.texture, textureSize))
        }

        filterTabs.forEach { (category, tab) ->
            tab.onCheck = { checked -> if (checked) showCategory(category) }
        }

        filterTabs[allCategory]?.check()
    }

    private fun showCategory(category: C) {
        val filtered = if (category == allCategory) entries
        else entries.filter { it.data.category == category }

        panel.showItems(filtered.map { it.actor })
    }
}