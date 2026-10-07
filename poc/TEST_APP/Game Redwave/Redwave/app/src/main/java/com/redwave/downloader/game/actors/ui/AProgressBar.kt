package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen

/**
 * .bar: висота 5, фон white 10 %, заповнення градієнтом red → coral.
 * Значення наближається плавно (як transition width .25s), не стрибками.
 */
class AProgressBar(
    override val screen: AdvancedScreen,
    from: Color = GameColor.red_FF2E4D,
    to: Color = GameColor.coral_FF8A70,
    back: Color = GameColor.white_10,
) : AdvancedGroup() {

    private val bg   = ARect(screen, 999f, back)
    private val fill = ARect(screen, 999f, from, to, angleCss = 90f)

    var target = 0f
    var shown  = 0f
        private set
    var smooth = true

    override fun addActorsOnGroup() {
        addAndFillActor(bg)
        addActor(fill)
        layoutFill()
    }

    fun set(value: Float, immediate: Boolean = false) {
        target = value.coerceIn(0f, 1f)
        if (immediate || !smooth) { shown = target; layoutFill() }
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (shown != target) {
            shown = MathUtils.lerp(shown, target, (delta * 10f).coerceAtMost(1f))
            if (kotlin.math.abs(shown - target) < 0.001f) shown = target
            layoutFill()
        }
    }

    private fun layoutFill() {
        val w = width * shown
        fill.isVisible = w > 0.5f
        fill.setBounds(0f, 0f, maxOf(w, height), height)
    }
}
