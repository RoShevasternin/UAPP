package com.treprosure.starbxup.game.actors.panel.daily

import com.treprosure.starbxup.game.actors.layout.AScrollLayout
import com.treprosure.starbxup.game.actors.layout.autoLayout.AAutoLayout
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.gdxGame

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