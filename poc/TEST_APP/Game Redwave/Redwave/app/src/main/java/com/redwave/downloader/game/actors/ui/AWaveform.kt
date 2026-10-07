package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.redwave.downloader.core.ringtone.CutHandle
import com.redwave.downloader.core.ringtone.CutSelection
import com.redwave.downloader.core.ringtone.RingtoneCut
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AWaveform — хвиля різака: 64 стовпчики (вибрані — red, інші — white 17 %),
// підсвітка виділення red 13 %, ручки: лінія 2 + коло r 7 (біле, обводка red 3).
// Драг: найближча ручка, межі RingtoneCut.drag() (3…40 с).
// ─────────────────────────────────────────────────────────────────────────────
class AWaveform(screen: AdvancedScreen) : AShape(screen) {

    var peaks = FloatArray(64) { 0.15f }
    var durationMs = 1L
    var sel = CutSelection(0, 1)
    var onChange: (CutSelection) -> Unit = {}

    private var handle = CutHandle.START
    private val selColor = GameColor.red_13
    private val offColor = Color(1f, 1f, 1f, 0.17f)

    init {
        touchable = Touchable.enabled
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val ms = RingtoneCut.fractionToMs(x / width, durationMs)
                handle = RingtoneCut.nearestHandle(sel, ms)
                drag(x); event.stop(); return true
            }
            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) { drag(x) }
        })
    }

    private fun drag(x: Float) {
        val ms = RingtoneCut.fractionToMs(x / width, durationMs)
        val n = RingtoneCut.drag(sel, handle, ms, durationMs)
        if (n != sel) { sel = n; onChange(n) }
    }

    override fun drawShape(alpha: Float) {
        val n = peaks.size
        val a = RingtoneCut.msToFraction(sel.startMs, durationMs)
        val b = RingtoneCut.msToFraction(sel.endMs, durationMs)
        val ax = x + a * width; val bx = x + b * width
        col(selColor, alpha); drawer.filledRectangle(ax, y, bx - ax, height)

        val step = width / n
        val bw = step - px(2f)
        for (i in 0 until n) {
            val f = (i + 0.5f) / n
            val h = (peaks[i] * height * 0.82f).coerceAtLeast(px(6f))
            col(if (f in a..b) GameColor.wave_FF3D55 else offColor, alpha)
            drawer.filledRectangle(x + i * step + px(1f), y + (height - h) / 2f, bw, h)
        }
        listOf(ax, bx).forEach { hx ->
            col(Color.WHITE, alpha); drawer.line(hx, y, hx, y + height, px(2f))
            val cy = y + px(9f)
            col(GameColor.red_FF2E4D, alpha); drawer.filledCircle(hx, cy, px(7f))
            col(Color.WHITE, alpha); drawer.filledCircle(hx, cy, px(7f) - px(2.2f))
        }
    }
}
