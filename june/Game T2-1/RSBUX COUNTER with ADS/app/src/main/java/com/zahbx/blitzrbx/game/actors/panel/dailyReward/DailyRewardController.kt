package com.zahbx.blitzrbx.game.actors.panel.dailyReward

import com.zahbx.blitzrbx.businesModule.backend.Bt
import com.zahbx.blitzrbx.businesModule.backend.Events
import com.zahbx.blitzrbx.businesModule.economy.Wallet
import com.zahbx.blitzrbx.game.model.PlayerModel
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
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

                    // claimDailyReward тепер лише рахує суму й рухає streak —
                    // монети нараховує Wallet (він же шле coins_earned).
                    val reward = model.claimDailyReward()
                    if (reward > 0) {
                        Wallet.add(reward.toInt(), bt = Bt.DAILY, block = BLOCK)
                        Events.featureComplete(bt = Bt.DAILY, block = BLOCK, amount = reward.toInt())
                    }
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

    companion object {
        private const val BLOCK = "daily_reward_screen"
    }
}
