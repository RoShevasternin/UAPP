package com.treprosure.starbxup.game.actors.panel.daily

import com.treprosure.starbxup.game.model.PlayerModel
import com.treprosure.starbxup.game.utils.actor.setOnTouchListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DailyRewardController(
    private val scope : CoroutineScope?,
    private val model : PlayerModel,
    private val items : List<AItemDailyReward>,
) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onGetReward: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun initialize() {
        bindClicks()
        collectDay()
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    private fun collectDay() {
        scope?.launch {
            model.dailyRewardDayFlow.collect {
                updateRewards()
            }
        }
    }

    // ------------------------------------------------------------------------
    // Clicks
    // ------------------------------------------------------------------------
    private fun bindClicks() {
        items.forEachIndexed { index, item ->

            val day = index + 1

            item.setOnTouchListener {
                if (day == model.dailyRewardDayFlow.value && model.canClaimDailyReward()) {
                    val reward = model.claimDailyReward()
                    onGetReward(reward)
                }
            }

        }
    }

    // ------------------------------------------------------------------------
    // UI
    // ------------------------------------------------------------------------
    private fun updateRewards() {
        val currentDay = model.dailyRewardDayFlow.value
        val canClaim   = model.canClaimDailyReward()

        items.forEachIndexed { index, item ->
            val day = index + 1
            item.setReward(day)

            when {
                day < currentDay  -> item.setState(AItemDailyReward.DailyRewardState.CLAIMED)

                day == currentDay -> {
                    if (canClaim) item.setState(AItemDailyReward.DailyRewardState.CLAIM)
                    else          item.setState(AItemDailyReward.DailyRewardState.LOCKED)
                }

                else -> item.setState(AItemDailyReward.DailyRewardState.LOCKED)
            }
        }
    }
}