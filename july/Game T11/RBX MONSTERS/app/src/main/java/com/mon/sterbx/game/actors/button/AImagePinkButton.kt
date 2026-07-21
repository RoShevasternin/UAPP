package com.mon.sterbx.game.actors.button

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.mon.sterbx.game.actors.button.base.AButtonAnim
import com.mon.sterbx.game.actors.button.base.AButtonStyles
import com.mon.sterbx.game.actors.layout.AlignH
import com.mon.sterbx.game.actors.layout.AlignV
import com.mon.sterbx.game.utils.actor.addActorAligned
import com.mon.sterbx.game.utils.actor.setSize
import com.mon.sterbx.game.utils.advanced.AdvancedScreen

open class AImagePinkButton(
    screen : AdvancedScreen,
    val texture: Drawable,
    val size   : Vector2,
) : AButtonAnim(
    screen    = screen,
    style     = AButtonStyles.Anim.ORANGE,
) {

    private val img = Image()

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        updateIcon(texture, size)
    }

    fun updateIcon(texture: Drawable, size: Vector2) {
        img.remove()

        img.drawable = texture
        img.setSize(size)
        addActorAligned(img, AlignH.CENTER, AlignV.CENTER)
    }

}