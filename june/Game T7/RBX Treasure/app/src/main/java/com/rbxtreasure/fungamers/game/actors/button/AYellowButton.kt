package com.rbxtreasure.fungamers.game.actors.button

import com.rbxtreasure.fungamers.game.actors.button.base.AButtonStyles
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontParameter

open class AYellowButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = GameColor.brown_291D0E,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_AlanSans_Bold,
    style     = AButtonStyles.Anim.YELLOW,
) {

//    override fun enable() {
//        super.enable()
//        label.setFontColor(Color.WHITE)
//    }
//
//    override fun disable() {
//        super.disable()
//        label.setFontColor(GameColor.gray_818181)
//    }
}