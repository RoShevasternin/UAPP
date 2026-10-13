package com.vidtools.framefall.game.screens.tabs

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.layout.ADyn
import com.vidtools.framefall.game.actors.ui.AButtonGhost
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.screens.AppScreen
import com.vidtools.framefall.game.screens.sheets.PickVideoSheet
import com.vidtools.framefall.game.screens.sheets.videoInfo
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
// StudioTab — інструменти над збереженими відео (.studio у прототипі): шапка з чипом
// «On your phone», картка «Working on» (мініатюра + Change), 7 рядків інструментів.
// Тап по інструменту → StudioScreen (редактор з прев'ю і смугою кадрів).
// ─────────────────────────────────────────────────────────────────────────────
class StudioTab(app: AppScreen) : ATabPage(app) {

    override fun build(col: AColumn) {
        col.addActor(header())
        col.addActor(ADyn(app, W, { gdxGame.model.studioVideo?.id }) { w -> buildWorking(this, w) })
        // Рядки інструментів щільніше за секції вкладки: одна група з gap 8
        val rowH = px(64f); val gap = px(8f); val tools = Copy.Studio.TOOLS
        col.addActor(object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                tools.forEachIndexed { i, t -> addActor(toolRow(t).apply { setPosition(0f, (tools.size - 1 - i) * (rowH + gap)) }) }
            }
        }.apply { setSize(W, tools.size * rowH + (tools.size - 1) * gap) })
    }

    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val t = lbl(Copy.Studio.TITLE, msdf.disp(28f)); t.setPosition(0f, (height - t.height) / 2f); addActor(t)
            val chip = AChip(app, Copy.Studio.CHIP, AChip.Kind.RED, assets.ic_wand)
            chip.setPosition(width - chip.width, (height - chip.height) / 2f); addActor(chip)
        }
    }.apply { setSize(W, px(40f)) }

    private fun buildWorking(g: ADyn, w: Float): Float {
        val v = gdxGame.model.studioVideo
        val pad = px(12f); val h = px(80f)
        g.addActor(ARect(app, px(18f), GameColor.card_16100C, stroke = GameColor.line_white_7).apply { setSize(w, h) })
        if (v == null) {
            val e = lbl(Copy.Studio.NO_VIDEOS, msdf.regular(12.5f, GameColor.muted_B3A293)).apply { setWrap(true); width = w - pad * 2; height = prefHeight }
            e.setPosition(pad, (h - e.height) / 2f); g.addActor(e)
            return h
        }
        val th = AVideoThumb(app, px(10f), showProgress = false).apply { setBounds(pad, (h - px(56f)) / 2f, px(100f), px(56f)) }
        th.setVideo(v); g.addActor(th)
        val change = AButtonGhost(app, Copy.Studio.CHANGE, small = true)
        change.setSize(change.label.width + px(24f), px(34f)); change.setPosition(w - pad - change.width, (h - change.height) / 2f)
        change.onClick { app.openSheet(PickVideoSheet(app) { picked -> gdxGame.model.update { it.copy(studioVideoId = picked.id) } }) }
        g.addActor(change)
        val tx = th.x + th.width + px(12f); val tw = change.x - px(10f) - tx
        val cap = lbl(Copy.Studio.WORKING_ON, msdf.regular(11.5f, GameColor.muted_B3A293))
        val name = lbl(v.title, msdf.bold(14f)).ellipsize(tw)
        val meta = lbl(videoInfo(v), msdf.mono(10.5f, GameColor.muted_B3A293)).ellipsize(tw)
        name.setPosition(tx, (h - name.height) / 2f); cap.setPosition(tx, name.y + name.height - px(4f)); meta.setPosition(tx, name.y - meta.height + px(4f))
        g.addActor(cap); g.addActor(name); g.addActor(meta)
        return h
    }

    private fun toolRow(t: Copy.Tool) = object : ATap(app, 0.98f) {
        override fun addContent() {
            addAndFillActor(ARect(app, px(18f), GameColor.card_16100C, stroke = GameColor.line_white_7))
            val circle = ARect(app, px(12f), GameColor.accent_14).apply { setSize(px(40f), px(40f)) }
            circle.setPosition(px(12f), (height - circle.height) / 2f)
            val ic = icon(assets.toolIcon(t.id), px(19f), GameColor.accentText_FFB04A)
            ic.setPosition(circle.x + (circle.width - ic.width) / 2f, circle.y + (circle.height - ic.height) / 2f)
            val title = lbl(t.title, msdf.bold(14.5f)); val sub = lbl(t.sub, msdf.regular(12f, GameColor.muted_B3A293))
            title.setPosition(px(64f), height / 2f + px(1f)); sub.setPosition(px(64f), height / 2f - sub.height - px(1f))
            val ch = icon(assets.ic_chev_right, px(16f), GameColor.muted_B3A293); ch.setPosition(width - px(14f) - ch.width, (height - ch.height) / 2f)
            addActor(circle); addActor(ic); addActor(title); addActor(sub); addActor(ch)
        }
    }.onClick { gdxGame.openStudio(t.id) }.apply { setSize(W, px(64f)) }
}
