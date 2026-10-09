package com.driftglass.home.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.logic.DayCycle
import com.driftglass.home.core.logic.HomeEntry
import com.driftglass.home.core.logic.HomeLayout
import com.driftglass.home.core.model.DaySlot
import com.driftglass.home.core.model.Shuffle
import com.driftglass.home.game.actors.AScrollPane
import com.driftglass.home.game.actors.label.AMsdfLabel
import com.driftglass.home.game.actors.layout.AColumn
import com.driftglass.home.game.actors.ui.AIconButton
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.wallpaper.AWallpaper
import com.driftglass.home.game.platform.LauncherApp
import com.driftglass.home.game.platform.TextInputRequest
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.screens.ui.MenuSheet
import com.driftglass.home.game.screens.ui.appIcon
import com.driftglass.home.game.screens.ui.folderIcon
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
import com.driftglass.home.util.log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// LauncherScreen — головний екран Driftglass (роль HOME):
//   живі шпалери на весь екран, годинник і дата, скляна картка поточних шпалер
//   (⇄ інші / ♡ обране / ⚙ налаштувати), бейджі циклу дня й автозміни, одна сторінка апок
//   з авто-папками (core/logic/HomeLayout; перша — сама Driftglass), без гортання вбік;
//   смужка «Усі застосунки» (ті самі папки + усі апки, вертикальний скрол), док із дефолтних апок людини.
// Жести на порожньому місці: подвійний тап — інші шпалери, довге натискання — меню,
// свайп угору (і з іконки теж) — усі застосунки. «Назад» нічого не робить; повторне «Додому» закриває все.
// ─────────────────────────────────────────────────────────────────────────────
class LauncherScreen : DgScreen() {

    private val side = px(16f)
    private lateinit var wall: AWallpaper
    private lateinit var clock: AMsdfLabel
    private lateinit var date: AMsdfLabel
    private var lastMinute = ""
    private var lastHomeId = ""
    private var lastSlot: DaySlot? = null
    private var lastModel = -1
    private var lastApps = -1
    private var checkIn = 0f
    private var drawer: Drawer? = null
    private lateinit var top: AdvancedGroup
    private lateinit var bottom: AdvancedGroup
    /** Низ верхнього блоку (картка / бейджі) — звідси починається сітка апок. */
    private var topBottom = 0f

    override val toastBottom: Float get() = safeNavBarUI + px(150f)

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        wall = AWallpaper(this).apply { setBounds(0f, 0f, w, h) }
        disposableSet += wall
        content.addActor(wall)
        wall.show(gdxGame.model.homeWallpaper(), instant = true)
        lastHomeId = gdxGame.model.homeWallpaper().id

        // Ловець жестів на порожньому місці — під усіма кнопками
        content.addActor(GestureCatcher().apply { setBounds(0f, 0f, w, h) })

