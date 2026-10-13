package com.vidtools.framefall.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.AppEvent
import com.vidtools.framefall.core.model.EventKind
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.core.model.VideoSource
import com.vidtools.framefall.core.model.withEvent
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.actors.ui.AButtonAccent
import com.vidtools.framefall.game.actors.ui.AButtonGhost
import com.vidtools.framefall.game.actors.ui.AIconButton
import com.vidtools.framefall.game.actors.ui.AProgressBar
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ASegmented
import com.vidtools.framefall.game.actors.ui.AVideo
import com.vidtools.framefall.game.platform.StudioJob
import com.vidtools.framefall.game.platform.StudioOutput
import com.vidtools.framefall.game.platform.VideoPlayer
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px
import com.vidtools.framefall.util.log
import java.util.Locale
import java.util.UUID

// ─────────────────────────────────────────────────────────────────────────────
// StudioScreen — редактор одного інструмента (.ed у прототипі):
//   шапка (← назва інструмента, назва відео), прев'ю (AVideo, грає лише вибраний шматок по колу),
//   смуга кадрів з ручками In / Out (кадр — один «плейхед»), In · Length · Out,
//   опції (Status — довжина частини, Compress — висота кадру), «Result ≈ …», кнопка дії.
//   Під час роботи — смуга прогресу й Cancel; результат — у Library (Studio) або фон Home.
// ─────────────────────────────────────────────────────────────────────────────
class StudioScreen : FramefallScreen() {

    override val analyticsBt    = com.vidtools.framefall.businesModule.backend.Bt.TOOL
    override val analyticsBlock = "studio_${gdxGame.studioTool ?: "trim"}"

    private val tool = Copy.Studio.tool(gdxGame.studioTool ?: "trim")
    private val video: Video? = gdxGame.model.studioVideo
    private val player: VideoPlayer = gdxGame.bridge.createVideoPlayer()
    private val dur = (video?.durationMs ?: 0L).coerceAtLeast(1000L)

    /** Вибраний шматок, мс. Для «frame» — лише start (кадр). */
    private var start = 0L
    private var end = dur
    private var option = when (tool.id) { "status" -> 30; "compress" -> 480; else -> 0 }

    private val isRange get() = tool.id != "compress" && tool.id != "frame"
    private val maxLen get() = when (tool.id) { "gif" -> 6_000L; "loop" -> 10_000L; else -> Long.MAX_VALUE }

    private lateinit var strip: AdvancedGroup
    private lateinit var dimL: ARect
    private lateinit var dimR: ARect
    private lateinit var hIn: ARect
    private lateinit var hOut: ARect
    private lateinit var head: ARect
    private lateinit var tcL: AMsdfLabel
    private lateinit var inL: AMsdfLabel
    private lateinit var lenL: AMsdfLabel
    private lateinit var outL: AMsdfLabel
    private lateinit var resL: AMsdfLabel
    private lateinit var cta: AButtonAccent
    private lateinit var progress: AProgressBar
    private lateinit var progressL: AMsdfLabel
    private lateinit var cancel: AButtonGhost
    private var working = false
    /** Що робить головна кнопка: спершу — інструмент, після успіху — «Open». */
    private var ctaAction: () -> Unit = { run() }
    private val stripTextures = ArrayList<Texture>()

