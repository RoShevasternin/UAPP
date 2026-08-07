package com.fimer.skintool.game.controller

import com.fimer.skintool.game.actors.panel.daily.AItemDailyReward
import com.fimer.skintool.game.model.PlayerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DailyRewardController(
    private val scope: CoroutineScope?,
    private val model: PlayerModel,
    private val items: List<AItemDailyReward>,
) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onGetReward     : (Long) -> Unit = {}
    var onShowClaimState: () -> Unit     = {}   // фон CLAIM, таймер схований
    var onShowWaitState : (Long) -> Unit = {}   // фон WAIT, таймер показати й стартувати

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun initialize() {
        model.validateDailyReward()   // скинути цикл якщо пропущено >48год
        collectDay()                  // StateFlow одразу емітне → перший updateRewards()
    }

    // ------------------------------------------------------------------------
    // Claim (виклик з кліку по панелі)
    // ------------------------------------------------------------------------
    fun tryClaim() {
        if (!model.canClaimDailyReward()) return
        val reward = model.claimDailyReward()
        if (reward > 0L) onGetReward(reward)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    private fun collectDay() {
        scope?.launch {
            model.dailyRewardDayFlow.collect { updateRewards() }
        }
    }

    // ------------------------------------------------------------------------
    // UI
    // ------------------------------------------------------------------------
    private fun updateRewards() {
        val currentDay = model.dailyRewardDayFlow.value
        val canClaim   = model.canClaimDailyReward()
        val remaining  = model.dailyRewardRemainingSeconds()

        items.forEachIndexed { index, item ->
            val day = index + 1
            item.setReward(day)
            when {
                day <  currentDay -> item.setState(AItemDailyReward.DailyRewardState.CLAIMED)
                day == currentDay -> {
                    item.setState(
                        if (canClaim) AItemDailyReward.DailyRewardState.CLAIM
                        else          AItemDailyReward.DailyRewardState.LOCKED
                    )
                }
                else              -> item.setState(AItemDailyReward.DailyRewardState.LOCKED)
            }
        }

        // показуємо WAIT тільки якщо реально є що чекати
        if (canClaim || remaining <= 0L) onShowClaimState()
        else                             onShowWaitState(remaining)
    }
}