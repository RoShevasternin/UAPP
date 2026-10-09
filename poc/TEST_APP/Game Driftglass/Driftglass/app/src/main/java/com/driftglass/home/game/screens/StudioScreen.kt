package com.driftglass.home.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.model.Palettes
import com.driftglass.home.core.model.Style
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.game.actors.layout.ARow
import com.driftglass.home.game.actors.AScrollPane
import com.driftglass.home.game.actors.ui.AButton
import com.driftglass.home.game.actors.ui.AIconButton
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.ui.ASegmented
import com.driftglass.home.game.actors.ui.ASlider
import com.driftglass.home.game.actors.wallpaper.AWallpaper
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.screens.ui.NeedHomeSheet
import com.driftglass.home.game.screens.ui.glass
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.screens.ui.tap
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px
import com.driftglass.home.util.log
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
// StudioScreen — свої шпалери за секунди: живий результат на весь екран, панель знизу:
// 5 стилів, 8 палітр, Рух / Масштаб / Зерно, кубик (нова варіація), Зберегти / Застосувати.
// Чернетка — GDXGame.draft (копія того, з чого прийшли).
// ─────────────────────────────────────────────────────────────────────────────
class StudioScreen : DgScreen() {

    private var d: Wallpaper = gdxGame.draft
    private lateinit var wall: AWallpaper
    private var panel: AdvancedGroup? = null

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        wall = AWallpaper(this).apply { setBounds(0f, 0f, w, h) }
        disposableSet += wall
        content.addActor(wall)
        wall.show(d, instant = true)

