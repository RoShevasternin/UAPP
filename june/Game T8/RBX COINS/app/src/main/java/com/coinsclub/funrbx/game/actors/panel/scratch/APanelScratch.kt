package com.coinsclub.funrbx.game.actors.panel.scratch

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedGroup
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.util.OneTime
import com.coinsclub.funrbx.util.log

class APanelScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(58)
        .setBorderAndShadow(border = 5f, shadowX = 5, shadowY = 4)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val randomResult = Result.entries.random().sum

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aScratchCard  = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.SCRATCH_HERE), scratchRadius = 0.085f)
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