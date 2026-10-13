package com.vidtools.framefall.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.DownloadItem
import com.vidtools.framefall.core.model.DownloadStatus
import com.vidtools.framefall.core.model.unseenEvents
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.layout.ADyn
import com.vidtools.framefall.game.actors.layout.ARow
import com.vidtools.framefall.game.actors.ui.AButtonAccent
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.ACover
import com.vidtools.framefall.game.actors.ui.AIconButton
import com.vidtools.framefall.game.actors.ui.AInputField
import com.vidtools.framefall.game.actors.ui.AProgressBar
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ASpinner
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.controller.DownloadController.Submit
import com.vidtools.framefall.game.screens.AppScreen
import com.vidtools.framefall.game.screens.sheets.EventsSheet
import com.vidtools.framefall.game.screens.sheets.QualitySheet
import com.vidtools.framefall.game.screens.sheets.SettingsSheet
import com.vidtools.framefall.game.utils.Fmt
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
// SaveTab — перша вкладка (.save у прототипі): шапка (лого, шестерня, дзвіночок),
// картка вставки (поле + Paste → помилка / перевірка → «Check & save» → чипи джерел),
// черга «Saving», «Continue watching», Studio (4 плитки), три цифри, промо Discover.
// ─────────────────────────────────────────────────────────────────────────────
class SaveTab(app: AppScreen) : ATabPage(app) {

    private var input = ""
    /** null — нічого; Checking — спінер; Error — блок помилки. */
    private var result: Submit? = null
    private var pasteKey = 0
    private var field: AInputField? = null

    override fun build(col: AColumn) {
        col.addActor(header())
        col.addActor(ADyn(app, W, { pasteKey }) { w -> buildPaste(this, w) })
        col.addActor(ADyn(app, W, { queueKey() }) { w -> buildQueue(this, w) })
        col.addActor(ADyn(app, W, { gdxGame.model.continueWatching.take(8).map { it.id to it.watchedMs / 10_000 } }) { w -> buildContinue(this, w) })
        col.addActor(studio())
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
    // Шапка
    // ------------------------------------------------------------------------
    private fun header() = object : AdvancedGroup() {
        override val screen = app
        override fun addActorsOnGroup() {
            val ph = height
            val logo = Image(assets.LOGO).apply { setSize(px(32f), px(32f)); setPosition(0f, (ph - px(32f)) / 2f) }
            val word = lbl(Copy.APP_NAME, msdf.disp(20f))
            word.setPosition(logo.width + px(10f), (height - word.height) / 2f)
            // Центр подій: крапка — лише коли є непереглянуте (збережене / лінк з буфера)
            val bell = AIconButton(app, assets.ic_bell).withBadge().apply { setBounds(W - px(38f), 0f, px(38f), px(38f)) }
            bell.onClick { app.openSheet(EventsSheet(app)) }
            bell.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.forever(com.badlogic.gdx.scenes.scene2d.actions.Actions.run {
                bell.badge?.isVisible = gdxGame.model.state.unseenEvents > 0
            }))
            val gear = AIconButton(app, assets.ic_gear).apply { setBounds(W - px(38f) * 2 - px(8f), 0f, px(38f), px(38f)) }
            gear.onClick { app.openSheet(SettingsSheet(app)) }
            addActor(logo); addActor(word); addActor(gear); addActor(bell)
        }
    }.apply { setSize(W, px(38f)) }

