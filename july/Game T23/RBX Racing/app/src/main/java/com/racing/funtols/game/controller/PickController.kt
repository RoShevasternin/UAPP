package com.racing.funtols.game.controller

// ═════════════════════════════════════════════════════════════════════════════
//  PickController — логіка гри "вибери каністру".
//
//  На дошці [cardCount] каністр, з них [winCount] виграшних (решта — пусті).
//  Гравець відкриває [picksAllowed] штук, кожна виграшна дає [rewardPerWin].
//
//  Тобто результат може бути:
//      2 виграшні → 50 RBX
//      1 виграшна → 25 RBX
//      0          → 0 RBX
//
//  Розподіл призів чесно рандомний: prizes перемішується на кожну нову гру,
//  тож гравець не може вивчити "де завжди приз".
// ═════════════════════════════════════════════════════════════════════════════

class PickController(
    val cardCount            : Int  = 5,
    private val winCount     : Int  = 2,
    private val picksAllowed : Int  = 2,
    private val rewardPerWin : Long = 25L,
) {

    // ------------------------------------------------------------------------
    // Board
    // ------------------------------------------------------------------------
    // prizes[i] == true → у каністрі i лежить приз
    private val prizes = MutableList(cardCount) { it < winCount }

    private val opened = mutableSetOf<Int>()

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    // Ввід заблоковано, поки в'юшка програє анімацію відкриття
    var isLocked = false
        private set

    private var picks = 0

    var wins = 0
        private set

    val totalReward get() = wins * rewardPerWin
    val isFinished  get() = picks >= picksAllowed
    val picksLeft   get() = picksAllowed - picks

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onReveal : (index: Int, isWin: Boolean) -> Unit = { _, _ -> }
    var onFinish : (totalReward: Long) -> Unit          = {}

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun isWinCard(index: Int) = prizes[index]

    fun isOpened(index: Int) = index in opened

    /** Нова гра: перемішати призи і скинути стан */
    fun newGame() {
        prizes.shuffle()
        opened.clear()
        picks    = 0
        wins     = 0
        isLocked = false
    }

    /** Клік по каністрі [index] */
    fun onCardClick(index: Int) {
        if (isLocked)        return // йде анімація відкриття
        if (isFinished)      return // спроби скінчились
        if (index in opened) return // ця вже відкрита

        opened.add(index)
        picks++

        val isWin = prizes[index]
        if (isWin) wins++

        isLocked = true
        onReveal(index, isWin)
    }

    /** В'юшка викликає, коли анімація відкриття завершилась */
    fun onRevealFinished() {
        isLocked = false
        if (isFinished) onFinish(totalReward)
    }
}