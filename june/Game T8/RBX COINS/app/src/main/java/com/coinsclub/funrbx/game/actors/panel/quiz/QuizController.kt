package com.coinsclub.funrbx.game.actors.panel.quiz

class QuizController(
    private val totalQuestions: Int = 5,
    private val rewardForIndex: (index: Int) -> Long = { (it + 1) * 10L },  // 1→10, 2→20...
) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onQuestion : (index: Int, text: String) -> Unit        = { _, _ -> }
    var onCorrect  : (reward: Long) -> Unit                    = {}
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
            val reward = rewardForIndex(currentIndex)   // ← нагорода за номер питання
            totalReward += reward
            onCorrect(reward)
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