        val y = h - safeStatusBarUI - px(10f) - px(38f)
        val back = AIconButton(this, assets.ic_back).apply { setOnClickListener { onBackPressed() } }
        back.setBounds(px(16f), y, px(38f), px(38f)); content.addActor(back)
        val title = lbl(L.studio, msdf.title(20f)); title.setPosition(back.x + back.width + px(12f), y + (px(38f) - title.height) / 2f); content.addActor(title)
        val dice = AIconButton(this, assets.ic_dice).apply { setOnClickListener { set(d.copy(seed = (Random.nextFloat() * 9f * 100).toInt() / 100f)) } }
        dice.setBounds(w - px(16f) - px(38f), y, px(38f), px(38f)); content.addActor(dice)
        buildPanel()
    }

    private fun set(nd: Wallpaper, rebuildPanel: Boolean = false) {
        val styleChanged = nd.style != d.style
        d = nd
        if (styleChanged) wall.show(d) else wall.live(d)
        if (rebuildPanel) com.driftglass.home.game.utils.runGDX { buildPanel() }   // не посеред кліку по кнопці, яку прибираємо
    }

    private fun buildPanel() {
        panel?.remove()
        val side = px(10f)
        val pw = worldWidth - side * 2
        val pad = px(16f)
        val iw = pw - pad * 2
        val segH = px(54f); val palH = px(44f); val slH = px(26f); val bh = px(46f)
        val ph = pad + segH + px(12f) + palH + px(12f) + (slH + px(8f)) * 3 + px(4f) + bh + pad
        val p = group(this, pw, ph) {
            addAndFillActor(glass(this@StudioScreen, px(26f)))
            var y = height - pad - segH
            val styles = Style.entries
            val icons = listOf(assets.ic_aur, assets.ic_drop, assets.ic_wave, assets.ic_hex, assets.ic_mesh)
            val seg = ASegmented(this@StudioScreen, styles.mapIndexed { i, s -> L.style(s) to icons[i] }, styles.indexOf(d.style), textSize = 10f) { i ->
                set(d.copy(style = styles[i]), rebuildPanel = true)
            }
            seg.setBounds(pad, y, iw, segH); addActor(seg)

            // палітри: квадрати з 4 смугами кольорів (conic у прототипі)
            y -= px(12f) + palH
            val row = ARow(this@StudioScreen, px(8f))
            Palettes.ALL.forEach { pal ->
                val on = pal.id == d.palette
                val sw = tap(this@StudioScreen, palH, palH, 0.92f) {
                    val q = width / 2f
                    val cs = pal.colors.map { Color.valueOf(it.removePrefix("#")) }
                    addActor(ARect(this@StudioScreen, px(12f), cs[0], cs[2], angleCss = 135f).apply { setBounds(0f, 0f, width, height) })
                    addActor(ARect(this@StudioScreen, px(6f), cs[1]).apply { setBounds(q - px(8f), q - px(8f), px(16f), px(16f)) })
                    if (on) addActor(ARect(this@StudioScreen, px(14f), Color(0f, 0f, 0f, 0f), stroke = Color.WHITE, strokeWidth = px(2f)).apply { setBounds(-px(2f), -px(2f), width + px(4f), height + px(4f)) })
                    addActor(ARect(this@StudioScreen, px(12f), Color(0f, 0f, 0f, 0f), stroke = GameColor.white_20).apply { setBounds(0f, 0f, width, height); touchable = Touchable.disabled })
                }.apply { setOnClickListener { set(d.copy(palette = pal.id), rebuildPanel = true) } }
                row.addActor(sw)
            }
            row.setSize(1f, palH)
            val sc = AScrollPane(row, scrollX = true, scrollY = false).apply { setBounds(pad, y, iw, palH) }
            addActor(sc)

            // повзунки
            fun slider(label: String, min: Float, max: Float, v: Float, fmt: (Float) -> String, onCh: (Float) -> Unit) {
                y -= px(8f) + slH
                val l = lbl(label, msdf.regular(12f, GameColor.white_70)); l.setPosition(pad, y + (slH - l.height) / 2f); addActor(l)
                val out = lbl(fmt(v), msdf.label(11f, GameColor.white_70, spacing = 0f))
                val ow = px(30f)
                out.setPosition(width - pad - out.width, y + (slH - out.height) / 2f); addActor(out)
                val s = ASlider(this@StudioScreen, min, max, v) { nv -> onCh(nv); out.setText(fmt(nv)); out.pack(); out.x = width - pad - out.width }
                s.setBounds(pad + px(72f), y, iw - px(72f) - ow - px(8f), slH); addActor(s)
            }
            y -= px(4f)
            slider(L.motion, 0f, 2f, d.speed, { "%.1f".format(it) }) { set(d.copy(speed = it)) }
            slider(L.scale, 0.5f, 2f, d.scale, { "%.1f".format(it) }) { set(d.copy(scale = it)) }
            slider(L.grain, 0f, 0.2f, d.grain, { "${(it * 500).toInt()}" }) { set(d.copy(grain = it)) }

            val save = AButton(this@StudioScreen, L.save, AButton.Kind.GHOST, assets.ic_plus)
            val apply = AButton(this@StudioScreen, L.apply, AButton.Kind.TEAL)
            val cw = (iw - px(10f)) * 0.42f
            save.setBounds(pad, pad, cw, bh); apply.setBounds(pad + cw + px(10f), pad, iw - cw - px(10f), bh)
            save.setOnClickListener { saveDraft(); showToast(L.tSaved) }
            apply.setOnClickListener { applyDraft() }
            addActor(save); addActor(apply)
        }
        p.setPosition(side, safeNavBarUI + px(12f))
        content.addActor(p)
        panel = p
    }

    /** Зберегти в «Моє скло» як нові шпалери (Скло #N). */
    private fun saveDraft(): Wallpaper {
        val st = gdxGame.model.state
        val w = d.copy(id = "mine-${System.currentTimeMillis()}", name = "", number = st.nextNumber)
        gdxGame.model.update { it.copy(created = listOf(w) + it.created) }
        log("Studio: збережено ${w.id} #${w.number} (${w.style}/${w.palette})")
        return w
    }

    private fun applyDraft() {
        val w = saveDraft()
        val b = gdxGame.bridge
        if (b.isDefaultHome()) {
            gdxGame.applyWallpaper(w.id)
            gdxGame.toast(L.tApplied)
            gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name)
            return
        }
        openSheet(NeedHomeSheet(this,
            onStill = {
                gdxGame.model.update { it.copy(applied = w.id, dayCycle = false, changedAt = System.currentTimeMillis()) }
                gdxGame.setStill(w, system = true, lock = gdxGame.model.state.settings.lockStill) { ok -> gdxGame.toast(if (ok) L.tStill else L.tStillFail) }
            },
            onHome = {
                gdxGame.model.update { it.copy(applied = w.id, dayCycle = false, changedAt = System.currentTimeMillis()) }
                b.requestDefaultHome { isHome -> if (isHome) { gdxGame.toast(L.tRole); gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name) } }
            },
        ))
    }
}
