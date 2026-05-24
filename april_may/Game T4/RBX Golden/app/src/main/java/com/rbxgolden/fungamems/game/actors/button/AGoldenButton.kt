package com.rbxgolden.fungamems.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.rbxgolden.fungamems.game.actors.button.base.AButtonStyles
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontParameter

open class AGoldenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    color     = Color.BLACK,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
    generator = screen.fontGenerator_Bold,
    style     = AButtonStyles.AnimTexture.GOLDEN,
) {

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
    }
}