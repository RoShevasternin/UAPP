package com.redwave.downloader.game.actors.ui

import com.redwave.downloader.core.model.Track
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// ATrackRow — .tr у бібліотеці: обкладинка 48 + назва 600/14 + рядок
// «artist · 3:24 · MP3» (формат — mono 600/10.5 apricot) + праворуч ⋮ або
// AEqBars, якщо цей трек зараз грає (тоді назва redHi).
// ─────────────────────────────────────────────────────────────────────────────
class ATrackRow(
    screen: AdvancedScreen,
    val track: Track,
    private val onMore: (Track) -> Unit,
) : ATap(screen, 0.985f) {

    private lateinit var title: AMsdfLabel
    private lateinit var eq: AEqBars
    private lateinit var more: ATap
    private var lastOn: Boolean? = null
    private var lastPlaying: Boolean? = null

    init { setSize(px(318f), px(62f)) }

    override fun addContent() {
        val cover = ACover(screen, px(12f)).apply { setBounds(px(4f), (this@ATrackRow.height - px(48f)) / 2f, px(48f), px(48f)) }
        addActor(cover)
        cover.setTrack(track)

        val mx = px(4f + 48f + 12f)
        val rightW = px(28f)
        val maxW = width - mx - rightW - px(8f)
        title = lbl(track.title, msdf.semibold(14f)).ellipsize(maxW)
        val sub = lbl("${track.artist} · ${Fmt.time(track.durationMs)} · ", msdf.regular(12f, GameColor.muted_A8949B))
        val fmt = lbl(track.format, msdf.monoSemi(10.5f, GameColor.apricot_FFB3A4))
        if (sub.width + fmt.width > maxW) sub.ellipsize(maxW - fmt.width)
        val cy = height / 2f
        title.setPosition(mx, cy + px(1f))
        sub.setPosition(mx, cy - sub.height - px(1f))
        fmt.setPosition(mx + sub.width + px(3f), sub.y + (sub.height - fmt.height) / 2f)
        addActor(title); addActor(sub); addActor(fmt)

        more = object : ATap(screen, 0.9f) {
            override fun addContent() { val i = icon(assets.ic_more, px(20f), GameColor.muted_A8949B); addActor(i); i.setPosition((width - i.width) / 2f, (height - i.height) / 2f) }
        }.onClick { onMore(track) }
        more.setBounds(width - rightW - px(4f), (height - px(40f)) / 2f, rightW + px(4f), px(40f))
        addActor(more)

        eq = AEqBars(screen, px(3f), px(2f), GameColor.red_FF2E4D).apply { setBounds(width - px(4f) - px(22f), (this@ATrackRow.height - px(16f)) / 2f, px(22f), px(16f)) }
        addActor(eq)
        refresh()
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (::eq.isInitialized) refresh()
    }

    private fun refresh() {
        val p = gdxGame.player
        val on = p.snap.trackId == track.id
        if (on == lastOn && p.isPlaying == lastPlaying) return
        lastOn = on; lastPlaying = p.isPlaying
        title.setTextColor(if (on) GameColor.redHi_FF5A6C else GameColor.text_FBF1F2)
        eq.isVisible = on; eq.playing = p.isPlaying
        more.isVisible = !on
    }
}
