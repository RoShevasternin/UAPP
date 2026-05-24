package com.rbuxrds.counterds.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.game.actors.button.base.AButtonStyles
import com.rbuxrds.counterds.game.actors.button.base.AButtonTexture
import com.rbuxrds.counterds.game.actors.label.ALabel
import com.rbuxrds.counterds.game.actors.layout.AlignH
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.utils.actor.addActorAligned
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.font.FontParameter

class APanelTop(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonTexture(screen, AButtonStyles.BACK)
    private val aTitleLbl = ALabel(screen, "", Color.WHITE, parameter, screen.fontGenerator_InterTight_Bold)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onBack = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTitleLbl()
        addBackBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(56f, 56f)
        addActorAligned(aBackBtn, AlignH.LEFT)

        aBackBtn.setOnClickListener { onBack() }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(90f, 24f)
        addActorAligned(aTitleLbl, AlignH.CENTER, AlignV.CENTER)
        aTitleLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}