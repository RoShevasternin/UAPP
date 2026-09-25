package com.sakurbx.fungambx.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
)