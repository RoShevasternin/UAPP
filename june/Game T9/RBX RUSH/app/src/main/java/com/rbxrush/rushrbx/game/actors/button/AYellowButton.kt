package com.rbxrush.rushrbx.game.actors.button

import com.rbxrush.rushrbx.game.actors.button.base.AButtonStyles
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontParameter

open class AYellowButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = GameColor.black_2C2C2C,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Fredoka_Bold,
    style     = AButtonStyles.Anim.YELLOW,
) {

//    override fun enable() {
//        super.enable()
//        label.setFontColor(GameColor.black_2C2C2C)
//    }
//
//    override fun disable() {
//        super.disable()
//        label.setFontColor(GameColor.gray_A5A5A6)
//    }
}