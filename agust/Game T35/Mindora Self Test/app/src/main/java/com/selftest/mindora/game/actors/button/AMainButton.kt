package com.selftest.mindora.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

open class AMainButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    styleMsdf = MsdfStyle(
        gdxGame.msdfManager,
        gdxGame.msdfManager.fontMontserrat_Bold,
        16f,
        Color.WHITE
    ),
    style = AButtonStyles.AnimTexture.MAIN,
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