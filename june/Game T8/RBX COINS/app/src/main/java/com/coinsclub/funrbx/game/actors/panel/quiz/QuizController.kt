package com.coinsclub.funrbx.game.actors.panel.quiz

import com.coinsclub.funrbx.businesModule.economy.Econ

class QuizController(
    private val totalQuestions: Int  = 5,
    // Нагорода росте з номером питання (1→10, 2→20 …) — сьогоднішня поведінка T8.
    // Суми їдуть списком economy.rewards_list.quiz; довжину звіряє сам Econ
    // (не збіглась → дефолт APK), тож список = рівно totalQuestions значень.
    private val rewards       : IntArray = Econ.rewardList("quiz", IntArray(totalQuestions) { (it + 1) * 10 }),
    // Штраф за неправильну відповідь. Дефолт 0 — сьогодні помилка нічого не
    // коштує; ключ economy.penalties.quiz_screen робить це ручкою.
    private val penalty       : Long = Econ.penalty("quiz_screen", 0).toLong(),
) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onQuestion : (index: Int, text: String) -> Unit        = { _, _ -> }
    var onCorrect  : (reward: Long) -> Unit                    = {}
    var onWrong    : (penalty: Long) -> Unit                   = {}
    var onFinished : (correct: Int, totalReward: Long) -> Unit = { _, _ -> }
    var onAnswered : (index: Int, correct: Boolean) -> Unit    = { _, _ -> }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private var questions    = listOf<QuizQuestion>()
    private var currentIndex = 0
    private var correctCount = 0
    private var totalReward  = 0L
    private var answered     = false   // блок повторного кліку на тому ж питанні

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun initialize() {
        questions    = QuizData.QUESTIONS.shuffled().take(totalQuestions)
        currentIndex = 0
        correctCount = 0
        totalReward  = 0L
        showCurrent()
    }

    // ------------------------------------------------------------------------
    // Answer
    // ------------------------------------------------------------------------
    fun answer(value: Boolean) {
        if (answered) return
        answered = true

        val correct = value == questions[currentIndex].answer
        if (correct) {
            correctCount++
            val reward = rewards[currentIndex].toLong()   // ← нагорода за номер питання
            totalReward += reward
            onCorrect(reward)
        } else {
            onWrong(penalty)
        }

        onAnswered(currentIndex, correct)

        currentIndex++
        if (currentIndex >= questions.size) {
            onFinished(correctCount, totalReward)
        } else {
            showCurrent()
        }
    }

    // ------------------------------------------------------------------------
    // UI
    // ------------------------------------------------------------------------
    private fun showCurrent() {
        answered = false
        onQuestion(currentIndex, questions[currentIndex].text)
    }
}