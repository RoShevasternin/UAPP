package com.rbxrush.rushrbx.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.rbxrush.rushrbx.game.actors.button.base.AButtonStyles
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.actor.setFontColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontParameter

open class ABlueButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Fredoka_Bold,
    style     = AButtonStyles.Anim.BLUE,
) {

    override fun enable() {
        super.enable()
        label.setFontColor(Color.WHITE)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_817E78)
    }
}