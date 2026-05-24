package com.rbuxrds.counter.game.actors.allCharacters

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.utils.GameColor
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter
import com.rbuxrds.counter.game.utils.gdxGame

class ACharacter(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg        = Image()
    private val aHexImg         = Image(gdxGame.assetsAll.hex_check)
    private val aDescriptionLbl = ALabel(screen, "", Color.WHITE, parameter, screen.fontGenerator_InterTight_Medium)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onDone: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addHexImg()
        addDescriptionLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLogoImg() {
        addActor(aLogoImg)
        aLogoImg.setBounds(88f, 354f, 168f, 187f)
    }

    private fun addHexImg() {
        addActor(aHexImg)
        aHexImg.setBounds(76f, 342f, 198f, 211f)
    }

    private fun addDescriptionLbl() {
        addActor(aDescriptionLbl)
        aDescriptionLbl.setBounds(0f, 0f, 344f, 330f)
        aDescriptionLbl.getLabelOrNull()?.apply {
            setAlignment(Align.top, Align.center)
            wrap = true
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setDataCharacter(data: DataCharacter) {
        aLogoImg.drawable = TextureRegionDrawable(data.region)
        aDescriptionLbl.setText(data.description)
    }

}