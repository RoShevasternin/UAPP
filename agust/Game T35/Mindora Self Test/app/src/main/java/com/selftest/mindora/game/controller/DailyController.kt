package com.selftest.mindora.game.controller

import com.selftest.mindora.game.model.PlayerModel
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// ----------------------------------------------------------------------------
// DailyController — ЄДИНЕ джерело правди для екрана Daily Reward.
//
//   Хто чим володіє:
//     PlayerModel      — персистентний стан (день циклу, стрік, час останнього клейму)
//     DailyController  — похідний стан для UI + множник x2 (живе тільки в сесії)
//     View (панелі)    — тупі, лише малюють те, що дав контролер
//
//   ДЕНЬ ЦИКЛУ vs СТРІК:
//     day    — 1..7, після 7 повертається до 1. Визначає, яку клітинку підсвітити
//              і яку суму видати (economy.dailyReward[day-1]).
//     streak — скільки днів підряд юзер заходить. НЕ обнуляється на 8-му дні:
//              8-й день підряд = streak 8, day 1.
//     Обидва скидаються разом, коли пропущено >48 год.
//
//   МНОЖНИК x2 живе тільки в контролері й гине разом з екраном — це навмисно.
//   Подивився рекламу, але вийшов не забравши → множник не зберігається.
// ----------------------------------------------------------------------------

class DailyController(
    private val scope: CoroutineScope?,
    private val model: PlayerModel,
) {

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    data class State(
        val day             : Int,      // 1..7 — день циклу
        val streak          : Int,      // 1, 2, 3 … не обнуляється
        val canClaim        : Boolean,
        val baseReward      : Long,     // сума за поточний день без множника
        val finalReward     : Long,     // baseReward * multiplier
        val isDoubled       : Boolean,
        val remainingSeconds: Long,     // скільки чекати до наступного клейму
    )

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    /** Викликається на кожну зміну стану — сюди вішається весь рендер UI. */
    var onRender: (State) -> Unit = {}

    /** Нагорода підтверджена: показати попап з цією сумою. Люмени НЕ нараховані. */
    var onClaimed: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var multiplier = 1
    private var tickJob: Job? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    fun initialize() {
        model.validateDailyReward()   // пропущено >48год → скинути цикл і стрік
        collectDay()
        startTicker()
        render()
    }

    /** Обов'язково викликати при виході з екрана — інакше тікер житиме далі. */
    fun dispose() {
        tickJob?.cancel()
        tickJob = null
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /**
     * Забрати нагороду. Модель просуває день/стрік/час, але люмени НЕ нараховує —
     * їх додає APopup у своїй кнопці Claim. Це навмисно: одна точка нарахування.
     */
    fun tryClaim() {
        if (!model.canClaimDailyReward()) return

        val base = model.claimDailyReward()
        if (base <= 0L) return

        val final = base * multiplier
        multiplier = 1              // множник згорає одразу після використання

        onClaimed(final)
        render()
    }

    /** Чи має сенс показувати кнопку x2 (є що подвоювати і ще не подвоєно). */
    fun canDouble(): Boolean = multiplier == 1 && model.canClaimDailyReward()

    /** Викликати ПІСЛЯ успішного показу rewarded-ролика. */
    fun applyDouble() {
        if (!canDouble()) return
        multiplier = 2
        render()
    }

    // ------------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------------
    private fun collectDay() {
        scope?.launch {
            model.dailyRewardDayFlow.collect { render() }
        }
    }

    /**
     * Раз на секунду перевіряє, чи не вийшов таймер. Потрібен, щоб кнопка Claim
     * сама розблокувалась, якщо юзер сидить на екрані в момент, коли минула доба.
     */
    private fun startTicker() {
        tickJob?.cancel()
        tickJob = scope?.launch {
            var wasClaimable = model.canClaimDailyReward()
            while (true) {
                delay(1000L.milliseconds)
                val nowClaimable = model.canClaimDailyReward()
                if (nowClaimable != wasClaimable) {
                    wasClaimable = nowClaimable
                    render()
                } else if (!nowClaimable) {
                    render()   // оновити зворотний відлік
                }
            }
        }
    }

    private fun render() = runGDX { onRender(buildState()) }

    private fun buildState(): State {
        val day      = model.dailyRewardDayFlow.value
        val base     = model.dailyRewardFor(day)
        val canClaim = model.canClaimDailyReward()

        return State(
            day              = day,
            streak           = model.dailyStreakFlow.value,
            canClaim         = canClaim,
            baseReward       = base,
            finalReward      = base * multiplier,
            isDoubled        = multiplier > 1,
            remainingSeconds = model.dailyRewardRemainingSeconds(),
        )
    }
}