    override fun buildContent() {
        val v = video ?: run { com.vidtools.framefall.game.utils.runGDX { close() }; return }
        val w = worldWidth; val h = worldHeight; val side = px(16f)
        defaultRange()

        // ── Шапка ──
        var y = h - safeStatusBarUI - px(8f) - px(38f)
        val back = AIconButton(this, assets.ic_chev_left).apply { setBounds(side, y, px(38f), px(38f)) }
        back.onClick { close() }
        val title = lbl(tool.title, msdf.bold(18f)); title.setPosition(side + px(48f), y + (px(38f) - title.height) / 2f)
        val name = lbl(v.title, msdf.mono(10.5f, GameColor.muted_B3A293)).ellipsize(w - title.x - title.width - side - px(16f))
        name.setPosition(w - side - name.width, y + (px(38f) - name.height) / 2f)
        content.addActor(back); content.addActor(title); content.addActor(name)

        // ── Прев'ю ──
        val pw = w - side * 2; val ph = pw * 9f / 16f
        y -= px(14f) + ph
        content.addActor(ARect(this, px(16f), Color.BLACK).apply { setBounds(side, y, pw, ph) })
        val pv = AVideo(this, player, AVideo.Fit.CONTAIN, radius = px(16f)).apply { setBounds(side, y, pw, ph) }
        content.addActor(pv)
        gdxGame.covers.forVideo(v) { tex -> if (tex != null) pv.poster = tex }
        tcL = lbl("0:00.0", msdf.monoSemi(11f, Color.WHITE))
        val tcBg = ARect(this, px(7f), Color(0f, 0f, 0f, 0.65f)).apply { setBounds(side + px(10f), y + px(10f), tcL.width + px(14f), tcL.height * 0.8f + px(8f)) }
        tcL.setPosition(tcBg.x + px(7f), tcBg.y + (tcBg.height - tcL.height) / 2f)
        content.addActor(tcBg); content.addActor(tcL)

        // ── Смуга кадрів ──
        if (tool.id != "compress") {
            y -= px(16f) + px(56f)
            strip = buildStrip(side, y, pw, px(56f))
            content.addActor(strip)
            y -= px(8f)
            inL = lbl("", msdf.monoSemi(11f, GameColor.muted_B3A293)); lenL = lbl("", msdf.monoSemi(11f, Color.WHITE)); outL = lbl("", msdf.monoSemi(11f, GameColor.muted_B3A293))
            y -= inL.height
            inL.setPosition(side, y); lenL.setPosition(w / 2f, y); outL.setPosition(w - side, y)
            content.addActor(inL); content.addActor(lenL); content.addActor(outL)
        }

        // ── Опції ──
        when (tool.id) {
            "status" -> {
                y -= px(16f) + px(42f)
                val seg = ASegmented(this, Copy.Studio.PART_LENGTHS.map { "${it}s" to null }, Copy.Studio.PART_LENGTHS.indexOf(option)) { i -> option = Copy.Studio.PART_LENGTHS[i]; refreshLabels() }
                seg.setBounds(side, y, pw, px(42f)); content.addActor(seg)
            }
            "compress" -> {
                y -= px(16f) + px(42f)
                val seg = ASegmented(this, Copy.Studio.COMPRESS_TARGETS.map { Copy.Studio.compressLabel(it) to null }, Copy.Studio.COMPRESS_TARGETS.indexOf(option)) { i -> option = Copy.Studio.COMPRESS_TARGETS[i]; refreshLabels() }
                seg.setBounds(side, y, pw, px(42f)); content.addActor(seg)
            }
        }
        val note = when (tool.id) { "gif" -> Copy.Studio.GIF_NOTE; "loop" -> Copy.Studio.LOOP_NOTE; "frame" -> Copy.Studio.FRAME_NOTE; "audio" -> Copy.Studio.AUDIO_NOTE; else -> null }
        note?.let {
            val nl = lbl(it, msdf.regular(12f, GameColor.muted_B3A293)).apply { setWrap(true); width = pw; height = prefHeight }
            y -= px(14f) + nl.height; nl.setPosition(side, y); content.addActor(nl)
        }

        // ── Result + кнопка (знизу) ──
        var by = safeNavBarUI + px(18f)
        cta = AButtonAccent(this, tool.cta, assets.toolIcon(tool.id)).apply { setBounds(side, by, pw, px(50f)) }
        cta.onClick { ctaAction() }
        content.addActor(cta)
        cancel = AButtonGhost(this, "Cancel", assets.ic_x).apply { setBounds(side, by, pw, px(50f)); isVisible = false }
        cancel.onClick { gdxGame.bridge.cancelStudio(); finishWork(); showToast("Cancelled") }
        content.addActor(cancel)
        by += px(50f) + px(12f)
        progress = AProgressBar(this).apply { setBounds(side, by + px(4f), pw, px(6f)); isVisible = false }
        progressL = lbl(Copy.Studio.WORKING, msdf.monoSemi(11.5f, GameColor.accentText_FFB04A)).apply { isVisible = false }
        progressL.setPosition(side, by + px(14f))
        content.addActor(progress); content.addActor(progressL)
        val resBox = ARect(this, px(14f), GameColor.card_16100C, stroke = GameColor.line_white_7).apply { setBounds(side, by, pw, px(46f)) }
        val resK = lbl(Copy.Studio.RESULT, msdf.semibold(13f)); resK.setPosition(side + px(14f), by + (px(46f) - resK.height) / 2f)
        resL = lbl("", msdf.style(gdxGame.msdfManager.fontArchivo_ExpandedExtraBold, 14f, GameColor.amber_FFC94D))
        resL.setPosition(w - side - px(14f), by + (px(46f) - resL.height) / 2f)
        content.addActor(resBox); content.addActor(resK); content.addActor(resL)
        resultBox = listOf(resBox, resK, resL)

        refreshLabels()
        player.open(v.localUri ?: "", autoplay = tool.id != "frame", loop = false, muted = tool.id == "loop" || tool.id == "gif", startMs = start)
        applyRange()
        if (tool.id != "compress") loadStrip(v)
    }
    private var resultBox: List<com.badlogic.gdx.scenes.scene2d.Actor> = emptyList()
    /** Прев'ю саме продовжує після реклами / фону, поки людина не поставила паузу (кадр — завжди стоїть). */
    private var resumeAcc = 0f

