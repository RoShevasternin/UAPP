package com.rbuxrds.counter.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.rbuxrds.counter.game.utils.actor.addAndFillActors
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// Текстурна кнопка
open class AButtonTexture(
    override val screen: AdvancedScreen,
    style: Style,
) : AButtonBase(screen) {

    data class Style(
        val default : Drawable,
        val pressed : Drawable = default,
        val disabled: Drawable = default,
    )

    private val defaultImage  = Image(style.default)
    private val pressedImage  = Image(style.pressed).apply  { color.a = 0f }
    private val disabledImage = Image(style.disabled).apply { color.a = 0f }

    override fun addActorsOnGroup() {
        addAndFillActors(listOf(defaultImage, pressedImage, disabledImage))
        super.addActorsOnGroup()
    }

    override fun press() {
        defaultImage.clearActions(); pressedImage.clearActions()
        defaultImage.animHide(0.05f); pressedImage.animShow(0.05f)
    }

    override fun unpress() {
        defaultImage.clearActions(); pressedImage.clearActions()
        defaultImage.animShow(0.4f); pressedImage.animHide(0.4f)
    }

    override fun disable() {
        touchable = Touchable.disabled
        defaultImage.clearActions(); pressedImage.clearActions(); disabledImage.clearActions()
        defaultImage.animHide(); pressedImage.animHide(); disabledImage.animShow()
    }

    override fun enable() {
        touchable = Touchable.enabled
        defaultImage.clearActions(); pressedImage.clearActions(); disabledImage.clearActions()
        defaultImage.animShow(); pressedImage.animHide(); disabledImage.animHide()
    }

    fun setStyle(style: Style) {
        defaultImage.drawable  = style.default
        pressedImage.drawable  = style.pressed
        disabledImage.drawable = style.disabled
    }
}