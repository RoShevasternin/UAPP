package com.rbuxrds.counter.game.utils.actor

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.rbuxrds.counter.game.actors.button.base.AButtonBase
import com.rbuxrds.counter.game.manager.util.SoundUtil
import com.rbuxrds.counter.game.utils.gdxGame

fun Actor.setOnClickListener(sound: SoundUtil.AdvancedSound? = gdxGame.soundUtil.CLICK, block: (Actor) -> Unit) {
    addListener(object : InputListener() {
        var isWithin = false

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDragged(event, x, y, pointer)
            event?.stop()
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            isWithin = x in 0f..width && y in 0f..height
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            if (isWithin) {
                isWithin = false
                block(this@setOnClickListener)
                sound?.let { gdxGame.soundUtil.play(it) }
            }
        }
    })
}

fun Actor.setOnTouchListener(
    sound : SoundUtil.AdvancedSound? = gdxGame.soundUtil.CLICK,
    radius: Float = 10f,
    block : (Actor) -> Unit
) {
    addListener(object : InputListener() {
        private var startX = 0f
        private var startY = 0f

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            startX = x
            startY = y
            return true
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            val dx = x - startX
            val dy = y - startY
            if (dx * dx + dy * dy <= radius * radius) {
                sound?.let { gdxGame.soundUtil.play(it) }
                block(this@setOnTouchListener)
            }
        }
    })
}

fun Actor.getTopParent(root: Group): Group {
    var top = this.parent

    if (top == root) return root

    while (top.parent != root) {
        top = top.parent
    }

    return top
}

// ------------------------------------------------------------------------
// disable | enable
// ------------------------------------------------------------------------

fun Actor.disable() = when(this) {
    is AButtonBase -> disable()
    else           -> touchable = Touchable.disabled
}

fun Actor.enable() = when(this) {
    is AButtonBase -> enable()
    else           -> touchable = Touchable.enabled
}

// ------------------------------------------------------------------------
// Default Params
// ------------------------------------------------------------------------

fun Actor.setBounds(bounds: Rectangle) {
    with(bounds) { setBounds(x, y, width, height) }
}

fun Actor.setBounds(position: Vector2, size: Vector2) {
    setBounds(position.x, position.y, size.x, size.y)
}

fun Actor.setPosition(position: Vector2) {
    setPosition(position.x, position.y)
}

fun Actor.setOrigin(vector: Vector2) {
    setOrigin(vector.x, vector.y)
}

fun Actor.setSize(vector: Vector2) {
    setSize(vector.x, vector.y)
}

// ------------------------------------------------------------------------
// Animations
// ------------------------------------------------------------------------
fun Actor.animShow(time: Float = 0f, block: () -> Unit = {}) {
    addAction(Actions.sequence(
        Actions.fadeIn(time),
        Actions.run(block)
    ))
}
fun Actor.animHide(time: Float = 0f, block: () -> Unit = {}) {
    addAction(Actions.sequence(
        Actions.fadeOut(time),
        Actions.run(block)
    ))
}
fun Actor.animShowAndEnable(time: Float = 0f, block: () -> Unit = {}) {
    animShow(time) {
        enable()
        block.invoke()
    }
}

fun Actor.animHideAndDisable(time: Float = 0f, block: () -> Unit = {}) {
    disable()
    animHide(time) { block.invoke() }
}
fun Actor.animMoveTo(
    x: Float, y: Float,
    time: Float = 0f,
    interpolation: Interpolation = Interpolation.linear,
    block: () -> Unit = {}
) {
    addAction(
        Actions.sequence(
            Actions.moveTo(x, y, time, interpolation),
            Actions.run { block() }
        ))
}
fun Actor.animDelay(time: Float, block: () -> Unit = {}) {
    addAction(
        Actions.sequence(
        Actions.delay(time),
        Actions.run { block.invoke() }
    ))
}
fun Actor.animRotateTo(
    rotation: Float,
    time: Float = 0f,
    interpolation: Interpolation = Interpolation.linear,
    block: () -> Unit = {}) {
    addAction(Actions.sequence(
        Actions.rotateTo(rotation, time, interpolation),
        Actions.run(block)
    ))
}
