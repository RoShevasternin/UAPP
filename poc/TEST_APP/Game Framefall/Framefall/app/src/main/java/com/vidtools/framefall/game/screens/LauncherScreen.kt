package com.vidtools.framefall.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.link.ResolvedLink
import com.vidtools.framefall.core.link.chipLabel
import com.vidtools.framefall.core.link.sourceUrl
import com.vidtools.framefall.core.model.DownloadStatus
import com.vidtools.framefall.game.actors.AScrollPane
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.layout.ADyn
import com.vidtools.framefall.game.actors.ui.AButtonGhost
import com.vidtools.framefall.game.actors.ui.AButtonAccent
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.ACover
import com.vidtools.framefall.game.actors.ui.AProgressBar
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.controller.DownloadController.Submit
import com.vidtools.framefall.game.platform.LauncherApp
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.screens.base.statusScrim
import com.vidtools.framefall.game.actors.ui.AIconButton
import com.vidtools.framefall.game.actors.ui.AVideo
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.platform.VideoPlayer
import com.vidtools.framefall.core.model.HomeBg
import com.vidtools.framefall.core.link.hostLabel
import com.vidtools.framefall.game.screens.sheets.AppMenuSheet
import com.vidtools.framefall.game.screens.sheets.HomeBgSheet
import com.vidtools.framefall.game.screens.sheets.QualitySheet
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// LauncherScreen — власний головний екран (роль HOME): живий відеофон (петля без звуку,
// вбудована або своя зі Studio), годинник, картка лінка з буфера (ClipGate), віджет
// «Saving», «Continue watching», «All apps», док, ⋮ — вибір фону.
// «Назад» нічого не робить; повторне «Додому» закриває сітку й гортає вгору.
// Довге натискання на іконку → App info / Uninstall, лише з AppFlags.isUninstall = true (картка сервера: app_open не custom*).
// ─────────────────────────────────────────────────────────────────────────────
class LauncherScreen : FramefallScreen() {

    override val analyticsBt    = com.vidtools.framefall.businesModule.backend.Bt.HUB
    override val analyticsBlock = "launcher_screen"

    private val side = px(18f)
    private lateinit var clock: AMsdfLabel
    private lateinit var date: AMsdfLabel
    private var lastMinute = ""
    private var drawer: AdvancedGroup? = null
    private var clipBusy = false
    private lateinit var col: AColumn
    private lateinit var scroll: AScrollPane

    override val toastBottom: Float get() = safeNavBarUI + px(120f)

    /** Відеофон: свій плеєр, без звуку, по колу. Ставиться на паузу, коли лаунчер схований. */
    private val bgPlayer: VideoPlayer = gdxGame.bridge.createVideoPlayer()
    private lateinit var bg: AVideo
    private var bgKey: String? = null

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        bg = AVideo(this, bgPlayer, AVideo.Fit.COVER).apply { setBounds(0f, 0f, w, h) }
        content.addActor(bg)
        openBackground()
        // Затемнення зверху (годинник) і знизу (док) — відео лишається видно посередині
        content.addActor(ARect(this, 0f, Color(0.04f, 0.027f, 0.02f, 0.55f), Color(0.04f, 0.027f, 0.02f, 0f), angleCss = 180f, mid = 0.35f).apply { setBounds(0f, h * 0.55f, w, h * 0.45f); touchable = Touchable.disabled })
        content.addActor(ARect(this, 0f, Color(0.04f, 0.027f, 0.02f, 0f), Color(0.04f, 0.027f, 0.02f, 0.7f), angleCss = 180f, mid = 0.4f).apply { setBounds(0f, 0f, w, h * 0.45f); touchable = Touchable.disabled })

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

