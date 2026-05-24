package com.rbxgolden.fungamems.game.actors.panel.dailyReward

import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class APanelDailyReward(override val screen: AdvancedScreen): AdvancedGroup() {

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
    override fun addActorsOnGroup() {
        addItems()

        controller.onGetReward = { onGetReward(it) }
        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addItems() {
        //var nx = 16f
        var ny = 516f
        listItems.forEachIndexed { index, item ->
            addActor(item)
            item.setBounds(16f, ny, 344f, 72f)

            ny -= 12f + 72f
//            if (index.inc() % 3 == 0) {
//                nx = 16f
//                ny -= 9f + 121f
//            }
        }

    }

}