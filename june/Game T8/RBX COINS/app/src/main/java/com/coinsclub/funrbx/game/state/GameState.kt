package com.coinsclub.funrbx.game.state

import com.coinsclub.funrbx.game.data.PlayerData
import kotlinx.coroutines.flow.MutableStateFlow

class GameState {

    val rbxFlow             = MutableStateFlow(1000L)
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