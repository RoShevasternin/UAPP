package com.rbuxdrop.cougame.game.model

import com.rbuxdrop.cougame.businesModule.economy.Econ
import com.rbuxdrop.cougame.game.data.PlayerData
import com.rbuxdrop.cougame.game.dataStore.DS_Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlayerModel(
    private val ds   : DS_Player,
    private val scope: CoroutineScope
) {

    // ------------------------------------------------------------------------
    // Джерело правди
    // ------------------------------------------------------------------------
    val playerFlow: StateFlow<PlayerData> =
        ds.flow.stateIn(
            scope         = scope,
            started       = SharingStarted.Eagerly,
            initialValue  = ds.flow.value
        )

    val currentPlayer: PlayerData
        get() = playerFlow.value

    // ------------------------------------------------------------------------
    // Daily Reward
    // ------------------------------------------------------------------------

    companion object {
        private const val IS_TEST_MODE = false

        private val DAY_MILLIS   = if (IS_TEST_MODE) 10_000L else 24 * 60 * 60 * 1000L
        private val RESET_MILLIS = if (IS_TEST_MODE) 20_000L else 48 * 60 * 60 * 1000L

        private const val DAILY_REWARD_KEY = "daily_reward"
        private val DAILY_REWARD_DEF = intArrayOf(100, 200, 400, 800, 1600, 3200, 6400)
    }

    val currentDailyRewardDay: Int
        get() = currentPlayer.dailyRewardDay

    val currentDailyRewardTime: Long
        get() = currentPlayer.dailyRewardTime

    // Суми по днях — з Econ (ключ daily_reward), дефолт = сьогоднішня поведінка.
    // Підпис на плитці і нарахування беруться звідси ж — розбіжності не буде.
    val listReward: List<Long>
        get() = Econ.rewardList(DAILY_REWARD_KEY, DAILY_REWARD_DEF).map { it.toLong() }

    fun canClaimDailyReward(): Boolean {

        val lastTime = currentDailyRewardTime

        if (lastTime == 0L) return true

        val diff = System.currentTimeMillis() - lastTime

        return diff >= DAY_MILLIS
    }

    fun validateDailyReward() {

        val lastTime = currentDailyRewardTime

        if (lastTime == 0L) return

        val diff = System.currentTimeMillis() - lastTime

        // streak втрачено
        if (diff >= RESET_MILLIS) {

            ds.update { data ->
                data.copy(
                    dailyRewardDay = 1
                )
            }
        }
    }

    fun claimDailyReward(): Long {

        validateDailyReward()

        if (!canClaimDailyReward()) return 0L

        // Нараховує екран (Wallet.add з його bt/block), модель лише рахує streak
        val reward = listReward[currentDailyRewardDay - 1]

        val nextDay =
            if (currentDailyRewardDay >= 7) 1
            else currentDailyRewardDay + 1

        ds.update { data ->
            data.copy(
                dailyRewardDay  = nextDay,
                dailyRewardTime = System.currentTimeMillis()
            )
        }

        return reward
    }
}