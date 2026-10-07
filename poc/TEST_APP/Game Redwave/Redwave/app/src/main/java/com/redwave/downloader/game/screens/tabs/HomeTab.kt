package com.redwave.downloader.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.model.DownloadItem
import com.redwave.downloader.core.model.DownloadStatus
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.layout.AColumn
import com.redwave.downloader.game.actors.layout.ADyn
import com.redwave.downloader.game.actors.layout.ARow
import com.redwave.downloader.game.actors.ui.AButtonRed
import com.redwave.downloader.game.actors.ui.AChip
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.AIconButton
import com.redwave.downloader.game.actors.ui.AInputField
import com.redwave.downloader.game.actors.ui.AProgressBar
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASpinner
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.controller.DownloadController.Submit
import com.redwave.downloader.game.screens.AppScreen
import com.redwave.downloader.game.screens.sheets.FeedSheet
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px
import java.util.Calendar

// ─────────────────────────────────────────────────────────────────────────────
// HomeTab — шапка, привітання, картка вставки (поле + Paste → помилка/перевірка
// → Download → чипи джерел), черга, «Recently added», три плитки, промо Discover.
// ─────────────────────────────────────────────────────────────────────────────
class HomeTab(app: AppScreen) : ATabPage(app) {

    private var input = ""
    /** null — нічого; Checking — спінер; Error — червоний блок. */
    private var result: Submit? = null
    private var pasteKey = 0
    private var field: AInputField? = null

    override fun build(col: AColumn) {
        col.addActor(header())
        col.addActor(hello())
        col.addActor(ADyn(app, W, { pasteKey }) { w -> buildPaste(this, w) })
        col.addActor(ADyn(app, W, { queueKey() }) { w -> buildQueue(this, w) })
        col.addActor(ADyn(app, W, { gdxGame.model.songs.take(6).map { it.id } }) { w -> buildRecent(this, w) })
        col.addActor(ADyn(app, W, { gdxGame.model.state.library.size }) { w -> buildStats(this, w) })
        col.addActor(promo())
        consumeSharedText()
    }

    fun consumeSharedText() {
        val t = gdxGame.sharedText ?: return
        gdxGame.sharedText = null
        input = t.trim()
        field?.setText(input)
        submit()
    }

