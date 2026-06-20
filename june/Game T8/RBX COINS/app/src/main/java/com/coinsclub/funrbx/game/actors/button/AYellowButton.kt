package com.coinsclub.funrbx.game.actors.button

import com.coinsclub.funrbx.game.actors.button.base.AButtonStyles
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.actor.setFontColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow

open class AYellowButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = GameColor.white_FFF5E3,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setBorderAndShadow().setSize(18),
    generator = screen.fontGenerator_LuckiestGuy_Regular,
    style     = AButtonStyles.Anim.YELLOW,
) {

    override fun enable() {
        super.enable()
        label.setFontColor(GameColor.white_FFF5E3)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_A5A5A6)
    }
}