package com.bossrbx.rbxcalculator.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    // ⚠️ rbx тут БІЛЬШЕ НЕМАЄ: баланс живе тільки у Wallet (правка 5).
    // Не повертати — друге сховище балансу = розбіжність у звітах.

    // Daily Reward
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
)