package com.rbxgolden.fungamems.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.rbxgolden.fungamems.game.actors.button.base.AButtonStyles
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.actor.setFontColor
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontParameter

open class AGoldenGrayButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonTexture(
    screen    = screen,
    text      = text,
    color     = Color.BLACK,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Bold,
    style     = AButtonStyles.Texture.COUNT_NOW,
) {

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
    }

    override fun enable() {
        super.enable()
        label.setFontColor(Color.BLACK)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_5C)
    }

}