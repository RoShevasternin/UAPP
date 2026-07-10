package com.zahbx.blitzrbx.game.actors.button

import com.zahbx.blitzrbx.game.actors.button.base.AButtonStyles
import com.zahbx.blitzrbx.game.utils.GameColor
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.font.FontParameter

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = GameColor.background,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Medium,
    style     = AButtonStyles.Anim.GREEN,
) {

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
    }
}