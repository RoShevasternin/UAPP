package com.rsbuxs.rcounbux.game.actors.button

import com.rsbuxs.rcounbux.game.actors.button.base.AButtonStyles
import com.rsbuxs.rcounbux.game.utils.GameColor
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter

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