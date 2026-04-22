package com.rbuxrds.counter.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter
import com.rbuxrds.counter.game.utils.gdxGame

class APanelLevel(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.panel_level)
    private val aTextLbl  = ALabel(screen, "0", Color.WHITE, parameter, screen.fontGenerator_InterTight_Medium)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addActor(aTextLbl)
        aTextLbl.setBounds(54f, 7f, 7f, 22f)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setLevel(level: Int) {
        aTextLbl.setText(level.toString())
    }

}