package com.vidtools.framefall.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.CatalogFilm
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.layout.ADyn
import com.vidtools.framefall.game.actors.layout.ARow
import com.vidtools.framefall.game.actors.ui.AButtonAccent
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.ACover
import com.vidtools.framefall.game.actors.ui.ADlButton
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.controller.DiscoverController.ItemState
import com.vidtools.framefall.game.screens.AppScreen
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// DiscoverTab — відкриті фільми, які можна зберегти й тримати (.disc у прототипі):
// шапка + чип «Free to keep», герой (Sintel), «Open movies» (Creative Commons),
// «Public domain» (класика і NASA), примітка про титри. Каталог — assets/catalog.json.
// ─────────────────────────────────────────────────────────────────────────────
class DiscoverTab(app: AppScreen) : ATabPage(app) {

    private val dc get() = gdxGame.discover

    override fun build(col: AColumn) {
        col.addActor(header())
        dc.featured?.let { f -> col.addActor(ADyn(app, W, { dc.stateOf(f)::class }) { w -> buildHero(this, w, f) }) }
        col.addActor(section(Copy.Discover.OPEN_MOVIES, Copy.Discover.OPEN_SUB, dc.openMovies))
        col.addActor(section(Copy.Discover.PUBLIC_DOMAIN, Copy.Discover.PD_SUB, dc.publicDomain))
        col.addActor(lbl(Copy.Discover.CREDIT_NOTE, msdf.regular(11.5f, GameColor.muted_B3A293), Align.left).apply { setWrap(true); width = W; height = prefHeight })
    }

    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val t = lbl(Copy.Discover.TITLE, msdf.disp(28f)); t.setPosition(0f, (height - t.height) / 2f); addActor(t)
            val chip = AChip(app, Copy.Discover.CHIP, AChip.Kind.GREEN, assets.ic_shield)
            chip.setPosition(width - chip.width, (height - chip.height) / 2f); addActor(chip)
        }
    }.apply { setSize(W, px(40f)) }

    // ------------------------------------------------------------------------
    // Герой
    // ------------------------------------------------------------------------
    private fun buildHero(g: ADyn, w: Float, f: CatalogFilm): Float {
        val h = px(250f)
        val pad = px(16f)
        val photo = ACover(app, px(22f)).apply { setSize(w, h) }
        g.addActor(photo); photo.setTexture(assets.FEAT_SINTEL)
        g.addActor(ARect(app, px(22f), GameColor.background.cpy().apply { a = 0.05f }, GameColor.background.cpy().apply { a = 0.92f }, angleCss = 180f, start = 0.25f, stroke = GameColor.white_10).apply { setSize(w, h) })

        val state = dc.stateOf(f)
        val btn = when (state) {
            is ItemState.Done -> AButtonAccent(app, Copy.Discover.WATCH, assets.ic_play_fill, small = true, glow = false).onClick {
                gdxGame.model.video(state.videoId)?.let { gdxGame.openVideo(it) }
            }
            is ItemState.Running -> AButtonAccent(app, Copy.Discover.SAVING, assets.ic_download, small = true, glow = false)
            ItemState.None -> AButtonAccent(app, Copy.Discover.save(Fmt.size(f.defaultFile?.sizeBytes ?: 0)), assets.ic_download, small = true, glow = false).onClick { dc.download(f) }
        }
        btn.setSize((btn as AButtonAccent).label.width + px(56f), px(38f))
        btn.setPosition(w - pad - btn.width, pad)
        val lic = AChip(app, f.license, if (f.pd) AChip.Kind.GREEN else AChip.Kind.BLUE, assets.ic_shield)
        lic.setPosition(pad, pad + (btn.height - lic.height) / 2f)
        val about = lbl(f.about, msdf.regular(12.5f, GameColor.white_75)).apply { setWrap(true); width = w - pad * 2; height = prefHeight }
        about.setPosition(pad, btn.y + btn.height + px(10f))
        val title = lbl(f.title, msdf.disp(26f, Color.WHITE)); title.setPosition(pad, about.y + about.height + px(2f))
        val ey = AChip(app, "Open movie · ${f.year}", AChip.Kind.GLASS)
        ey.setPosition(pad, title.y + title.height + px(6f))
        g.addActor(ey); g.addActor(title); g.addActor(about); g.addActor(lic); g.addActor(btn)
        return h
    }

    // ------------------------------------------------------------------------
    // Секції-каруселі
    // ------------------------------------------------------------------------
    private fun section(title: String, sub: String, films: List<CatalogFilm>): AdvancedGroup {
        val head = secHeader(title, right = sub)
        val cw = px(168f); val th = px(94f)
        val itemH = th + px(8f) + px(18f) + px(15f) + px(8f) + px(38f)
        val row = ARow(app, px(12f), padX = 0f)
        films.forEach { f -> row.addActor(card(f, cw, th, itemH)) }
        val car = hScroll(row, itemH)
        val h = head.height + px(10f) + itemH
        return object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                head.setPosition(0f, h - head.height); addActor(head)
                car.setPosition(-(app.worldWidth - W) / 2f, 0f)
                row.padX = (app.worldWidth - W) / 2f
                addActor(car)
            }
        }.apply { setSize(W, h) }
    }

    private fun card(f: CatalogFilm, cw: Float, th: Float, itemH: Float) = object : ATap(app, 0.97f) {
        override fun addContent() {
            val ph = height
            val t = AVideoThumb(app, px(12f)).apply { setBounds(0f, ph - th, cw, th) }
            t.setAsset(f.thumb, f.durationSec); addActor(t)
            val name = lbl(f.title, msdf.bold(13.5f)).ellipsize(cw)
            val by = lbl("${f.by} · ${f.year}", msdf.regular(11.5f, GameColor.muted_B3A293)).ellipsize(cw)
            name.setPosition(0f, t.y - px(8f) - name.height); by.setPosition(0f, name.y - by.height + px(2f))
            addActor(name); addActor(by)
            val dl = ADlButton(app, { dc.stateOf(f) }, { dc.download(f) }) { id -> gdxGame.model.video(id)?.let { gdxGame.openVideo(it) } }
            dl.setPosition(cw - dl.width, 0f)
            val lic = AChip(app, f.license.substringBefore(" · "), if (f.pd) AChip.Kind.GREEN else AChip.Kind.BLUE, assets.ic_shield)
            lic.setPosition(0f, (dl.height - lic.height) / 2f)
            addActor(lic); addActor(dl)
        }
    }.onClick {
        when (val s = dc.stateOf(f)) {
            is ItemState.Done -> gdxGame.model.video(s.videoId)?.let { gdxGame.openVideo(it) }
            ItemState.None -> dc.download(f)
            else -> {}
        }
    }.apply { setSize(cw, itemH) }
}
