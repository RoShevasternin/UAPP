package com.diam.ondbit.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val rbx             : Long = 1_000L,
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
)