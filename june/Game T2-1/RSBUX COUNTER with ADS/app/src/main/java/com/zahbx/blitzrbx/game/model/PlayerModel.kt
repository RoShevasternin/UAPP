package com.zahbx.blitzrbx.game.model

import com.zahbx.blitzrbx.businesModule.economy.Econ
import com.zahbx.blitzrbx.game.data.PlayerData
import com.zahbx.blitzrbx.game.dataStore.DS_Player
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
    // Було: PlayerData.rbx + addRbx/spendRbx/setRbx. Своє сховище балансу і
    // Wallet розходяться мовчки, тому джерело правди одне.
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Boost Mode — механіка апки (×2 до нагород), не сховище балансу.
    // Лишається тут; множник застосовується ДО Wallet.add у місці нарахування.
    // ------------------------------------------------------------------------
    var isBoostMode: Boolean = false

    fun boosted(amount: Int): Int = if (isBoostMode) amount * 2 else amount

    // ------------------------------------------------------------------------
    // Daily Reward
    // ------------------------------------------------------------------------

    companion object {
        private const val IS_TEST_MODE = false

        private val DAY_MILLIS   = if (IS_TEST_MODE) 10_000L else 24 * 60 * 60 * 1000L
        private val RESET_MILLIS = if (IS_TEST_MODE) 20_000L else 48 * 60 * 60 * 1000L
    }

    val currentDailyRewardDay: Int
        get() = currentPlayer.dailyRewardDay

    val currentDailyRewardTime: Long
        get() = currentPlayer.dailyRewardTime

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

        // Дефолт 5 за день = сьогоднішня поведінка (день × 5);
        // ключ economy.rewards.daily_reward_screen. Нараховує Wallet у контролері.
        val reward = boosted(currentDailyRewardDay * Econ.reward("daily_reward_screen", 5)).toLong()

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