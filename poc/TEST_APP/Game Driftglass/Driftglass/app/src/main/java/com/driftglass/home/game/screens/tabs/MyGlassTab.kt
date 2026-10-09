package com.driftglass.home.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.logic.DayCycle
import com.driftglass.home.core.model.Catalog
import com.driftglass.home.core.model.DaySlot
import com.driftglass.home.core.model.Shuffle
import com.driftglass.home.game.actors.layout.AColumn
import com.driftglass.home.game.actors.ui.AButton
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.ui.ASegmented
import com.driftglass.home.game.actors.ui.AToggle
import com.driftglass.home.game.actors.wallpaper.AThumb
import com.driftglass.home.game.screens.AppScreen
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.screens.ui.tap
import com.driftglass.home.game.screens.ui.wallpaperCard
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px
import com.driftglass.home.util.log

// ─────────────────────────────────────────────────────────────────────────────
// MyGlassTab («Моє скло»): створені в Studio / обране (сітка 3 колонки),
// Автозміна (вимк. / розблокування / щогодини / щодня), Цикл дня з 4 слотами.
// ─────────────────────────────────────────────────────────────────────────────
class MyGlassTab(app: AppScreen) : TabPage(app) {

    private var showCreated = true
    private var lastVersion = -1

    override fun build(col: AColumn) {
        val m = gdxGame.model
        val s = m.state
        lastVersion = m.version
        col.addActor(header(L.myGlass))

        col.addActor(ASegmented(app, listOf("${L.created} · ${s.created.size}" to null, "${L.favorites} · ${s.favorites.size}" to null), if (showCreated) 0 else 1, textSize = 12f) { i ->
            showCreated = i == 0; rebuild()
        }.apply { setSize(W, px(40f)) })

        val list = if (showCreated) s.created else s.favorites.mapNotNull { s.wallpaper(it) }
        if (list.isEmpty()) {
            col.addActor(empty())
        } else {
            val gap = px(10f)
            val cw = (W - gap * 2) / 3f
            list.chunked(3).forEach { row ->
                col.addActor(group(app, W, cw * 16f / 9f) {
                    row.forEachIndexed { i, w ->
                        val c = wallpaperCard(app, w, cw, small = true) { app.openPreview(w.id) }
                        c.setPosition(i * (cw + gap), 0f); addActor(c)
                    }
                })
            }
        }

        col.addActor(shuffleCard())
        col.addActor(dayCycleCard())
    }

    private fun empty() = group(app, W, px(150f)) {
        val ic = icon(if (showCreated) assets.ic_sliders else assets.ic_heart, px(22f), GameColor.white_55)
        val l = lbl(if (showCreated) L.emptyCreated else L.emptyFav, msdf.regular(13f, GameColor.white_55), Align.center).apply { setWrap(true); width = W - px(40f); height = prefHeight }
        val b = AButton(app, if (showCreated) L.openStudio else L.browse, AButton.Kind.TEAL, small = true)
        b.setSize(b.label.width + px(28f), px(36f))
        b.setOnClickListener { if (showCreated) app.openStudio() else app.select(AppScreen.TAB_DISCOVER) }
        val total = ic.height + px(10f) + l.height + px(12f) + b.height
        var y = (height + total) / 2f
        y -= ic.height; ic.setPosition((width - ic.width) / 2f, y); y -= px(10f) + l.height; l.setPosition(px(20f), y); y -= px(12f) + b.height; b.setPosition((width - b.width) / 2f, y)
        addActor(ic); addActor(l); addActor(b)
    }

    private fun card(h: Float, build: com.driftglass.home.game.utils.advanced.AdvancedGroup.() -> Unit) = group(app, W, h) {
        addAndFillActor(ARect(app, px(20f), GameColor.white_05, stroke = GameColor.white_08))
        build()
    }

