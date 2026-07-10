package com.rbxrush.rushrbx.game.state

import com.rbxrush.rushrbx.game.data.PlayerData
import kotlinx.coroutines.flow.MutableStateFlow

class GameState {

    val rbxFlow             = MutableStateFlow(100L)
    val dailyRewardDayFlow  = MutableStateFlow(1)
    val dailyRewardTimeFlow = MutableStateFlow(0L)

    fun loadFrom(data: PlayerData) {
        rbxFlow.value             = data.rbx
        dailyRewardDayFlow.value  = data.dailyRewardDay
        dailyRewardTimeFlow.value = data.dailyRewardTime
    }


    fun toPlayerData() = PlayerData(
        rbx             = rbxFlow.value,
        dailyRewardDay  = dailyRewardDayFlow.value,
        dailyRewardTime = dailyRewardTimeFlow.value,
    )
}