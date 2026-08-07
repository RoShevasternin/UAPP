package com.fimer.skintool.game.actors.button

import com.fimer.skintool.game.actors.button.base.AButtonStyles
import com.fimer.skintool.game.utils.GameColor
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame

open class AYellowButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    styleMsdf = MsdfStyle(gdxGame.msdfManager, gdxGame.msdfManager.fontNunitoSans_Black, 18f, GameColor.black_0F0F0F),
    style     = AButtonStyles.AnimTexture.YELLOW,
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