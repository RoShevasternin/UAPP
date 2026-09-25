package com.racing.funtols.game.model

import com.racing.funtols.businesModule.economy.Econ
import com.racing.funtols.game.state.GameState
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
        // Дефолт = сьогоднішні числа; довжину звіряє сам Econ.
        private val DEFAULT_DAILY = intArrayOf(100, 200, 300, 400, 500, 600, 700)
        val LIST_REWARD: List<Long>
            get() = Econ.rewardList("daily_reward", DEFAULT_DAILY).map { it.toLong() }
    }

    // ------------------------------------------------------------------------
    // Баланс тут БІЛЬШЕ НЕ ЖИВЕ — тільки Wallet (businesModule/economy).
    // Було: GameState.rbxFlow + addRbx/setRbx/getRbx/spendRbx. Своє сховище
    // балансу і Wallet розходяться мовчки, тому джерело правди одне.
    // ------------------------------------------------------------------------

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