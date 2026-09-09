package com.bossrbx.rbxcalculator.game.model

import com.bossrbx.rbxcalculator.businesModule.economy.Econ
import com.bossrbx.rbxcalculator.game.data.PlayerData
import com.bossrbx.rbxcalculator.game.dataStore.DS_Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// ═══════════════════════════════════════════════════════════════════════════
// ⚠️ БАЛАНСУ ТУТ БІЛЬШЕ НЕМАЄ (правка 5).
//
// Єдина точка мутації монет на весь застосунок — businesModule/economy/Wallet:
// він же сам шле coins_earned / coins_spent. Своє сховище балансу поруч з ним
// означало б два джерела правди і дірки в звітах, тому rbx прибраний і з
// PlayerData, і звідси.
//
// Тут лишилось те, що балансом не є: streak щоденної нагороди (день + час
// останнього забору). Самі СУМИ нагород приїжджають з конфігу через Econ —
// одна точка істини і для підпису «+15» на картці дня, і для нарахування.
// ═══════════════════════════════════════════════════════════════════════════

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

        // Дефолт = те, що апка платить сьогодні. Він же фолбек, коли блока
        // economy немає, ключа немає або довжина в конфізі роз'їхалась.
        private val DEFAULT_DAILY_REWARDS = intArrayOf(15, 20, 25, 30, 40, 50, 100)
    }

    val currentDailyRewardDay: Int
        get() = currentPlayer.dailyRewardDay

    val currentDailyRewardTime: Long
        get() = currentPlayer.dailyRewardTime

    // Набір однотипних значень → rewards_list, а не 7 окремих ключів.
    // Читаємо на кожен виклик: конфіг під'їжджає асинхронно, поле спіймало б
    // дефолти першого кадру. Довжину звіряє сам Econ.
    val listReward: List<Long>
        get() = Econ.rewardList("daily_reward", DEFAULT_DAILY_REWARDS).map { it.toLong() }

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

    /**
     * Забирає нагороду дня і зсуває streak.
     *
     * ⚠️ Монети СЮДИ не нараховуються: це робить викликач через
     * Wallet.add(bt, block) — інакше подія coins_earned поїхала б без розрізу
     * механіки/екрана. Повертає суму (0 = забирати ще рано).
     */
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
