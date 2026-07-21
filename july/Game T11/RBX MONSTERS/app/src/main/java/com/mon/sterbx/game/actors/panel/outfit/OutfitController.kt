package com.mon.sterbx.game.actors.panel.outfit

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.util.log

class OutfitController<C>(
    private val screen     : AdvancedScreen,
    private val filterTabs : Map<C, AFilterTab>,
    private val panel      : APanelOutfit,
    private val labelStyleT: Label.LabelStyle,
    private val labelStyleD: Label.LabelStyle,
    private val textureSize: Vector2,
    private val items      : List<OutfitItem<C>>,
    private val allCategory: C,                       // яка категорія = "показати все"
) {

    private data class Entry<C>(val data: OutfitItem<C>, val actor: ACard)
    private var entries = listOf<Entry<C>>()

    fun initialize() {
        entries = items.map { data ->
            Entry(data, ACard(screen, data.name, data.desc, labelStyleT, labelStyleD, data.texture, textureSize))
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