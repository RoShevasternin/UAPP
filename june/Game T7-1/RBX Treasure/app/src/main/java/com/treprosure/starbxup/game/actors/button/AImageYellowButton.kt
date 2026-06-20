package com.treprosure.starbxup.game.actors.button

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.treprosure.starbxup.game.actors.button.base.AButtonAnim
import com.treprosure.starbxup.game.actors.button.base.AButtonStyles
import com.treprosure.starbxup.game.actors.layout.AlignH
import com.treprosure.starbxup.game.actors.layout.AlignV
import com.treprosure.starbxup.game.utils.actor.addActorAligned
import com.treprosure.starbxup.game.utils.actor.setSize
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen

open class AImageYellowButton(
    screen : AdvancedScreen,
    val texture: Drawable,
    val size   : Vector2,
) : AButtonAnim(
    screen    = screen,
    style     = AButtonStyles.Anim.YELLOW,
) {

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val img = Image(texture)
        img.setSize(size)
        addActorAligned(img, AlignH.CENTER, AlignV.CENTER)
    }

}