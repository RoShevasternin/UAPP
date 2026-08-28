package com.selftest.mindora.game.state

import com.selftest.mindora.game.data.PlayerData
import com.selftest.mindora.game.data.SavedTestResult
import kotlinx.coroutines.flow.MutableStateFlow

class GameState {

    // ------------------------------------------------------------------------
    // Flows
    // ------------------------------------------------------------------------
    val lumensFlow = MutableStateFlow(0L)

    // Daily Reward
    val dailyRewardDayFlow  = MutableStateFlow(1)
    val dailyRewardTimeFlow = MutableStateFlow(0L)
    val dailyStreakFlow     = MutableStateFlow(0)

    // Портрет
    val testResultsFlow     = MutableStateFlow<Map<String, SavedTestResult>>(emptyMap())
    val portraitTitleIdFlow = MutableStateFlow<String?>(null)

    // Куплені тести. Окремо від testResults: куплений ≠ пройдений.
    val purchasedTestsFlow  = MutableStateFlow<Set<String>>(emptySet())

    val isFirstOpenFlow     = MutableStateFlow(true)

    /** LOAD SIGNAL
     * Стає true ПІСЛЯ повного loadFrom. Моделі, що залежать від збереженого
     * стану, чекають саме його — це усуває race з флоу.
     */
    val isLoadedFlow = MutableStateFlow(false)

    // ------------------------------------------------------------------------
    // Persistence
    // ------------------------------------------------------------------------
    fun loadFrom(data: PlayerData) {
        lumensFlow.value             = data.lumens

        dailyRewardDayFlow.value  = data.dailyRewardDay
        dailyRewardTimeFlow.value = data.dailyRewardTime
        dailyStreakFlow.value     = data.dailyStreak

        testResultsFlow.value     = data.testResults
        portraitTitleIdFlow.value = data.portraitTitleId
        purchasedTestsFlow.value  = data.purchasedTests

        isFirstOpenFlow.value     = data.isFirstOpen

        // сигнал "усе завантажено" — після всіх присвоєнь
        isLoadedFlow.value = true
    }

    fun toPlayerData() = PlayerData(
        lumens = lumensFlow.value,

        dailyRewardDay  = dailyRewardDayFlow.value,
        dailyRewardTime = dailyRewardTimeFlow.value,
        dailyStreak     = dailyStreakFlow.value,

        testResults     = testResultsFlow.value,
        portraitTitleId = portraitTitleIdFlow.value,
        purchasedTests  = purchasedTestsFlow.value,

        isFirstOpen     = isFirstOpenFlow.value,
    )

}