package com.redwave.downloader.game.screens.sheets

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.eq.EqCurve
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.AChip
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.actors.ui.AToggle
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────────────────────
// EqSheet — «Equalizer» + On/Off, пресети (Flat, Bass Boost, Vocal, Lo-fi, Rock,
// + Custom після руху повзунка), 5 смуг −12…+12 dB, Done. Зміни — одразу в
// bridge.setEqualizer (через PlayerController.applyEq) і в AppState.
// ─────────────────────────────────────────────────────────────────────────────
class EqSheet(screen: AdvancedScreen) : ASheet(screen) {

    private val m = gdxGame.model
    private val bands = m.state.eqBandsDb.toFloatArray()
    private var preset = m.state.eqPreset
    private var enabled = m.state.eqEnabled
    private val valueLabels = ArrayList<AMsdfLabel>()
    private val sliders = ArrayList<Slider>()
    private val chips = HashMap<String, AChip>()

    private val rowH = px(34f)
    override val contentHeight: Float get() = px(30f) + px(14f) + px(33f) * 2 + px(6f) + px(12f) + 5 * rowH + px(12f) + px(44f)

    override fun buildContent(w: Float) {
        var y = contentHeight
        // ── Заголовок + тумблер ──
        val title = lbl(Copy.Player.EQUALIZER, msdf.bold(17f))
        y -= px(30f); title.setPosition(0f, y + (px(30f) - title.height) / 2f); add(title)
        val tgl = AToggle(screen, enabled)
        val tglLbl = lbl(if (enabled) Copy.Player.EQ_ON else Copy.Player.EQ_OFF, msdf.semibold(13f, GameColor.muted_A8949B))
        val tglTap = object : ATap(screen, 0.95f) {
            override fun addContent() { addActor(tglLbl); tglLbl.setPosition(0f, (height - tglLbl.height) / 2f); addActor(tgl); tgl.setPosition(width - tgl.width, (height - tgl.height) / 2f) }
        }.onClick {
            enabled = !enabled; tgl.isOn = enabled
            tglLbl.setText(if (enabled) Copy.Player.EQ_ON else Copy.Player.EQ_OFF); tglLbl.pack()
            save()
        }
        tglTap.setBounds(w - px(80f), y, px(80f), px(30f)); add(tglTap)

        // ── Пресети (flex-wrap) ──
        y -= px(14f)
        val names = EqCurve.PRESETS.keys.toList() + "Custom"
        var x = 0f; var rowTop = y
        names.forEach { n ->
            val c = AChip(screen, n, AChip.Kind.MOOD)
            if (x > 0f && x + c.width > w) { x = 0f; rowTop -= c.height + px(6f) }
            c.setPosition(x, rowTop - c.height)
            c.isOn = n == preset
            c.isVisible = n != "Custom" || preset == "Custom"
            c.onClick { if (n != "Custom") applyPreset(n) }
            chips[n] = c; add(c)
            x += c.width + px(6f)
        }
        y = rowTop - px(33f) - px(12f)

        // ── Смуги ──
        EqCurve.UI_LABELS.forEachIndexed { i, label ->
            y -= rowH
            val l = lbl(label, msdf.monoSemi(11.5f, GameColor.muted_A8949B)); l.setPosition(0f, y + (rowH - l.height) / 2f); add(l)
            val v = lbl(fmtDb(bands[i]), msdf.monoSemi(12f, GameColor.text_FBF1F2)); v.setPosition(w - v.width, y + (rowH - v.height) / 2f); add(v)
            valueLabels += v
            val s = Slider(i).apply { setBounds(px(66f), y, w - px(66f) - px(62f), rowH) }
            sliders += s; add(s)
        }

        // ── Done ──
        val done = AButtonGhost(screen, Copy.Player.DONE).apply { setBounds(0f, 0f, w, px(44f)) }
        done.onClick { close() }
        add(done)
    }

    private fun fmtDb(v: Float): String { val r = v.roundToInt(); return (if (r > 0) "+$r" else "$r") + " dB" }

    private fun applyPreset(name: String) {
        val p = EqCurve.PRESETS[name] ?: return
        p.copyInto(bands)
        preset = name
        refreshAll()
        save()
    }

    private fun refreshAll() {
        chips.forEach { (n, c) -> c.isOn = n == preset; if (n == "Custom") c.isVisible = preset == "Custom" }
        valueLabels.forEachIndexed { i, l -> val right = l.x + l.width; l.setText(fmtDb(bands[i])); l.pack(); l.x = right - l.width }
        sliders.forEach { it.layoutKnob() }   // пресет міняє всі смуги — повзунки теж
    }

    private fun save() {
        m.update { it.copy(eqPreset = preset, eqBandsDb = bands.toList(), eqEnabled = enabled) }
        gdxGame.player.applyEq()
    }

    /** Повзунок −12…+12: доріжка 4, заповнення від центру, кружок 18. */
    private inner class Slider(private val i: Int) : AdvancedGroup() {
        override val screen = this@EqSheet.screen
        private val track = ARect(screen, 999f, GameColor.white_10)
        private val fill  = ARect(screen, 999f, GameColor.red_FF2E4D)
        private val knob  = ARect(screen, 999f, com.badlogic.gdx.graphics.Color.WHITE, stroke = GameColor.red_FF2E4D, strokeWidth = px(3f))
        private val k = px(18f)

        override fun addActorsOnGroup() {
            addActor(track); addActor(fill); addActor(knob)
            track.setBounds(0f, (height - px(4f)) / 2f, width, px(4f))
            layoutKnob()
            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { drag(x); event.stop(); return true }
                override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) { drag(x) }
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) { save() }
            })
        }

        private fun drag(x: Float) {
            val f = ((x - k / 2f) / (width - k)).coerceIn(0f, 1f)
            val db = (MathUtils.lerp(EqCurve.UI_MIN_DB, EqCurve.UI_MAX_DB, f) * 2f).roundToInt() / 2f
            if (db == bands[i]) return
            bands[i] = db
            preset = "Custom"
            layoutKnob(); refreshAll()
            gdxGame.bridge.setEqualizer(enabled, bands)
        }

        fun layoutKnob() {
            if (width <= 0f) return
            val f = (bands[i] - EqCurve.UI_MIN_DB) / (EqCurve.UI_MAX_DB - EqCurve.UI_MIN_DB)
            val kx = f * (width - k)
            knob.setBounds(kx, (height - k) / 2f, k, k)
            val cx = width / 2f; val px0 = kx + k / 2f
            fill.setBounds(minOf(cx, px0), (height - px(4f)) / 2f, kotlin.math.abs(px0 - cx).coerceAtLeast(0.01f), px(4f))
        }
    }
}
