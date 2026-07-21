package com.mon.sterbx.game.actors.panel.quiz

class QuizController(
    private val totalQuestions: Int  = 5,
    private val reward        : Long = 10L,
) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onQuestion : (index: Int, text: String) -> Unit        = { _, _ -> }
    var onCorrect  : (reward: Long) -> Unit                    = {}
    var onFinished : (correct: Int, totalReward: Long) -> Unit = { _, _ -> }
    var onProgress : (current: Int, total: Int) -> Unit        = { _, _ -> }

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
        onProgress(currentIndex + 1, totalQuestions)   // 1-based для UI
        onQuestion(currentIndex, questions[currentIndex].text)
    }
}