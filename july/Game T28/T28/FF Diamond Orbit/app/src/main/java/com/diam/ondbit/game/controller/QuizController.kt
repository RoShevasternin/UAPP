package com.diam.ondbit.game.controller

import com.diam.ondbit.game.data.QuizData
import com.diam.ondbit.game.data.QuizItem

// ═════════════════════════════════════════════════════════════════════════════
//  QuizController — логіка вікторини.
//
//  З банку в 20 питань на кожен раунд береться [questionCount] випадкових,
//  тож два раунди підряд майже ніколи не збігаються.
//
//  За кожну ПРАВИЛЬНУ відповідь — [rewardPerCorrect] діамантів.
//  Прогрес росте на кожну відповідь, правильну чи ні:
//  10 питань × 10% = 100%.
// ═════════════════════════════════════════════════════════════════════════════

class QuizController(
    private val questionCount    : Int  = 10,
    private val rewardPerCorrect : Long = 15L,
) {

    // ------------------------------------------------------------------------
    // Round
    // ------------------------------------------------------------------------
    private var questions: List<QuizItem> = emptyList()

    private var index = 0

    var correct = 0
        private set

    // Ввід заблоковано, поки програється реакція на відповідь
    var isLocked = false
        private set

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    val total       get() = questionCount
    val isFinished  get() = index >= questionCount
    val totalReward get() = correct * rewardPerCorrect

    /** 0..100 — скільки питань уже пройдено */
    val progressPercent get() = index * (100f / questionCount)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onQuestion : (item: QuizItem, number: Int) -> Unit = { _, _ -> }
    var onResult   : (isCorrect: Boolean) -> Unit          = {}
    var onProgress : (percent: Float) -> Unit              = {}
    var onFinish   : (totalReward: Long) -> Unit           = {}

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Новий раунд: 10 випадкових питань з банку */
    fun newGame() {
        questions = QuizData.items().shuffled().take(questionCount)
        index    = 0
        correct  = 0
        isLocked = false

        onProgress(0f)
        onQuestion(questions[0], 1)
    }

    /** Гравець натиснув TRUE або FALSE */
    fun onAnswer(answer: Boolean) {
        if (isLocked)   return
        if (isFinished) return

        isLocked = true

        val isCorrect = answer == questions[index].isTrue
        if (isCorrect) correct++

        onResult(isCorrect)
    }

    /** В'юшка викликає, коли показала реакцію на відповідь */
    fun onResultFinished() {
        index++
        isLocked = false

        onProgress(progressPercent)

        if (isFinished) onFinish(totalReward)
        else            onQuestion(questions[index], index + 1)
    }
}