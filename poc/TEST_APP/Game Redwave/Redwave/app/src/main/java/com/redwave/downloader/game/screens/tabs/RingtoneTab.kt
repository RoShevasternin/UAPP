package com.redwave.downloader.game.screens.tabs

import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.ringtone.CutSelection
import com.redwave.downloader.core.ringtone.RingtoneCut
import com.redwave.downloader.core.ringtone.SaveAs
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.layout.AColumn
import com.redwave.downloader.game.actors.layout.ADyn
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.AButtonRed
import com.redwave.downloader.game.actors.ui.AChip
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASegmented
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.actors.ui.AToggle
import com.redwave.downloader.game.actors.ui.AWaveform
import com.redwave.downloader.game.screens.AppScreen
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// RingtoneTab — трек (Change = наступна пісня), хвиля з ручками, START/LENGTH/END,
// Fade in/out, Ringtone/Alarm/Notification, Preview (вся довжина фрагмента)
// і Save: без WRITE_SETTINGS — пояснення + екран налаштувань, інакше Transformer.
// ─────────────────────────────────────────────────────────────────────────────
class RingtoneTab(app: AppScreen) : ATabPage(app) {

    private val m = gdxGame.model
    private var trackId: String? = null
    private var sel = CutSelection(0, 0)
    private var fadeIn = true
    private var fadeOut = true
    private var saveAs = SaveAs.RINGTONE
    private var previewing = false
    private var saving = false
    private var key = 0

    private val track: Track? get() = m.track(trackId) ?: m.songs.firstOrNull()

    override fun onShown() {
        gdxGame.ringtoneTrackId?.let { id -> gdxGame.ringtoneTrackId = null; setTrack(m.track(id)) }
        if (trackId == null) setTrack(track)
    }

    private fun setTrack(t: Track?) {
        trackId = t?.id
        sel = RingtoneCut.default(t?.durationMs ?: 0)
        stopPreview()
        key++
    }

