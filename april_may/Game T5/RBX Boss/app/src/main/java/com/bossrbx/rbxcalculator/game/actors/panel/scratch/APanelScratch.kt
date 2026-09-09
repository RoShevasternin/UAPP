package com.bossrbx.rbxcalculator.game.actors.panel.scratch

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.businesModule.economy.Econ
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.addActors
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APanelScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter48 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+")
        .setSize(48)

    private val parameter12 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You win RBX!")
        .setSize(12)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var listRandomResult = Result.entries.shuffled().take(4).map { payout(it) }
        set(value) {
            value.forEachIndexed { index, i ->
                listResultLbl[index].setText(i.toString())
                listSubResultLbl[index].setText("You win $i RBX!")
            }
            field = value
        }

    //private val resultSum get() = listRandomResult.sum()

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg        = Image(gdxGame.assetsAll.PANEL_SCRATCH)
    private val listScratchCard  = List(4) { AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.SCRATCH_FRONT), scratchRadius = 0.10f) }
    private val listResultImg    = List(4) { Image(gdxGame.assetsAll.SCRATCH_RESULT) }
    private val listResultLbl    = List(4) { Label("+0", FontFactory.create(screen, parameter48, screen.fontGenerator_FIRENIGHT, Color.WHITE)) }
    private val listSubResultLbl = List(4) { Label("You win 0 RBX!", FontFactory.create(screen, parameter12, screen.fontGenerator_Light, GameColor.gray_808080)) }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResultOnceScratchCard: (sum: Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)

        listRandomResult = Result.entries.shuffled().take(4).map { payout(it) }

        addListResult()
        addListScratchCard()

    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addListResult() {
        var nx = 16f
        var ny = 178f

        listResultImg.forEachIndexed { index, card ->
            addActor(card)
            card.setBounds(nx, ny, 150f, 150f)

            val lblResult    = listResultLbl[index]
            val lblSubResult = listSubResultLbl[index]
            addActors(lblResult, lblSubResult)
            lblResult.setBounds(nx + 18f, ny + 63f, 59f, 48f)
            lblSubResult.setBounds(nx + 30f, ny + 39f, 91f, 20f)

            lblResult.setAlignment(Align.right)
            lblSubResult.setAlignment(Align.center)

            nx += 12f + 150f
            if (index.inc() % 2 == 0) {
                nx = 12f
                ny -= 12f + 150f
            }

        }

    }

    private fun addListScratchCard() {
        var nx = 16f
        var ny = 178f

        val listIsScratched = MutableList(4) { false }

        listScratchCard.forEachIndexed { index, card ->
            addActor(card)
            card.setBounds(nx, ny, 150f, 150f)

            nx += 12f + 150f
            if (index.inc() % 2 == 0) {
                nx = 12f
                ny -= 12f + 150f
            }

            card.onScratched = { percent ->
                if (percent > 85) {
                    if (!listIsScratched[index]) {
                        listIsScratched[index] = true
                        onResultOnceScratchCard(listRandomResult[index])
                    }
                }
            }
        }

    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    // правка 5: номінали карток приїжджають СПИСКОМ з конфігу —
    // economy.rewards_list.scratch, порядок = порядок enum Result.
    // ОДНА точка і для підпису на картці, і для нарахування: два різні
    // джерела числа = гравець бачить 50, отримує 5.
    private fun payout(result: Result): Int =
        Econ.rewardList("scratch", DEFAULT_SUMS).getOrElse(result.ordinal) { result.sum }

    companion object {
        private val DEFAULT_SUMS = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50)
    }

    enum class Result(val sum: Int) {
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