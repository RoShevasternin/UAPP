package com.treprosure.starbxup.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val rbx             : Long = 1000L,
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
)