    // ------------------------------------------------------------------------
    // Картка вставки
    // ------------------------------------------------------------------------
    private fun buildPaste(g: ADyn, w: Float): Float {
        val pad = px(14f); val gap = px(11f); val inner = w - pad * 2

        val title = lbl(Copy.Save.TITLE, msdf.bold(16f))

        val pasteBtn = object : ATap(app, 0.94f) {
            override fun addContent() {
                addAndFillActor(ARect(app, px(11f), GameColor.card2_221812))
                val ic = icon(assets.ic_clip, px(15f), GameColor.text_FFF6EC)
                val l = lbl(Copy.Save.PASTE, msdf.bold(12.5f))
                val cw = ic.width + px(6f) + l.width
                ic.setPosition((width - cw) / 2f, (height - ic.height) / 2f); l.setPosition(ic.x + ic.width + px(6f), (height - l.height) / 2f)
                addActor(ic); addActor(l)
            }
        }.onClick { paste() }
        val pl = lbl(Copy.Save.PASTE, msdf.bold(12.5f))
        pasteBtn.setSize(px(11f) * 2 + px(15f) + px(6f) + pl.width, px(34f))

        val f = AInputField(app, Copy.Save.INPUT_HINT, assets.ic_link, isUrl = true, bg = GameColor.input_0D0906, trailing = pasteBtn)
        f.setSize(inner, px(46f))
        f.setText(input)
        f.onChange = { input = it; if (result is Submit.Error) { result = null; pasteKey++ } }
        f.onSubmit = { input = it; if (it.isNotBlank()) submit() }
        field = f

        val res = result?.let { resultBlock(it, inner) }
        val go = AButtonAccent(app, Copy.Save.CHECK_AND_SAVE, assets.ic_download).apply { setSize(inner, px(48f)) }
        go.onClick { field?.let { if (it.isEditing) gdxGame.bridge.endTextInput() }; submit() }
        val chips = sourceChips(inner)

        val h = pad + title.height + gap + f.height + (res?.let { gap + it.height } ?: 0f) + gap + go.height + gap + chips.height + pad

        // фон: card + бурштиновий відблиск згори (165°) + рамка
        val glow = Image(assets.GLOW).apply { color.set(GameColor.accent_FF8A2A); color.a = 0.28f; setBounds(px(30f), -px(30f), w - px(60f), px(70f)); touchable = Touchable.disabled }
        g.addActor(glow)
        g.addActor(ARect(app, px(24f), GameColor.card_16100C).apply { setSize(w, h) })
        g.addActor(ARect(app, px(24f), GameColor.accent_20, GameColor.accent_03, angleCss = 165f, mid = 0.55f, stroke = GameColor.accentText_25).apply { setSize(w, h) })

        var y = h - pad
        y -= title.height; title.setPosition(pad, y); g.addActor(title)
        y -= gap + f.height; f.setPosition(pad, y); g.addActor(f)
        res?.let { y -= gap + it.height; it.setPosition(pad, y); g.addActor(it) }
        y -= gap + go.height; go.setPosition(pad, y); g.addActor(go)
        y -= gap + chips.height; chips.setPosition(pad, y); g.addActor(chips)
        return h
    }

