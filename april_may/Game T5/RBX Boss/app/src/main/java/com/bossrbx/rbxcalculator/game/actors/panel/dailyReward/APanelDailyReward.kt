package com.bossrbx.rbxcalculator.game.actors.panel.dailyReward

import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame

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
        var nx = 0f
        var ny = 116f
        listItems.forEachIndexed { index, item ->
            addActor(item)
            item.setBounds(nx, ny, 80f, 108f)

            nx += 8f + 80f
            if (index.inc() % 4 == 0) {
                nx = 0f
                ny -= 8f + 108f
            }
        }

    }

}