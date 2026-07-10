package com.rbxhubpro.rohumex.game.actors.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.rbxhubpro.rohumex.game.actors.button.base.AButtonAnim
import com.rbxhubpro.rohumex.game.actors.button.base.AButtonStyles
import com.rbxhubpro.rohumex.game.actors.label.ALabel
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.actor.disable
import com.rbxhubpro.rohumex.game.utils.actor.setOnClickListener
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.font.FontParameter

class ADailyConverterItem(
    override val screen: AdvancedScreen,
    title: String,
): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBtn = AButtonAnim(screen, AButtonStyles.DAILY_CONVERTER_ITEM)
    private val aLbl = ALabel(screen, title, Color.WHITE, parameter, screen.fontGenerator_InterTight_Medium)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClick = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBtn)
        addTitleLbl()

        aBtn.setOnClickListener { onClick() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addTitleLbl() {
        addActor(aLbl)
        aLbl.disable()
        aLbl.setBounds(72f, 25f, 168f, 22f)
    }

}