        clock = lbl("9:41", msdf.clock(62f).apply { dropShadow(0f, 2f, 10f, Color(0f, 0f, 0f, 0.45f)) })
        date = lbl("Tuesday, October 6", msdf.regular(14f, GameColor.white_75).apply { dropShadow(0f, 1f, 6f, Color(0f, 0f, 0f, 0.5f)) })
        col.addActor(object : AdvancedGroup() {
            override val screen = this@LauncherScreen
            override fun addActorsOnGroup() {
                date.setPosition(0f, 0f); clock.setPosition(-px(2f), date.height + px(2f)); addActor(clock); addActor(date)
                // ⋮ — фон головного екрана
                val menu = AIconButton(this@LauncherScreen, assets.ic_more, px(18f), bgColor = Color(0f, 0f, 0f, 0.3f), strokeColor = GameColor.white_14)
                menu.setBounds(width - px(38f), height - px(38f) - px(6f), px(38f), px(38f))
                menu.onClick { openSheet(HomeBgSheet(this@LauncherScreen) { openBackground() }) }
                addActor(menu)
            }
        }.apply { setSize(w - side * 2, clock.height + px(2f) + date.height) })
        updateClock(true)

        val cw = w - side * 2
        col.addActor(ADyn(this, cw, { gdxGame.clipVersion to gdxGame.model.state.showClipCard }) { cww -> buildClip(this, cww) })
        col.addActor(ADyn(this, cw, { gdxGame.model.state.downloads.filter { it.status != DownloadStatus.FAILED }.map { it.id } }) { cww -> buildDownloads(this, cww) })
        col.addActor(ADyn(this, cw, { gdxGame.model.state.showContinue to widgetVideos().map { it.id } }) { cww -> buildContinue(this, cww) })
    }

    // ------------------------------------------------------------------------
    // Відеофон
    // ------------------------------------------------------------------------
    private fun openBackground() {
        val key = gdxGame.model.state.homeBg
        if (key == bgKey) return
        bgKey = key
        val own = !HomeBg.isBuiltIn(key) && java.io.File(key).exists()
        val uri = if (own) "file://$key" else gdxGame.bridge.builtInBackground(key)
        bg.poster = if (!own && key == HomeBg.SINTEL) assets.BG_SINTEL else assets.LAUNCHER_BG
        bgPlayer.open(uri, autoplay = true, loop = true, muted = true)
    }

    override fun hide() {
        bgPlayer.release()
        super.hide()
    }

    override fun dispose() {
        bgPlayer.release()
        super.dispose()
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
        // Повернулись на Home (GL-пауза ставить фон на паузу) — фон знову грає; фон змінили — відкриваємо новий
        if (::bg.isInitialized) {
            openBackground()
            if (!bgPlayer.isPlaying && sheet == null) { bgAcc += delta; if (bgAcc > 0.5f) { bgAcc = 0f; bgPlayer.play() } } else bgAcc = 0f
        }
        super.render(delta)
    }
    private var bgAcc = 0f

    // ------------------------------------------------------------------------
    // Картка буфера
    // ------------------------------------------------------------------------
    private fun buildClip(g: ADyn, w: Float): Float {
        val link = gdxGame.clipLink ?: return 0f
        if (!gdxGame.model.state.showClipCard) return 0f
        val isPage = link is ResolvedLink.ArchiveItem || link is ResolvedLink.NasaItem
        val pad = px(13f)
        val url = lbl(link.sourceUrl ?: "", msdf.mono(11.5f, GameColor.white_70)).apply {
            setWrap(true); width = w - pad * 2; height = prefHeight
        }
        // Довгий URL обрізаємо до 3 рядків
        if (url.height > url.style.font.lineHeight * url.fontScaleY * 3.2f) url.height = url.style.font.lineHeight * url.fontScaleY * 3f
        val btnH = px(38f)
        val h = pad + px(18f) + px(7f) + px(20f) + px(7f) + url.height + px(10f) + btnH + pad

        val card = object : AdvancedGroup() {
            override val screen = this@LauncherScreen
            override fun addActorsOnGroup() {
                val pw = width
                addActor(Image(assets.GLOW).apply { color.set(GameColor.accent_FF8A2A); color.a = 0.3f; setBounds(px(20f), -px(30f), pw - px(40f), px(70f)) })
                addAndFillActor(ARect(screen, px(22f), Color(0.1f, 0.07f, 0.05f, 0.82f), Color(0.07f, 0.05f, 0.035f, 0.88f), angleCss = 160f, mid = 0.6f, stroke = GameColor.accentText_35))
                var y = height - pad - px(18f)
                val ic = icon(assets.ic_clip, px(14f), GameColor.ccEye_FFD59A)
                val ey = lbl("${Copy.Launcher.CLIP_EYEBROW} · ${link.hostLabel}".uppercase(), msdf.style(gdxGame.msdfManager.fontJetBrainsMono_SemiBold, 10.5f, GameColor.ccEye_FFD59A) { letterSpacing = 8f })
                val chip = AChip(screen, link.chipLabel, AChip.Kind.KIND); chip.setPosition(width - pad - chip.width, y + (px(18f) - chip.height) / 2f)
                ic.setPosition(pad, y + (px(18f) - ic.height) / 2f); ey.ellipsize(chip.x - pad - px(28f)); ey.setPosition(pad + px(20f), y + (px(18f) - ey.height) / 2f)
                addActor(ic); addActor(ey); addActor(chip)
                y -= px(7f) + px(20f)
                val b = lbl(if (isPage) Copy.Launcher.PAGE_FOUND else Copy.Launcher.VIDEO_FOUND, msdf.bold(16f)); b.setPosition(pad, y); addActor(b)
                y -= px(7f) + url.height
                url.setPosition(pad, y); addActor(url)
                y -= px(10f) + btnH
                val dismiss = AButtonGhost(screen, Copy.Launcher.DISMISS, small = true)
                dismiss.setSize(dismiss.label.width + px(24f), btnH); dismiss.setPosition(width - pad - dismiss.width, y)
                dismiss.onClick { gdxGame.dismissClip() }
                val go = AButtonAccent(screen, if (isPage) Copy.Launcher.CHOOSE else Copy.Launcher.SAVE, assets.ic_download, small = true, glow = false)
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

    private fun clipGo(link: ResolvedLink, btn: AButtonAccent) {
        if (clipBusy) return
        if (!gdxGame.bridge.isOnline()) { showToast(Copy.Errors.NO_NETWORK_TITLE); return }
        clipBusy = true
        gdxGame.downloads.startLink(link) { s ->
            when (s) {
                Submit.Checking -> btn.setText(Copy.Save.CHECKING)
                is Submit.Started -> { clipBusy = false; gdxGame.dismissClip() }
                is Submit.Choose -> { clipBusy = false; gdxGame.dismissClip(); openSheet(QualitySheet(this, s)) }
                is Submit.Error -> { clipBusy = false; btn.setText(Copy.Launcher.SAVE); showToast(s.title) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Віджети
    // ------------------------------------------------------------------------
    private fun glass(w: Float, h: Float) = ARect(this, px(22f), Color(0.06f, 0.045f, 0.035f, 0.62f), stroke = GameColor.white_10).apply { setSize(w, h) }

    private fun buildDownloads(g: ADyn, w: Float): Float {
        val items = gdxGame.model.state.downloads.filter { it.status != DownloadStatus.FAILED }
        if (items.isEmpty()) return 0f
        val pad = px(12f); val rowH = px(30f)
        val h = pad + px(16f) + items.size * (px(10f) + rowH) + pad
        g.addActor(glass(w, h))
        val ic = icon(assets.ic_download, px(14f), GameColor.white_75)
        val head = lbl(Copy.Launcher.savingWidget(items.size), msdf.bold(11f, GameColor.white_75))
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
                    pct = lbl("100%", msdf.monoSemi(11f, GameColor.amber_FFC94D))
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

    /** «Continue watching»; нічого не почато — останні збережені. */
    private fun widgetVideos() = gdxGame.model.continueWatching.ifEmpty { gdxGame.model.videos }.take(3)

    private fun buildContinue(g: ADyn, w: Float): Float {
        if (!gdxGame.model.state.showContinue) return 0f
        val list = widgetVideos()
        if (list.isEmpty()) return 0f
        val pad = px(12f); val gap = px(8f)
        val tw = (w - pad * 2 - gap * 2) / 3f; val th = tw * 9f / 16f
        val h = pad + px(16f) + px(10f) + th + px(6f) + px(15f) + pad
        g.addActor(glass(w, h))
        val head = lbl((if (gdxGame.model.continueWatching.isEmpty()) Copy.Launcher.RECENT else Copy.Launcher.CONTINUE).uppercase(), msdf.style(gdxGame.msdfManager.fontJetBrainsMono_SemiBold, 10.5f, GameColor.white_75) { letterSpacing = 8f })
        head.setPosition(pad, h - pad - px(16f) + (px(16f) - head.height) / 2f); g.addActor(head)
        val n = lbl(gdxGame.model.continueWatching.size.takeIf { it > 0 }?.toString() ?: "", msdf.monoSemi(11f, GameColor.white_60))
        n.setPosition(w - pad - n.width, head.y); g.addActor(n)
        list.forEachIndexed { i, v ->
            val b = object : ATap(this, 0.96f) {
                override fun addContent() {
                    val ph = height
                    val t = AVideoThumb(screen, px(10f), showDuration = false).apply { setBounds(0f, ph - th, tw, th) }
                    t.setVideo(v); addActor(t)
                    val l = lbl(v.title, msdf.semibold(11.5f).apply { dropShadow(0f, 1f, 4f, Color(0f, 0f, 0f, 0.5f)) }).ellipsize(tw)
                    l.setPosition(0f, 0f); addActor(l)
                }
            }.onClick { gdxGame.openVideo(v) }
            b.setBounds(pad + i * (tw + gap), pad, tw, th + px(6f) + px(15f)); g.addActor(b)
        }
        return h
    }

    // ------------------------------------------------------------------------
    // Док + сітка застосунків
    // ------------------------------------------------------------------------
    private fun appIcon(app: LauncherApp?, label: String?, size: Float, framefall: Boolean = false) = object : ATap(this, 0.92f) {
        override fun addContent() {
            val pw = width; val ph = height; val img = Image().apply { setBounds((pw - size) / 2f, ph - size, size, size); touchable = Touchable.disabled }
            if (framefall) img.drawable = TextureRegionDrawable(assets.LOGO)
            else if (app != null) gdxGame.covers.forApp(app, 144) { tex -> tex?.let { img.drawable = TextureRegionDrawable(it) } }
            addActor(img)
            label?.let { l ->
                val lb = lbl(l, msdf.medium(11f, Color.WHITE).apply { dropShadow(0f, 1f, 4f, Color(0f, 0f, 0f, 0.5f)) }).ellipsize(width)
                lb.setAlignment(Align.center); lb.setPosition((width - lb.width) / 2f, 0f); addActor(lb)
            }
        }
    }.onClick {
        when {
            framefall -> gdxGame.openAppFromLauncher()
            app != null -> gdxGame.bridge.launchApp(app)
        }
    }.apply {
        // AppFlags.isUninstall: true — як системний лаунчер (App info / Uninstall);
        // false — довге натискання нічого не робить, видалення лише через системні Налаштування
        onLongPress = lp@{
            if (!gdxGame.flags.isUninstall || (app == null && !framefall)) return@lp false
            gdxGame.bridge.vibrate(20)
            openSheet(AppMenuSheet(this@LauncherScreen, if (framefall) null else app))
            true
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
