package com.vidtools.framefall.game.screens.sheets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.AppEvent
import com.vidtools.framefall.core.model.EventKind
import com.vidtools.framefall.game.actors.ui.AButtonGhost
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ASheet
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.controller.DownloadController.Submit
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// EventsSheet — дзвіночок на Home (VELDAN, 07.10.2026): останні завантаження
// (готово / помилка) і аудіолінки, які апка зловила з буфера. Лише всередині апки:
// без системних сповіщень і без жодних дозволів.
//   • готово  → тап грає трек;
//   • помилка → тап повторює завантаження;
//   • лінк    → тап завантажує (RSS — відкриває шторку подкасту).
// Відкрили шторку — усе позначено переглянутим, крапка на дзвіночку зникає.
// ─────────────────────────────────────────────────────────────────────────────
class EventsSheet(private val rs: FramefallScreen) : ASheet(rs) {

    private val rowH = px(58f)
    private val gap = px(6f)
    private val maxRows = 8
    private val events: List<AppEvent> get() = gdxGame.model.state.events.take(maxRows)

    init {
        gdxGame.model.update { it.copy(eventsSeenAt = System.currentTimeMillis()) }
    }

    override val contentHeight: Float get() {
        val n = events.size
        return px(30f) + px(12f) + if (n == 0) px(56f) else n * rowH + (n - 1) * gap
    }

    override fun buildContent(w: Float) {
        var y = contentHeight
        val title = lbl(Copy.Events.TITLE, msdf.bold(17f))
        title.setPosition(0f, y - px(30f) + (px(30f) - title.height) / 2f); add(title)
        if (events.isNotEmpty()) {
            val clear = AButtonGhost(screen, Copy.Events.CLEAR, small = true)
            clear.setSize(clear.label.width + px(24f), px(30f)); clear.setPosition(w - clear.width, y - px(30f))
            clear.onClick { gdxGame.model.update { it.copy(events = emptyList()) }; rebuild() }
            add(clear)
        }
        y -= px(30f) + px(12f)

        if (events.isEmpty()) {
            val e = lbl(Copy.Events.EMPTY, msdf.regular(13.5f, GameColor.muted_B3A293), Align.left).apply {
                setWrap(true); width = w; height = prefHeight
            }
            e.setPosition(0f, y - px(56f) + (px(56f) - e.height) / 2f); add(e)
            return
        }

        events.forEachIndexed { i, ev ->
            y -= rowH
            val row = object : ATap(screen, 0.985f) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, px(14f), GameColor.white_4))
                    val (region, col) = when (ev.kind) {
                        EventKind.DOWNLOADED  -> assets.ic_check to GameColor.ok_47E0A0
                        EventKind.STUDIO_DONE -> assets.ic_wand to GameColor.ok_47E0A0
                        EventKind.FAILED      -> assets.ic_x to GameColor.err_FF7A6B
                        EventKind.CLIP_LINK   -> assets.ic_clip to GameColor.accentText_FFB04A
                    }
                    val ph = height    // не всередині apply: там height — це сам кружок (0)
                    val circle = ARect(screen, 999f, Color(col.r, col.g, col.b, 0.14f)).apply { setBounds(px(10f), (ph - px(36f)) / 2f, px(36f), px(36f)) }
                    val ic = icon(region, px(18f), col); ic.setPosition(circle.x + (px(36f) - ic.width) / 2f, circle.y + (px(36f) - ic.height) / 2f)
                    val textX = px(56f); val textW = width - textX - px(12f)
                    val t = lbl(ev.title, msdf.semibold(14f)).ellipsize(textW)
                    val sub = when (ev.kind) {
                        EventKind.DOWNLOADED  -> "${Copy.Events.DOWNLOADED}${if (ev.detail.isNotBlank()) " · ${ev.detail}" else ""} · ${Fmt.ago(ev.at)}"
                        EventKind.STUDIO_DONE -> "${Copy.Events.STUDIO}${if (ev.detail.isNotBlank()) " · ${ev.detail}" else ""} · ${Fmt.ago(ev.at)}"
                        EventKind.FAILED      -> "${Copy.Events.FAILED} · ${Fmt.ago(ev.at)}"
                        EventKind.CLIP_LINK   -> "${Copy.Events.CLIP} · ${Fmt.ago(ev.at)}"
                    }
                    val s = lbl(sub, msdf.regular(11.5f, GameColor.muted_B3A293)).ellipsize(textW)
                    t.setPosition(textX, height / 2f + px(1f)); s.setPosition(textX, height / 2f - s.height - px(1f))
                    addActor(circle); addActor(ic); addActor(t); addActor(s)
                }
            }.onClick { act(ev) }
            row.setBounds(0f, y, w, rowH); add(row)
            if (i < events.lastIndex) y -= gap
        }
    }

    private fun act(ev: AppEvent) {
        when (ev.kind) {
            EventKind.DOWNLOADED, EventKind.STUDIO_DONE -> {
                val v = gdxGame.model.video(ev.videoId)
                if (v == null) { gdxGame.toast(Copy.Events.GONE); return }
                close(); gdxGame.openVideo(v)
            }
            EventKind.FAILED, EventKind.CLIP_LINK -> {
                val url = ev.url ?: return
                if (!gdxGame.bridge.isOnline()) { gdxGame.toast(Copy.Events.OFFLINE); return }
                close()
                gdxGame.downloads.submit(url) { s ->
                    when (s) {
                        is Submit.Choose  -> rs.openSheet(QualitySheet(rs, s))
                        is Submit.Error   -> gdxGame.toast(s.title)
                        else              -> {}
                    }
                }
            }
        }
    }
}
