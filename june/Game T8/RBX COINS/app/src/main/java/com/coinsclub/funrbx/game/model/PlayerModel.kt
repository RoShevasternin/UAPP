package com.coinsclub.funrbx.game.model

import com.coinsclub.funrbx.game.state.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class PlayerModel(
    private val gameState: GameState,
    private val scope: CoroutineScope,
) {

    companion object {
        private const val IS_TEST_MODE = false
        private val DAY_MILLIS   = if (IS_TEST_MODE) 10_000L else 24 * 60 * 60 * 1000L
        private val RESET_MILLIS = if (IS_TEST_MODE) 60_000L else 48 * 60 * 60 * 1000L

        val LIST_REWARD  = listOf<Long>(100, 200, 400, 800, 1600, 3200, 6400)
    }

    // ------------------------------------------------------------------------
    // RBX
    // ------------------------------------------------------------------------
    val rbxFlow: StateFlow<Long> = gameState.rbxFlow

    fun addRbx(amount: Long)     { gameState.rbxFlow.value += amount }
    fun setRbx(amount: Long)     { gameState.rbxFlow.value  = amount }
    fun getRbx(): Long           = gameState.rbxFlow.value

    fun spendRbx(amount: Long): Boolean {
        if (gameState.rbxFlow.value < amount) return false
        gameState.rbxFlow.value -= amount
        return true
    }

    // ------------------------------------------------------------------------
    // Daily Reward
    // ------------------------------------------------------------------------
    val dailyRewardDayFlow : StateFlow<Int>  = gameState.dailyRewardDayFlow
    val dailyRewardTimeFlow: StateFlow<Long> = gameState.dailyRewardTimeFlow

    fun canClaimDailyReward(): Boolean {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return true
        return System.currentTimeMillis() - lastTime >= DAY_MILLIS - 1000L  // ← буфер 1с
    }

    fun dailyRewardRemainingSeconds(): Long {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return 0L
        val remainMillis = DAY_MILLIS - (System.currentTimeMillis() - lastTime)
        return remainMillis.coerceAtLeast(0L) / 1000L
    }

    fun claimDailyReward(): Long {
        validateDailyReward()
        if (!canClaimDailyReward()) return 0L

        val day    = gameState.dailyRewardDayFlow.value
        val reward = LIST_REWARD[day - 1]

        addRbx(reward)

        gameState.dailyRewardTimeFlow.value = System.currentTimeMillis()   // ← СПЕРШУ час
        gameState.dailyRewardDayFlow.value  = if (day >= 7) 1 else day + 1 // ← потім день (тригерить collect)

        return reward
    }

    fun validateDailyReward() {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return
        if (System.currentTimeMillis() - lastTime >= RESET_MILLIS) {
            gameState.dailyRewardDayFlow.value  = 1
            gameState.dailyRewardTimeFlow.value = 0L  // ← скидай і час!
        }
    }
}