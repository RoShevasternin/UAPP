package com.rbxrush.rushrbx.game.actors.panel.guess

import com.rbxrush.rushrbx.game.utils.actor.setOnTouchListener

class GuessController(
    private val items: List<AItemGuess>,
) {

    companion object {
        private const val INITIAL_PICKS = 3
        private const val PICKS_PER_AD  = 3
        private const val MAX_ADS       = 2
        private const val WIN_COUNT     = 3

        // можливі суми нагород для виграшної картки
        private val REWARD_POOL = listOf(100L, 200L, 300L, 500L, 700L)
    }

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onPicksChanged   : (Int) -> Unit                     = {}
    var onReward         : (Long) -> Unit                    = {}
    var onResult         : (wins: Int, reward: Long) -> Unit = { _, _ -> }
    var onGetFreeEnabled : (Boolean) -> Unit                 = {}

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val winRewards = mutableMapOf<Int, Long>()   // index → сума нагороди
    private val revealed   = mutableSetOf<Int>()

    private var picksLeft   = 0
    private var adsUsed     = 0
    private var totalReward = 0L
    private var finished    = false
    private var winsPicked  = 0

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------
    fun initialize() {
        // випадкові виграшні картки + випадкова сума кожній
        winRewards.clear()
        items.indices.shuffled().take(WIN_COUNT).forEach { index ->
            winRewards[index] = REWARD_POOL.random()
        }

        revealed.clear()
        items.forEach { it.setState(AItemGuess.State.CLOSE) }

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
    // GetFree (кнопка / MORE у попапі)
    // ------------------------------------------------------------------------
    fun canGetFree(): Boolean = !finished && picksLeft > 0 && adsUsed < MAX_ADS

    // чи лишились реклами — для кнопки MORE у попапі (коли гра вже finished)
    fun hasAdsLeft(): Boolean = adsUsed < MAX_ADS

    fun addFreePicks() {
        if (adsUsed >= MAX_ADS) return
        adsUsed++
        picksLeft += PICKS_PER_AD
        finished = false              // гра відновлюється після MORE
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

    private fun onCardClick(index: Int, item: AItemGuess) {
        if (finished || picksLeft <= 0) return
        if (index in revealed) return

        revealed.add(index)

        val reward = winRewards[index]
        if (reward != null) {
            item.setState(AItemGuess.State.WIN, reward)
            winsPicked++
            totalReward += reward
            onReward(reward)
        } else {
            item.setState(AItemGuess.State.LOSE)
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