    // ------------------------------------------------------------------------
    // Шапка + привітання
    // ------------------------------------------------------------------------
    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val pw = width; val ph = height; val logo = Image(assets.LOGO).apply { setSize(px(30f), px(30f)); setPosition(0f, (ph - px(30f)) / 2f) }
            val word = lbl(Copy.APP_NAME, msdf.disp(19f))
            word.setPosition(logo.width + px(9f), (height - word.height) / 2f)
            val bell = AIconButton(app, assets.ic_bell).withBadge().apply { setBounds(W - px(38f), 0f, px(38f), px(38f)) }
            bell.onClick { gdxGame.toast("No new notifications") }
            addActor(logo); addActor(word); addActor(bell)
        }
    }.apply { setSize(W, px(38f)) }

    private fun hello(): AdvancedGroup {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greet = lbl(Copy.Home.greeting(hour).uppercase(), msdf.monoSemi(12f, GameColor.muted_A8949B, spacing = 12f))
        val l1 = lbl(Copy.Home.TITLE_1, msdf.disp(29f))
        val l2 = lbl(Copy.Home.TITLE_ACCENT, msdf.disp(29f, GameColor.redHi_FF5A6C))
        val h = greet.height + px(6f) + l1.height * 0.95f + l2.height
        return object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                l2.setPosition(0f, 0f); l1.setPosition(0f, l2.height * 0.95f); greet.setPosition(0f, height - greet.height)
                addActor(greet); addActor(l1); addActor(l2)
            }
        }.apply { setSize(W, h) }
    }

    // ------------------------------------------------------------------------
    // Картка вставки
    // ------------------------------------------------------------------------
    private fun buildPaste(g: ADyn, w: Float): Float {
        val pad = px(14f); val gap = px(11f); val inner = w - pad * 2

        val pasteBtn = object : ATap(app, 0.94f) {
            override fun addContent() {
                addAndFillActor(ARect(app, px(11f), GameColor.card2_21161A))
                val ic = icon(assets.ic_clip, px(15f), GameColor.text_FBF1F2)
                val l = lbl(Copy.Home.PASTE, msdf.bold(12.5f))
                val cw = ic.width + px(6f) + l.width
                ic.setPosition((width - cw) / 2f, (height - ic.height) / 2f); l.setPosition(ic.x + ic.width + px(6f), (height - l.height) / 2f)
                addActor(ic); addActor(l)
            }
        }.onClick { paste() }
        val pl = lbl(Copy.Home.PASTE, msdf.bold(12.5f))
        pasteBtn.setSize(px(11f) * 2 + px(15f) + px(6f) + pl.width, px(34f))

        val f = AInputField(app, Copy.Home.INPUT_HINT, assets.ic_link, isUrl = true, bg = GameColor.input_0C0708, trailing = pasteBtn)
        f.setSize(inner, px(46f))
        f.setText(input)
        f.onChange = { input = it; if (result is Submit.Error) { result = null; pasteKey++ } }
        f.onSubmit = { input = it; if (it.isNotBlank()) submit() }
        field = f

        val res = result?.let { resultBlock(it, inner) }
        val dl = AButtonRed(app, Copy.Home.DOWNLOAD, assets.ic_download).apply { setSize(inner, px(46f)) }
        dl.onClick { field?.let { if (it.isEditing) gdxGame.bridge.endTextInput() }; submit() }
        val chips = sourceChips(inner)

        val h = pad + f.height + (res?.let { gap + it.height } ?: 0f) + gap + dl.height + gap + chips.height + pad

        // фон: card + червоний відблиск згори (165°, red 20 % → 3 % на 55 %) + рамка redHi 25 %
        val glow = Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.35f; setBounds(px(30f), -px(30f), w - px(60f), px(70f)); touchable = Touchable.disabled }
        g.addActor(glow)
        g.addActor(ARect(app, px(24f), GameColor.card_170F12).apply { setSize(w, h) })
        g.addActor(ARect(app, px(24f), GameColor.red_20, GameColor.red_03, angleCss = 165f, mid = 0.55f, stroke = GameColor.redHi_25).apply { setSize(w, h) })

        var y = h - pad
        y -= f.height; f.setPosition(pad, y); g.addActor(f)
        res?.let { y -= gap + it.height; it.setPosition(pad, y); g.addActor(it) }
        y -= gap + dl.height; dl.setPosition(pad, y); g.addActor(dl)
        y -= gap + chips.height; chips.setPosition(pad, y); g.addActor(chips)
        return h
    }

    private fun sourceChips(w: Float): AdvancedGroup {
        val list = listOf(
            AChip(app, Copy.Home.SOURCE_CHIPS[0], AChip.Kind.SRC, assets.ic_file),
            AChip(app, Copy.Home.SOURCE_CHIPS[1], AChip.Kind.SRC, assets.ic_cloud),
            AChip(app, Copy.Home.SOURCE_CHIPS[2], AChip.Kind.SRC, assets.ic_rss),
        )
        val gap = px(6f)
        // flex-wrap
        var x = 0f; var row = 0
        val pos = list.map { c ->
            if (x > 0f && x + c.width > w) { x = 0f; row++ }
            val p = x to row; x += c.width + gap; p
        }
        val ch = list[0].height
        val h = (row + 1) * ch + row * gap
        return object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                list.forEachIndexed { i, c -> addActor(c); c.setPosition(pos[i].first, h - (pos[i].second + 1) * ch - pos[i].second * gap) }
            }
        }.apply { setSize(w, h) }
    }

    /** .res.bad / .res.info */
    private fun resultBlock(r: Submit, w: Float): AdvancedGroup {
        val padX = px(12f); val padY = px(10f)
        return when (r) {
            Submit.Checking -> object : AdvancedGroup() {
                override val screen = app
                override fun addActorsOnGroup() {
                    addAndFillActor(ARect(app, px(14f), GameColor.white_5))
                    val sp = ASpinner(app, px(2f), Color(1f, 1f, 1f, 0.2f), GameColor.red_FF2E4D).apply { setSize(px(16f), px(16f)) }
                    sp.setPosition(padX, (height - sp.height) / 2f)
                    val l = lbl(Copy.Home.CHECKING, msdf.regular(12.5f)); l.setPosition(padX + px(26f), (height - l.height) / 2f)
                    addActor(sp); addActor(l)
                }
            }.apply { setSize(w, px(40f)) }
            is Submit.Error -> {
                val ic = when {
                    r.title == Copy.Errors.NOT_A_LINK_TITLE -> assets.ic_x
                    r.title == Copy.Errors.NOT_AUDIO_TITLE -> assets.ic_globe
                    r.title == Copy.Errors.NO_NETWORK_TITLE -> assets.ic_wifi_off
                    else -> assets.ic_ban
                }
                val textX = padX + px(18f + 10f)
                val tw = w - textX - padX
                val title = lbl(r.title, msdf.bold(13.5f, GameColor.pink_FF8A98)).apply { setWrap(true); width = tw; height = prefHeight }
                val body = lbl(r.body, msdf.regular(12.5f, GameColor.errText_D9C3C8)).apply { setWrap(true); width = tw; height = prefHeight }
                val h = padY + title.height + px(2f) + body.height + padY
                errorBlock(w, h, ic, title, body, padX, padY, textX)
            }
            else -> object : AdvancedGroup() { override val screen = app; override fun addActorsOnGroup() {} }.apply { setSize(w, 1f) }
        }
    }

    private fun errorBlock(w: Float, h: Float, ic: TextureRegion, title: AMsdfLabel, body: AMsdfLabel, padX: Float, padY: Float, textX: Float) =
        object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                addAndFillActor(ARect(app, px(14f), GameColor.red_10, stroke = GameColor.red_35))
                val i = icon(ic, px(18f), GameColor.pink_FF8A98); i.setPosition(padX, height - padY - i.height - px(1f))
                title.setPosition(textX, height - padY - title.height)
                body.setPosition(textX, title.y - px(2f) - body.height)
                addActor(i); addActor(title); addActor(body)
            }
        }.apply { setSize(w, h) }

    // ------------------------------------------------------------------------
    // Дії
    // ------------------------------------------------------------------------
    private fun paste() {
        val t = gdxGame.bridge.readClipboardText()
        if (t.isNullOrBlank()) { gdxGame.toast(Copy.Toasts.CLIPBOARD_EMPTY); return }
        input = t.trim()
        field?.setText(input)
        if (result != null) { result = null; pasteKey++ }
    }

    private fun submit() {
        val text = input.trim()
        if (text.isEmpty()) { gdxGame.toast(Copy.Home.EMPTY_INPUT_TOAST); return }
        gdxGame.downloads.submit(text) { s ->
            when (s) {
                is Submit.Started -> { result = null; input = ""; field?.setText("") }
                is Submit.Feed -> { result = null; app.openSheet(FeedSheet(app, s.url)) }
                else -> result = s
            }
            pasteKey++
        }
    }

    // ------------------------------------------------------------------------
    // Черга
    // ------------------------------------------------------------------------
    private fun queueKey() = gdxGame.model.state.downloads.filter { it.status != DownloadStatus.FAILED }.map { it.id }

    private fun buildQueue(g: ADyn, w: Float): Float {
        val items = gdxGame.model.state.downloads.filter { it.status != DownloadStatus.FAILED }
        if (items.isEmpty()) return 0f
        val pad = px(12f); val gap = px(10f); val rowH = px(44f)
        val head = lbl(Copy.Home.DOWNLOADING, msdf.bold(16f))
        val n = lbl(" ${items.size}", msdf.bold(16f, GameColor.redHi_FF5A6C))
        val h = pad + head.height + items.size * (gap + rowH) + pad
        g.addActor(ARect(app, px(20f), GameColor.card_170F12, stroke = GameColor.line_white_7).apply { setSize(w, h) })
        head.setPosition(pad, h - pad - head.height); n.setPosition(pad + head.width, head.y)
        g.addActor(head); g.addActor(n)
        var y = head.y
        items.forEach { d ->
            y -= gap + rowH
            val row = QueueRow(d.id, w - pad * 2, rowH).apply { setPosition(pad, y) }
            g.addActor(row)
        }
        return h
    }

    /** .q-it: обкладинка 44 + назва/відсоток + смуга + «4.1 of 5.2 MB · 1.8 MB/s · MP3». */
    private inner class QueueRow(private val id: Long, w: Float, h: Float) : AdvancedGroup() {
        override val screen = app
        private lateinit var pct: AMsdfLabel
        private lateinit var sub: AMsdfLabel
        private lateinit var bar: AProgressBar
        init { setSize(w, h) }

        override fun addActorsOnGroup() {
            val d = item() ?: return
            val cover = ACover(app, px(10f)).apply { setBounds(0f, 0f, px(44f), px(44f)) }
            if (d.coverUrl != null) cover.setUrl(d.coverUrl, d.trackId) else cover.setHueFrom(d.trackId)
            addActor(cover)
            val mx = px(44f + 11f); val mw = width - mx
            pct = lbl("100%", msdf.monoSemi(12f, GameColor.redHi_FF5A6C))
            val title = lbl(d.title, msdf.bold(13f)).ellipsize(mw - pct.width - px(8f))
            bar = AProgressBar(app).apply { setSize(mw, px(5f)) }
            sub = lbl("", msdf.mono(11f, GameColor.muted_A8949B))
            title.setPosition(mx, height - title.height + px(2f))
            bar.setPosition(mx, height / 2f - px(4f))
            sub.setPosition(mx, -px(2f))
            addActor(title); addActor(pct); addActor(bar); addActor(sub)
            refresh(true)
        }

        private fun item(): DownloadItem? = gdxGame.model.state.downloads.firstOrNull { it.id == id }

        override fun act(delta: Float) {
            super.act(delta)
            if (::bar.isInitialized) refresh(false)
        }

        private fun refresh(first: Boolean) {
            val d = item() ?: return
            bar.set(d.progress, first)
            pct.setText("${(d.progress * 100).toInt()}%"); pct.pack()
            pct.setPosition(width - pct.width, height - pct.height + px(2f))
            val speed = gdxGame.downloads.speed[id] ?: 0f
            val total = if (d.bytesTotal > 0) Fmt.mb(d.bytesTotal) else "?"
            sub.setText("${Fmt.mb(d.bytesDone)} of $total MB · ${Fmt.speed(speed)} · ${d.format ?: ""}"); sub.pack()
        }
    }

    // ------------------------------------------------------------------------
    // Recently added
    // ------------------------------------------------------------------------
    private fun buildRecent(g: ADyn, w: Float): Float {
        val recent = gdxGame.model.songs.take(6)
        val head = secHeader(Copy.Home.RECENT, right = Copy.Home.SEE_ALL, rightIsAction = true) { app.select(AppScreen.TAB_LIBRARY) }
        if (recent.isEmpty()) {
            val e = emptyText(Copy.Library.EMPTY, w)
            val h = head.height + px(10f) + e.height
            head.setPosition(0f, h - head.height); e.setPosition(0f, 0f)
            g.addActor(head); g.addActor(e)
            return h
        }
        val itemH = px(124f + 5f + 18f + 3f + 15f)
        val row = ARow(app, px(12f), padX = 0f)
        recent.forEach { t ->
            row.addActor(object : ATap(app, 0.97f) {
                override fun addContent() {
                    val pw = width; val ph = height; val c = ACover(app, px(12f)).apply { setBounds(0f, ph - px(124f), px(124f), px(124f)) }
                    c.setTrack(t)
                    val b = lbl(t.title, msdf.semibold(13f)).ellipsize(px(124f))
                    val s = lbl(t.artist, msdf.regular(11.5f, GameColor.muted_A8949B)).ellipsize(px(124f))
                    b.setPosition(0f, c.y - px(5f) - b.height); s.setPosition(0f, b.y - px(1f) - s.height)
                    addActor(c); addActor(b); addActor(s)
                }
            }.onClick { gdxGame.player.play(t) }.apply { setSize(px(124f), itemH) })
        }
        val car = hScroll(row, itemH)
        val h = head.height + px(10f) + itemH
        head.setPosition(0f, h - head.height)
        g.addActor(head)
        // карусель на всю ширину екрана: ADyn стоїть із відступом SIDE — компенсуємо
        car.setPosition(-app.worldWidth / 2f + w / 2f, 0f)
        row.padX = (app.worldWidth - w) / 2f
        g.addActor(car)
        return h
    }

    // ------------------------------------------------------------------------
    // Плитки
    // ------------------------------------------------------------------------
    private fun buildStats(g: ADyn, w: Float): Float {
        val m = gdxGame.model
        val tiles = listOf(
            m.state.library.size.toString() to Copy.Home.STAT_TRACKS,
            Fmt.size(m.totalBytes) to Copy.Home.STAT_ON_DEVICE,
            Fmt.hours(m.state.library.sumOf { it.durationMs }) to Copy.Home.STAT_OFFLINE,
        )
        val gap = px(8f); val tw = (w - gap * 2) / 3f; val h = px(58f)
        tiles.forEachIndexed { i, (b, s) ->
            val x = i * (tw + gap)
            g.addActor(ARect(app, px(16f), GameColor.card_170F12, stroke = GameColor.line_white_7).apply { setBounds(x, 0f, tw, h) })
            val big = lbl(b, msdf.style(gdxGame.msdfManager.fontArchivo_ExpandedExtraBold, 18f) { letterSpacing = -1f })
            val small = lbl(s, msdf.regular(11f, GameColor.muted_A8949B))
            small.setPosition(x + px(12f), px(10f)); big.setPosition(x + px(12f), small.y + small.height - px(1f))
            g.addActor(big); g.addActor(small)
        }
        return h
    }

    // ------------------------------------------------------------------------
    // Промо Discover
    // ------------------------------------------------------------------------
    private fun promo() = object : ATap(app, 0.98f) {
        override fun addContent() {
            val photo = ACover(app, px(22f))
            addAndFillActor(photo)
            photo.setTexture(assets.FEAT_VINYL)
            // linear-gradient(90deg, rgba(11,7,8,.94) 35%, rgba(11,7,8,.15))
            addAndFillActor(ARect(app, px(22f), GameColor.background.cpy().apply { a = 0.94f }, GameColor.background.cpy().apply { a = 0.15f }, angleCss = 90f, start = 0.35f, stroke = GameColor.redHi_25))
            val pad = px(16f)
            val go = lbl(Copy.Home.PROMO_GO, msdf.bold(12.5f, GameColor.apricot_FFB3A4))
            val chev = icon(assets.ic_chev_right, px(15f), GameColor.apricot_FFB3A4)
            val lines = Copy.Home.PROMO_TITLE.split("\n")
            val t2 = lbl(lines[1], msdf.disp(24f, Color.WHITE)); val t1 = lbl(lines[0], msdf.disp(24f, Color.WHITE))
            val chip = AChip(app, Copy.Home.PROMO_CHIP, AChip.Kind.RED, assets.ic_shield)
            var y = pad
            go.setPosition(pad, y); chev.setPosition(pad + go.width + px(4f), y + (go.height - chev.height) / 2f); y += go.height + px(8f)
            t2.setPosition(pad, y); y += t2.height * 0.95f
            t1.setPosition(pad, y); y += t1.height + px(8f)
            chip.setPosition(pad, y)
            addActor(go); addActor(chev); addActor(t2); addActor(t1); addActor(chip)
        }
    }.onClick { app.select(AppScreen.TAB_DISCOVER) }.apply { setSize(W, px(150f)) }
}
