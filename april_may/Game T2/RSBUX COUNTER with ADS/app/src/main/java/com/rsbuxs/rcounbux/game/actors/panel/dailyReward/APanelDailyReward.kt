package com.rsbuxs.rcounbux.game.actors.panel.dailyReward

import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class APanelDailyReward(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listItems = List(7) { AItemDailyReward(screen) }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy {
        DailyRewardController(
            scope = coroutine,
            model = gdxGame.modelPlayer,
            items = listItems
        )
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addItems()

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addItems() {
        var nx = 16f
        var ny = 274f
        listItems.forEachIndexed { index, item ->
            addActor(item)
            item.setBounds(nx, ny, 109f, 121f)

            nx += 9f + 109f
            if (index.inc() % 3 == 0) {
                nx = 16f
                ny -= 9f + 121f
            }
        }

    }

}