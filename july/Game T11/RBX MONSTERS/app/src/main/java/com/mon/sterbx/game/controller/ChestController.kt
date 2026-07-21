package com.mon.sterbx.game.controller

import com.badlogic.gdx.math.MathUtils
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ChestController(
    private val scope: CoroutineScope?,
) {

    companion object {
        private const val ATTEMPTS     = 3
        private const val WIN_REWARD   = 500L
        private const val WIN_CHANCE   = 0.5f
        private const val RESULT_DELAY = 1000L   // 1 сек показу результату
    }

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onChestState     : (State) -> Unit                    = {}
    var onAttemptsChanged: (Int) -> Unit                      = {}
    var onButtonState    : (text: String, enabled: Boolean) -> Unit = { _, _ -> }
    var onReward         : (Long) -> Unit                     = {}
    var onFinished       : () -> Unit                         = {}

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var attemptsLeft   = 0
    private var isWaitingClaim = false

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun initialize() {
        attemptsLeft   = ATTEMPTS
        isWaitingClaim = false

        onChestState(State.CLOSED)
        onAttemptsChanged(attemptsLeft)
        onButtonState("OPEN", true)
    }

    // ------------------------------------------------------------------------
    // Click
    // ------------------------------------------------------------------------
    fun onButtonClick() {
        if (isWaitingClaim) claim() else open()
    }

    private fun open() {
        if (attemptsLeft <= 0) return

        onButtonState("OPEN", false)          // блокуємо на час показу

        val isWin = MathUtils.random() < WIN_CHANCE
        onChestState(if (isWin) State.WIN else State.LOSE)

        if (isWin) {
            gdxGame.soundUtil.apply { play(REWARD) }
            // виграш — чекаємо поки юзер забере
            isWaitingClaim = true
            onButtonState("CLAIM", true)
        } else {
            gdxGame.soundUtil.apply { play(FAIL) }

            // програш — через 1 сек закриваємо і йдемо далі
            scope?.launch {
                delay(RESULT_DELAY.milliseconds)
                runGDX { nextAttempt() }
            }
        }
    }

    private fun claim() {
        isWaitingClaim = false
        onReward(WIN_REWARD)
        nextAttempt()
    }

    private fun nextAttempt() {
        attemptsLeft--
        onAttemptsChanged(attemptsLeft)
        onChestState(State.CLOSED)

        if (attemptsLeft > 0) {
            onButtonState("OPEN", true)
        } else {
            onButtonState("OPEN", false)
            onFinished()
        }
    }

    // ------------------------------------------------------------------------
    // enum
    // ------------------------------------------------------------------------
    enum class State { CLOSED, WIN, LOSE }
}