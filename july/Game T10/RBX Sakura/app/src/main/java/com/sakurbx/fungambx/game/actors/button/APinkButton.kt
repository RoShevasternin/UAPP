package com.sakurbx.fungambx.game.actors.button

import com.sakurbx.fungambx.game.actors.button.base.AButtonStyles
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.actor.setFontColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontParameter

open class APinkButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = GameColor.purple_9F0E59,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Laila_Bold,
    style     = AButtonStyles.Anim.PINK,
) {

    override fun enable() {
        super.enable()
        label.setFontColor(GameColor.purple_9F0E59)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_5B5B5B)
    }
}