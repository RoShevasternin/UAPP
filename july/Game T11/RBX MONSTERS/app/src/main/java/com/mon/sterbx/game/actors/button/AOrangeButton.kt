package com.mon.sterbx.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.mon.sterbx.game.actors.button.base.AButtonStyles
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.actor.setFontColor
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontParameter

open class AOrangeButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(24),
    generator = screen.fontGenerator_BeVietnamPro_BlackItalic,
    style     = AButtonStyles.Anim.ORANGE,
) {

//    override fun enable() {
//        super.enable()
//        label.setFontColor(GameColor.purple_9F0E59)
//    }
//
//    override fun disable() {
//        super.disable()
//        label.setFontColor(GameColor.gray_5B5B5B)
//    }
}