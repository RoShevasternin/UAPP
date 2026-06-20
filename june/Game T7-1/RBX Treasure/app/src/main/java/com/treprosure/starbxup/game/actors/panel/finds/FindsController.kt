package com.treprosure.starbxup.game.actors.panel.finds

import com.treprosure.starbxup.game.utils.actor.setOnTouchListener

class FindsController(
    private val items: List<AItemFinds>,
) {

    companion object {
        private const val INITIAL_PICKS = 3
        private const val PICKS_PER_AD  = 3
        private const val MAX_ADS       = 2
        private const val WIN_COUNT     = 3
        private const val WIN_REWARD    = 100L
    }

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onPicksChanged   : (Int) -> Unit                     = {}   // оновити aCardsLbl
    var onReward         : (Long) -> Unit                    = {}   // знайдено виграшну карту
    var onResult         : (wins: Int, reward: Long) -> Unit = { _, _ -> }
    var onGetFreeEnabled : (Boolean) -> Unit                 = {}   // вкл/викл кнопку

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val winSet   = mutableSetOf<Int>()
    private val revealed = mutableSetOf<Int>()

    private var picksLeft   = 0
    private var adsUsed     = 0
    private var totalReward = 0L
    private var finished    = false
    private var winsPicked = 0

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun initialize() {
        winSet.clear()
        winSet.addAll(items.indices.shuffled().take(WIN_COUNT))

        revealed.clear()
        items.forEach { it.setState(AItemFinds.State.CLOSE) }

        adsUsed     = 0
        totalReward = 0L
        finished    = false
        picksLeft   = INITIAL_PICKS
        winsPicked  = 0

        bindClicks()
        onPicksChanged(picksLeft)
        updateGetFree()
    }

    // ------------------------------------------------------------------------
    // GetFree (кнопка)
    // ------------------------------------------------------------------------
    fun canGetFree(): Boolean = !finished && picksLeft > 0 && adsUsed < MAX_ADS

    fun addFreePicks() {
        if (!canGetFree()) return
        adsUsed++
        picksLeft += PICKS_PER_AD
        onPicksChanged(picksLeft)
        updateGetFree()
    }

    // ------------------------------------------------------------------------
    // Clicks
    // ------------------------------------------------------------------------
    private fun bindClicks() {
        items.forEachIndexed { index, item ->
            item.setOnTouchListener { onCardClick(index, item) }
        }
    }

    private fun onCardClick(index: Int, item: AItemFinds) {
        if (finished || picksLeft <= 0) return
        if (index in revealed) return

        revealed.add(index)

        val isWin = index in winSet
        item.setState(if (isWin) AItemFinds.State.WIN else AItemFinds.State.LOSE)

        if (isWin) {
            winsPicked++
            totalReward += WIN_REWARD
            onReward(WIN_REWARD)
        }

        picksLeft--
        onPicksChanged(picksLeft)
        updateGetFree()

        if (picksLeft == 0) {
            finished = true
            onResult(winsPicked, totalReward)
        }
    }

    // ------------------------------------------------------------------------
    // UI
    // ------------------------------------------------------------------------
    private fun updateGetFree() {
        onGetFreeEnabled(canGetFree())
    }
}