    private fun sourceChips(w: Float): AdvancedGroup {
        val list = listOf(
            AChip(app, Copy.Save.SOURCE_CHIPS[0], AChip.Kind.SRC, assets.ic_film),
            AChip(app, Copy.Save.SOURCE_CHIPS[1], AChip.Kind.SRC, assets.ic_cloud),
            AChip(app, Copy.Save.SOURCE_CHIPS[2], AChip.Kind.SRC, assets.ic_globe),
        )
        val gap = px(6f)
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
                    val sp = ASpinner(app, px(2f), Color(1f, 1f, 1f, 0.2f), GameColor.accent_FF8A2A).apply { setSize(px(16f), px(16f)) }
                    sp.setPosition(padX, (height - sp.height) / 2f)
                    val l = lbl(Copy.Save.CHECKING, msdf.regular(12.5f)); l.setPosition(padX + px(26f), (height - l.height) / 2f)
                    addActor(sp); addActor(l)
                }
            }.apply { setSize(w, px(40f)) }
            is Submit.Error -> {
                val ic = when (r.title) {
                    Copy.Errors.NOT_A_LINK_TITLE -> assets.ic_x
                    Copy.Errors.NOT_VIDEO_TITLE, Copy.Errors.NO_FILES_TITLE -> assets.ic_globe
                    Copy.Errors.NO_NETWORK_TITLE -> assets.ic_wifi_off
                    Copy.Errors.STREAM_TITLE, Copy.Errors.CODEC_TITLE -> assets.ic_alert
                    else -> assets.ic_ban
                }
                val textX = padX + px(18f + 10f)
                val tw = w - textX - padX
                val title = lbl(r.title, msdf.bold(13.5f, GameColor.err_FF7A6B)).apply { setWrap(true); width = tw; height = prefHeight }
                val body = lbl(r.body, msdf.regular(12.5f, GameColor.errText_E3CFC0)).apply { setWrap(true); width = tw; height = prefHeight }
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
                addAndFillActor(ARect(app, px(14f), GameColor.err_10, stroke = GameColor.err_35))
                val i = icon(ic, px(18f), GameColor.err_FF7A6B); i.setPosition(padX, height - padY - i.height - px(1f))
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
        if (text.isEmpty()) { gdxGame.toast(Copy.Save.EMPTY_INPUT_TOAST); return }
        gdxGame.downloads.submit(text) { s ->
            when (s) {
                is Submit.Started -> { result = null; input = ""; field?.setText("") }
                is Submit.Choose -> { result = null; app.openSheet(QualitySheet(app, s) { input = ""; field?.setText(""); pasteKey++ }) }
                else -> result = s
            }
            pasteKey++
        }
    }

    // ------------------------------------------------------------------------
    // Черга «Saving»
    // ------------------------------------------------------------------------
    private fun queueKey() = gdxGame.model.state.downloads.map { it.id to it.status }

    private fun buildQueue(g: ADyn, w: Float): Float {
        val items = gdxGame.model.state.downloads
        if (items.isEmpty()) return 0f
        val pad = px(12f); val gap = px(12f); val rowH = px(44f)
        val head = lbl(Copy.Save.SAVING, msdf.bold(16f))
        val n = lbl("${items.size}", msdf.bold(16f, GameColor.accentText_FFB04A))
        val h = pad + head.height + items.size * (gap + rowH) + pad
        g.addActor(ARect(app, px(20f), GameColor.card_16100C, stroke = GameColor.line_white_7).apply { setSize(w, h) })
        head.setPosition(pad, h - pad - head.height); n.setPosition(pad + head.width + px(6f), head.y)
        g.addActor(head); g.addActor(n)
        var y = head.y
        items.forEach { d ->
            y -= gap + rowH
            g.addActor(QueueRow(d.id, w - pad * 2, rowH).apply { setPosition(pad, y) })
        }
        return h
    }

    /** .q-it: мініатюра 64×36 + назва/відсоток + смуга + «24.1 of 61.9 MB · 1.8 MB/s · MP4» + ✕ / ↻. */
    private inner class QueueRow(private val id: Long, w: Float, h: Float) : AdvancedGroup() {
        override val screen = app
        private lateinit var pct: AMsdfLabel
        private lateinit var sub: AMsdfLabel
        private lateinit var bar: AProgressBar
        private var subW = 0f
        init { setSize(w, h) }

        override fun addActorsOnGroup() {
            val d = item() ?: return
            val failed = d.status == DownloadStatus.FAILED
            val th = ACover(app, px(8f)).apply { setBounds(0f, (this@QueueRow.height - px(36f)) / 2f, px(64f), px(36f)) }
            if (d.thumbAsset != null) th.setAsset(d.thumbAsset) else th.setHueFrom(d.videoId)
            addActor(th)
            val btn = AIconButton(app, if (failed) assets.ic_loop else assets.ic_x, px(16f)).apply { setBounds(this@QueueRow.width - px(32f), (this@QueueRow.height - px(32f)) / 2f, px(32f), px(32f)) }
            btn.onClick { if (failed) gdxGame.downloads.retry(d) else gdxGame.downloads.cancel(id) }
            addActor(btn)
            val mx = px(64f + 11f); val mw = btn.x - px(10f) - mx
            pct = lbl("100%", msdf.monoSemi(12f, if (failed) GameColor.err_FF7A6B else GameColor.accentText_FFB04A))
            val title = lbl(d.title, msdf.bold(13f)).ellipsize(mw - pct.width - px(8f))
            bar = AProgressBar(app).apply { setSize(mw, px(5f)) }
            sub = lbl("", msdf.mono(10.5f, if (failed) GameColor.err_FF7A6B else GameColor.muted_B3A293))
            title.setPosition(mx, height - title.height + px(2f))
            bar.setPosition(mx, height / 2f - px(4f))
            sub.setPosition(mx, -px(2f))
            addActor(title); addActor(pct); addActor(bar); addActor(sub)
            pct.setPosition(mx + mw - pct.width, title.y)
            subW = mw
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
            val right = pct.x + pct.width
            pct.setText(if (d.status == DownloadStatus.FAILED) "!" else "${(d.progress * 100).toInt()}%"); pct.pack(); pct.x = right - pct.width
            if (d.status == DownloadStatus.FAILED) { sub.setText(Copy.Errors.DOWNLOAD_FAILED); sub.pack(); return }
            val sp = gdxGame.downloads.speed[id] ?: 0f
            val total = if (d.bytesTotal > 0) Fmt.mb(d.bytesTotal) else "?"
            // швидкість — лише коли вже відома (DownloadManager оновлює байти раз на ~1–2 с)
            sub.setText("${Fmt.mb(d.bytesDone)} of $total MB" + (if (sp > 1000f) " · ${Fmt.speed(sp)}" else "") + " · ${d.format ?: ""}"); sub.pack()
            if (subW > 0f && sub.width > subW) sub.width = subW
        }
    }

    // ------------------------------------------------------------------------
    // Continue watching
    // ------------------------------------------------------------------------
    private fun buildContinue(g: ADyn, w: Float): Float {
        val list = gdxGame.model.continueWatching.take(8)
        if (list.isEmpty()) return 0f
        val head = secHeader(Copy.Save.CONTINUE, right = Copy.Save.LIBRARY, rightIsAction = true) { app.select(AppScreen.TAB_LIBRARY) }
        val tw = px(150f); val th = px(84f)
        val itemH = th + px(6f) + px(18f) + px(16f)
        val row = ARow(app, px(10f), padX = 0f)
        list.forEach { v ->
            row.addActor(object : ATap(app, 0.97f) {
                override fun addContent() {
                    val ph = height
                    val t = AVideoThumb(app, px(12f)).apply { setBounds(0f, ph - th, tw, th) }
                    t.setVideo(v); addActor(t)
                    val b = lbl(v.title, msdf.semibold(13f)).ellipsize(tw)
                    val s = lbl(Copy.Save.left(Fmt.time(v.durationMs - v.watchedMs)), msdf.regular(11.5f, GameColor.muted_B3A293)).ellipsize(tw)
                    b.setPosition(0f, t.y - px(6f) - b.height); s.setPosition(0f, b.y - s.height + px(2f))
                    addActor(b); addActor(s)
                }
            }.onClick { gdxGame.openVideo(v) }.apply { setSize(tw, itemH) })
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
    // Studio: 4 плитки
    // ------------------------------------------------------------------------
    private fun studio(): AdvancedGroup {
        val head = secHeader(Copy.Save.STUDIO, right = Copy.Save.ALL_TOOLS, rightIsAction = true) { app.select(AppScreen.TAB_STUDIO) }
        val tools = Copy.Studio.TOOLS.take(4)
        val gap = px(8f); val tw = (W - gap * 3) / 4f; val th = px(74f)
        val h = head.height + px(10f) + th
        return object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                head.setPosition(0f, h - head.height); addActor(head)
                tools.forEachIndexed { i, t ->
                    val tile = object : ATap(app, 0.95f) {
                        override fun addContent() {
                            addAndFillActor(ARect(app, px(16f), GameColor.card_16100C, stroke = GameColor.line_white_7))
                            val circle = ARect(app, 999f, GameColor.accent_14).apply { setSize(px(34f), px(34f)) }
                            circle.setPosition((width - circle.width) / 2f, height - px(10f) - circle.height)
                            val ic = icon(assets.toolIcon(t.id), px(17f), GameColor.accentText_FFB04A)
                            ic.setPosition(circle.x + (circle.width - ic.width) / 2f, circle.y + (circle.height - ic.height) / 2f)
                            val l = lbl(t.title.substringBefore(' ').let { if (t.id == "status") "Status" else if (t.id == "audio") "Audio" else if (t.id == "gif") "GIF" else it }, msdf.semibold(11.5f))
                            l.setPosition((width - l.width) / 2f, px(9f))
                            addActor(circle); addActor(ic); addActor(l)
                        }
                    }.onClick { gdxGame.openStudio(t.id) }
                    tile.setBounds(i * (tw + gap), 0f, tw, th); addActor(tile)
                }
            }
        }.apply { setSize(W, h) }
    }

    // ------------------------------------------------------------------------
    // Три цифри
    // ------------------------------------------------------------------------
    private fun buildStats(g: ADyn, w: Float): Float {
        val m = gdxGame.model
        if (m.state.library.isEmpty()) return 0f
        val tiles = listOf(
            m.state.library.size.toString() to Copy.Save.STAT_FILES,
            Fmt.size(m.totalBytes) to Copy.Save.STAT_ON_PHONE,
            Fmt.hours(m.leftToWatchMs) to Copy.Save.STAT_TO_WATCH,
        )
        val gap = px(8f); val tw = (w - gap * 2) / 3f; val h = px(58f)
        tiles.forEachIndexed { i, (b, s) ->
            val x = i * (tw + gap)
            g.addActor(ARect(app, px(16f), GameColor.card_16100C, stroke = GameColor.line_white_7).apply { setBounds(x, 0f, tw, h) })
            val big = lbl(b, msdf.style(gdxGame.msdfManager.fontArchivo_ExpandedExtraBold, 18f) { letterSpacing = -1f })
            val small = lbl(s, msdf.regular(11f, GameColor.muted_B3A293))
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
            photo.setTexture(assets.FEAT_SINTEL)
            addAndFillActor(ARect(app, px(22f), GameColor.background.cpy().apply { a = 0.94f }, GameColor.background.cpy().apply { a = 0.1f }, angleCss = 90f, start = 0.35f, stroke = GameColor.accentText_25))
            val pad = px(16f)
            val go = lbl(Copy.Save.PROMO_GO, msdf.bold(12.5f, GameColor.amber_FFC94D))
            val chev = icon(assets.ic_chev_right, px(15f), GameColor.amber_FFC94D)
            val lines = Copy.Save.PROMO_TITLE.split("\n")
            val t2 = lbl(lines[1], msdf.disp(22f, Color.WHITE)); val t1 = lbl(lines[0], msdf.disp(22f, Color.WHITE))
            val chip = AChip(app, Copy.Save.PROMO_CHIP, AChip.Kind.GREEN, assets.ic_shield)
            var y = pad
            go.setPosition(pad, y); chev.setPosition(pad + go.width + px(4f), y + (go.height - chev.height) / 2f); y += go.height + px(8f)
            t2.setPosition(pad, y); y += t2.height * 0.95f
            t1.setPosition(pad, y); y += t1.height + px(8f)
            chip.setPosition(pad, y)
            addActor(go); addActor(chev); addActor(t2); addActor(t1); addActor(chip)
        }
    }.onClick { app.select(AppScreen.TAB_DISCOVER) }.apply { setSize(W, px(150f)) }
}
