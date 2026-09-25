package com.zahbx.blitzrbx.game.actors.scratch

import com.zahbx.blitzrbx.businesModule.economy.Econ
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.zahbx.blitzrbx.game.actors.label.ALabel
import com.zahbx.blitzrbx.game.utils.actor.addAndFillActor
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedGroup
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.font.FontParameter
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.util.OneTime

class AScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You win RBX!")
        .setSize(24)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var randomResult = Result.entries.random()
        set(value) {
            aResultLbl.setText(value.toString())
            field = value
        }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg   = Image(gdxGame.assetsAll.PANEL_SCRATCH_RESULT)
    private val aResultLbl   = ALabel(screen, "You win $randomResult RBX!", Color.WHITE, parameter, screen.fontGenerator_Medium)
    private val aScratchCard = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.PANEL_SCRATCH), scratchRadius = 0.1f)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (result: Result) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aResultImg)
        addActor(aResultLbl)
        addAndFillActor(aScratchCard)

        aResultLbl.setAlignment(Align.center)
        aResultLbl.setBounds(82f, 74f, 179f, 32f)

        val oneTimeResult = OneTime()
        aScratchCard.onScratched = { percent -> if (percent > 55) {
            oneTimeResult.use { onResult(randomResult) }
        } }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun regenerateScratch() {
        aScratchCard.reset()
        randomResult = Result.entries.random()
    }

    // Номінали їдуть СПИСКОМ з конфігу — economy.rewards_list.scratch, порядок =
    // порядок enum Result. Читаємо на кожен виклик: конфіг під'їжджає
    // асинхронно. Довжину звіряє сам Econ — не збіглась, повернеться
    // DEFAULT_SUMS (те, що НАМАЛЬОВАНО на текстурі).
    fun payout(result: Result): Int =
        Econ.rewardList("scratch", DEFAULT_SUMS).getOrElse(result.ordinal) { result.sum }

    companion object {
        private val DEFAULT_SUMS = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100, 150)
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
        _100(100),
        _150(150),
    }

}