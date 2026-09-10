package com.rbxgolden.fungamems.game.actors.panel.scratch

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.businesModule.economy.Econ
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.util.OneTime

class AScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter64 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setBorder(2f, GameColor.orange_FE)
        .setSize(64)
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You win 500 RBX!")
        .setSize(14)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var randomResult = Result.entries.random()
        set(value) {
            // payout, а не ім'я enum: підпис картки і нарахування зобов'язані
            // бути ОДНИМ числом. Ім'я enum ще й друкується як «_50» —
            // підкреслення у шрифті NUMBERS немає.
            val win = payout(value)
            aRewardLbl.setText(win.toString())
            aTextLbl.setText("You win $win RBX!")
            field = value
        }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg   = Image(gdxGame.assetsAll.PANEL_SCRATCH_RESULT)
    private val aRewardLbl   = Label("500", FontFactory.create(screen, parameter64, screen.fontGenerator_Bold, GameColor.yellow_FF))
    private val aTextLbl     = Label("You win 500 RBX!", FontFactory.create(screen, parameter14, screen.fontGenerator_Medium, Color.WHITE))
    private val aScratchCard = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.PANEL_SCRATCH), scratchRadius = 0.05f)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (result: Result) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        randomResult = Result.entries.random()

        addAndFillActor(aResultImg)
        addActor(aRewardLbl)
        addActor(aTextLbl)
        addAndFillActor(aScratchCard)

        aRewardLbl.setBounds(152f, 117f, 130f, 72f)

        aTextLbl.setBounds(112f, 83f, 118f, 22f)
        aTextLbl.setAlignment(Align.center)

        val oneTimeResult = OneTime()
        aScratchCard.onScratched = { percent -> if (percent > 85) {
            oneTimeResult.use { onResult(randomResult) }
        } }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    // Номінали карток їдуть СПИСКОМ з конфігу — economy.rewards_list.scratch.
    // Одна точка на ОБА застосування: що намальовано на картці і що впало
    // в гаманець. Два різні джерела числа = гравець бачить 150, отримує 50.
    fun payout(result: Result): Int =
        Econ.rewardList("scratch", DEFAULT_SUMS).getOrElse(result.ordinal) { result.sum }

    fun regenerateScratch() {
        aScratchCard.reset()
        randomResult = Result.entries.random()
    }

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