package com.rbuxrds.counter.game.actors.button.base

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// Анімована кнопка
open class AButtonAnim(
    override val screen: AdvancedScreen,
    style: Style,
) : AButtonBase(screen) {

    data class Style(val default: Drawable)

    // Налаштування анімації
    var pressScale  = 0.93f
    var pressDim    = 0.75f
    var pressTime   = 0.08f
    var unpressTime = 0.20f

    private val image = Image(style.default)

    override fun addActorsOnGroup() {
        addAndFillActor(image)
        super.addActorsOnGroup()
    }

    override fun press() {
        clearActions()
        addAction(Actions.parallel(
            Actions.scaleTo(pressScale, pressScale, pressTime, Interpolation.fastSlow),
            Actions.color(Color(pressDim, pressDim, pressDim, 1f), pressTime)
        ))
    }

    override fun unpress() {
        clearActions()
        addAction(Actions.parallel(
            Actions.scaleTo(1f, 1f, unpressTime, Interpolation.fastSlow),
            Actions.color(Color.WHITE, unpressTime)
        ))
    }

    override fun disable() {
        touchable = Touchable.disabled
        clearActions()
        addAction(Actions.color(Color(0.5f, 0.5f, 0.5f, 1f), 0.15f))
    }

    override fun enable() {
        touchable = Touchable.enabled
        clearActions()
        addAction(Actions.color(Color.WHITE, 0.15f))
        unpress()
    }
}