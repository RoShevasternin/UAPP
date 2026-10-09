package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.px

/**
 * Повзунок Studio (.sl input, accent-color teal): доріжка 4, заповнення бірюзове, кружок 18.
 * Значення [min]..[max]; onChange — під час перетягування (живе оновлення шпалер).
 * event.stop() на touchDown — щоб шторка/скрол під повзунком не перехоплювали жест.
 */
class ASlider(
    override val screen: AdvancedScreen,
    private val min: Float,
    private val max: Float,
    value: Float,
    private val onChange: (Float) -> Unit,
) : AdvancedGroup() {

    var value = value.coerceIn(min, max)
        private set

    private val track = ARect(screen, 999f, GameColor.white_20)
    private val fill  = ARect(screen, 999f, GameColor.teal_5FF2D1)
    private val knob  = ARect(screen, 999f, Color.WHITE)
    private val kSize = px(18f)

    override fun addActorsOnGroup() {
        addActor(track); addActor(fill); addActor(knob)
        layoutParts()
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { event.stop(); set(x); return true }
            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) { set(x) }
        })
    }

    private fun set(x: Float) {
        val t = ((x - kSize / 2f) / (width - kSize)).coerceIn(0f, 1f)
        val v = min + (max - min) * t
        if (v != value) { value = v; layoutParts(); onChange(v) }
    }

    private fun layoutParts() {
        val th = px(4f)
        val t = (value - min) / (max - min)
        val kx = (width - kSize) * t
        track.setBounds(0f, (height - th) / 2f, width, th)
        fill.setBounds(0f, (height - th) / 2f, kx + kSize / 2f, th)
        knob.setBounds(kx, (height - kSize) / 2f, kSize, kSize)
    }
}
