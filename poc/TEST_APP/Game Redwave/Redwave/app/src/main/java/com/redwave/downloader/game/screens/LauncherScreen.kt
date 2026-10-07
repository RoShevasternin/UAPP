package com.redwave.downloader.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.link.chipLabel
import com.redwave.downloader.core.link.sourceUrl
import com.redwave.downloader.core.model.DownloadStatus
import com.redwave.downloader.game.actors.AScrollPane
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.layout.AColumn
import com.redwave.downloader.game.actors.layout.ADyn
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.AButtonRed
import com.redwave.downloader.game.actors.ui.AChip
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.AProgressBar
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.controller.DownloadController.Submit
import com.redwave.downloader.game.platform.LauncherApp
import com.redwave.downloader.game.screens.base.RedwaveScreen
import com.redwave.downloader.game.screens.base.statusScrim
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// LauncherScreen — власний головний екран (роль HOME): годинник, картка лінка
// з буфера (ClipGate), віджет завантажень, віджет плеєра, «All apps», док.
// «Назад» нічого не робить; повторне «Додому» закриває сітку й гортає вгору.
// ─────────────────────────────────────────────────────────────────────────────
class LauncherScreen : RedwaveScreen() {

    private val side = px(18f)
    private lateinit var clock: AMsdfLabel
    private lateinit var date: AMsdfLabel
    private var lastMinute = ""
    private var drawer: AdvancedGroup? = null
    private var clipBusy = false
    private lateinit var col: AColumn
    private lateinit var scroll: AScrollPane

    override val toastBottom: Float get() = safeNavBarUI + px(120f)

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        content.addActor(Image(TextureRegionDrawable(ACover.coverRegion(assets.LAUNCHER_BG, w, h))).apply { setBounds(0f, 0f, w, h) })
        // linear-gradient(180deg, rgba(8,4,5,.35), rgba(8,4,5,.75)) поверх уже розмитого фону
        content.addActor(ARect(this, 0f, Color(0.03f, 0.016f, 0.02f, 0.25f), Color(0.03f, 0.016f, 0.02f, 0.6f), angleCss = 180f).apply { setBounds(0f, 0f, w, h) })

