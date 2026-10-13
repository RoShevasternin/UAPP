package com.vidtools.framefall.game.screens.tabs

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.layout.ADyn
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.AProgressBar
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.screens.AppScreen
import com.vidtools.framefall.game.screens.sheets.VideoMenuSheet
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// LibraryTab — усе, що на телефоні (.lib у прототипі): плашка теки Movies/Framefall
// (скільки займає, скільки вільно), фільтри All / Videos / Audio / Studio, сітка
// 2 колонки: мініатюра 16:9 + назва + «MP4 · 61.9 MB» + ⋮ (меню відео).
// ─────────────────────────────────────────────────────────────────────────────
class LibraryTab(app: AppScreen) : ATabPage(app) {

    private var filter = 0

    override fun build(col: AColumn) {
        col.addActor(lbl(Copy.Library.TITLE, msdf.disp(28f)).apply { height = px(40f) })
        col.addActor(ADyn(app, W, { gdxGame.model.state.library.size to gdxGame.model.totalBytes }) { w -> buildStorage(this, w) })
        col.addActor(ADyn(app, W, { filter }) { w -> buildFilters(this, w) })
        col.addActor(ADyn(app, W, { filter to gdxGame.model.state.library.map { it.id to it.watchedMs / 10_000 } }) { w -> buildGrid(this, w) })
    }

    private fun buildStorage(g: ADyn, w: Float): Float {
        val m = gdxGame.model
        val info = gdxGame.bridge.storageInfo()
        val pad = px(14f); val h = px(84f)
        g.addActor(ARect(app, px(18f), GameColor.card_16100C, stroke = GameColor.line_white_7).apply { setSize(w, h) })
        val folder = lbl(Copy.Library.FOLDER, msdf.semibold(13f, GameColor.muted_B3A293))
        val size = lbl(Fmt.size(m.totalBytes), msdf.bold(14f))
        folder.setPosition(pad, h - pad - folder.height); size.setPosition(w - pad - size.width, h - pad - size.height)
        val bar = AProgressBar(app).apply { setBounds(pad, h / 2f - px(5f), w - pad * 2, px(5f)) }
        val used = if (info.totalBytes > 0) (m.totalBytes.toFloat() / info.totalBytes).coerceIn(0.02f, 1f) else 0f
        bar.set(used, immediate = true)
        val files = lbl(Copy.Library.files(m.state.library.size), msdf.regular(12f, GameColor.muted_B3A293))
        val free = lbl(Copy.Library.free(Fmt.size(info.freeBytes)), msdf.regular(12f, GameColor.muted_B3A293))
        files.setPosition(pad, pad - px(3f)); free.setPosition(w - pad - free.width, pad - px(3f))
        g.addActor(folder); g.addActor(size); g.addActor(bar); g.addActor(files); g.addActor(free)
        return h
    }

    private fun buildFilters(g: ADyn, w: Float): Float {
        var x = 0f
        Copy.Library.FILTERS.forEachIndexed { i, name ->
            val c = AChip(app, name, AChip.Kind.MOOD).apply { isOn = i == filter }
            c.onClick { filter = i }
            c.setPosition(x, 0f); g.addActor(c)
            x += c.width + px(8f)
        }
        return AChip(app, "All", AChip.Kind.MOOD).height
    }

    private fun items(): List<Video> {
        val m = gdxGame.model
        return when (filter) { 1 -> m.videos; 2 -> m.audio; 3 -> m.studio; else -> m.all }
    }

    private fun buildGrid(g: ADyn, w: Float): Float {
        val list = items()
        if (list.isEmpty()) {
            val e = emptyText(if (gdxGame.model.state.library.isEmpty()) Copy.Library.EMPTY else Copy.Library.EMPTY_FILTER, w)
            g.addActor(e); return e.height
        }
        val gap = px(12f); val cw = (w - gap) / 2f; val th = cw * 9f / 16f
        val ch = th + px(8f) + px(18f) + px(15f)
        val rows = (list.size + 1) / 2
        val h = rows * ch + (rows - 1) * px(16f)
        list.forEachIndexed { i, v ->
            val r = i / 2; val c = i % 2
            val card = object : ATap(app, 0.97f) {
                override fun addContent() {
                    val ph = height
                    val t = AVideoThumb(app, px(12f)).apply { setBounds(0f, ph - th, cw, th) }
                    t.setVideo(v); addActor(t)
                    val more = object : ATap(app, 0.9f) {
                        override fun addContent() { val i2 = icon(assets.ic_more, px(18f), GameColor.muted_B3A293); addActor(i2); i2.setPosition((width - i2.width) / 2f, (height - i2.height) / 2f) }
                    }.onClick { app.openSheet(VideoMenuSheet(app, v)) }
                    more.setBounds(cw - px(26f), 0f, px(26f), px(40f)); addActor(more)
                    val name = lbl(v.title, msdf.semibold(13f)).ellipsize(cw - px(28f))
                    val meta = lbl("${v.format} · ${Fmt.size(v.sizeBytes)}", msdf.regular(11.5f, GameColor.muted_B3A293)).ellipsize(cw - px(28f))
                    name.setPosition(0f, t.y - px(8f) - name.height); meta.setPosition(0f, name.y - meta.height + px(2f))
                    addActor(name); addActor(meta)
                }
            }.onClick { gdxGame.openVideo(v) }
            card.onLongPress = { app.openSheet(VideoMenuSheet(app, v)); true }
            card.setBounds(c * (cw + gap), h - (r + 1) * ch - r * px(16f), cw, ch)
            g.addActor(card)
        }
        return h
    }

    override fun onShown() {}
}
