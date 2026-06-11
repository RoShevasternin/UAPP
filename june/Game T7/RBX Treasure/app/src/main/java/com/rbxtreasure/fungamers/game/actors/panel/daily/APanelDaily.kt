package com.rbxtreasure.fungamers.game.actors.panel.daily

import com.rbxtreasure.fungamers.game.actors.layout.AScrollLayout
import com.rbxtreasure.fungamers.game.actors.layout.autoLayout.AAutoLayout
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame

class APanelDaily(override val screen: AdvancedScreen): AScrollLayout(screen, 8f, 300f) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listItems = List(7) { AItemDailyReward(screen) }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onGetReward: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy {
        DailyRewardController(
            scope = coroutine,
            model = gdxGame.modelPlayer,
            items = listItems,
        )
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun AAutoLayout.addContent() {
        addItems()

        controller.onGetReward = { onGetReward(it) }
        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.addItems() {
        listItems.forEach { item ->
            item.setSize(343f, 72f)
            add(item)
        }

    }

}