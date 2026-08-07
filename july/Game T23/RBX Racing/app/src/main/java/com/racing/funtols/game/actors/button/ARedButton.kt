package com.racing.funtols.game.actors.button

import com.racing.funtols.game.actors.button.base.AButtonStyles
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

open class ARedButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    styleMsdf = MsdfStyle(gdxGame.msdfManager, gdxGame.msdfManager.fontBarlow_Bold, 18f),
    style     = AButtonStyles.Anim.RED,
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