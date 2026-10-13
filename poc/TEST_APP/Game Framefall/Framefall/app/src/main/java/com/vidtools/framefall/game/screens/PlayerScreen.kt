package com.vidtools.framefall.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.AIconButton
import com.vidtools.framefall.game.actors.ui.AProgressBar
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.actors.ui.AVideo
import com.vidtools.framefall.game.platform.VideoPlayer
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.screens.sheets.VideoMenuSheet
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.TIME_ANIM_SCREEN
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// PlayerScreen — плеєр відео (.pl у прототипі).
//
//   Верх: кадр (AVideo, CONTAIN) на чорному, висота під пропорцію (вертикальне —
//   до 58 % екрана). Тап — показати/сховати оверлей: «назад», повний екран, −10 / ▶ / +10,
//   час і перемотка. Під час відтворення оверлей ховається через 3 с.
//   Низ: назва, чипи (автор · рік, ліцензія), 8 дій (Speed, Loop, Studio, Home bg,
//   Audio, Share, Fullscreen, More), таблиця «Format / Size / Saved to / Source / Credit».
//
//   Повний екран — той самий кадр, повернутий на 90° на весь екран (Activity лишається
//   портретною: лаунчер не повертаємо). «Назад» спершу виходить з повного екрана.
//   Позиція зберігається (Continue watching) раз на 5 с і при виході.
// ─────────────────────────────────────────────────────────────────────────────
class PlayerScreen : FramefallScreen() {

    override val analyticsBt    = com.vidtools.framefall.businesModule.backend.Bt.TOOL
    override val analyticsBlock = "player_screen"

    private val video: Video? = gdxGame.model.video(gdxGame.playerVideoId)
    private val player: VideoPlayer = gdxGame.bridge.createVideoPlayer()

    private lateinit var frameBox: AdvancedGroup       // область кадру
    private lateinit var videoActor: AVideo
    private lateinit var overlay: Group
    private lateinit var playIcon: Image
    private lateinit var seek: AProgressBar
    private lateinit var knob: ARect
    private lateinit var posL: AMsdfLabel
    private lateinit var durL: AMsdfLabel
    private lateinit var speedL: AMsdfLabel
    private lateinit var loopIc: Image
    private var fs: Group? = null
    private var seeking = false
    private var hideAt = 0f
    private var clock = 0f
    private var saveAcc = 0f
    /** Паузу поставила людина. Інакше (реклама поверх, GL-пауза) — продовжуємо самі. */
    private var userPaused = false
    private var resumeAcc = 0f

    private val speeds = listOf(1f, 1.25f, 1.5f, 2f, 0.5f, 0.75f)

