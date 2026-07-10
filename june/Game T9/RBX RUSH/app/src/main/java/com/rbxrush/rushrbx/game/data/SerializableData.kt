package com.rbxrush.rushrbx.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val rbx             : Long = 100L,
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
)