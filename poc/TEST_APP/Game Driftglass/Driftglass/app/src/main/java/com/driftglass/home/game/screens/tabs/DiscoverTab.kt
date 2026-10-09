package com.driftglass.home.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.model.Catalog
import com.driftglass.home.core.model.Style
import com.driftglass.home.game.actors.layout.AColumn
import com.driftglass.home.game.actors.layout.ARow
import com.driftglass.home.game.actors.ui.AButton
import com.driftglass.home.game.actors.ui.AIconButton
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.wallpaper.AThumb
import com.driftglass.home.game.screens.AppScreen
import com.driftglass.home.game.screens.LauncherScreen
import com.driftglass.home.game.screens.ui.glass
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.screens.ui.tap
import com.driftglass.home.game.screens.ui.wallpaperCard
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.ellipsize
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// DiscoverTab (Огляд): банер «увімкнути Home» (без ролі), «Скло дня», чіпи стилів,
// сітка 2 колонки (9:16). Тап — PreviewScreen.
// ─────────────────────────────────────────────────────────────────────────────
class DiscoverTab(app: AppScreen) : TabPage(app) {

    private var filter: Style? = null
    private var lastFav = -1
    private var lastRole: Boolean? = null

    override fun build(col: AColumn) {
        val dice = AIconButton(app, assets.ic_dice).apply { setOnClickListener { app.openPreview(Catalog.ALL.random().id) } }
        col.addActor(header(L.discover, dice))

        val isHome = gdxGame.bridge.isDefaultHome()
        lastRole = isHome
        if (!isHome) col.addActor(banner())

        // «Скло дня»
        val today = Catalog.today(System.currentTimeMillis() / 86_400_000L)
        val heroH = px(196f)
        col.addActor(tap(app, W, heroH, 0.985f) {
            addAndFillActor(AThumb(app, today, px(24f)))
            val lh = px(58f)
            val lbx = group(app, width - px(24f), lh) {
                addAndFillActor(glass(app, px(16f)))
                val view = AButton(app, L.view, AButton.Kind.WHITE, small = true)
                view.setSize(view.label.width + px(26f), px(34f)); view.setPosition(width - px(12f) - view.width, (height - view.height) / 2f)
                view.touchable = Touchable.disabled
                val tw = view.x - px(24f)
                val eb = lbl(L.today, msdf.label(9.5f, GameColor.teal_5FF2D1, spacing = 12f))
                val nm = lbl(today.name, msdf.semibold(14.5f)).ellipsize(tw)
                val sb = lbl("${L.style(today.style)} · ${L.palette(today.palette)}", msdf.regular(11.5f, GameColor.white_70)).ellipsize(tw)
                val total = eb.height + px(3f) + nm.height + sb.height
                var y = (height + total) / 2f
                y -= eb.height; eb.setPosition(px(12f), y); y -= px(3f) + nm.height; nm.setPosition(px(12f), y); y -= sb.height; sb.setPosition(px(12f), y)
                addActor(eb); addActor(nm); addActor(sb); addActor(view)
            }
            lbx.setPosition(px(12f), px(12f)); lbx.touchable = Touchable.disabled
            addActor(lbx)
        }.apply { setOnClickListener { app.openPreview(today.id) } })

        // Чіпи стилів
        val row = ARow(app, px(8f), padX = px(16f))
        val chips = listOf<Style?>(null) + Style.entries
        chips.forEach { s ->
            val on = s == filter
            val l = lbl(s?.let { L.style(it) } ?: L.all, msdf.medium(12.5f, if (on) GameColor.ink_071016 else Color.WHITE))
            val chip = tap(app, l.width + px(26f), px(34f), 0.95f) {
                addAndFillActor(if (on) ARect(app, 999f, Color.WHITE) else ARect(app, 999f, GameColor.white_05, stroke = GameColor.white_14))
                l.setPosition((width - l.width) / 2f, (height - l.height) / 2f); addActor(l)
            }.apply { setOnClickListener { filter = s; rebuild() } }
            row.addActor(chip)
        }
        col.addActor(hScroll(row, px(34f)))

        // Сітка 2 колонки
        val list = Catalog.ALL.filter { filter == null || it.style == filter }
        val gap = px(10f)
        val cw = (W - gap) / 2f
        list.chunked(2).forEach { pair ->
            col.addActor(group(app, W, cw * 16f / 9f) {
                pair.forEachIndexed { i, w ->
                    val card = wallpaperCard(app, w, cw) { app.openPreview(w.id) }
                    card.setPosition(i * (cw + gap), 0f); addActor(card)
                }
            })
        }
        lastFav = gdxGame.model.state.favorites.hashCode()
    }

    /** .banner: живі шпалери — лише на Driftglass Home. */
    private fun banner() = group(app, W, px(56f)) {
        addAndFillActor(ARect(app, px(18f), Color(0.37f, 0.95f, 0.82f, 0.18f), Color(0.55f, 0.48f, 1f, 0.18f), stroke = Color(0.37f, 0.95f, 0.82f, 0.3f)))
        val ic = icon(assets.ic_home, px(18f), GameColor.teal_5FF2D1); ic.setPosition(px(12f), (height - ic.height) / 2f); addActor(ic)
        val btn = AButton(app, L.turnOn, AButton.Kind.TEAL, small = true)
        btn.setSize(btn.label.width + px(24f), px(34f)); btn.setPosition(width - px(11f) - btn.width, (height - btn.height) / 2f)
        btn.setOnClickListener {
            gdxGame.bridge.requestDefaultHome { isHome ->
                if (isHome) { gdxGame.toast(L.tRole); gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name) }
            }
        }
        addActor(btn)
        val l = lbl(L.banner, msdf.medium(12.5f)).apply { setWrap(true); width = btn.x - px(40f) - px(8f); height = prefHeight }
        l.setPosition(px(40f), (height - l.height) / 2f); addActor(l)
    }

    override fun act(delta: Float) {
        super.act(delta)
        // серця на картках / банер ролі — перебудувати, якщо змінились
        if (stage != null && isVisible) {
            val fav = gdxGame.model.state.favorites.hashCode()
            if (lastFav != -1 && fav != lastFav) { lastFav = fav; rebuild() }
        }
    }

    override fun onResumed() {
        if (lastRole != gdxGame.bridge.isDefaultHome()) { lastRole = gdxGame.bridge.isDefaultHome(); rebuild() }
    }
}
