package com.rbuxrds.counterds.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.game.actors.button.base.AButtonStyles
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.actor.disable
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.font.FontParameter
import com.rbuxrds.counterds.util.log

// ABlueButton.kt — використовує ATextButton з новим стилем
open class ABlueButton(
    override val screen: AdvancedScreen,
    text: String,
) : AdvancedGroup() {

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    private val btn = ATextButton(
        screen    = screen,
        text      = text,
        color     = Color.WHITE,
        parameter = parameter,
        generator = screen.fontGenerator_InterTight_Bold,
        style     = AButtonStyles.NEXT,  // ← стиль синьої кнопки
    )

    var onClick = {}

    override fun addActorsOnGroup() {
        addAndFillActor(btn)
        btn.setOnClickListener { onClick() }
    }

    fun disableBtn() {
        btn.disable()
    }

    fun enableBtn() {
        btn.enable()
    }
}