        // ── Док знизу ──
        val dock = dock(w - side * 2).apply { setPosition(side, safeNavBarUI + px(12f)) }
        content.addActor(dock)
        // ── «All apps» над доком ──
        val handle = object : ATap(this, 0.95f) {
            override fun addContent() {
                val pw = width; val ph = height; val bar = ARect(screen, 999f, GameColor.white_60).apply { setBounds((pw - px(36f)) / 2f, ph - px(4f), px(36f), px(4f)) }
                val l = lbl(Copy.Launcher.ALL_APPS, msdf.semibold(11f, GameColor.white_75)); l.setPosition((width - l.width) / 2f, bar.y - px(4f) - l.height)
                addActor(bar); addActor(l)
            }
        }.onClick { openDrawer() }
        handle.setBounds((w - px(120f)) / 2f, dock.y + dock.height + px(10f), px(120f), px(28f))
        content.addActor(handle)
        // свайп вгору по нижній частині — теж відкриває сітку
        content.addListener(object : InputListener() {
            var sy = 0f
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { sy = y; return true }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) { if (y - sy > px(90f) && sy < h * 0.45f) openDrawer() }
        })

        // ── Колонка зверху (скрол) ──
        col = AColumn(this, px(12f), padTop = safeStatusBarUI + px(14f), padBottom = px(12f), padX = side)
        col.setSize(w, 1f)
        scroll = AScrollPane(col)
        val bottom = handle.y + handle.height + px(6f)
        scroll.setBounds(0f, bottom, w, h - bottom)
        content.addActor(scroll)
        content.addActor(statusScrim(this, Color(0.03f, 0.016f, 0.02f, 1f), alpha = 0.7f))

        clock = lbl("9:41", msdf.clock(62f))
        date = lbl("Tuesday, October 6", msdf.regular(14f, GameColor.white_75))
        col.addActor(object : AdvancedGroup() {
            override val screen = this@LauncherScreen
            override fun addActorsOnGroup() { date.setPosition(0f, 0f); clock.setPosition(-px(2f), date.height + px(2f)); addActor(clock); addActor(date) }
        }.apply { setSize(w - side * 2, clock.height + px(2f) + date.height) })
        updateClock(true)

        val cw = w - side * 2
        col.addActor(ADyn(this, cw, { gdxGame.clipVersion }) { cww -> buildClip(this, cww) })
        col.addActor(ADyn(this, cw, { gdxGame.model.state.downloads.filter { it.status != DownloadStatus.FAILED }.map { it.id } }) { cww -> buildDownloads(this, cww) })
        col.addActor(ADyn(this, cw, { "${gdxGame.player.now?.id}:${gdxGame.model.songs.take(4).map { it.id }}" }) { cww -> buildPlayerWidget(this, cww) })
    }

    // ------------------------------------------------------------------------
    // Годинник
    // ------------------------------------------------------------------------
    private val clockFmt = SimpleDateFormat("H:mm", Locale.US)
    private val dateFmt = SimpleDateFormat("EEEE, MMMM d", Locale.US)

    private fun updateClock(force: Boolean) {
        val now = Date()
        val m = clockFmt.format(now)
        if (!force && m == lastMinute) return
        lastMinute = m
        clock.setText(m); clock.pack()
        date.setText(dateFmt.format(now)); date.pack()
    }

    override fun render(delta: Float) {
        if (::clock.isInitialized) updateClock(false)
        super.render(delta)
    }

    // ------------------------------------------------------------------------
    // Картка буфера
    // ------------------------------------------------------------------------
    private fun buildClip(g: ADyn, w: Float): Float {
        val link = gdxGame.clipLink ?: return 0f
        val isFeed = link is ResolvedLink.PodcastFeed
        val pad = px(13f)
        val url = lbl(link.sourceUrl ?: "", msdf.mono(11.5f, GameColor.white_70)).apply {
            setWrap(true); width = w - pad * 2; height = prefHeight
        }
        // Довгий URL обрізаємо до 3 рядків
        if (url.height > url.style.font.lineHeight * url.fontScaleY * 3.2f) url.height = url.style.font.lineHeight * url.fontScaleY * 3f
        val btnH = px(36f)
        val h = pad + px(18f) + px(7f) + px(20f) + px(7f) + url.height + px(9f) + btnH + pad

        val card = object : AdvancedGroup() {
            override val screen = this@LauncherScreen
            override fun addActorsOnGroup() {
                val pw = width; val ph = height; addActor(Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.3f; setBounds(px(20f), -px(30f), pw - px(40f), px(70f)) })
                addAndFillActor(ARect(screen, px(22f), GameColor.red_35, Color(0.12f, 0.04f, 0.055f, 0.85f), angleCss = 160f, mid = 0.6f, stroke = Color(1f, 0.47f, 0.51f, 0.45f)))
                var y = height - pad - px(18f)
                val ic = icon(assets.ic_clip, px(14f), GameColor.ccEye_FFC2B5)
                val ey = lbl(Copy.Launcher.CLIP_EYEBROW.uppercase(), msdf.style(gdxGame.msdfManager.fontJetBrainsMono_SemiBold, 10.5f, GameColor.ccEye_FFC2B5) { letterSpacing = 10f })
                ic.setPosition(pad, y + (px(18f) - ic.height) / 2f); ey.setPosition(pad + px(20f), y + (px(18f) - ey.height) / 2f)
                val chip = AChip(screen, link.chipLabel, AChip.Kind.KIND); chip.setPosition(width - pad - chip.width, y + (px(18f) - chip.height) / 2f)
                addActor(ic); addActor(ey); addActor(chip)
                y -= px(7f) + px(20f)
                val b = lbl(if (isFeed) Copy.Launcher.FEED_FOUND else Copy.Launcher.AUDIO_FOUND, msdf.bold(16f)); b.setPosition(pad, y); addActor(b)
                y -= px(7f) + url.height
                url.setPosition(pad, y); addActor(url)
                y -= px(9f) + btnH
                val dismiss = AButtonGhost(screen, Copy.Launcher.DISMISS, small = true)
                dismiss.setSize(dismiss.label.width + px(24f), btnH); dismiss.setPosition(width - pad - dismiss.width, y)
                dismiss.onClick { gdxGame.dismissClip() }
                val go = AButtonRed(screen, if (isFeed) Copy.Launcher.CHOOSE_EPISODE else Copy.Home.DOWNLOAD, assets.ic_download, small = true, glow = false)
                go.setBounds(pad, y, dismiss.x - px(8f) - pad, btnH)
                go.onClick { clipGo(link, go) }
                addActor(go); addActor(dismiss)
            }
        }.apply { setSize(w, h) }
        g.addActor(card)
        // поява: зсув +12 і scale 0.98 → 1 за 0.4 с
        card.isTransform = true; card.setOrigin(Align.center)
        card.color.a = 0f; card.y = -px(12f); card.setScale(0.98f)
        card.addAction(Actions.parallel(Actions.fadeIn(0.4f), Actions.moveTo(0f, 0f, 0.4f, Interpolation.pow3Out), Actions.scaleTo(1f, 1f, 0.4f, Interpolation.pow3Out)))
        return h
    }

    private fun clipGo(link: ResolvedLink, btn: AButtonRed) {
        if (clipBusy) return
        if (link is ResolvedLink.PodcastFeed) { openSheet(FeedSheet(this, link.url)); gdxGame.dismissClip(); return }
        if (!gdxGame.bridge.isOnline()) { showToast(Copy.Errors.NO_NETWORK_TITLE); return }
        clipBusy = true
        gdxGame.downloads.startLink(link) { s ->
            when (s) {
                Submit.Checking -> btn.setText(Copy.Home.CHECKING)
                is Submit.Started -> { clipBusy = false; gdxGame.dismissClip() }
                is Submit.Feed -> { clipBusy = false; gdxGame.dismissClip(); openSheet(FeedSheet(this, s.url)) }
                is Submit.Error -> { clipBusy = false; btn.setText(Copy.Home.DOWNLOAD); showToast(s.title) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Віджети
    // ------------------------------------------------------------------------
    private fun glass(w: Float, h: Float) = ARect(this, px(22f), Color(0.07f, 0.035f, 0.047f, 0.6f), stroke = GameColor.white_10).apply { setSize(w, h) }

    private fun buildDownloads(g: ADyn, w: Float): Float {
        val items = gdxGame.model.state.downloads.filter { it.status != DownloadStatus.FAILED }
        if (items.isEmpty()) return 0f
        val pad = px(12f); val rowH = px(30f)
        val h = pad + px(16f) + items.size * (px(10f) + rowH) + pad
        g.addActor(glass(w, h))
        val ic = icon(assets.ic_download, px(14f), GameColor.white_75)
        val head = lbl(Copy.Launcher.downloadingWidget(items.size), msdf.bold(11f, GameColor.white_75))
        ic.setPosition(pad, h - pad - px(16f) + (px(16f) - ic.height) / 2f); head.setPosition(pad + px(20f), h - pad - px(16f) + (px(16f) - head.height) / 2f)
        g.addActor(ic); g.addActor(head)
        var y = h - pad - px(16f)
        items.forEach { d ->
            y -= px(10f) + rowH
            val row = object : AdvancedGroup() {
                override val screen = this@LauncherScreen
                private lateinit var bar: AProgressBar
                private lateinit var pct: AMsdfLabel
                override fun addActorsOnGroup() {
                    pct = lbl("100%", msdf.monoSemi(11f, GameColor.redHi_FF5A6C))
                    val t = lbl(d.title, msdf.semibold(13f)).ellipsize(width - pct.width - px(8f))
                    bar = AProgressBar(screen).apply { setBounds(0f, 0f, w - pad * 2, px(4f)) }
                    t.setPosition(0f, height - t.height + px(2f)); pct.setPosition(width - pct.width, t.y)
                    addActor(t); addActor(pct); addActor(bar)
                }
                override fun act(delta: Float) {
                    super.act(delta)
                    if (!::bar.isInitialized) return
                    val cur = gdxGame.model.state.downloads.firstOrNull { it.id == d.id } ?: return
                    bar.set(cur.progress); pct.setText("${(cur.progress * 100).toInt()}%"); pct.pack(); pct.x = width - pct.width
                }
            }.apply { setBounds(pad, y, w - pad * 2, rowH) }
            g.addActor(row)
        }
        return h
    }

    private fun buildPlayerWidget(g: ADyn, w: Float): Float {
        val p = gdxGame.player
        val now = p.now
        val pad = px(12f)
        if (now != null) {
            val h = px(76f)
            val wid = object : ATap(this, 0.98f) {
                private lateinit var btnIc: Image
                private val prog = ARect(this@LauncherScreen, 0f, GameColor.red_FF2E4D)
                override fun addContent() {
                    addAndFillActor(glass(width, height))
                    val pw = width; val ph = height; val c = ACover(screen, px(12f)).apply { setBounds(pad, (ph - px(52f)) / 2f, px(52f), px(52f)) }; c.setTrack(now); addActor(c)
                    val btn = object : ATap(screen, 0.9f) {
                        override fun addContent() {
                            addAndFillActor(ARect(screen, 999f, Color.WHITE))
                            btnIc = icon(assets.ic_pause_fill, px(16f), GameColor.miniInk_14080B); btnIc.setPosition((width - btnIc.width) / 2f, (height - btnIc.height) / 2f); addActor(btnIc)
                        }
                    }.onClick { p.toggle() }
                    btn.setBounds(width - pad - px(40f), (height - px(40f)) / 2f, px(40f), px(40f)); addActor(btn)
                    val mx = pad + px(52f + 11f); val mw = btn.x - px(10f) - mx
                    val t = lbl(now.title, msdf.bold(14f)).ellipsize(mw); val a = lbl(now.artist, msdf.regular(12f, GameColor.white_60)).ellipsize(mw)
                    t.setPosition(mx, height / 2f + px(1f)); a.setPosition(mx, height / 2f - a.height - px(1f))
                    addActor(t); addActor(a); addActor(prog)
                }
                override fun act(delta: Float) {
                    super.act(delta)
                    if (!::btnIc.isInitialized) return
                    btnIc.drawable = TextureRegionDrawable(if (p.isPlaying) assets.ic_pause_fill else assets.ic_play_fill)
                    prog.setBounds(px(14f), px(2f), (width - px(28f)) * p.progress, px(3f))
                }
            }.onClick { gdxGame.navigationManager.navigate(PlayerScreen::class.java.name, LauncherScreen::class.java.name) }
            wid.setSize(w, h); g.addActor(wid)
            return h
        }
        val recent = gdxGame.model.songs.take(4)
        if (recent.isEmpty()) return 0f
        val gap = px(8f); val cs = (w - pad * 2 - gap * 3) / 4f
        val h = pad + px(16f) + px(10f) + cs + pad
        g.addActor(glass(w, h))
        val head = lbl(Copy.Launcher.RECENT_WIDGET, msdf.bold(11f, GameColor.white_75)); head.setPosition(pad, h - pad - px(16f) + (px(16f) - head.height) / 2f); g.addActor(head)
        recent.forEachIndexed { i, t ->
            val b = object : ATap(this, 0.95f) {
                override fun addContent() { val c = ACover(screen, px(12f)); addAndFillActor(c); c.setTrack(t) }
            }.onClick { gdxGame.player.play(t) }
            b.setBounds(pad + i * (cs + gap), pad, cs, cs); g.addActor(b)
        }
        return h
    }

    // ------------------------------------------------------------------------
    // Док + сітка застосунків
    // ------------------------------------------------------------------------
    private fun appIcon(app: LauncherApp?, label: String?, size: Float, redwave: Boolean = false) = object : ATap(this, 0.92f) {
        override fun addContent() {
            val pw = width; val ph = height; val img = Image().apply { setBounds((pw - size) / 2f, ph - size, size, size); touchable = Touchable.disabled }
            if (redwave) img.drawable = TextureRegionDrawable(assets.LOGO)
            else if (app != null) gdxGame.covers.forApp(app, 144) { tex -> tex?.let { img.drawable = TextureRegionDrawable(it) } }
            addActor(img)
            label?.let { l ->
                val lb = lbl(l, msdf.medium(11f, Color.WHITE).apply { dropShadow(0f, 1f, 4f, Color(0f, 0f, 0f, 0.5f)) }).ellipsize(width)
                lb.setAlignment(Align.center); lb.setPosition((width - lb.width) / 2f, 0f); addActor(lb)
            }
        }
    }.onClick {
        when {
            redwave -> gdxGame.navigationManager.navigate(AppScreen::class.java.name, LauncherScreen::class.java.name)
            app != null -> gdxGame.bridge.launchApp(app)
        }
    }

    private fun dock(w: Float) = object : AdvancedGroup() {
        override val screen = this@LauncherScreen
        override fun addActorsOnGroup() {
            addAndFillActor(ARect(screen, px(26f), GameColor.white_10))
            val apps = gdxGame.bridge.dockApps()
            val slots: List<Pair<LauncherApp?, Boolean>> = listOf(apps.getOrNull(0) to false, apps.getOrNull(1) to false, null to true, apps.getOrNull(2) to false, apps.getOrNull(3) to false)
            val pad = px(8f); val iw = (width - pad * 2) / 5f
            slots.forEachIndexed { i, (app, rw) ->
                val ic = appIcon(app, null, px(48f), rw)
                ic.setBounds(pad + i * iw, px(10f), iw, px(48f))
                addActor(ic)
            }
        }
    }.apply { setSize(w, px(68f)) }

    private fun openDrawer() {
        if (drawer != null) return
        val w = worldWidth; val h = worldHeight
        val d = object : AdvancedGroup() {
            override val screen = this@LauncherScreen
            private var lastApps = -1
            private lateinit var grid: AColumn
            override fun addActorsOnGroup() {
                addAndFillActor(Image(drawerUtil.whiteRegion).apply { color.set(0.04f, 0.024f, 0.027f, 0.985f) })
                val title = lbl(Copy.Launcher.ALL_APPS, msdf.bold(17f)); title.setPosition(side, h - safeStatusBarUI - px(14f) - title.height); addActor(title)
                grid = AColumn(screen, px(18f), padTop = px(6f), padBottom = safeNavBarUI + px(18f), padX = side)
                grid.setSize(w, 1f)
                val sc = AScrollPane(grid).apply { setBounds(0f, 0f, w, title.y - px(12f)) }
                addActor(sc)
                fill()
                addListener(object : InputListener() {
                    var sy = 0f
                    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { sy = y; return true }
                    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) { if (sy - y > px(120f) && sc.scrollY <= 0f) closeDrawer() }
                })
            }
            fun fill() {
                lastApps = gdxGame.appsVersion
                grid.disposeAndClearChildren()
                val apps = gdxGame.bridge.listApps()
                val cols = 4; val cw = (w - side * 2 - px(6f) * 3) / cols; val ch = px(48f + 6f + 15f)
                apps.chunked(cols).forEach { rowApps ->
                    val row = object : AdvancedGroup() {
                        override val screen = this@LauncherScreen
                        override fun addActorsOnGroup() {
                            rowApps.forEachIndexed { i, a -> val ic = appIcon(a, a.label, px(48f)); ic.setBounds(i * (cw + px(6f)), 0f, cw, ch); addActor(ic) }
                        }
                    }.apply { setSize(w - side * 2, ch) }
                    grid.addActor(row)
                }
            }
            override fun act(delta: Float) {
                super.act(delta)
                if (::grid.isInitialized && gdxGame.appsVersion != lastApps) { gdxGame.covers.invalidateApps(); fill() }
            }
        }
        d.setBounds(0f, 0f, w, h)
        content.addActor(d)
        d.color.a = 0f; d.y = -px(30f)
        d.addAction(Actions.parallel(Actions.fadeIn(0.22f), Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow3Out)))
        drawer = d
    }

    private fun closeDrawer() {
        val d = drawer ?: return
        drawer = null
        d.touchable = Touchable.disabled
        d.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0.18f), Actions.moveTo(0f, -px(30f), 0.18f)), Actions.removeActor()))
    }

    // ------------------------------------------------------------------------
    // HOME / Back
    // ------------------------------------------------------------------------
    /** «Додому», коли лаунчер уже відкритий: закрити шторку/сітку і догори. */
    fun onHomeAgain() {
        sheet?.close()
        closeDrawer()
        if (::scroll.isInitialized) scroll.scrollY = 0f
    }

    override fun onBackPressed() {
        when {
            sheet != null -> sheet?.close()
            drawer != null -> closeDrawer()
            else -> {}   // лаунчер «Назад» не закриває
        }
    }
}
