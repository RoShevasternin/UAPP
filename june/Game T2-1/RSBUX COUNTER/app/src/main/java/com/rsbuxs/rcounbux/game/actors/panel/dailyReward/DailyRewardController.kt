package com.rsbuxs.rcounbux.game.actors.panel.dailyReward

import com.rsbuxs.rcounbux.game.model.PlayerModel
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DailyRewardController(
    private val scope : CoroutineScope?,
    private val model : PlayerModel,
    private val items : List<AItemDailyReward>,
) {

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

            item.setOnClickListener {

                if (
                    day == model.currentDailyRewardDay &&
                    model.canClaimDailyReward()
                ) {

                    model.claimDailyReward()
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