package com.vidtools.framefall.game.screens.sheets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// Спільні рядки шторок (.opt / .menu-l у прототипі).
// ─────────────────────────────────────────────────────────────────────────────

/** .opt: підкладка white 4 %, іконка 18 + Onest 600/14; danger — червоний. */
fun optionRow(screen: AdvancedScreen, ic: TextureRegion, label: String, danger: Boolean = false, sub: String? = null, onClick: () -> Unit): ATap =
    object : ATap(screen, 0.98f) {
        override fun addContent() {
            addAndFillActor(ARect(screen, px(14f), GameColor.white_4))
            val col = if (danger) GameColor.err_FF7A6B else Color.WHITE
            val im = icon(ic, px(18f), col); im.setPosition(px(12f), (height - im.height) / 2f)
            val l = lbl(label, msdf.semibold(14f, col))
            addActor(im); addActor(l)
            if (sub == null) l.setPosition(px(42f), (height - l.height) / 2f)
            else {
                val s = lbl(sub, msdf.regular(11.5f, GameColor.muted_B3A293)).ellipsize(width - px(54f))
                l.setPosition(px(42f), height / 2f - px(1f)); s.setPosition(px(42f), height / 2f - s.height + px(1f))
                addActor(s)
            }
        }
    }.onClick(onClick)

/** Рядок «мініатюра 64×36 + назва + підпис» (вибір відео, фон Home). */
fun videoRow(screen: AdvancedScreen, v: Video?, title: String, sub: String, asset: String? = null, onClick: () -> Unit): ATap =
    object : ATap(screen, 0.98f) {
        override fun addContent() {
            addAndFillActor(ARect(screen, px(14f), GameColor.white_4))
            val ph = height
            val th = AVideoThumb(screen, px(8f), showDuration = false, showProgress = false).apply { setBounds(px(8f), (ph - px(36f)) / 2f, px(64f), px(36f)) }
            if (v != null) th.setVideo(v) else if (asset != null) th.setAsset(asset, 0)
            addActor(th)
            val mx = px(8f + 64f + 12f)
            val t = lbl(title, msdf.semibold(13.5f)).ellipsize(width - mx - px(10f))
            val s = lbl(sub, msdf.regular(11.5f, GameColor.muted_B3A293)).ellipsize(width - mx - px(10f))
            t.setPosition(mx, height / 2f - px(1f)); s.setPosition(mx, height / 2f - s.height + px(1f))
            addActor(t); addActor(s)
        }
    }.onClick(onClick)

/** «MP4 · 1280×720 · 61.9 MB · 9:56» */
fun videoInfo(v: Video): String = listOfNotNull(
    v.format,
    if (v.width > 0) "${v.width}×${v.height}" else null,
    if (v.sizeBytes > 0) Fmt.size(v.sizeBytes) else null,
    if (v.durationMs > 0) Fmt.time(v.durationMs) else null,
).joinToString(" · ")
