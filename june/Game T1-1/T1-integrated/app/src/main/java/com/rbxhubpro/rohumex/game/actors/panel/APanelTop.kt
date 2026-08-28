package com.rbxhubpro.rohumex.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.rbxhubpro.rohumex.game.actors.button.base.AButtonStyles
import com.rbxhubpro.rohumex.game.actors.button.base.AButtonTexture
import com.rbxhubpro.rohumex.game.actors.label.ALabel
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.font.FontParameter

class APanelTop(override val screen: AdvancedScreen, val isBalance: Boolean = true): AdvancedGroup() {

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

    private val aPanelMainRBX = APanelMainRBX(screen)

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
        if (isBalance) addPanelRBX()
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

    private fun addPanelRBX() {
        aPanelMainRBX.setSize(95f, 23f)
        addActorAligned(aPanelMainRBX, AlignH.CENTER)
        aPanelMainRBX.y = -9f
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}