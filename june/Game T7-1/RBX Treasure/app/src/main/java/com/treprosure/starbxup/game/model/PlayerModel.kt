package com.treprosure.starbxup.game.model

import com.treprosure.starbxup.businesModule.economy.Econ
import com.treprosure.starbxup.game.state.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class PlayerModel(
    private val gameState: GameState,
    private val scope: CoroutineScope,
) {

    companion object {
        private const val IS_TEST_MODE = false
        private val DAY_MILLIS   = if (IS_TEST_MODE) 5_000L else 24 * 60 * 60 * 1000L
        private val RESET_MILLIS = if (IS_TEST_MODE) 60_000L else 48 * 60 * 60 * 1000L

        // Суми днів їдуть списком з конфігу — economy.rewards_list.daily_reward.
        private val DEFAULT_DAILY = intArrayOf(100, 200, 400, 800, 1600, 3200, 6400)
        val LIST_REWARD: List<Long>
            get() = Econ.rewardList("daily_reward", DEFAULT_DAILY).map { it.toLong() }
    }

    // ------------------------------------------------------------------------
    // RBX
    // ------------------------------------------------------------------------
    // Баланс тут БІЛЬШЕ НЕ ЖИВЕ — тільки Wallet (businesModule/economy).
    // Було: GameState.rbxFlow + addRbx/setRbx/getRbx/spendRbx. Своє сховище
    // балансу і Wallet розходяться мовчки, тому джерело правди одне.

    // ------------------------------------------------------------------------
    // Daily Reward
    // ------------------------------------------------------------------------
    val dailyRewardDayFlow : StateFlow<Int>  = gameState.dailyRewardDayFlow
    val dailyRewardTimeFlow: StateFlow<Long> = gameState.dailyRewardTimeFlow

    fun canClaimDailyReward(): Boolean {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return true
        return System.currentTimeMillis() - lastTime >= DAY_MILLIS
    }

    fun claimDailyReward(): Long {
        validateDailyReward()
        if (!canClaimDailyReward()) return 0L

        val day    = gameState.dailyRewardDayFlow.value
        val reward = LIST_REWARD[day - 1]

        gameState.dailyRewardDayFlow.value  = if (day >= 7) 1 else day + 1
        gameState.dailyRewardTimeFlow.value = System.currentTimeMillis()

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