package com.rbuxrds.counter.game.actors.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.button.base.AButtonAnim
import com.rbuxrds.counter.game.actors.button.base.AButtonStyles
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.disable
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter

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