package com.redwave.downloader.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.model.CatalogTrack
import com.redwave.downloader.core.model.FeaturedPlaylist
import com.redwave.downloader.game.actors.layout.AColumn
import com.redwave.downloader.game.actors.layout.ADyn
import com.redwave.downloader.game.actors.layout.ARow
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.AChip
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.ADlButton
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASpinner
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.controller.DiscoverController.Load
import com.redwave.downloader.game.screens.AppScreen
import com.redwave.downloader.game.screens.sheets.FeedSheet
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// DiscoverTab — каталог CC: настрої, підбірки, «Trending this week» з кнопкою
// ⬇ / кільце / ✓, подкасти (RSS → FeedSheet). Дані — DiscoverController.
// ─────────────────────────────────────────────────────────────────────────────
class DiscoverTab(app: AppScreen) : ATabPage(app) {

    private val d = gdxGame.discover
    private var mood = "All"
    private var moodKey = 0

    override fun onShown() { d.loadCatalog() }

    override fun build(col: AColumn) {
        col.addActor(header())
        col.addActor(lbl(Copy.Discover.LEAD, msdf.regular(13.5f, GameColor.muted_A8949B)).apply { setWrap(true); width = W; setLineHeight(112f); height = prefHeight - px(8f) })
        col.addActor(moods())
        col.addActor(ADyn(app, W, { d.version }) { w -> buildFeatured(this, w) })
        col.addActor(ADyn(app, W, { "${d.version}:$moodKey" }) { w -> buildTrending(this, w) })
        col.addActor(ADyn(app, W, { d.version }) { w -> buildPodcasts(this, w) })
    }

    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val t = lbl(Copy.Discover.TITLE, msdf.disp(30f)); t.setPosition(0f, (height - t.height) / 2f); addActor(t)
            val chip = AChip(app, Copy.Discover.CHIP, AChip.Kind.RED, assets.ic_shield); chip.setPosition(width - chip.width, (height - chip.height) / 2f); addActor(chip)
        }
    }.apply { setSize(W, px(38f)) }

    // ------------------------------------------------------------------------
    // Настрої
    // ------------------------------------------------------------------------
    private val moodChips = ArrayList<AChip>()

    private fun moods() = hScroll(ARow(app, px(7f), padX = (app.worldWidth - W) / 2f).also { row ->
        Copy.Discover.MOODS.forEach { m ->
            val c = AChip(app, m, AChip.Kind.MOOD).apply { isOn = m == mood }
            c.onClick { selectMood(m) }
            moodChips += c
            row.addActor(c)
        }
    }, AChip(app, "All", AChip.Kind.MOOD).height)

    private fun selectMood(m: String) {
        mood = m
        moodChips.forEachIndexed { i, c -> c.isOn = Copy.Discover.MOODS[i] == m }
        moodKey++
    }

    // ------------------------------------------------------------------------
    // Підбірки 250×172
    // ------------------------------------------------------------------------
    private fun buildFeatured(g: ADyn, w: Float): Float {
        val list = d.catalog?.featured.orEmpty()
        val fw = px(250f); val fh = fw * 11f / 16f
        if (list.isEmpty()) {
            if (d.catalogLoad == Load.READY) return 0f
            if (d.catalogLoad == Load.ERROR) {
                val msg = lbl("Couldn’t load the catalog.", msdf.regular(13f, GameColor.muted_A8949B))
                val retry = AButtonGhost(app, "Retry", small = true).apply { setSize(px(90f), px(36f)) }
                retry.onClick { d.loadCatalog(force = true) }
                msg.setPosition(0f, (px(40f) - msg.height) / 2f); retry.setPosition(w - retry.width, px(2f))
                g.addActor(msg); g.addActor(retry)
                return px(40f)
            }
            g.addActor(ASpinner(app, px(2.5f), Color(1f, 1f, 1f, 0.2f), GameColor.red_FF2E4D).apply { setBounds((w - px(24f)) / 2f, (fh - px(24f)) / 2f, px(24f), px(24f)) })
            return fh
        }
        val row = ARow(app, px(12f), padX = (app.worldWidth - w) / 2f)
        list.forEach { f -> row.addActor(featCard(f, fw, fh)) }
        val car = hScroll(row, fh)
        car.setPosition(-(app.worldWidth - w) / 2f, 0f)
        g.addActor(car)
        return fh
    }

    private fun featTexture(f: FeaturedPlaylist): Texture? = when (f.id) {
        "f-live" -> assets.FEAT_LIVE; "f-night" -> assets.FEAT_NIGHT; "f-vinyl" -> assets.FEAT_VINYL; else -> null
    }

    private fun featCard(f: FeaturedPlaylist, fw: Float, fh: Float) = object : ATap(app, 0.98f) {
        override fun addContent() {
            val photo = ACover(app, px(22f)); addAndFillActor(photo)
            featTexture(f)?.let { photo.setTexture(it) } ?: photo.setUrl(f.cover, f.id)
            // linear-gradient(180deg, rgba(8,4,5,0) 30%, rgba(8,4,5,.92))
            addAndFillActor(ARect(app, px(22f), Color(0.03f, 0.016f, 0.02f, 0f), Color(0.03f, 0.016f, 0.02f, 0.92f), angleCss = 180f, start = 0.3f, stroke = Color(1f, 1f, 1f, 0.06f)))
            val chip = AChip(app, f.mood, AChip.Kind.GLASS); chip.setPosition(px(12f), height - px(12f) - chip.height); addActor(chip)
            val sub = lbl(f.subtitle, msdf.regular(12f, GameColor.white_75)).ellipsize(width - px(28f) - px(48f))
            val t = lbl(f.title, msdf.disp(21f, Color.WHITE)).ellipsize(width - px(28f) - px(48f))
            sub.setPosition(px(14f), px(14f)); t.setPosition(px(14f), sub.y + sub.height + px(2f))
            addActor(t); addActor(sub)
            val play = object : AdvancedGroup() {
                override val screen = app
                override fun addActorsOnGroup() {
                    val pw = width; val ph = height; addActor(Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.55f; setBounds(-pw * 0.3f, -ph * 0.6f, pw * 1.6f, ph * 1.5f) })
                    addAndFillActor(ARect(app, 999f, GameColor.gradTop_FF5468, GameColor.gradBot_E3002B))
                    val ic = icon(assets.ic_play_fill, px(16f)); ic.setPosition((width - ic.width) / 2f + px(1f), (height - ic.height) / 2f); addActor(ic)
                }
            }.apply { setSize(px(40f), px(40f)) }
            play.setPosition(width - px(12f) - px(40f), px(12f))
            addActor(play)
        }
    }.onClick { selectMood(f.mood) }.apply { setSize(fw, fh) }

    // ------------------------------------------------------------------------
    // Trending
    // ------------------------------------------------------------------------
    private fun buildTrending(g: ADyn, w: Float): Float {
        if (d.catalogLoad != Load.READY) return 0f
        val list = d.tracks(mood)
        val head = secHeader(Copy.Discover.TRENDING, right = Copy.Discover.tracksCount(list.size))
        val rowH = px(62f)
        val body: List<com.badlogic.gdx.scenes.scene2d.Actor> =
            if (list.isEmpty()) listOf(emptyText(Copy.Discover.EMPTY_MOOD, w))
            else list.mapIndexed { k, t -> trackRow(k, t, w, rowH) }
        val h = head.height + px(10f) + body.sumOf { it.height.toDouble() }.toFloat() + px(2f) * (body.size - 1).coerceAtLeast(0)
        var y = h
        y -= head.height; head.setPosition(0f, y); g.addActor(head); y -= px(10f)
        body.forEach { a -> y -= a.height; a.setPosition(0f, y); g.addActor(a); y -= px(2f) }
        return h
    }

    private fun trackRow(k: Int, t: CatalogTrack, w: Float, h: Float) = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val rank = lbl("${k + 1}", msdf.style(gdxGame.msdfManager.fontJetBrainsMono_SemiBold, 13f, GameColor.muted_A8949B))
            rank.setPosition(px(4f) + (px(16f) - rank.width) / 2f, (height - rank.height) / 2f); addActor(rank)
            val cx = px(4f + 16f + 12f)
            val cover = ACover(app, px(12f)).apply { setBounds(cx, (h - px(48f)) / 2f, px(48f), px(48f)) }
            cover.setUrl(t.cover, t.id); addActor(cover)
            val btn = ADlButton(app, { d.stateOf(t.id, t.url) }, { d.download(t) }, { id -> gdxGame.model.track(id)?.let { gdxGame.player.play(it) } })
            btn.setPosition(width - px(4f) - btn.width, (height - btn.height) / 2f); addActor(btn)
            val mx = cx + px(48f + 12f); val mw = btn.x - px(12f) - mx
            val title = lbl(t.title, msdf.semibold(14f)).ellipsize(mw)
            val lic = lbl(t.license, msdf.bold(12f, GameColor.pink_FF8A98))
            val artist = lbl("${t.artist} · ", msdf.regular(12f, GameColor.muted_A8949B))
            if (artist.width + lic.width > mw) artist.ellipsize(mw - lic.width)
            title.setPosition(mx, height / 2f + px(1f)); artist.setPosition(mx, height / 2f - artist.height - px(1f))
            lic.setPosition(mx + artist.width + px(3f), artist.y)
            addActor(title); addActor(artist); addActor(lic)
        }
    }.apply { setSize(w, h) }

    // ------------------------------------------------------------------------
    // Подкасти
    // ------------------------------------------------------------------------
    private fun buildPodcasts(g: ADyn, w: Float): Float {
        val feeds = d.feeds.values.toList()
        val head = secHeader(Copy.Discover.PODCASTS, right = "RSS")
        if (feeds.isEmpty()) return 0f
        val ph = px(80f)
        val h = head.height + px(10f) + feeds.size * ph + (feeds.size - 1) * px(8f)
        var y = h
        y -= head.height; head.setPosition(0f, y); g.addActor(head); y -= px(10f)
        feeds.forEach { f ->
            y -= ph
            val card = object : ATap(app, 0.98f) {
                override fun addContent() {
                    addAndFillActor(ARect(app, px(18f), GameColor.card_170F12, stroke = GameColor.line_white_7))
                    val pw = width; val ph = height; val c = ACover(app, px(12f)).apply { setBounds(px(10f), (ph - px(60f)) / 2f, px(60f), px(60f)) }
                    c.setUrl(f.imageUrl, f.url); addActor(c)
                    val chev = icon(assets.ic_chev_right, px(20f), GameColor.muted_A8949B); chev.setPosition(width - px(12f) - chev.width, (height - chev.height) / 2f); addActor(chev)
                    val mx = px(10f + 60f + 12f); val mw = chev.x - px(8f) - mx
                    val t = lbl(f.title, msdf.bold(14.5f)).ellipsize(mw)
                    val s = lbl("Weekly · ${f.episodes.size} episodes", msdf.regular(12f, GameColor.muted_A8949B))
                    t.setPosition(mx, height / 2f + px(1f)); s.setPosition(mx, height / 2f - s.height - px(1f))
                    addActor(t); addActor(s)
                }
            }.onClick { app.openSheet(FeedSheet(app, f.url)) }
            card.setBounds(0f, y, w, ph); g.addActor(card)
            y -= px(8f)
        }
        return h
    }
}
