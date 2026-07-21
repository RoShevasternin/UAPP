package com.mon.sterbx.game.actors.panel.scratch

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.util.OneTime

class APanelScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(48)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_BeVietnamPro_Black, GameColor.orange_FED12E)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val randomResult = Result.entries.random().sum

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aScratchCard  = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.SCRATCH_HERE), scratchRadius = 0.077f)
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
        addActor(aResultLbl)
        aResultLbl.setBounds(60f, 192f, 223f, 61f)
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

    enum class Result(val sum: Long) {
        _50  (50),
        _100 (150 ),
        _150 (150 ),
        _200 (250 ),
        _250 (250 ),
        _300 (350 ),
        _350 (350 ),
        _400 (450 ),
        _450 (450 ),
        _500 (550 ),
    }

}