    private fun shuffleCard() = card(px(132f)) {
        val s = gdxGame.model.state
        val pad = px(14f)
        var y = height - pad
        val t = lbl(L.autoShuffle, msdf.semibold(15f)); y -= t.height; t.setPosition(pad, y); addActor(t)
        val ic = icon(assets.ic_cycle, px(18f), GameColor.white_70); ic.setPosition(width - pad - ic.width, y + (t.height - ic.height) / 2f); addActor(ic)
        y -= px(10f) + px(40f)
        val modes = Shuffle.entries
        val seg = ASegmented(app, modes.map { L.shuffleSeg(it) to null }, modes.indexOf(s.shuffle), textSize = 11f) { i ->
            gdxGame.model.update { it.copy(shuffle = modes[i], changedAt = System.currentTimeMillis()) }
            log("Auto-shuffle = ${modes[i]}")
            rebuild()
        }
        seg.setBounds(pad, y, width - pad * 2, px(40f)); addActor(seg)
        val hint = lbl(L.picks + (if (s.favorites.size < 2) L.add2 else "") + ".", msdf.regular(11.5f, GameColor.white_55))
        y -= px(8f) + hint.height; hint.setPosition(pad, y); addActor(hint)
    }

    private fun dayCycleCard(): com.driftglass.home.game.utils.advanced.AdvancedGroup {
        val pad = px(14f)
        val gap = px(8f)
        val sw = (W - pad * 2 - gap * 3) / 4f
        val sh = sw * 14f / 9f
        return card(pad + px(40f) + px(12f) + sh + px(20f) + px(10f) + px(16f) + pad) {
            val m = gdxGame.model
            val s = m.state
            val curSlot = DayCycle.slotOf(m.hourNow())
            var y = height - pad
            val t = lbl(L.dayCycle, msdf.semibold(15f))
            val sub = lbl(L.cycleSub, msdf.regular(11.5f, GameColor.white_55)).apply { setWrap(true); width = W - pad * 2 - px(56f); height = prefHeight }
            y -= t.height; t.setPosition(pad, y); addActor(t)
            y -= sub.height; sub.setPosition(pad, y); addActor(sub)
            val tog = tap(app, px(48f), px(32f), 0.95f) {
                val tg = AToggle(app, s.dayCycle); tg.setPosition(width - tg.width, (height - tg.height) / 2f); addActor(tg)
            }.apply { setOnClickListener { gdxGame.model.update { it.copy(dayCycle = !it.dayCycle) }; log("Day cycle = ${gdxGame.model.state.dayCycle}"); rebuild() } }
            tog.setPosition(width - pad - tog.width, height - pad - tog.height); addActor(tog)
            y = minOf(y, tog.y) - px(12f) - sh
            DaySlot.entries.forEachIndexed { i, slot ->
                val wid = s.slots[slot] ?: "tidepool"
                val w = s.wallpaper(wid) ?: Catalog.ALL.first()
                val cur = s.dayCycle && slot == curSlot
                val cell = tap(app, sw, sh + px(18f), 0.95f) {
                    val th = AThumb(app, w, px(12f)); th.setBounds(0f, px(18f), width, sh); addActor(th)
                    if (cur) addActor(ARect(app, px(14f), Color(0f, 0f, 0f, 0f), stroke = GameColor.teal_5FF2D1, strokeWidth = px(2f)).apply { setBounds(-px(3f), px(15f), width + px(6f), sh + px(6f)); touchable = Touchable.disabled })
                    val l = lbl(L.slot(slot), msdf.medium(10.5f, if (cur) GameColor.teal_5FF2D1 else Color.WHITE)); l.setPosition((width - l.width) / 2f, 0f); addActor(l)
                }.apply {
                    setOnClickListener {
                        val ids = Catalog.ALL.map { it.id } + gdxGame.model.state.created.map { it.id }
                        val next = ids[(ids.indexOf(wid) + 1).mod(ids.size)]
                        gdxGame.model.update { st -> st.copy(slots = st.slots + (slot to next)) }
                        rebuild()
                    }
                }
                cell.setPosition(pad + i * (sw + gap), y - px(18f)); addActor(cell)
            }
            val hint = lbl(L.slotHint, msdf.regular(11f, GameColor.white_55))
            hint.setPosition(pad, pad); addActor(hint)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        // змінилось ззовні (обране з Огляду / створене в Studio) — перебудувати
        if (stage != null && isVisible && lastVersion != -1 && gdxGame.model.version != lastVersion) { lastVersion = gdxGame.model.version; rebuild() }
    }
}
