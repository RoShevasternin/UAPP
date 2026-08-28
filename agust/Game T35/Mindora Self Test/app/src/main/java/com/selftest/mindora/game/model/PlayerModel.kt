package com.selftest.mindora.game.model

import com.selftest.mindora.game.content.TestCatalog
import com.selftest.mindora.game.content.TestOutcome
import com.selftest.mindora.game.data.SavedTestResult
import com.selftest.mindora.game.state.GameState
import com.selftest.mindora.game.utils.gdxGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class PlayerModel(
    private val gameState: GameState,
    private val scope: CoroutineScope,
) {

    companion object {
        private const val IS_TEST_MODE = false
        private val DAY_MILLIS   = if (IS_TEST_MODE) 5_000L  else 24 * 60 * 60 * 1000L
        private val RESET_MILLIS = if (IS_TEST_MODE) 60_000L else 48 * 60 * 60 * 1000L

        private const val CYCLE_DAYS = 7
    }

    val isLoadedFlow = gameState.isLoadedFlow

    /** Суми нагород — ЄДИНЕ джерело: Remote Config. Ніяких локальних списків. */
    private val economy get() = gdxGame.activity.appConfig.economy

    /** Ціни тестів — звідти ж. */
    private val costs get() = gdxGame.activity.appConfig.costs

    // ------------------------------------------------------------------------
    // Lumens
    // ------------------------------------------------------------------------
    val lumensFlow: StateFlow<Long> = gameState.lumensFlow

    fun addLumens(amount: Long) { gameState.lumensFlow.value += amount }
    fun setLumens(amount: Long) { gameState.lumensFlow.value  = amount }
    fun getLumens(): Long       = gameState.lumensFlow.value

    /** Атомарне списання: false якщо не вистачає — баланс НЕ чіпається. */
    fun spendLumens(amount: Long): Boolean {
        if (gameState.lumensFlow.value < amount) return false
        gameState.lumensFlow.value -= amount
        return true
    }

    // ------------------------------------------------------------------------
    // isFirstOpen
    // ------------------------------------------------------------------------
    val isFirstOpenFlow: StateFlow<Boolean> = gameState.isFirstOpenFlow

    fun setIsFirstOpen(flag: Boolean) { gameState.isFirstOpenFlow.value = flag }
    fun getIsFirstOpen(): Boolean = gameState.isFirstOpenFlow.value

    // ------------------------------------------------------------------------
    // Daily Reward
    //
    //   day    — 1..7, циклічний. Визначає клітинку і суму.
    //   streak — наскрізний лічильник днів підряд, НЕ обнуляється на 8-му дні.
    //   Обидва скидаються разом при пропуску >48 год.
    // ------------------------------------------------------------------------
    val dailyRewardDayFlow : StateFlow<Int>  = gameState.dailyRewardDayFlow
    val dailyRewardTimeFlow: StateFlow<Long> = gameState.dailyRewardTimeFlow
    val dailyStreakFlow    : StateFlow<Int>  = gameState.dailyStreakFlow

    /** Сума за конкретний день циклу. Безпечна до короткого масиву в конфізі. */
    fun dailyRewardFor(day: Int): Long = economy.dailyRewardFor(day)

    fun canClaimDailyReward(): Boolean {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return true
        return System.currentTimeMillis() - lastTime >= DAY_MILLIS - 1000L  // буфер 1с
    }

    fun dailyRewardRemainingSeconds(): Long {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return 0L
        val remainMillis = DAY_MILLIS - (System.currentTimeMillis() - lastTime)
        return remainMillis.coerceAtLeast(0L) / 1000L
    }

    /**
     * Просунути цикл і повернути БАЗОВУ суму за забраний день.
     *
     * УВАГА: люмени тут НЕ нараховуються. Нарахування робить APopup у своїй
     * кнопці Claim — щоб точка `addLumens` була рівно одна і множник x2 міг
     * застосуватись між цими двома кроками.
     */
    fun claimDailyReward(): Long {
        validateDailyReward()
        if (!canClaimDailyReward()) return 0L

        val day    = gameState.dailyRewardDayFlow.value
        val reward = dailyRewardFor(day)

        gameState.dailyRewardTimeFlow.value = System.currentTimeMillis()          // ← СПЕРШУ час
        gameState.dailyStreakFlow.value     = gameState.dailyStreakFlow.value + 1
        gameState.dailyRewardDayFlow.value  = if (day >= CYCLE_DAYS) 1 else day + 1  // ← тригерить collect

        return reward
    }

    /** Пропущено більше RESET_MILLIS — цикл і стрік починаються заново. */
    fun validateDailyReward() {
        val lastTime = gameState.dailyRewardTimeFlow.value
        if (lastTime == 0L) return
        if (System.currentTimeMillis() - lastTime >= RESET_MILLIS) {
            gameState.dailyStreakFlow.value     = 0
            gameState.dailyRewardDayFlow.value  = 1
            gameState.dailyRewardTimeFlow.value = 0L
        }
    }

    // ------------------------------------------------------------------------
    // Тести і Портрет
    //
    //   5 вимірів = 5 тестів (за макетом). Пройдений тест = +1 вимір.
    //   Синтез фіксує portraitTitleId назавжди — повторний resolve при
    //   зміні контенту synthesis.json НЕ перейменує вже виданий портрет.
    // ------------------------------------------------------------------------
    val testResultsFlow    : StateFlow<Map<String, SavedTestResult>> = gameState.testResultsFlow
    val portraitTitleIdFlow: StateFlow<String?>                      = gameState.portraitTitleIdFlow
    val purchasedTestsFlow : StateFlow<Set<String>>                  = gameState.purchasedTestsFlow

    fun testResults(): Map<String, SavedTestResult> = gameState.testResultsFlow.value

    fun resultOf(testId: String): SavedTestResult? = testResults()[testId]

    /** Скільки вимірів портрета відкрито (0..5). */
    fun unlockedDimensions(): Int = testResults().size

    /** Зберегти результат теста. Повторне проходження перезаписує попередній. */
    fun saveTestResult(testId: String, outcome: TestOutcome) {
        gameState.testResultsFlow.value =
            testResults() + (testId to SavedTestResult(outcome.resultIds, outcome.scores))
    }

    // ------------------------------------------------------------------------
    // Покупка тестів
    //
    //   Куплений ≠ пройдений. Купівля відкриває доступ НАЗАВЖДИ: повторне
    //   проходження («Take Again») безкоштовне, інакше юзер боявся б
    //   перепроходити і ми втрачали б найдешевший спосіб його повернути.
    // ------------------------------------------------------------------------

    /** Ціна теста з конфігу. 0 = безкоштовний (картка покаже «Free»). */
    fun costOf(testId: String): Long = costs.of(TestCatalog.byId(testId).testId)

    fun purchasedTests(): Set<String> = gameState.purchasedTestsFlow.value

    /** Відкритий = куплений АБО вже пройдений (страховка для старих збережень). */
    fun isTestUnlocked(testId: String): Boolean =
        testId in purchasedTests() || testId in testResults()

    /**
     * Відкрити тест: списати ціну, якщо ще не куплений.
     *
     * @return false — не вистачило люменів. Баланс НЕ чіпається, тест НЕ
     *         відкривається; викликаючий вирішує, що показати юзеру.
     *
     * Порядок важливий: спершу spendLumens (атомарний), і лише при успіху
     * позначаємо купленим. Навпаки — і невдале списання дало б безкоштовний
     * доступ.
     */
    fun unlockTest(testId: String): Boolean {
        if (isTestUnlocked(testId)) return true

        val price = costOf(testId)
        if (price > 0L && !spendLumens(price)) return false

        gameState.purchasedTestsFlow.value = purchasedTests() + testId
        return true
    }

    fun portraitTitleId(): String? = gameState.portraitTitleIdFlow.value

    fun savePortraitTitle(id: String) { gameState.portraitTitleIdFlow.value = id }

    fun clearPortraitTitle() { gameState.portraitTitleIdFlow.value = null }

    /** Повний рестарт портрета: результати + титул + покупки. */
    fun clearTestResults() {
        gameState.testResultsFlow.value     = emptyMap()
        gameState.portraitTitleIdFlow.value = null
        gameState.purchasedTestsFlow.value  = emptySet()
    }
}