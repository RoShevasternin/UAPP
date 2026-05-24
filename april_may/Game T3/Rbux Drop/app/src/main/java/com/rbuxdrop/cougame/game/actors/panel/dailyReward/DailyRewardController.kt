package com.rbuxdrop.cougame.game.actors.panel.dailyReward

import com.rbuxdrop.cougame.game.model.PlayerModel
import com.rbuxdrop.cougame.game.utils.actor.setOnClickListener
import com.rbuxdrop.cougame.game.utils.actor.setOnTouchListener
import com.rbuxdrop.cougame.util.log
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
        collectPlayer()
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    private fun collectPlayer() {

        scope?.launch {
            model.playerFlow.collect {
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
                if (day == model.currentDailyRewardDay && model.canClaimDailyReward()) {
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

        model.validateDailyReward()

        val currentDay = model.currentDailyRewardDay
        val canClaim   = model.canClaimDailyReward()

        items.forEachIndexed { index, item ->

            val day = index + 1
            item.setReward(day)

            when {

                // already claimed
                day < currentDay -> {
                    item.setState(
                        AItemDailyReward.DailyRewardState.CLAIMED
                    )
                }

                // current active day
                day == currentDay -> {

                    if (canClaim) {

                        item.setState(
                            AItemDailyReward.DailyRewardState.CLAIM
                        )

                    } else {

                        item.setState(
                            AItemDailyReward.DailyRewardState.LOCKED
                        )
                    }
                }

                // future days
                else -> {

                    item.setState(
                        AItemDailyReward.DailyRewardState.LOCKED
                    )
                }
            }
        }
    }
}