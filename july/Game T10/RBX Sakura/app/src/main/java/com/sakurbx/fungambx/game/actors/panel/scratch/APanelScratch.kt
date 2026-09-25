package com.sakurbx.fungambx.game.actors.panel.scratch

import com.sakurbx.fungambx.businesModule.economy.Econ
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.util.OneTime

class APanelScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(57)
        .setShadow(-2, -2, GameColor.purple_D64791)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold, GameColor.beige_FFFAD3)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    // ⚠️ Підпис картки і нарахування — ОДНЕ число. Номінали їдуть списком
    // economy.rewards_list.scratch, порядок = порядок enum Result.
    private val randomIndex  = Result.entries.indices.random()
    private val randomResult = Econ.rewardList("scratch", DEFAULT_SUMS)
        .getOrElse(randomIndex) { Result.entries[randomIndex].sum.toInt() }.toLong()

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aScratchCard  = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.SCRATCH_HERE), scratchRadius = 0.067f)
    private val aResultImg    = Image(gdxGame.assetsAll.SCRATCH_WIN)
    private val aResultLbl    = Label("$randomResult RBX", lsDef)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (sum: Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addResult()
        addResultLbl()
        addScratchCard()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addResult() {
        addAndFillActor(aResultImg)
    }

    private fun addResultLbl() {
        addAndFillActor(aResultLbl)
        aResultLbl.setAlignment(Align.center)
    }

    private fun addScratchCard() {
        addAndFillActor(aScratchCard)

        val oneTime = OneTime()
        aScratchCard.onScratched = { percent ->
            if (percent > 85) {
                oneTime.use { onResult(randomResult) }
            }
        }

    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    companion object {
        private val DEFAULT_SUMS = intArrayOf(50, 100, 150, 200, 250, 300, 350, 400, 450, 500)
    }

    enum class Result(val sum: Long) {
        _50  (50),
        _100 (100),
        _150 (150),
        _200 (200),
        _250 (250),
        _300 (300),
        _350 (350),
        _400 (400),
        _450 (450),
        _500 (500),
    }

}