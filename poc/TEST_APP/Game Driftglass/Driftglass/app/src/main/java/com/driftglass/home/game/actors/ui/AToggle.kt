package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.px

/** .tog: 40×24, кружок 18; увімкнений — бірюзовий. Лише вигляд: тап обробляє батько. */
class AToggle(override val screen: AdvancedScreen, on: Boolean) : AdvancedGroup() {

    private val track = ARect(screen, 999f, Color(1f, 1f, 1f, 0.2f))
    private val knob  = ARect(screen, 999f, Color.WHITE)
    private val inset = px(3f)
    private val k     = px(18f)

    var isOn = on
        set(v) { field = v; apply(true) }

    init { setSize(px(40f), px(24f)) }

    override fun addActorsOnGroup() {
        addAndFillActor(track)
        addActor(knob)
        knob.setSize(k, k)
        apply(false)
    }

    private fun apply(animate: Boolean) {
        val x = if (isOn) width - inset - k else inset
        track.fill(if (isOn) GameColor.teal_5FF2D1 else Color(1f, 1f, 1f, 0.2f))
        knob.clearActions()
        if (animate) knob.addAction(Actions.moveTo(x, inset, 0.2f, Interpolation.pow2Out)) else knob.setPosition(x, inset)
    }
}