    override fun build(col: AColumn) {
        col.addActor(header())
        col.addActor(lbl(Copy.Ringtone.LEAD, msdf.regular(13.5f, GameColor.muted_A8949B)).apply { setWrap(true); width = W; setLineHeight(112f); height = prefHeight - px(8f) })
        col.addActor(ADyn(app, W, { "$key:${m.state.library.size}" }) { w -> buildBody(this, w) })
    }

    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val t = lbl(Copy.Ringtone.TITLE, msdf.disp(30f)); t.setPosition(0f, (height - t.height) / 2f); addActor(t)
            val chip = AChip(app, Copy.Ringtone.CHIP, AChip.Kind.RED, assets.ic_scissors); chip.setPosition(width - chip.width, (height - chip.height) / 2f); addActor(chip)
        }
    }.apply { setSize(W, px(38f)) }

    private lateinit var startL: AMsdfLabel
    private lateinit var lenL: AMsdfLabel
    private lateinit var endL: AMsdfLabel
    private var previewBtn: AButtonGhost? = null

    private fun buildBody(g: ADyn, w: Float): Float {
        val t = track
        if (t == null) {
            val e = emptyText("Download a track first: it will show up here.", w)
            g.addActor(e); return e.height
        }
        if (trackId != t.id) { trackId = t.id; sel = RingtoneCut.default(t.durationMs) }
        val gap = px(18f)
        val parts = listOf(trackCard(t, w), waveBox(t, w), toggles(w), segment(w), buttons(w))
        val h = parts.sumOf { it.height.toDouble() }.toFloat() + gap * (parts.size - 1)
        var y = h
        parts.forEach { p -> y -= p.height; p.setPosition(0f, y); g.addActor(p); y -= gap }
        return h
    }

    private fun trackCard(t: Track, w: Float) = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            addAndFillActor(ARect(app, px(18f), GameColor.card_170F12, stroke = GameColor.line_white_7))
            val pw = width; val ph = height; val c = ACover(app, px(12f)).apply { setBounds(px(10f), (ph - px(52f)) / 2f, px(52f), px(52f)) }; c.setTrack(t); addActor(c)
            val change = object : ATap(app, 0.94f) {
                override fun addContent() {
                    addAndFillActor(ARect(app, px(11f), GameColor.card2_21161A))
                    val l = lbl(Copy.Ringtone.CHANGE, msdf.bold(12.5f)); l.setPosition((width - l.width) / 2f, (height - l.height) / 2f); addActor(l)
                }
            }.onClick { nextTrack() }
            change.setBounds(width - px(10f) - px(64f), (height - px(32f)) / 2f, px(64f), px(32f)); addActor(change)
            val mx = px(10f + 52f + 12f); val mw = change.x - px(8f) - mx
            val b = lbl(t.title, msdf.semibold(14f)).ellipsize(mw)
            val s = lbl("${t.artist} · ${Fmt.time(t.durationMs)}", msdf.regular(12f, GameColor.muted_A8949B)).ellipsize(mw)
            b.setPosition(mx, height / 2f + px(1f)); s.setPosition(mx, height / 2f - s.height - px(1f))
            addActor(b); addActor(s)
        }
    }.apply { setSize(w, px(72f)) }

    private fun waveBox(t: Track, w: Float): AdvancedGroup {
        val pad = px(12f)
        val waveH = px(110f)
        val h = pad + px(14f) + px(10f) + waveH + px(10f) + px(40f) + px(14f)
        return object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                addAndFillActor(ARect(app, px(22f), GameColor.card_170F12, stroke = GameColor.line_white_7))
                addAndFillActor(ARect(app, px(22f), GameColor.red_08, GameColor.red_08.cpy().apply { a = 0f }, angleCss = 180f, mid = 0.6f))
                var y = height - pad - px(14f)
                val st = msdf.monoSemi(10.5f, GameColor.muted_A8949B)
                val l0 = lbl("0:00", st); val lm = lbl(Copy.Ringtone.DRAG_HINT, st); val l1 = lbl(Fmt.time(t.durationMs), st)
                l0.setPosition(pad, y); lm.setPosition((width - lm.width) / 2f, y); l1.setPosition(width - pad - l1.width, y)
                addActor(l0); addActor(lm); addActor(l1)
                y -= px(10f) + waveH
                val wave = AWaveform(app).apply {
                    setBounds(pad, y, this@RingtoneTab.W - pad * 2, waveH)
                    durationMs = t.durationMs.coerceAtLeast(1); sel = this@RingtoneTab.sel
                    onChange = { s -> this@RingtoneTab.sel = s; stopPreview(); refreshInfo() }
                }
                addActor(wave)
                gdxGame.bridge.waveformPeaks(t, 64) { p -> if (p.isNotEmpty()) wave.peaks = p }
                y -= px(10f) + px(40f)
                val cw = (width - pad * 2) / 3f
                listOf(Copy.Ringtone.START, Copy.Ringtone.LENGTH, Copy.Ringtone.END).forEachIndexed { i, label ->
                    val cap = lbl(label.uppercase(), msdf.monoSemi(10f, GameColor.muted_A8949B, spacing = 12f))
                    val v = lbl("0:00", msdf.style(gdxGame.msdfManager.fontArchivo_ExpandedExtraBold, 18f, if (i == 1) GameColor.redHi_FF5A6C else GameColor.text_FBF1F2) { letterSpacing = -1f })
                    val cx = pad + cw * i + cw / 2f
                    cap.setPosition(cx - cap.width / 2f, y + px(40f) - cap.height); v.setPosition(cx - v.width / 2f, y)
                    addActor(cap); addActor(v)
                    when (i) { 0 -> startL = v; 1 -> lenL = v; else -> endL = v }
                }
                refreshInfo()
            }
        }.apply { setSize(w, h) }
    }

    private fun refreshInfo() {
        if (!::startL.isInitialized) return
        listOf(startL to sel.startMs, lenL to sel.lengthMs, endL to sel.endMs).forEach { (l, ms) ->
            val cx = l.x + l.width / 2f
            l.setText(Fmt.time(ms)); l.pack(); l.x = cx - l.width / 2f
        }
    }

    private fun toggles(w: Float) = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val tw = (width - px(8f)) / 2f
            listOf(Copy.Ringtone.FADE_IN, Copy.Ringtone.FADE_OUT).forEachIndexed { i, label ->
                val tgl = AToggle(app, if (i == 0) fadeIn else fadeOut)
                val tap = object : ATap(app, 0.97f) {
                    override fun addContent() {
                        addAndFillActor(ARect(app, px(14f), GameColor.card_170F12, stroke = GameColor.line_white_7))
                        val l = lbl(label, msdf.semibold(13f)); l.setPosition(px(12f), (height - l.height) / 2f); addActor(l)
                        addActor(tgl); tgl.setPosition(width - px(12f) - tgl.width, (height - tgl.height) / 2f)
                    }
                }.onClick {
                    if (i == 0) { fadeIn = !fadeIn; tgl.isOn = fadeIn } else { fadeOut = !fadeOut; tgl.isOn = fadeOut }
                }
                tap.setBounds(i * (tw + px(8f)), 0f, tw, height); addActor(tap)
            }
        }
    }.apply { setSize(w, px(44f)) }

    private fun segment(w: Float) = ASegmented(app, Copy.Ringtone.SAVE_AS.map { it to null }, saveAs.ordinal) { i ->
        saveAs = SaveAs.entries[i]; key++
    }.apply { setSize(w, px(44f)) }

    private fun buttons(w: Float) = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val gw = (width - px(8f)) / 2.4f
            val prev = AButtonGhost(app, if (previewing) Copy.Ringtone.STOP else Copy.Ringtone.PREVIEW, if (previewing) assets.ic_pause_fill else assets.ic_play_fill)
            prev.setBounds(0f, 0f, gw, height); prev.onClick { togglePreview() }
            previewBtn = prev
            val kind = Copy.Ringtone.SAVE_AS[saveAs.ordinal]
            val save = AButtonRed(app, Copy.Ringtone.save(kind), assets.ic_check)
            save.setBounds(gw + px(8f), 0f, width - gw - px(8f), height); save.onClick { save() }
            addActor(prev); addActor(save)
        }
    }.apply { setSize(w, px(46f)) }

    // ------------------------------------------------------------------------
    // Дії
    // ------------------------------------------------------------------------
    private fun nextTrack() {
        val songs = m.songs
        if (songs.isEmpty()) return
        val i = songs.indexOfFirst { it.id == trackId }
        setTrack(songs[(i + 1) % songs.size])
    }

    private fun togglePreview() {
        val t = track ?: return
        if (previewing) { stopPreview(); return }
        previewing = true
        gdxGame.bridge.setRepeatOne(false)
        gdxGame.bridge.play(listOf(t), 0, sel.startMs)
        updatePreviewBtn()
    }

    private fun stopPreview() {
        if (!previewing) return
        previewing = false
        gdxGame.bridge.pause()
        updatePreviewBtn()
    }

    private fun updatePreviewBtn() {
        previewBtn?.setContent(if (previewing) Copy.Ringtone.STOP else Copy.Ringtone.PREVIEW, if (previewing) assets.ic_pause_fill else assets.ic_play_fill)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (previewing) {
            val s = gdxGame.player.snap
            if (s.trackId == trackId && s.positionMs >= sel.endMs) stopPreview()
        }
    }

    private fun save() {
        val t = track ?: return
        if (saving) return
        val b = gdxGame.bridge
        // Файл зберігаємо завжди (він з'явиться в системному виборі звуків); дозвіл
        // WRITE_SETTINGS потрібен лише щоб поставити його за замовчуванням.
        stopPreview()
        saving = true
        val kind = Copy.Ringtone.SAVE_AS[saveAs.ordinal]
        val canSet = b.canWriteSettings()
        b.saveRingtone(t, sel, fadeIn, fadeOut, saveAs) { r ->
            saving = false
            r.onSuccess {
                if (canSet) gdxGame.toast(Copy.Ringtone.savedToast(kind, t.title, Fmt.time(sel.lengthMs)))
                else { gdxGame.toast(Copy.Ringtone.savedNotSet(kind)); b.openWriteSettings() }
            }.onFailure { gdxGame.toast("Couldn’t save: ${it.message ?: "error"}") }
        }
    }
}
