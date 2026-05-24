package com.rbuxrds.counterds.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.game.actors.label.ALabel
import com.rbuxrds.counterds.game.utils.GameColor
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.font.FontParameter
import com.rbuxrds.counterds.game.utils.gdxGame

class APanelRBX(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.rbx_panel)
    private val aTextLbl  = ALabel(screen, "0", Color.WHITE, parameter, screen.fontGenerator_InterTight_SemiBold)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addActor(aTextLbl)
        aTextLbl.setBounds(52f, 8f, 39f, 32f)
        aTextLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setResult(result: Int) {
        aTextLbl.setText(result.toString())
    }

}