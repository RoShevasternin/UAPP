package com.rbuxdrop.cougame.game.actors.panel.scratch

import com.rbuxdrop.cougame.businesModule.economy.Econ
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedGroup
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.util.OneTime

class AScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You win RBX!")
        .setSize(24)

    private val parameter16 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Scratch & WIN!")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    // Суми з Econ (ключ "scratch"); підпис і нарахування — з одного payout,
    // тож на картці написано рівно те, що впаде на баланс.
    private var payout = randomPayout()
        set(value) {
            aResultLbl.setText(resultText(value))
            field = value
        }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg   = Image(gdxGame.assetsAll.PANEL_SCRATCH_RESULT)
    private val aResultLbl   = ALabel(screen, resultText(payout), GameColor.purple_3D, parameter24, screen.fontGenerator_Bold)
    private val aScratchCard = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.PANEL_SCRATCH), scratchRadius = 0.06f)
    private val aBottomLbl   = ALabel(screen, "Scratch & WIN!", GameColor.gary_7F, parameter16, screen.fontGenerator_Medium)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (win: Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aResultImg)
        addActor(aResultLbl)
        addAndFillActor(aScratchCard)
        addActor(aBottomLbl)

        aResultLbl.setBounds(69f, 62f, 206f, 32f)
        aResultLbl.setAlignment(Align.center)

        aBottomLbl.setBounds(0f, -48f, 344f, 24f)
        aBottomLbl.setAlignment(Align.center)

        val oneTimeResult = OneTime()
        aScratchCard.onScratched = { percent -> if (percent > 85) {
            oneTimeResult.use { onResult(payout) }
        } }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun regenerateScratch() {
        aScratchCard.reset()
        payout = randomPayout()
    }

    private fun resultText(win: Int) = "You win $win RBX!"

    private fun randomPayout(): Int = Econ.rewardList(REWARDS_KEY, SCRATCH_DEF).random()

    companion object {
        private const val REWARDS_KEY = "scratch"
        private val SCRATCH_DEF = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100, 150)
    }

}