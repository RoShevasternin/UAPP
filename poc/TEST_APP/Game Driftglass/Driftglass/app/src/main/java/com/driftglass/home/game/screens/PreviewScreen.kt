package com.driftglass.home.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.model.Catalog
import com.driftglass.home.game.actors.ui.AButton
import com.driftglass.home.game.actors.ui.AIconButton
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.wallpaper.AWallpaper
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.screens.ui.NeedHomeSheet
import com.driftglass.home.game.screens.ui.displayName
import com.driftglass.home.game.screens.ui.glass
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.screens.ui.tap
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.ellipsize
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// PreviewScreen — шпалери наживо на весь екран, «привид» головного екрана
// (годинник і ряд іконок, вмикається кнопкою-сіткою), панель: назва, палітра,
// «кадр і на екран блокування», Налаштувати / Застосувати.
// Застосувати без ролі — шторка «Живим шпалерам потрібен Driftglass Home».
// ─────────────────────────────────────────────────────────────────────────────
class PreviewScreen : DgScreen() {

    private val wp = gdxGame.model.wallpaper(gdxGame.previewId) ?: Catalog.ALL.first()
    private var ghostOn = true
    private var alsoLock = gdxGame.model.state.settings.lockStill
    private lateinit var ghost: AdvancedGroup
    private lateinit var panel: AdvancedGroup
    private lateinit var top: AdvancedGroup

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        val wall = AWallpaper(this).apply { setBounds(0f, 0f, w, h) }
        disposableSet += wall
        content.addActor(wall)
        wall.show(wp, instant = true)

