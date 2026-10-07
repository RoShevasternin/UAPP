package com.redwave.downloader.game.screens.tabs

import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.game.actors.layout.AColumn
import com.redwave.downloader.game.actors.layout.ADyn
import com.redwave.downloader.game.actors.ui.AButtonRed
import com.redwave.downloader.game.actors.ui.AIconButton
import com.redwave.downloader.game.actors.ui.AInputField
import com.redwave.downloader.game.actors.ui.AProgressBar
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASegmented
import com.redwave.downloader.game.actors.ui.ATrackRow
import com.redwave.downloader.game.screens.AppScreen
import com.redwave.downloader.game.screens.sheets.TrackMenuSheet
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// LibraryTab — пошук, Songs/Podcasts, Shuffle all, список (нові зверху; той, що
// грає, — redHi + AEqBars), плашка сховища Music/Redwave.
// ─────────────────────────────────────────────────────────────────────────────
class LibraryTab(app: AppScreen) : ATabPage(app) {

    private val m = gdxGame.model
    private var seg = 0
    private var query = ""
    private var listKey = 0

    override fun build(col: AColumn) {
        col.addActor(header())
        col.addActor(AInputField(app, Copy.Library.SEARCH_HINT, assets.ic_search, isUrl = false, bg = GameColor.card_170F12).apply {
            setSize(W, px(44f))
            onChange = { query = it; listKey++ }
            onSubmit = { query = it; listKey++ }
        })
        col.addActor(ADyn(app, W, { "${m.version}:$seg" }) { w ->
            val s = ASegmented(app, listOf(Copy.Library.SONGS to "${m.songs.size}", Copy.Library.PODCASTS to "${m.podcasts.size}"), seg) { seg = it; listKey++ }
            s.setSize(w, px(44f)); addActor(s); s.height
        })
        col.addActor(actions())
        col.addActor(ADyn(app, W, { "${m.version}:$listKey" }) { w -> buildList(this, w) })
        col.addActor(ADyn(app, W, { m.version }) { w -> buildStorage(this, w) })
    }

    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val t = lbl(Copy.Library.TITLE, msdf.disp(30f)); t.setPosition(0f, (height - t.height) / 2f); addActor(t)
            val pw = width; val ph = height; val b = AIconButton(app, assets.ic_sliders).apply { setBounds(pw - px(38f), 0f, px(38f), px(38f)) }
            b.onClick { gdxGame.toast("Sorted by ${Copy.Library.SORT_RECENT.lowercase()}") }
            addActor(b)
        }
    }.apply { setSize(W, px(38f)) }

    private fun actions() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val sh = AButtonRed(app, Copy.Library.SHUFFLE_ALL, assets.ic_shuffle, small = true)
            sh.setSize(sh.label.width + px(14f) * 2 + px(16f) + px(8f), px(36f))
            sh.setPosition(0f, 0f)
            sh.onClick { if (m.songs.isEmpty()) gdxGame.toast(Copy.Library.EMPTY) else gdxGame.player.shuffleAll() }
            val r = lbl(Copy.Library.SORT_RECENT, msdf.regular(12.5f, GameColor.muted_A8949B)); r.setPosition(width - r.width, (height - r.height) / 2f)
            addActor(sh); addActor(r)
        }
    }.apply { setSize(W, px(36f)) }

    private fun buildList(g: ADyn, w: Float): Float {
        val q = query.trim().lowercase()
        val base = if (seg == 0) m.songs else m.podcasts
        val list = base.filter { q.isEmpty() || "${it.title} ${it.artist}".lowercase().contains(q) }
        if (list.isEmpty()) {
            val e = emptyText(if (q.isNotEmpty()) Copy.Library.noMatches(query.trim()) else Copy.Library.EMPTY, w)
            g.addActor(e); e.setPosition(0f, 0f)
            return e.height
        }
        val rowH = px(62f); val gap = px(2f)
        val h = list.size * rowH + (list.size - 1) * gap
        var y = h
        list.forEach { t ->
            y -= rowH
            val row = ATrackRow(app, t) { tr -> openMenu(tr) }.apply { setBounds(0f, y, w, rowH) }
            row.onClick { gdxGame.player.play(t) }
            g.addActor(row)
            y -= gap
        }
        return h
    }

    private fun openMenu(t: Track) {
        app.openSheet(TrackMenuSheet(app, t) { tr ->
            gdxGame.ringtoneTrackId = tr.id
            app.select(AppScreen.TAB_RINGTONE)
        })
    }

    private fun buildStorage(g: ADyn, w: Float): Float {
        val pad = px(12f); val h = px(12f + 16f + 8f + 5f + 12f)
        g.addActor(ARect(app, px(16f), GameColor.card_170F12, stroke = GameColor.line_white_7).apply { setSize(w, h) })
        val info = gdxGame.bridge.storageInfo()
        val tot = m.totalBytes
        val ic = icon(assets.ic_folder, px(14f), GameColor.muted_A8949B)
        val folder = lbl(Copy.Library.STORAGE_FOLDER, msdf.regular(12f, GameColor.muted_A8949B))
        val right = lbl("${Fmt.size(tot)} · ${Fmt.size(info.freeBytes)} free", msdf.semibold(12f))
        val topY = h - pad - px(16f)
        ic.setPosition(pad, topY + (px(16f) - ic.height) / 2f); folder.setPosition(pad + px(20f), topY + (px(16f) - folder.height) / 2f)
        right.setPosition(w - pad - right.width, folder.y)
        g.addActor(ic); g.addActor(folder); g.addActor(right)
        val bar = AProgressBar(app).apply { setBounds(pad, pad, w - pad * 2, px(5f)) }
        val frac = if (info.freeBytes + tot > 0) tot.toFloat() / (info.freeBytes + tot) else 0f
        g.addActor(bar); bar.set(frac.coerceIn(0.03f, 1f), immediate = true)
        return h
    }
}
