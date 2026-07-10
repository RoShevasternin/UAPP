package com.zahbx.blitzrbx.game.actors.button

import com.zahbx.blitzrbx.game.actors.button.base.AButtonStyles
import com.zahbx.blitzrbx.game.utils.GameColor
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.font.FontParameter

open class AGreenGrayButton(screen: AdvancedScreen) : ATextButtonTexture(
    screen = screen,
    text = "",
    color = GameColor.background,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_Bold,
    style = AButtonStyles.Texture.GREEN_GRAY,
) {
    override fun enable() {
        super.enable()
        label.setText("Next")
        label.setLabelColor(GameColor.background)
    }

    override fun disable() {
        super.disable()
        label.setText("Count Now")
        label.setLabelColor(GameColor.white_10)
    }
}