        ghost = buildGhost()
        content.addActor(ghost)
        top = group(this, w, h) {}.apply { touchable = Touchable.childrenOnly }
        content.addActor(top)
        buildTop()
        buildPanel()
    }

    /** «Привид» Home: годинник, дата, 4 іконки — щоб видно, як шпалери ляжуть під інтерфейс. */
    private fun buildGhost() = group(this, worldWidth, worldHeight) {
        touchable = Touchable.disabled
        val now = Date()
        val c = lbl(SimpleDateFormat("H:mm", Locale.US).format(now), msdf.clock(56f))
        val loc = Locale.forLanguageTag(L.lang.code)
        val d = lbl(SimpleDateFormat("EEEE, d MMMM", loc).format(now).replaceFirstChar { it.titlecase(loc) }, msdf.medium(14f, Color.WHITE.cpy().apply { a = 0.9f }))
        var y = height - safeStatusBarUI - px(70f)
        y -= c.height; c.setPosition(px(13f), y); addActor(c)
        y -= px(4f) + d.height; d.setPosition(px(16f), y); addActor(d)
        val apps = gdxGame.bridge.listApps().take(4)
        val iw = (width - px(32f)) / 4f
        apps.forEachIndexed { i, a ->
            val ic = com.driftglass.home.game.screens.ui.appIcon(this@PreviewScreen, a, iw, px(52f), label = true)
            ic.setPosition(px(16f) + i * iw, safeNavBarUI + px(270f)); ic.touchable = Touchable.disabled; addActor(ic)
        }
        color.a = 0.9f
    }

    private fun buildTop() {
        top.disposeAndClearChildren()
        val y = worldHeight - safeStatusBarUI - px(10f) - px(38f)
        val back = AIconButton(this, assets.ic_back).apply { setOnClickListener { onBackPressed() } }
        back.setBounds(px(16f), y, px(38f), px(38f)); top.addActor(back)
        val fav = gdxGame.model.state.isFavorite(wp.id)
        val heart = AIconButton(this, if (fav) assets.ic_heart_fill else assets.ic_heart, if (fav) GameColor.rose_FF7AA8 else Color.WHITE).apply {
            setOnClickListener { val on = gdxGame.model.toggleFavorite(wp.id); showToast(if (on) L.tFavAdd else L.tFavRem); buildTop() }
        }
        heart.setBounds(worldWidth - px(16f) - px(38f), y, px(38f), px(38f)); top.addActor(heart)
        val grid = AIconButton(this, assets.ic_grid, if (ghostOn) GameColor.teal_5FF2D1 else Color.WHITE).apply {
            setOnClickListener { ghostOn = !ghostOn; ghost.isVisible = ghostOn; buildTop() }
        }
        grid.setBounds(heart.x - px(8f) - px(38f), y, px(38f), px(38f)); top.addActor(grid)
    }

    private fun buildPanel() {
        if (::panel.isInitialized) panel.remove()
        val side = px(10f)
        val pw = worldWidth - side * 2
        val pad = px(16f)
        val iw = pw - pad * 2
        val bh = px(46f)
        val ph = pad + px(42f) + px(12f) + px(22f) + px(12f) + bh + pad
        panel = group(this, pw, ph) {
            addAndFillActor(glass(this@PreviewScreen, px(26f)))
            var y = height - pad
            // назва + свотчі
            val sw = group(this@PreviewScreen, px(4 * 22f + 3 * 6f), px(22f)) {
                wp.pal.colors.forEachIndexed { i, hex ->
                    addActor(ARect(this@PreviewScreen, px(8f), Color.valueOf(hex.removePrefix("#")), stroke = GameColor.white_20).apply { setBounds(i * px(28f), 0f, px(22f), px(22f)) })
                }
            }
            val nm = lbl(displayName(wp), msdf.semibold(15f)).ellipsize(iw - sw.width - px(10f))
            val sb = lbl("${L.style(wp.style)} · ${L.palette(wp.palette)} · ${L.live}", msdf.regular(12f, GameColor.white_70)).ellipsize(iw - sw.width - px(10f))
            y -= nm.height; nm.setPosition(pad, y); addActor(nm)
            y -= px(2f) + sb.height; sb.setPosition(pad, y); addActor(sb)
            sw.setPosition(width - pad - sw.width, y + (nm.height + sb.height - sw.height) / 2f); addActor(sw)
            // чекбокс
            y = height - pad - px(42f) - px(12f) - px(22f)
            val chk = tap(this@PreviewScreen, iw, px(22f), 0.98f) {
                val box = group(this@PreviewScreen, px(20f), px(20f)) {
                    addAndFillActor(if (alsoLock) ARect(this@PreviewScreen, px(7f), GameColor.teal_5FF2D1) else ARect(this@PreviewScreen, px(7f), Color(0f, 0f, 0f, 0f), stroke = GameColor.white_55, strokeWidth = px(1.5f)))
                    if (alsoLock) { val ic = icon(assets.ic_check, px(14f), GameColor.inkTeal_04110E); ic.setPosition(px(3f), px(3f)); addActor(ic) }
                }
                box.setPosition(0f, (height - box.height) / 2f); addActor(box)
                val l = lbl(L.alsoLock, msdf.regular(12.5f, GameColor.white_92.cpy().apply { a = 0.8f })).ellipsize(width - px(29f))
                l.setPosition(px(29f), (height - l.height) / 2f); addActor(l)
            }.apply { setOnClickListener { alsoLock = !alsoLock; buildPanel() } }
            chk.setPosition(pad, y); addActor(chk)
            // кнопки
            val cust = AButton(this@PreviewScreen, L.customize, AButton.Kind.GHOST, assets.ic_sliders)
            val apply = AButton(this@PreviewScreen, L.apply, AButton.Kind.TEAL)
            val cw = (iw - px(10f)) * 0.44f
            cust.setBounds(pad, pad, cw, bh); apply.setBounds(pad + cw + px(10f), pad, iw - cw - px(10f), bh)
            cust.setOnClickListener { gdxGame.draft = wp.copy(); gdxGame.navigationManager.navigate(StudioScreen::class.java.name, PreviewScreen::class.java.name) }
            apply.setOnClickListener { apply() }
            addActor(cust); addActor(apply)
        }
        panel.setPosition(side, safeNavBarUI + px(12f))
        content.addActor(panel)
    }

    private fun apply() {
        val b = gdxGame.bridge
        if (b.isDefaultHome()) {
            gdxGame.applyWallpaper(wp.id, alsoLock)
            gdxGame.toast(L.tApplied)
            gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name)
            return
        }
        openSheet(NeedHomeSheet(this,
            onStill = {
                gdxGame.model.update { it.copy(applied = wp.id, dayCycle = false, changedAt = System.currentTimeMillis()) }
                gdxGame.setStill(wp, system = true, lock = alsoLock) { ok -> gdxGame.toast(if (ok) L.tStill else L.tStillFail) }
            },
            onHome = {
                gdxGame.model.update { it.copy(applied = wp.id, dayCycle = false, changedAt = System.currentTimeMillis()) }
                b.requestDefaultHome { isHome -> if (isHome) { gdxGame.toast(L.tRole); gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name) } }
            },
        ))
    }
}
