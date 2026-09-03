package com.selftest.mindora.game.controller

import com.selftest.mindora.game.content.TestContent
import com.selftest.mindora.game.content.TestOutcome
import com.selftest.mindora.game.content.TestQuestion
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.content.TestScoring
import com.selftest.mindora.game.model.PlayerModel

// ═════════════════════════════════════════════════════════════════════════════
// TestController — стан проходження одного теста.
//
// Екран не знає ні про движки скорингу, ні про формат відповідей — він
// питає «яке зараз питання» і доповідає «юзер відповів отак». Уся механіка
// (порядок, повернення назад, підрахунок, збереження) — тут, і тому її
// можна ганяти юніт-тестами без libGDX.
//
// Відповіді зберігаються ПОІНДЕКСНО в Int?-масиві:
//   scale  → значення 1..scaleSize
//   choice → індекс вибраної опції (0-based)
// — рівно те, що очікує TestScoring.score. null = ще не відповідали
// (можливо після повернення «назад» і зміни думки).
// ═════════════════════════════════════════════════════════════════════════════
class TestController(
    val testId: String,
    private val model: PlayerModel,
) {

    val test: TestContent = TestRepository.get(testId)

    private val answers = arrayOfNulls<Int>(test.questions.size)

    var index = 0
        private set

    // ------------------------------------------------------------------------
    // Читання стану
    // ------------------------------------------------------------------------
    val question      : TestQuestion get() = test.questions[index]
    val total         : Int          get() = test.questions.size
    val isLast        : Boolean      get() = index == total - 1
    val currentAnswer : Int?         get() = answers[index]

    /** «Question 3» — акцентна частина (білим). */
    val progressHead: String get() = "Question ${index + 1}"

    /** «of 12» — приглушена частина. */
    val progressTail: String get() = "/$total"

    /** Частка для прогрес-бара: перше питання вже трохи зафарбоване (як у макеті). */
    val progressFraction : Float get() = (index + 1).toFloat() / total

    // ------------------------------------------------------------------------
    // Кроки
    // ------------------------------------------------------------------------

    /**
     * Записати відповідь поточного питання.
     * @return true — є наступне питання (index уже посунуто);
     *         false — це була остання відповідь, час рахувати результат.
     */
    fun answer(value: Int): Boolean {
        answers[index] = value
        return if (isLast) false else { index++; true }
    }

    /**
     * Крок назад по питаннях.
     * @return false — ми на першому питанні, назад = вихід з екрана.
     */
    fun back(): Boolean {
        if (index == 0) return false
        index--
        return true
    }

    // ------------------------------------------------------------------------
    // Фінал
    // ------------------------------------------------------------------------

    /**
     * Порахувати і зберегти. Викликати ТІЛЬКИ коли answer() повернув false —
     * інакше require в TestScoring упаде на неповному масиві, і це правильно:
     * половинний результат гірший за жодного.
     */
    fun finish(): TestOutcome {
        val filled = answers.map { requireNotNull(it) { "Test '$testId': не всі питання мають відповідь" } }
        val outcome = TestScoring.score(test, filled)
        model.saveTestResult(testId, outcome)
        return outcome
    }
}