    override fun buildContent() {
        val v = video ?: run { com.vidtools.framefall.game.utils.runGDX { closePlayer() }; return }
        val w = worldWidth; val h = worldHeight
        gdxGame.backgroundColor = GameColor.background

        // ── Кадр ──
        val top = h - safeStatusBarUI
        val aspect = v.aspect.coerceIn(0.4f, 2.4f)
        val vh = (w / aspect).coerceIn(w * 9f / 16f * 0.75f, h * 0.58f)
        frameBox = object : AdvancedGroup() {
            override val screen = this@PlayerScreen
            override fun addActorsOnGroup() {}
        }
        frameBox.setBounds(0f, top - vh, w, vh)
        content.addActor(Image(drawerUtil.whiteRegion).apply { color.set(Color.BLACK); setBounds(0f, top - vh, w, vh + safeStatusBarUI) })
        content.addActor(frameBox)
        videoActor = AVideo(this, player, AVideo.Fit.CONTAIN)
        videoActor.setBounds(0f, 0f, w, vh)
        frameBox.addActor(videoActor)
        gdxGame.covers.forVideo(v) { tex -> if (tex != null) videoActor.poster = tex }
        if (v.thumbAsset != null) gdxGame.covers.forAsset(v.thumbAsset)?.let { videoActor.poster = it }
        if (v.kind == MediaKind.AUDIO) frameBox.addActor(icon(assets.ic_music, px(56f), GameColor.white_70).apply { setPosition((w - width) / 2f, (vh - height) / 2f) })
        buildOverlay(w, vh)
        frameBox.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = true
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                if (event.target === frameBox || event.target === videoActor) toggleOverlay()
            }
        })

        // ── Низ ──
        val side = px(18f)
        var y = top - vh - px(16f)
        val title = lbl(v.title, msdf.bold(21f)).apply { setWrap(true); width = w - side * 2; height = prefHeight.coerceAtMost(px(56f)) }
        y -= title.height; title.setPosition(side, y); content.addActor(title)
        y -= px(10f)
        val chips = Group()
        var cx = 0f
        listOfNotNull(
            AChip(this, v.credit, AChip.Kind.GLASS),
            v.license?.let { AChip(this, it, if (it.startsWith("Public")) AChip.Kind.GREEN else AChip.Kind.BLUE, assets.ic_shield) },
            if (v.tool != null) AChip(this, v.tool, AChip.Kind.RED, assets.ic_wand) else null,
        ).forEach { c -> if (cx + c.width < w - side * 2) { c.setPosition(cx, 0f); chips.addActor(c); cx += c.width + px(6f) } }
        val chipH = chips.children.firstOrNull()?.height ?: 0f
        y -= chipH; chips.setPosition(side, y); content.addActor(chips)

        y -= px(16f)
        val grid = actionsGrid(w - side * 2, v)
        y -= grid.height; grid.setPosition(side, y); content.addActor(grid)

        y -= px(16f)
        val rows = listOfNotNull(
            Copy.Player.FORMAT to listOfNotNull(v.format, if (v.width > 0) "${v.width}×${v.height}" else null).joinToString(" · "),
            Copy.Player.SIZE to Fmt.size(v.sizeBytes),
            Copy.Player.SAVED_TO to "Movies/Framefall",
            v.sourceUrl?.let { Copy.Player.SOURCE to (runCatching { java.net.URI(it).host?.removePrefix("www.") }.getOrNull() ?: "—") },
            Copy.Player.CREDIT to v.credit,
        )
        rows.forEach { (k, value) ->
            if (y < safeNavBarUI + px(24f)) return@forEach
            val kl = lbl(k, msdf.regular(12.5f, GameColor.muted_B3A293))
            val vl = lbl(value, msdf.semibold(12.5f)).ellipsize(w - side * 2 - px(110f))
            y -= kl.height + px(6f)
            kl.setPosition(side, y); vl.setPosition(w - side - vl.width, y)
            content.addActor(kl); content.addActor(vl)
        }

        // Відтворення: продовжуємо з місця, де зупинились
        val start = if (v.inProgress) v.watchedMs else 0L
        player.open(v.localUri ?: "", autoplay = true, loop = false, muted = false, startMs = start)
        showOverlay(true)
    }

    // ------------------------------------------------------------------------
    // Оверлей поверх кадру
    // ------------------------------------------------------------------------
    private fun buildOverlay(w: Float, vh: Float) {
        overlay = Group().apply { setSize(w, vh); touchable = Touchable.childrenOnly }
        overlay.addActor(ARect(this, 0f, Color(0f, 0f, 0f, 0.45f), Color(0f, 0f, 0f, 0.1f), angleCss = 180f).apply { setSize(w, vh); touchable = Touchable.disabled })
        val back = AIconButton(this, assets.ic_chev_left, bgColor = Color(0f, 0f, 0f, 0.35f), strokeColor = GameColor.white_12).apply { setBounds(px(12f), vh - px(50f), px(38f), px(38f)) }
        back.onClick { closePlayer() }
        val full = AIconButton(this, assets.ic_expand, px(18f), bgColor = Color(0f, 0f, 0f, 0.35f), strokeColor = GameColor.white_12).apply { setBounds(w - px(50f), vh - px(50f), px(38f), px(38f)) }
        full.onClick { enterFullscreen() }
        overlay.addActor(back); overlay.addActor(full)

        val cy = vh / 2f + px(6f)
        val rew = AIconButton(this, assets.ic_rew10, px(28f), bgColor = Color(0f, 0f, 0f, 0.3f), strokeColor = GameColor.white_12).apply { setBounds(w / 2f - px(110f), cy - px(24f), px(48f), px(48f)) }
        rew.onClick { player.seekTo((player.positionMs - 10_000).coerceAtLeast(0)); bump() }
        val fwd = AIconButton(this, assets.ic_fwd10, px(28f), bgColor = Color(0f, 0f, 0f, 0.3f), strokeColor = GameColor.white_12).apply { setBounds(w / 2f + px(62f), cy - px(24f), px(48f), px(48f)) }
        fwd.onClick { player.seekTo((player.positionMs + 10_000).coerceAtMost(player.durationMs)); bump() }
        val play = object : ATap(this, 0.92f) {
            override fun addContent() {
                addAndFillActor(ARect(this@PlayerScreen, 999f, GameColor.gradTop_FFC94D, GameColor.gradBot_F2511B))
                playIcon = icon(assets.ic_pause_fill, px(24f), GameColor.onAccent_1A0E05)
                playIcon.setPosition((width - playIcon.width) / 2f, (height - playIcon.height) / 2f); addActor(playIcon)
            }
        }.onClick { togglePlay() }
        play.setBounds(w / 2f - px(30f), cy - px(30f), px(60f), px(60f))
        overlay.addActor(rew); overlay.addActor(fwd); overlay.addActor(play)

        // час + перемотка
        val sx = px(14f)
        posL = lbl("0:00", msdf.monoSemi(11f, Color.WHITE)); durL = lbl("0:00", msdf.monoSemi(11f, GameColor.white_75))
        posL.setPosition(sx, px(16f)); durL.setPosition(w - sx - durL.width, px(16f))
        val barX = sx + px(40f); val barW = w - barX * 2
        seek = AProgressBar(this, GameColor.amber_FFC94D, GameColor.accent_FF8A2A, GameColor.white_25).apply { setBounds(barX, px(16f) + posL.height / 2f - px(2.5f), barW, px(5f)) }
        knob = ARect(this, 999f, Color.WHITE).apply { setSize(px(13f), px(13f)); touchable = Touchable.disabled }
        overlay.addActor(posL); overlay.addActor(durL); overlay.addActor(seek); overlay.addActor(knob)
        val hit = object : AdvancedGroup() { override val screen = this@PlayerScreen; override fun addActorsOnGroup() {} }
        hit.setBounds(barX - px(8f), seek.y - px(14f), barW + px(16f), px(33f))
        hit.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { seeking = true; scrub(x - px(8f), barW); return true }
            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) { scrub(x - px(8f), barW) }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                seeking = false
                if (player.durationMs > 0) player.seekTo((player.durationMs * ((x - px(8f)) / barW).coerceIn(0f, 1f)).toLong())
                bump()
            }
        })
        overlay.addActor(hit)
        frameBox.addActor(overlay)
    }

    private fun scrub(x: Float, barW: Float) { seek.set((x / barW).coerceIn(0f, 1f), immediate = true); bump() }

    private fun toggleOverlay() = showOverlay(!overlay.isVisible || overlay.color.a < 0.5f)

    private fun showOverlay(show: Boolean) {
        overlay.clearActions()
        if (show) { overlay.isVisible = true; overlay.addAction(Actions.fadeIn(0.15f)); bump() }
        else overlay.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.visible(false)))
    }

    /** Будь-яка дія з оверлеєм — ще 3 с показу. */
    private fun bump() { hideAt = clock + 3f }

    private fun togglePlay() {
        if (player.isPlaying) { player.pause(); userPaused = true } else { player.play(); userPaused = false }
        bump()
    }

    // ------------------------------------------------------------------------
    // Дії
    // ------------------------------------------------------------------------
    private fun actionsGrid(w: Float, v: Video): AdvancedGroup {
        val isVideo = v.kind == MediaKind.VIDEO
        val items: List<Triple<TextureRegion, String, () -> Unit>> = listOf(
            Triple(assets.ic_gauge, Copy.Player.SPEED) { cycleSpeed() },
            Triple(assets.ic_loop, Copy.Player.LOOP) { toggleLoop() },
            Triple(assets.ic_wand, Copy.Player.STUDIO) { leaveTo { gdxGame.openStudio("trim", v) } },
            Triple(assets.ic_home, Copy.Player.HOME_BG) { leaveTo { gdxGame.openStudio("loop", v) } },
            Triple(assets.ic_music, Copy.Player.AUDIO) { leaveTo { gdxGame.openStudio("audio", v) } },
            Triple(assets.ic_share, Copy.Player.SHARE) { gdxGame.bridge.shareVideo(v) },
            Triple(assets.ic_expand, "Full") { enterFullscreen() },
            Triple(assets.ic_more, Copy.Player.MORE) { openSheet(VideoMenuSheet(this, v) { closePlayer() }) },
        )
        val cols = 4; val gap = px(8f); val cw = (w - gap * (cols - 1)) / cols; val ch = px(58f)
        return object : AdvancedGroup() {
            override val screen = this@PlayerScreen
            override fun addActorsOnGroup() {
                items.forEachIndexed { i, (ic, label, act) ->
                    val enabled = isVideo || i in listOf(0, 1, 5, 7)
                    val b = object : ATap(this@PlayerScreen, 0.95f) {
                        override fun addContent() {
                            addAndFillActor(ARect(this@PlayerScreen, px(14f), GameColor.card_16100C, stroke = GameColor.line_white_7))
                            val im = icon(ic, px(19f), GameColor.text_FFF6EC)
                            val l = lbl(label, msdf.semibold(10.5f))
                            when (i) { 0 -> speedL = l; 1 -> loopIc = im }
                            val tot = im.height + px(4f) + l.height * 0.85f
                            im.setPosition((width - im.width) / 2f, (height + tot) / 2f - im.height)
                            l.setPosition((width - l.width) / 2f, im.y - px(4f) - l.height * 0.92f)
                            addActor(im); addActor(l)
                        }
                    }.onClick(act)
                    if (!enabled) b.disable()
                    val r = i / cols; val c = i % cols
                    b.setBounds(c * (cw + gap), height - (r + 1) * ch - r * gap, cw, ch)
                    addActor(b)
                }
            }
        }.apply { setSize(w, ch * 2 + gap) }
    }

    private fun cycleSpeed() {
        val next = speeds[(speeds.indexOf(player.speed).coerceAtLeast(0) + 1) % speeds.size]
        player.speed = next
        val s = (if (next % 1f == 0f) next.toInt().toString() else next.toString()) + "×"
        speedL.setText(if (next == 1f) Copy.Player.SPEED else s); speedL.pack(); speedL.x = (speedL.parent.width - speedL.width) / 2f
        showToast(Copy.Player.speedToast(s))
    }

    private fun toggleLoop() {
        player.loop = !player.loop
        loopIc.color.set(if (player.loop) GameColor.accent_FF8A2A else GameColor.text_FFF6EC)
        showToast(if (player.loop) Copy.Player.LOOP_ON else Copy.Player.LOOP_OFF)
    }

    /** Перейти в Studio: позицію зберегти, плеєр зупинити. */
    private fun leaveTo(go: () -> Unit) {
        savePosition()
        userPaused = true
        player.pause()
        go()
    }

    // ------------------------------------------------------------------------
    // Повний екран: кадр повернутий на 90°
    // ------------------------------------------------------------------------
    private fun enterFullscreen() {
        if (fs != null || video?.kind != MediaKind.VIDEO) return
        val w = worldWidth; val h = worldHeight
        val g = Group().apply { setSize(w, h) }
        g.addActor(Image(drawerUtil.whiteRegion).apply { color.set(Color.BLACK); setSize(w, h) })
        // Повернута група: її «ширина» — висота екрана
        val rot = Group().apply { isTransform = true; setSize(h, w); setOrigin(0f, 0f); rotation = 90f; setPosition(w, 0f) }
        val big = AVideo(this, player, AVideo.Fit.CONTAIN).apply { setBounds(0f, 0f, h, w) }
        rot.addActor(big)
        val exit = AIconButton(this, assets.ic_x, px(18f), bgColor = Color(0f, 0f, 0f, 0.4f), strokeColor = GameColor.white_12).apply { setBounds(h - px(56f), w - px(56f), px(40f), px(40f)) }
        exit.onClick { exitFullscreen() }
        rot.addActor(exit)
        g.addActor(rot)
        g.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = true
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) { if (event.target !== exit && !exit.isAscendantOf(event.target)) togglePlay() }
        })
        g.color.a = 0f; g.addAction(Actions.fadeIn(0.2f))
        stageUI.root.addActor(g)
        fs = g
    }

    private fun exitFullscreen() {
        val g = fs ?: return
        fs = null
        g.addAction(Actions.sequence(Actions.fadeOut(0.15f), Actions.removeActor()))
    }

    // ------------------------------------------------------------------------
    // Кадр за кадром
    // ------------------------------------------------------------------------
    override fun render(delta: Float) {
        clock += delta
        if (::overlay.isInitialized) refresh(delta)
        super.render(delta)
    }

    private fun refresh(delta: Float) {
        val dur = player.durationMs.takeIf { it > 0 } ?: video?.durationMs ?: 0L
        val pos = if (seeking) (seek.shown * dur).toLong() else player.positionMs
        if (!seeking && dur > 0) seek.set((pos.toFloat() / dur).coerceIn(0f, 1f))
        knob.setPosition(seek.x + seek.width * seek.shown - knob.width / 2f, seek.y + seek.height / 2f - knob.height / 2f)
        posL.setText(Fmt.time(pos)); posL.pack()
        durL.setText(Fmt.time(dur)); durL.pack(); durL.x = worldWidth - px(14f) - durL.width
        playIcon.drawable = TextureRegionDrawable(if (player.isPlaying) assets.ic_pause_fill else assets.ic_play_fill)
        // ховаємо оверлей, коли грає і ніхто не чіпав 3 с
        if (player.isPlaying && overlay.isVisible && overlay.color.a >= 1f && clock > hideAt && sheet == null) showOverlay(false)
        if (!player.isPlaying && !overlay.isVisible) showOverlay(true)
        if (player.isEnded && video != null) gdxGame.model.saveWatch(video.id, 0L)
        // Повернулись з реклами / з фону: відео стоїть не з нашої волі — продовжуємо
        if (!userPaused && !player.isPlaying && !player.isEnded && player.error == null && sheet == null) {
            resumeAcc += delta
            if (resumeAcc > 0.6f) { resumeAcc = 0f; player.play() }
        } else resumeAcc = 0f
        saveAcc += delta
        if (saveAcc >= 5f) { saveAcc = 0f; savePosition() }
        player.error?.let { if (clock > 1f && !errorShown) { errorShown = true; showToast("Can’t play this file · $it") } }
    }
    private var errorShown = false

    private fun savePosition() {
        val v = video ?: return
        if (v.kind == MediaKind.IMAGE || player.durationMs <= 0) return
        gdxGame.model.saveWatch(v.id, if (player.isEnded) 0L else player.positionMs)
    }

    // ------------------------------------------------------------------------
    // Вихід
    // ------------------------------------------------------------------------
    /** Системне «Назад»: шторка → повний екран → вихід з back-інтерстішалом. */
    override fun onBackPressed() {
        when {
            sheet != null -> super.onBackPressed()
            fs != null -> exitFullscreen()
            else -> closePlayer()
        }
    }

    private fun closePlayer() {
        savePosition()
        userPaused = true
        player.pause()
        // «Назад» у застосунок — back-інтерстішал; на лаунчер (Continue watching з Home) — без реклами
        val toApp = gdxGame.navigationManager.fromScreenName == AppScreen::class.java.name
        animHideScreen {
            gdxGame.navigationManager.back()
            if (toApp) gdxGame.adBack()
        }
    }

    override fun hide() {
        savePosition()
        player.release()
        super.hide()
    }

    override fun dispose() {
        player.release()
        super.dispose()
    }

    // ------------------------------------------------------------------------
    // Анімація входу: slide-up 0.3 с
    // ------------------------------------------------------------------------
    override fun animShowScreen(blockEnd: com.vidtools.framefall.game.utils.Block) {
        content.y = -worldHeight * 0.08f
        content.color.a = 0f
        content.addAction(Actions.sequence(
            Actions.parallel(Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow3Out), Actions.fadeIn(TIME_ANIM_SCREEN)),
            Actions.run(blockEnd),
        ))
    }

    override fun animHideScreen(blockEnd: com.vidtools.framefall.game.utils.Block) {
        content.touchable = Touchable.disabled
        content.addAction(Actions.sequence(
            Actions.parallel(Actions.moveTo(0f, -worldHeight * 0.06f, 0.2f, Interpolation.pow2In), Actions.fadeOut(0.2f)),
            Actions.run(blockEnd),
        ))
    }
}
