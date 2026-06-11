package com.skindustry.skinly.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.skindustry.skinly.game.actors.button.base.AButtonStyles
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.actor.setFontColor
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontParameter

open class AOrangeButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_SemiBold,
    style     = AButtonStyles.AnimTexture.ORANGE,
) {

    override fun enable() {
        super.enable()
        label.setFontColor(Color.WHITE)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_818181)
    }
}