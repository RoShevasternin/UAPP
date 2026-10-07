package com.redwave.downloader.game.screens.sheets

import com.badlogic.gdx.graphics.Color
import com.redwave.downloader.core.model.Episode
import com.redwave.downloader.core.model.PodcastFeed
import com.redwave.downloader.game.actors.ui.ADlButton
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.actors.ui.ASpinner
import com.redwave.downloader.game.controller.DiscoverController.Load
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px
import java.net.URI

// ─────────────────────────────────────────────────────────────────────────────
// FeedSheet — подкаст за RSS: шапка (обкладинка 58 + назва D 20 + host · RSS ·
// N latest) і епізоди з кнопкою ⬇ / кільце / ✓. Поки вантажиться — спінер,
// помилка — текст + Retry.
// ─────────────────────────────────────────────────────────────────────────────
class FeedSheet(screen: AdvancedScreen, private val url: String) : ASheet(screen) {

    private val d = gdxGame.discover
    private val feed: PodcastFeed? get() = d.feeds[url]
    private val load: Load get() = d.feedLoad[url] ?: Load.IDLE
    private var lastLoad: Load? = null

    private val headH = px(58f)
    private val epH = px(62f)
    private val epGap = px(8f)

    init { d.loadFeed(url) }

    override val contentHeight: Float get() {
        val f = feed
        return when {
            f != null -> headH + px(14f) + minOf(f.episodes.size, 6) * (epH + epGap)
            else -> headH + px(14f) + px(70f)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (load != lastLoad) { lastLoad = load; rebuild() }
    }

    override fun buildContent(w: Float) {
        lastLoad = load
        val f = feed
        val top = contentHeight
        val cover = ACover(screen, px(14f)).apply { setBounds(0f, top - headH, headH, headH) }
        cover.setUrl(f?.imageUrl, url)
        add(cover)
        val host = runCatching { URI(url).host?.removePrefix("www.") }.getOrNull() ?: "RSS"
        val title = lbl(f?.title ?: "Podcast", msdf.bold(20f)).ellipsize(w - headH - px(12f))
        val sub = lbl(listOfNotNull(host, "RSS", f?.let { "${minOf(it.episodes.size, 6)} latest" }).joinToString(" · "), msdf.regular(12f, GameColor.muted_A8949B))
        title.setPosition(headH + px(12f), top - headH / 2f + px(1f)); sub.setPosition(headH + px(12f), top - headH / 2f - sub.height - px(2f))
        add(title); add(sub)

        var y = top - headH - px(14f)
        when {
            f != null -> f.episodes.take(6).forEach { e -> y -= epH; add(episode(f, e, w).apply { setPosition(0f, y) }); y -= epGap }
            load == Load.ERROR -> {
                val msg = lbl("Couldn’t load this feed.", msdf.regular(13f, GameColor.muted_A8949B))
                msg.setPosition(0f, y - px(30f)); add(msg)
                val retry = AButtonGhost(screen, "Retry", small = true).apply { setSize(px(90f), px(36f)); setPosition(w - px(90f), y - px(40f)) }
                retry.onClick { d.loadFeed(url, force = true) }
                add(retry)
            }
            else -> add(ASpinner(screen, px(2.5f), Color(1f, 1f, 1f, 0.2f), GameColor.red_FF2E4D).apply { setBounds((w - px(24f)) / 2f, y - px(46f), px(24f), px(24f)) })
        }
    }

    private fun episode(f: PodcastFeed, e: Episode, w: Float) = object : com.redwave.downloader.game.utils.advanced.AdvancedGroup() {
        override val screen = this@FeedSheet.screen
        override fun addActorsOnGroup() {
            addAndFillActor(ARect(screen, px(16f), GameColor.white_4, stroke = GameColor.line_white_7))
            val btn = ADlButton(screen, { d.stateOf(e.guid, e.audioUrl) }, { d.download(f, e) }, { id -> gdxGame.model.track(id)?.let { gdxGame.player.play(it) } })
            btn.setPosition(width - px(10f) - btn.width, (height - btn.height) / 2f); addActor(btn)
            val tw = width - px(20f) - btn.width - px(12f)
            val t = lbl(e.title, msdf.semibold(13.5f)).ellipsize(tw)
            val ext = e.audioUrl.substringAfterLast('.').substringBefore('?').uppercase().take(4)
            val meta = listOfNotNull(e.durationSec?.let { Fmt.time(it * 1000L) }, e.lengthBytes.takeIf { it > 0 }?.let { "${Fmt.mb(it)} MB" }, ext).joinToString(" · ")
            val s = lbl(meta, msdf.regular(11.5f, GameColor.muted_A8949B))
            t.setPosition(px(12f), height / 2f + px(1f)); s.setPosition(px(12f), height / 2f - s.height - px(1f))
            addActor(t); addActor(s)
        }
    }.apply { setSize(w, epH) }
}