        top = group(this, w, h) {}.apply { touchable = Touchable.childrenOnly }
        bottom = group(this, w, h) {}.apply { touchable = Touchable.childrenOnly }
        content.addActor(top); content.addActor(bottom)
        rebuildTop(); rebuildBottom()
        updateClock(true)
    }

    // ------------------------------------------------------------------------
    // Верх: годинник, дата, скляна картка, бейджі
    // ------------------------------------------------------------------------
    private fun rebuildTop() {
        top.disposeAndClearChildren()
        val m = gdxGame.model
        val wp = m.homeWallpaper()
        val cw = worldWidth - side * 2
        var y = worldHeight - safeStatusBarUI - px(18f)

        clock = lbl("9:41", msdf.clock(66f))
        date = lbl("", msdf.medium(14f, Color.WHITE.cpy().apply { a = 0.9f }).apply { dropShadow(0f, 1f, 6f, GameColor.black_35) })
        y -= clock.height; clock.setPosition(side - px(3f), y); top.addActor(clock)
        y -= px(4f) + date.height; date.setPosition(side, y); top.addActor(date)
        updateClock(true)

        // .now — скляна картка
        val ch = px(60f)
        y -= px(14f) + ch
        val card = group(this, cw, ch) {
            addAndFillActor(glass(this@LauncherScreen))
            val bs = px(38f); val gap = px(8f); val pad = px(11f)
            val bx = width - pad - bs * 3 - gap * 2
            val sub = buildString {
                append(L.style(wp.style)); append(" · "); append(L.palette(wp.palette))
                if (m.state.dayCycle) { append(" · "); append(L.slot(DayCycle.slotOf(m.hourNow()))) }
            }
            val t = lbl(displayName(wp), msdf.semibold(14f)).ellipsize(bx - px(14f) - px(8f))
            val s = lbl(sub, msdf.regular(11.5f, GameColor.white_70)).ellipsize(bx - px(14f) - px(8f))
            val total = t.height + px(1f) + s.height
            t.setPosition(px(14f), (height + total) / 2f - t.height); s.setPosition(px(14f), t.y - px(1f) - s.height)
            addActor(t); addActor(s)
            val fav = m.state.isFavorite(wp.id)
            val buttons = listOf(
                AIconButton(this@LauncherScreen, assets.ic_shuffle).apply { setOnClickListener { doShuffle() } },
                AIconButton(this@LauncherScreen, if (fav) assets.ic_heart_fill else assets.ic_heart, if (fav) GameColor.rose_FF7AA8 else Color.WHITE).apply {
                    setOnClickListener { val on = gdxGame.model.toggleFavorite(wp.id); showToast(if (on) L.tFavAdd else L.tFavRem) }
                },
                AIconButton(this@LauncherScreen, assets.ic_sliders).apply { setOnClickListener { gdxGame.draft = wp.copy(); gdxGame.navigationManager.navigate(StudioScreen::class.java.name, LauncherScreen::class.java.name) } },
            )
            buttons.forEachIndexed { i, b -> b.setBounds(bx + i * (bs + gap), (height - bs) / 2f, bs, bs); addActor(b) }
        }
        card.setPosition(side, y); top.addActor(card)

        // бейджі (.cyc)
        val badges = mutableListOf<Pair<com.badlogic.gdx.graphics.g2d.TextureRegion, String>>()
        if (m.state.dayCycle) {
            val slot = DayCycle.slotOf(m.hourNow())
            badges += (if (slot == DaySlot.NIGHT) assets.ic_moon else assets.ic_sun) to "${L.dayCycle} · ${L.slot(slot)}"
        }
        if (m.state.shuffle != Shuffle.OFF && !m.state.dayCycle) badges += assets.ic_cycle to "${L.autoShuffle} · ${L.shuffleOn(m.state.shuffle)}"
        badges.forEach { (region, text) ->
            val l = lbl(text, msdf.medium(11.5f))
            val bh = px(28f); val bw = px(10f) + px(14f) + px(6f) + l.width + px(10f)
            y -= px(8f) + bh
            val b = group(this, bw, bh) {
                addAndFillActor(glass(this@LauncherScreen, 999f))
                val ic = icon(region, px(14f)); ic.setPosition(px(10f), (height - ic.height) / 2f); addActor(ic)
                l.setPosition(px(30f), (height - l.height) / 2f); addActor(l)
            }
            b.setPosition(side, y); top.addActor(b)
        }
        topBottom = y
        lastModel = m.version
    }

    // ------------------------------------------------------------------------
    // Низ: док, смужка, крапки сторінок і сітка апок з папками (як у MIUI / Pixel)
    // ------------------------------------------------------------------------
    private fun rebuildBottom() {
        bottom.disposeAndClearChildren()
        lastApps = gdxGame.appsVersion
        val st = gdxGame.model.state.settings
        val bridge = gdxGame.bridge
        val w = worldWidth
        val cw = w - side * 2

        // док
        val dockH = px(76f)
        val dockApps = bridge.dockApps()
        val dock = group(this, cw, dockH) {
            addAndFillActor(glass(this@LauncherScreen, px(28f)))
            val pad = px(8f); val iw = (width - pad * 2) / 4f
            dockApps.forEachIndexed { i, app ->
                if (app == null) return@forEachIndexed
                val ic = appIcon(this@LauncherScreen, app, iw, px(52f), label = false)
                ic.setPosition(pad + i * iw, (height - ic.height) / 2f)
                ic.setOnClickListener { bridge.launchApp(app) }
                ic.onLongPress = { appMenu(app); true }
                addActor(ic)
            }
        }
        dock.setPosition(side, safeNavBarUI + px(14f)); bottom.addActor(dock)

        // смужка «усі застосунки»
        val handle = tap(this, px(120f), px(22f), 0.95f) {
            addActor(ARect(this@LauncherScreen, 999f, GameColor.white_55).apply { setBounds((width - px(44f)) / 2f, (height - px(5f)) / 2f, px(44f), px(5f)) })
        }.apply { setOnClickListener { openDrawer() } }
        handle.setPosition((w - handle.width) / 2f, dock.y + dockH + px(4f)); bottom.addActor(handle)

        // Головний екран — ОДНА сторінка без гортання (рішення VELDAN 09.10.2026): Driftglass + папки + апки,
        // скільки влазить між карткою шпалер і доком. Усе інше — свайпом угору (Drawer, вертикальний скрол).
        val cols = st.cols
        val dockKeys = dockApps.filterNotNull().map { it.packageName to it.activityName }.toSet()
        val entries = homeEntries(bridge.listApps().filter { (it.packageName to it.activityName) !in dockKeys })
        val items: List<HomeEntry<LauncherApp?>> = listOf<HomeEntry<LauncherApp?>>(HomeEntry.App(null)) + entries

        val iconSize = if (cols == 5) px(46f) else px(54f)
        val labelH = if (st.labels) px(19f) else 0f
        val cellH = iconSize + labelH + px(16f)
        val colW = cw / cols
        val gridBottom = handle.y + handle.height + px(6f)
        val gridTop = topBottom - px(18f)
        val rows = HomeLayout.rowsFor(gridTop - gridBottom, cellH).coerceAtMost(6)
        val shown = HomeLayout.pages(items, cols, rows).first()

        val grid = group(this, w, gridTop - gridBottom) {}.apply { touchable = Touchable.childrenOnly }
        grid.setPosition(0f, gridBottom)
        shown.forEachIndexed { k, e ->
            val r = k / cols; val c = k % cols
            val ic = entryIcon(e, colW, iconSize, st.labels)
            ic.setPosition(side + c * colW, grid.height - (r + 1) * cellH + px(16f))
            grid.addActor(ic)
        }
        grid.addListener(SwipeUpListener())
        bottom.addActor(grid)
    }

    /** Папки + апки (core/logic/HomeLayout) — однаково для головного екрана і списку всіх апок. */
    private fun homeEntries(apps: List<LauncherApp>): List<HomeEntry<LauncherApp>> =
        HomeLayout.build(apps, { it.packageName }, { it.label }, { it.category }, { it.system })

    /** Клітинка сітки: папка (тап → вікно папки) або апка (null — сама Driftglass). */
    private fun entryIcon(e: HomeEntry<LauncherApp?>, colW: Float, iconSize: Float, labels: Boolean): com.driftglass.home.game.actors.ui.ATap = when (e) {
        is HomeEntry.Folder -> {
            val title = L.folder(e.kind); val apps = e.apps.filterNotNull()
            folderIcon(this, title, apps, colW, iconSize, labels).apply { setOnClickListener { openFolder(title, apps) } }
        }
        is HomeEntry.App -> {
            val app = e.app
            appIcon(this, app, colW, iconSize, labels).apply {
                if (app == null) {
                    setOnClickListener { gdxGame.openAppFromLauncher() }
                    onLongPress = { appMenu(null); true }
                } else {
                    setOnClickListener { gdxGame.bridge.launchApp(app) }
                    onLongPress = { appMenu(app); true }
                }
            }
        }
    }

    /** Свайп угору, що почався на іконці / папці, — теж відкриває всі застосунки. */
    private inner class SwipeUpListener : InputListener() {
        private var downY = 0f
        private var taken = false
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            if (pointer > 0) return false
            downY = event.stageY; taken = false
            return true
        }
        override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
            if (!taken && event.stageY - downY > px(70f)) {
                taken = true
                event.stage.cancelTouchFocusExcept(this, event.listenerActor)
                openDrawer()
            }
        }
    }

    // ------------------------------------------------------------------------
    // Папка: скляне вікно з назвою і сіткою апок (тап поза ним / «Назад» / «Додому» — закрити)
    // ------------------------------------------------------------------------
    private var folder: AdvancedGroup? = null

    private fun openFolder(title: String, apps: List<LauncherApp>) {
        if (folder != null) return
        val w = worldWidth; val h = worldHeight
        val f = group(this, w, h) {
            val dim = tap(this@LauncherScreen, w, h, 1f) {
                addAndFillActor(Image(drawerUtil.whiteRegion).apply { color.set(0f, 0f, 0f, 0.5f) })
            }.apply { setOnClickListener { closeFolder() } }
            addActor(dim)
            val cols = 4
            val pw = w - side * 2
            val pad = px(14f)
            val colW = (pw - pad * 2) / cols
            val cellH = px(52f) + px(19f) + px(14f)
            val rowsAll = (apps.size + cols - 1) / cols
            val rowsShown = rowsAll.coerceIn(1, 5)
            val ph = pad * 2 + rowsShown * cellH - px(14f)
            val panel = group(this@LauncherScreen, pw, ph) {
                addAndFillActor(glass(this@LauncherScreen, px(28f), GameColor.glassStrong))
                val grid = group(this@LauncherScreen, pw - pad * 2, rowsAll * cellH - px(14f)) {
                    apps.forEachIndexed { k, a ->
                        val r = k / cols; val c = k % cols
                        val ic = appIcon(this@LauncherScreen, a, colW, px(52f), label = true)
                        ic.setPosition(c * colW, height - (r + 1) * cellH + px(14f))
                        ic.setOnClickListener { closeFolder(); gdxGame.bridge.launchApp(a) }
                        ic.onLongPress = { appMenu(a); true }
                        addActor(ic)
                    }
                }
                val sc = AScrollPane(grid, scrollX = false, scrollY = rowsAll > rowsShown).apply { setBounds(pad, pad, pw - pad * 2, ph - pad * 2) }
                addActor(sc)
            }
            panel.setPosition(side, (h - ph) / 2f - px(10f))
            panel.setOrigin(com.badlogic.gdx.utils.Align.center)
            addActor(panel)
            val t = lbl(title, msdf.title(22f).apply { dropShadow(0f, 1f, 6f, GameColor.black_55) })
            t.setPosition((w - t.width) / 2f, panel.y + ph + px(14f)); t.touchable = Touchable.disabled
            addActor(t)
            panel.setScale(0.88f)
            panel.addAction(Actions.scaleTo(1f, 1f, 0.26f, Interpolation.pow3Out))
        }
        f.color.a = 0f
        f.addAction(Actions.fadeIn(0.18f))
        content.addActor(f)
        folder = f
        log("folder $title: ${apps.size} apps")
    }

    private fun closeFolder() {
        val f = folder ?: return
        folder = null
        f.touchable = Touchable.disabled
        f.addAction(Actions.sequence(Actions.fadeOut(0.15f), Actions.removeActor()))
    }

    private fun appMenu(app: LauncherApp?) {
        // AD_MODE (is_uninstall = false): довге натискання нічого не робить — як у Redwave
        if (!gdxGame.flags.isUninstall) return
        val b = gdxGame.bridge
        b.vibrate(18)
        val items = mutableListOf<Triple<com.badlogic.gdx.graphics.g2d.TextureRegion, String, () -> Unit>>(
            Triple(assets.ic_info, L.appInfo) { b.openAppInfo(app) },
        )
        if (b.canUninstall(app)) items += Triple(assets.ic_trash, L.uninstall) { b.uninstallApp(app) }
        openSheet(MenuSheet(this, app?.label ?: "Driftglass", items, dangerLast = items.size > 1))
    }

    private fun homeMenu() {
        gdxGame.bridge.vibrate(18)
        openSheet(MenuSheet(this, null, listOf(
            Triple(assets.ic_sparkle, L.mWallpapers) { gdxGame.openAppFromLauncher(AppScreen.TAB_DISCOVER) },
            Triple(assets.ic_sliders, L.studio) { gdxGame.draft = gdxGame.model.homeWallpaper().copy(); gdxGame.navigationManager.navigate(StudioScreen::class.java.name, LauncherScreen::class.java.name) },
            Triple(assets.ic_gear, L.mHomeSettings) { gdxGame.openAppFromLauncher(AppScreen.TAB_SETTINGS) },
        )))
    }

    private fun doShuffle() {
        val w = gdxGame.shuffle(auto = false) ?: return
        wall.show(w)
    }

    // ------------------------------------------------------------------------
    // Годинник і стежка за змінами (раз на секунду)
    // ------------------------------------------------------------------------
    private fun updateClock(force: Boolean) {
        if (!::clock.isInitialized) return
        val now = Date()
        val m = SimpleDateFormat("H:mm", Locale.US).format(now)
        if (!force && m == lastMinute) return
        lastMinute = m
        clock.setText(m); clock.pack()
        val loc = Locale.forLanguageTag(com.driftglass.home.core.i18n.L.lang.code)
        val d = SimpleDateFormat("EEEE, d MMMM", loc).format(now).replaceFirstChar { it.titlecase(loc) }
        date.setText(d); date.pack()
    }

    override fun render(delta: Float) {
        checkIn -= delta
        if (checkIn <= 0f) {
            checkIn = 1f
            updateClock(false)
            val m = gdxGame.model
            val wp = m.homeWallpaper()
            val slot = if (m.state.dayCycle) DayCycle.slotOf(m.hourNow()) else null
            if (wp.id != lastHomeId) { lastHomeId = wp.id; wall.show(wp) }
            if (m.version != lastModel || slot != lastSlot) {
                lastSlot = slot
                val before = topBottom
                rebuildTop()
                if (topBottom != before) rebuildBottom()   // з'явився / зник бейдж — сітка зсувається
            }
            if (gdxGame.appsVersion != lastApps) { rebuildBottom(); drawer?.refill() }
        }
        super.render(delta)
    }

    override fun onResumed() {
        // повернулись з налаштувань сітки / мови — перебудувати
        rebuildTop(); rebuildBottom()
    }

    // ------------------------------------------------------------------------
    // Жести на порожньому місці
    // ------------------------------------------------------------------------
    private inner class GestureCatcher : Actor() {
        private var downAt = 0L
        private var downX = 0f; private var downY = 0f
        private var lastTapAt = 0L
        private var pressing = false
        private var moved = false
        init {
            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (pointer > 0) return false
                    downAt = System.currentTimeMillis(); downX = x; downY = y; pressing = true; moved = false
                    return true
                }
                override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                    if (Math.abs(x - downX) > px(12f) || Math.abs(y - downY) > px(12f)) moved = true
                }
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    if (!pressing) return
                    pressing = false
                    if (y - downY > px(70f)) { openDrawer(); return }       // свайп угору
                    if (moved) return
                    val now = System.currentTimeMillis()
                    if (now - downAt > 450) return                           // було довге натискання
                    if (now - lastTapAt < 320 && gdxGame.model.state.settings.doubleTap) { lastTapAt = 0; doShuffle() }
                    else lastTapAt = now
                }
            })
        }
        override fun act(delta: Float) {
            super.act(delta)
            if (pressing && !moved && System.currentTimeMillis() - downAt > 480) { pressing = false; homeMenu() }
        }
    }

    // ------------------------------------------------------------------------
    // Усі застосунки
    // ------------------------------------------------------------------------
    private fun openDrawer() {
        if (drawer != null) return
        val d = Drawer()
        d.setBounds(0f, 0f, worldWidth, worldHeight)
        content.addActor(d)
        d.color.a = 0f; d.y = -px(30f)
        d.addAction(Actions.parallel(Actions.fadeIn(0.22f), Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow3Out)))
        drawer = d
        // головний екран ховаємо: крізь напівпрозорий список не просвічують годинник і іконки, лише шпалери
        listOf(top, bottom).forEach { it.clearActions(); it.touchable = Touchable.disabled; it.addAction(Actions.fadeOut(0.18f)) }
        log("drawer: ${gdxGame.bridge.listApps().size} apps")
    }

    private fun closeDrawer() {
        val d = drawer ?: return
        drawer = null
        if ((gdxGame.bridge as? com.driftglass.home.android.AndroidBridge)?.isTextInputActive == true) gdxGame.bridge.endTextInput()
        d.touchable = Touchable.disabled
        d.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0.18f), Actions.moveTo(0f, -px(30f), 0.18f)), Actions.removeActor()))
        listOf(top, bottom).forEach { it.clearActions(); it.touchable = Touchable.childrenOnly; it.addAction(Actions.fadeIn(0.22f)) }
    }

    private inner class Drawer : AdvancedGroup() {
        override val screen = this@LauncherScreen
        private var query = ""
        private lateinit var grid: AColumn
        private lateinit var field: AMsdfLabel
        private lateinit var searchBox: AdvancedGroup

        override fun addActorsOnGroup() {
            val w = width; val h = height
            addAndFillActor(Image(drawerUtil.whiteRegion).apply { color.set(GameColor.drawer) })
            // поле пошуку (.search): тап → нативний EditText поверх
            val sh = px(44f)
            searchBox = tap(screen, w - side * 2, sh, 0.99f) {
                addAndFillActor(ARect(screen, px(16f), GameColor.white_10, stroke = GameColor.white_14))
                val ic = icon(assets.ic_search, px(17f), GameColor.white_70); ic.setPosition(px(12f), (height - ic.height) / 2f); addActor(ic)
                field = lbl(L.searchApps, msdf.regular(14f, GameColor.white_55)); field.setPosition(px(38f), (height - field.height) / 2f); addActor(field)
            }.apply { setOnClickListener { beginSearch() } }
            searchBox.setPosition(side, h - safeStatusBarUI - px(12f) - sh); addActor(searchBox)

            grid = AColumn(screen, px(18f), padTop = px(8f), padBottom = safeNavBarUI + px(70f), padX = side)
            grid.setSize(w, 1f)
            val sc = AScrollPane(grid).apply { setBounds(0f, 0f, w, searchBox.y - px(8f)) }
            addActor(sc)
            // закрити: свайп униз, коли список нагорі
            addListener(object : InputListener() {
                var sy = 0f
                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { sy = y; return true }
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) { if (sy - y > px(110f) && sc.scrollY <= 0f) closeDrawer() }
            })
            refill()
        }

        fun refill() {
            if (!::grid.isInitialized) return
            grid.disposeAndClearChildren()
            val q = query.trim().lowercase()
            val all = gdxGame.bridge.listApps()
            // без пошуку — ті самі папки, що й на головному екрані, далі всі апки; з пошуком — плоский список збігів
            val entries: List<HomeEntry<LauncherApp?>> =
                if (q.isEmpty()) homeEntries(all)
                else all.filter { it.label.lowercase().contains(q) }.map { HomeEntry.App(it) }
            val cols = 4
            val cw = (width - side * 2) / cols
            if (entries.isEmpty()) {
                val l = lbl("${L.noMatch}: “$query”", msdf.regular(13f, GameColor.white_55))
                grid.addActor(group(screen, width - side * 2, px(60f)) { l.setPosition((width - l.width) / 2f, (height - l.height) / 2f); addActor(l) })
                return
            }
            entries.chunked(cols).forEach { rowItems ->
                val row = group(screen, width - side * 2, px(52f + 19f)) {
                    rowItems.forEachIndexed { i, e ->
                        val ic = entryIcon(e, cw, px(52f), labels = true)
                        ic.setPosition(i * cw, 0f)
                        addActor(ic)
                    }
                }
                grid.addActor(row)
            }
        }

        private fun beginSearch() {
            val sb = searchBox
            val v = com.badlogic.gdx.math.Vector2(sb.x + px(38f), sb.y + sb.height)
            sb.parent.localToStageCoordinates(v)
            val sx = com.badlogic.gdx.Gdx.graphics.width / worldWidth
            val sy = com.badlogic.gdx.Gdx.graphics.height / worldHeight
            val req = TextInputRequest(
                xPx = (v.x * sx).toInt(), yPx = ((worldHeight - v.y) * sy).toInt(),
                wPx = ((sb.width - px(48f)) * sx).toInt(), hPx = (sb.height * sy).toInt(),
                text = query, hint = L.searchApps, textSizePx = px(14f) * sy,
            )
            field.isVisible = false
            gdxGame.bridge.beginTextInput(req, onChange = { t -> query = t; refill() }) { t, _ ->
                query = t
                field.setText(if (t.isBlank()) L.searchApps else t); field.pack()
                field.isVisible = true
                refill()
            }
        }
    }

    // ------------------------------------------------------------------------
    // HOME / Back
    // ------------------------------------------------------------------------
    /** «Додому», коли лаунчер уже відкритий: закрити шторку і список. */
    fun onHomeAgain() {
        sheet?.close()
        closeFolder()
        closeDrawer()
    }

    override fun onBackPressed() {
        when {
            sheet != null -> sheet?.close()
            folder != null -> closeFolder()
            drawer != null -> closeDrawer()
            else -> {}   // лаунчер «Назад» не закриває
        }
    }
}
