package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.px

/** .tgl i: 34×20, кружок 14; увімкнений — red. Лише вигляд: тап обробляє батько. */
class AToggle(override val screen: AdvancedScreen, on: Boolean) : AdvancedGroup() {

    private val track = ARect(screen, 999f, Color(1f, 1f, 1f, 0.15f))
    private val knob  = ARect(screen, 999f, Color.WHITE)
    private val inset = px(3f)
    private val k     = px(14f)

    var isOn = on
        set(v) { field = v; apply(true) }

    init { setSize(px(34f), px(20f)) }

    override fun addActorsOnGroup() {
        addAndFillActor(track)
        addActor(knob)
        knob.setSize(k, k)
        apply(false)
    }

    private fun apply(animate: Boolean) {
        val x = if (isOn) width - inset - k else inset
        track.fill(if (isOn) GameColor.red_FF2E4D else Color(1f, 1f, 1f, 0.15f))
        knob.clearActions()
        if (animate) knob.addAction(Actions.moveTo(x, inset, 0.2f, Interpolation.pow2Out)) else knob.setPosition(x, inset)
    }
}
