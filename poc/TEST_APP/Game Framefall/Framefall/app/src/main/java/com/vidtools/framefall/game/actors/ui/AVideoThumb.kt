package com.vidtools.framefall.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AVideoThumb — .th у прототипі: кадр 16:9 з заокругленням, плашка тривалості
// праворуч унизу (mono на чорному 70 %), смужка «скільки подивились» по низу,
// ліворуч угорі — мітка типу (AUDIO / GIF / PHOTO / формат зі Studio).
// ─────────────────────────────────────────────────────────────────────────────
class AVideoThumb(
    override val screen: AdvancedScreen,
    private val radius: Float = px(12f),
    private val showDuration: Boolean = true,
    private val showProgress: Boolean = true,
) : AdvancedGroup() {

    private val cover = ACover(screen, radius)
    private var badge: AdvancedGroup? = null
    private var kindBadge: AdvancedGroup? = null
    private val progBg = ARect(screen, 0f, Color(1f, 1f, 1f, 0.25f))
    private val prog = ARect(screen, 0f, GameColor.accent_FF8A2A)
    private var video: Video? = null
    private var asset: Pair<String, Int>? = null
    private var inited = false

    init { touchable = Touchable.disabled }

    override fun addActorsOnGroup() {
        addAndFillActor(cover)
        addActor(progBg); addActor(prog)
        inited = true
        video?.let { bind(it) }
        asset?.let { (n, d) -> bindAsset(n, d) }
    }

    fun setVideo(v: Video) { video = v; asset = null; if (inited) bind(v) }

    /** Кадр каталогу без Video (Discover). */
    fun setAsset(name: String, durationSec: Int) { asset = name to durationSec; video = null; if (inited) bindAsset(name, durationSec) }

    private fun bindAsset(name: String, durationSec: Int) {
        cover.setAsset(name)
        progBg.isVisible = false; prog.isVisible = false
        setDuration(durationSec * 1000L)
    }

    private fun bind(v: Video) {
        cover.setVideo(v)
        setDuration(if (showDuration && v.kind != MediaKind.IMAGE) v.durationMs else 0L)
        kindBadge?.remove(); kindBadge = null
        val tag = when {
            v.kind == MediaKind.AUDIO -> "AUDIO"
            v.format == "GIF" -> "GIF"
            v.kind == MediaKind.IMAGE -> "PHOTO"
            else -> null
        }
        tag?.let { kindBadge = pill(it, left = true) }
        val show = showProgress && v.inProgress
        progBg.isVisible = show; prog.isVisible = show
        if (show) {
            progBg.setBounds(px(8f), px(6f), width - px(16f), px(3f))
            prog.setBounds(px(8f), px(6f), (width - px(16f)) * v.progress, px(3f))
        }
        if (v.kind == MediaKind.AUDIO) cover.setGlyph(assets.ic_music)
    }

    private fun setDuration(ms: Long) {
        badge?.remove(); badge = null
        if (ms > 0) badge = pill(Fmt.time(ms), left = false)
    }

    private fun pill(text: String, left: Boolean): AdvancedGroup {
        val l: AMsdfLabel = lbl(text, msdf.monoSemi(if (left) 9f else 10f, Color.WHITE))
        val padX = px(5f); val h = l.height * 0.8f + px(5f)
        val g = object : AdvancedGroup() {
            override val screen = this@AVideoThumb.screen
            override fun addActorsOnGroup() {
                addAndFillActor(ARect(screen, px(6f), Color(0f, 0f, 0f, 0.7f)))
                addActor(l); l.setPosition(padX, (height - l.height) / 2f)
            }
        }
        g.setSize(l.width + padX * 2, h)
        g.setPosition(if (left) px(6f) else width - g.width - px(6f), if (left) height - h - px(6f) else px(6f) + if (progBg.isVisible) px(5f) else 0f)
        addActor(g)
        return g
    }
}
