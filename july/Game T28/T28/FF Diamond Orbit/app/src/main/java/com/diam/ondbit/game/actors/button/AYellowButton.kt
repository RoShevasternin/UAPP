package com.diam.ondbit.game.actors.button

import com.diam.ondbit.game.actors.button.base.AButtonStyles
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame

open class AYellowButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    styleMsdf = MsdfStyle(gdxGame.msdfManager, gdxGame.msdfManager.fontSpaceGrotesk_Bold, 18f, GameColor.black_07021A),
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