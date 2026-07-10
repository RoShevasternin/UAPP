package com.rsbuxs.rcounbux.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val rbx : Long = 100L,

    // Daily Reward
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
)