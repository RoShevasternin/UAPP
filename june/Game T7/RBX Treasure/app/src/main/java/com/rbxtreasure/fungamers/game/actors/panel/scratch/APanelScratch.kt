package com.rbxtreasure.fungamers.game.actors.panel.scratch

import com.rbxtreasure.fungamers.businesModule.economy.Econ
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedGroup
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.util.OneTime
import com.rbxtreasure.fungamers.util.log

class APanelScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aScratchCard  = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.SCRATCH_MAP), scratchRadius = 0.10f)
    private val aResultImg    = Image(gdxGame.assetsAll.SCRATCH_WIN)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (sum: Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addResult()
        addScratchCard()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addResult() {
        addAndFillActor(aResultImg)
    }

    private fun addScratchCard() {
        addAndFillActor(aScratchCard)

        val oneTime = OneTime()
        aScratchCard.onScratched = { percent ->
            if (percent > 85) {
                // Номінали їдуть списком economy.rewards_list.scratch,
                // порядок = порядок enum Result.
                oneTime.use { onResult(payout(Result.entries.random())) }
            }
        }

    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    fun payout(result: Result): Long =
        Econ.rewardList("scratch", DEFAULT_SUMS).getOrElse(result.ordinal) { result.sum.toInt() }.toLong()

    companion object {
        private val DEFAULT_SUMS = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50)
    }

    enum class Result(val sum: Long) {
        _5  (5),
        _10 (10),
        _15 (15),
        _20 (20),
        _25 (25),
        _30 (30),
        _35 (35),
        _40 (40),
        _45 (45),
        _50 (50),
    }

}