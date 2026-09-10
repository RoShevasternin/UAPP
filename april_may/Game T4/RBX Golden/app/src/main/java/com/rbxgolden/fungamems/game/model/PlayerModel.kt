package com.rbxgolden.fungamems.game.model

import com.rbxgolden.fungamems.game.data.PlayerData
import com.rbxgolden.fungamems.businesModule.economy.Econ
import com.rbxgolden.fungamems.game.dataStore.DS_Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
    // Баланс тут БІЛЬШЕ НЕ ЖИВЕ — тільки Wallet (businesModule/economy).
    // Було: PlayerData.rbx + addRbx/spendRbx/setRbx + boost. Своє сховище
    // балансу і Wallet розходяться мовчки, тому джерело правди одне.
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Daily Reward
    // ------------------------------------------------------------------------

    companion object {
        private const val IS_TEST_MODE = false

        private val DEFAULT_DAILY = intArrayOf(100, 200, 400, 800, 1600, 3200, 6400)

        private val DAY_MILLIS   = if (IS_TEST_MODE) 10_000L else 24 * 60 * 60 * 1000L
        private val RESET_MILLIS = if (IS_TEST_MODE) 20_000L else 48 * 60 * 60 * 1000L
    }

    val currentDailyRewardDay: Int
        get() = currentPlayer.dailyRewardDay

    val currentDailyRewardTime: Long
        get() = currentPlayer.dailyRewardTime

    // Суми днів їдуть списком з конфігу — economy.rewards_list.daily_reward.
    // Дефолт = сьогоднішні числа; довжину звіряє сам Econ.
    val listReward: List<Long>
        get() = Econ.rewardList("daily_reward", DEFAULT_DAILY).map { it.toLong() }

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