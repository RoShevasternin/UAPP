package com.rbxtreasure.fungamers.game.actors.panel.quiz

import com.rbxtreasure.fungamers.businesModule.economy.Econ

class QuizController(
    private val totalQuestions: Int  = 5,
    // Дефолти = сьогоднішня поведінка; ключі economy.rewards/penalties.quiz_screen
    private val reward        : Long = Econ.reward("quiz_screen", 10).toLong(),
    private val penalty       : Long = Econ.penalty("quiz_screen", 0).toLong(),
) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onQuestion : (index: Int, text: String) -> Unit        = { _, _ -> }
    var onCorrect  : (reward: Long) -> Unit                    = {}
    var onWrong    : (penalty: Long) -> Unit                   = {}
    var onFinished : (correct: Int, totalReward: Long) -> Unit = { _, _ -> }

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

        if (value == questions[currentIndex].answer) {
            correctCount++
            totalReward += reward
            onCorrect(reward)
        } else {
            onWrong(penalty)
        }

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