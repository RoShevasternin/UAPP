package com.rbuxdrop.cougame.game.actors.panel.scratch

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
    private var randomResult = Result.entries.random()
        set(value) {
            aResultLbl.setText(value.toString())
            field = value
        }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg   = Image(gdxGame.assetsAll.PANEL_SCRATCH_RESULT)
    private val aResultLbl   = ALabel(screen, "You win $randomResult RBX!", GameColor.purple_3D, parameter24, screen.fontGenerator_Bold)
    private val aScratchCard = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.PANEL_SCRATCH), scratchRadius = 0.06f)
    private val aBottomLbl   = ALabel(screen, "Scratch & WIN!", GameColor.gary_7F, parameter16, screen.fontGenerator_Medium)

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
        addActor(aBottomLbl)

        aResultLbl.setBounds(69f, 62f, 206f, 32f)
        aResultLbl.setAlignment(Align.center)

        aBottomLbl.setBounds(0f, -48f, 344f, 24f)
        aBottomLbl.setAlignment(Align.center)

        val oneTimeResult = OneTime()
        aScratchCard.onScratched = { percent -> if (percent > 85) {
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