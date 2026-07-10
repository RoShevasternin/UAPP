package com.rsbuxs.rcounbux.game.model

import com.rsbuxs.rcounbux.game.data.PlayerData
import com.rsbuxs.rcounbux.game.dataStore.DS_Player
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
    // RBX
    // ------------------------------------------------------------------------
    val rbxFlow: StateFlow<Long> =
        playerFlow
            .map { it.rbx }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, currentPlayer.rbx)

    val currentRbx: Long
        get() = rbxFlow.value

    fun addRbx(amount: Long) {
        if (amount <= 0) return
        val boosted = applyBoost(amount)
        ds.update { data -> data.copy(rbx = data.rbx + boosted) }
    }

    fun spendRbx(amount: Long): Boolean {
        if (amount <= 0) return false
        if (currentRbx < amount) return false
        ds.update { data -> data.copy(rbx = data.rbx - amount) }
        return true
    }

    fun setRbx(amount: Long) {
        ds.update { data -> data.copy(rbx = amount.coerceAtLeast(0L)) }
    }

    // ------------------------------------------------------------------------
    // Boost Mode
    // ------------------------------------------------------------------------
    var isBoostMode: Boolean = false

    private fun applyBoost(amount: Long): Long = if (isBoostMode) amount * 2 else amount

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

        val reward = currentDailyRewardDay * 5L

        addRbx(reward)

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