    /** Діапазон за замовчуванням: короткі інструменти — з 10 % тривалості. */
    private fun defaultRange() {
        val p = (dur / 10).coerceAtMost(dur - 1000)
        when (tool.id) {
            "gif", "loop" -> { start = p; end = (p + maxLen).coerceAtMost(dur) }
            "frame" -> { start = (dur / 3); end = start }
            // довгий фільм: хвилина з 10 % тривалості — ручки видно на смузі, а не в одній точці
            "trim" -> if (dur <= 90_000L) { start = 0L; end = dur } else { start = p; end = (p + 60_000L).coerceAtMost(dur) }
            else -> { start = 0L; end = dur }
        }
    }

    // ------------------------------------------------------------------------
    // Смуга кадрів з ручками
    // ------------------------------------------------------------------------
    private fun buildStrip(x: Float, y: Float, w: Float, h: Float): AdvancedGroup {
        val g = object : AdvancedGroup() {
            override val screen = this@StudioScreen
            override fun addActorsOnGroup() {}
        }
        g.setBounds(x, y, w, h)
        g.addActor(ARect(this, px(10f), GameColor.card2_221812).apply { setSize(w, h); touchable = Touchable.disabled })
        dimL = ARect(this, px(10f), Color(0f, 0f, 0f, 0.6f)).apply { touchable = Touchable.disabled }
        dimR = ARect(this, px(10f), Color(0f, 0f, 0f, 0.6f)).apply { touchable = Touchable.disabled }
        hIn = ARect(this, px(4f), GameColor.amber_FFC94D).apply { setSize(px(12f), h + px(4f)); touchable = Touchable.disabled }
        hOut = ARect(this, px(4f), GameColor.amber_FFC94D).apply { setSize(px(12f), h + px(4f)); touchable = Touchable.disabled }
        head = ARect(this, 999f, Color.WHITE).apply { setSize(px(3f), h + px(10f)); touchable = Touchable.disabled }
        g.addListener(object : InputListener() {
            var drag = 0   // 1 — In, 2 — Out, 3 — плейхед / кадр
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (working) return false
                val t = (x / w).coerceIn(0f, 1f) * dur
                // найближча ручка; коли стоять поруч — праворуч від In береться Out (інакше їх не розвести)
                val dIn = kotlin.math.abs(x - xOf(start)); val dOut = kotlin.math.abs(x - xOf(end))
                drag = when {
                    !isRange -> 3
                    minOf(dIn, dOut) > px(26f) -> 3
                    dOut < dIn || (x > xOf(start) && dOut - dIn < px(6f)) -> 2
                    else -> 1
                }
                move(drag, t.toLong()); return true
            }
            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) { move(drag, ((x / w).coerceIn(0f, 1f) * dur).toLong()) }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                applyRange()
                if (drag != 3) player.seekTo(start)
                if (tool.id != "frame") player.play()
            }
        })
        return g
    }

    private fun xOf(ms: Long) = strip.width * ms / dur.toFloat()

    private fun move(what: Int, t: Long) {
        val minLen = 1000L
        when (what) {
            1 -> { start = t.coerceIn(0L, end - minLen); if (end - start > maxLen) end = start + maxLen }
            2 -> { end = t.coerceIn(start + minLen, dur); if (end - start > maxLen) start = end - maxLen }
            3 -> if (!isRange) { start = t.coerceIn(0L, dur - 100); end = start; player.seekTo(start) } else player.seekTo(t.coerceIn(start, end))
        }
        refreshLabels()
    }

    private fun applyRange() = if (isRange) player.setRange(start, end) else player.setRange(0, -1)

    private fun loadStrip(v: Video) {
        val n = 10
        gdxGame.bridge.stripFrames(v, n) { frames ->
            if (frames.isEmpty() || !::strip.isInitialized) return@stripFrames
            val fw = strip.width / frames.size
            frames.forEachIndexed { i, bytes ->
                runCatching {
                    val pm = Pixmap(bytes, 0, bytes.size)
                    val tex = Texture(pm).apply { setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear) }
                    pm.dispose(); stripTextures += tex
                    val img = Image(com.vidtools.framefall.game.actors.ui.ACover.coverRegion(tex, fw, strip.height)).apply {
                        setBounds(i * fw, 0f, fw + 0.5f, strip.height); touchable = Touchable.disabled; color.a = 0f
                        addAction(Actions.fadeIn(0.2f))
                    }
                    strip.addActorAt(1, img)
                }.onFailure { log("strip frame: ${it.message}") }
            }
        }
        strip.addActor(dimL); strip.addActor(dimR); strip.addActor(hIn); strip.addActor(hOut); strip.addActor(head)
    }

    private fun refreshLabels() {
        if (::strip.isInitialized) {
            val h = strip.height
            if (isRange) {
                dimL.setBounds(0f, 0f, xOf(start), h); dimR.setBounds(xOf(end), 0f, strip.width - xOf(end), h)
                hIn.setPosition(xOf(start) - hIn.width / 2f, -px(2f)); hOut.setPosition(xOf(end) - hOut.width / 2f, -px(2f))
            } else { dimL.isVisible = false; dimR.isVisible = false; hIn.isVisible = false; hOut.isVisible = false }
            inL.setText("${Copy.Studio.IN} ${Fmt1.t(start)}"); inL.pack()
            outL.setText("${Copy.Studio.OUT} ${Fmt1.t(if (isRange) end else start)}"); outL.pack(); outL.x = worldWidth - px(16f) - outL.width
            val len = if (isRange) end - start else 0L
            lenL.setText(if (isRange) "${Copy.Studio.LENGTH} ${Fmt1.t(len)}" else "${Copy.Studio.FRAME} ${Fmt1.t(start)}"); lenL.pack()
            lenL.x = (worldWidth - lenL.width) / 2f
            inL.isVisible = isRange; outL.isVisible = isRange
        }
        if (::resL.isInitialized) {
            resL.setText(estimate()); resL.pack(); resL.x = worldWidth - px(16f) - px(14f) - resL.width
        }
    }

    private fun parts() = (((end - start) + option * 1000L - 1) / (option * 1000L)).toInt().coerceAtLeast(1)

    /** Грубо, але чесно: «≈ 12.4 MB» / «4 files · ≈ 20 MB». */
    private fun estimate(): String {
        val v = video ?: return ""
        val len = (end - start).coerceAtLeast(0L)
        val share = len.toFloat() / dur
        val bytes: Long = when (tool.id) {
            "trim", "status" -> (v.sizeBytes * share).toLong()
            "audio" -> len / 1000 * 16_000L
            "gif" -> len / 100 * 30_000L
            "frame" -> 250_000L
            "loop" -> len / 1000 * 250_000L
            "compress" -> dur / 1000 * when (option) { 720 -> 330_000L; 480 -> 165_000L; else -> 100_000L }
            else -> 0L
        }
        val s = "~${Fmt.size(bytes.coerceAtLeast(1024))}"
        return if (tool.id == "status") "${Copy.Studio.parts(parts(), option)} · $s" else s
    }

    // ------------------------------------------------------------------------
    // Робота
    // ------------------------------------------------------------------------
    private fun run() {
        val v = video ?: return
        if (working) return
        if (tool.id == "audio" && !gdxGame.bridge.hasAudio(v)) { showToast(Copy.Studio.NO_AUDIO); return }
        working = true
        player.pause()
        cta.isVisible = false; cancel.isVisible = true
        resultBox.forEach { it.isVisible = false }
        progress.isVisible = true; progressL.isVisible = true; progress.set(0f, immediate = true)
        val job = StudioJob(tool.id, v, start, if (isRange) end else start, option)
        gdxGame.bridge.runStudio(job, { p ->
            progress.set(p)
            progressL.setText("${Copy.Studio.WORKING} ${(p * 100).toInt()}%"); progressL.pack()
        }) { r ->
            if (!working) return@runStudio   // скасували
            finishWork()
            r.onSuccess { outs -> onDone(v, outs) }
             .onFailure { showToast(Copy.Studio.FAILED); log("studio failed: ${it.message}") }
        }
    }

    private fun finishWork() {
        working = false
        cta.isVisible = true; cancel.isVisible = false
        resultBox.forEach { it.isVisible = true }
        progress.isVisible = false; progressL.isVisible = false
    }

    private fun onDone(src: Video, outs: List<StudioOutput>) {
        if (tool.id == "loop") {
            val path = outs.firstOrNull()?.localUri?.removePrefix("file://") ?: return
            gdxGame.model.update { it.copy(homeBg = path) }
            showToast(Copy.Studio.HOME_BG_SET)
            return
        }
        val now = System.currentTimeMillis()
        val added = outs.mapIndexed { i, o ->
            Video(
                id = UUID.randomUUID().toString(), title = o.title, credit = src.credit,
                durationMs = o.durationMs, sizeBytes = o.sizeBytes, format = o.format, width = o.width, height = o.height,
                kind = o.kind, source = VideoSource.STUDIO, sourceUrl = null, localUri = o.localUri, thumbPath = o.thumbPath,
                license = src.license, addedAt = now + i, parentId = src.id, tool = tool.title,
            )
        }
        gdxGame.model.update { s ->
            added.fold(s.copy(library = s.library + added)) { acc, a -> acc.withEvent(AppEvent("st-${a.id}", EventKind.STUDIO_DONE, a.title, tool.title, videoId = a.id, at = a.addedAt)) }
        }
        val folder = when (added.firstOrNull()?.kind) { MediaKind.AUDIO -> "Music/Framefall"; MediaKind.IMAGE -> "Pictures/Framefall"; else -> "Movies/Framefall" }
        showToast(Copy.Studio.savedTo(folder))
        // Відкрити результат одразу — як «Open» у прототипі
        added.firstOrNull()?.let { first ->
            cta.setText(Copy.Studio.OPEN, assets.ic_play_fill)
            ctaAction = { gdxGame.openVideo(first) }
            cta.addAction(Actions.sequence(Actions.scaleTo(1.03f, 1.03f, 0.12f, Interpolation.pow2Out), Actions.scaleTo(1f, 1f, 0.2f)))
        }
    }

    // ------------------------------------------------------------------------
    // Кадр за кадром
    // ------------------------------------------------------------------------
    override fun render(delta: Float) {
        if (::tcL.isInitialized && tool.id != "frame" && !working && sheet == null && !player.isPlaying && player.error == null) {
            resumeAcc += delta
            if (resumeAcc > 0.6f) { resumeAcc = 0f; player.play() }
        } else resumeAcc = 0f
        if (::tcL.isInitialized) {
            val pos = player.positionMs
            tcL.setText(Fmt1.t(pos)); tcL.pack()
            if (::strip.isInitialized && ::head.isInitialized) head.setPosition(xOf(pos.coerceIn(0L, dur)) - head.width / 2f, -px(5f))
        }
        super.render(delta)
    }

    override fun onBackPressed() {
        if (sheet != null) { super.onBackPressed(); return }
        close()
    }

    private fun close() {
        if (working) { gdxGame.bridge.cancelStudio(); working = false }
        player.pause()
        val toApp = gdxGame.navigationManager.fromScreenName == AppScreen::class.java.name
        animHideScreen {
            gdxGame.navigationManager.back()
            if (toApp) gdxGame.adBack()
        }
    }

    override fun hide() {
        player.release()
        super.hide()
    }

    override fun dispose() {
        player.release()
        stripTextures.forEach { it.dispose() }
        super.dispose()
    }

    /** Таймкод з десятими: 1:02.4 (JetBrains Mono). */
    private object Fmt1 {
        fun t(ms: Long): String {
            val s = ms.coerceAtLeast(0L) / 1000f
            return String.format(Locale.US, "%d:%04.1f", (s / 60).toInt(), s % 60)
        }
    }
}
