package com.bossrbx.rbxcalculator.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.bossrbx.rbxcalculator.game.actors.button.base.AButtonStyles
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.setFontColor
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter

open class ABlueButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_FIRENIGHT,
    style     = AButtonStyles.AnimTexture.BLUE,
) {

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
    }

    override fun enable() {
        super.enable()
        label.setFontColor(Color.WHITE)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_333333)
    }
}