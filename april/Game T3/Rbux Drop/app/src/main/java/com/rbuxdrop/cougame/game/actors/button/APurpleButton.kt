package com.rbuxdrop.cougame.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.rbuxdrop.cougame.game.actors.button.base.AButtonStyles
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter

open class APurpleButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Medium,
    style     = AButtonStyles.Anim.PURPLE,
) {

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
    }
}