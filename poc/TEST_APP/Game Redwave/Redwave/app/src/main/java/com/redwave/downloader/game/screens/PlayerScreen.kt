package com.redwave.downloader.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.AIconButton
import com.redwave.downloader.game.actors.ui.AProgressBar
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.actors.ui.AVisualizer
import com.redwave.downloader.game.screens.base.RedwaveScreen
import com.redwave.downloader.game.screens.sheets.EqSheet
import com.redwave.downloader.game.screens.sheets.TrackMenuSheet
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.TIME_ANIM_SCREEN
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px
import com.redwave.downloader.game.utils.vfx.VfxImage
import com.redwave.downloader.game.utils.vfx.effects.MipBlurEffect

// ─────────────────────────────────────────────────────────────────────────────
// PlayerScreen — повноекранний плеєр: розмитий фон з обкладинки, «пульс»
// обкладинки від басів, назва/артист/♥, візуалізатор, перемотка, керування,
// панель дій (Equalizer · Ringtone · Sleep · Share). Вхід — slide-up 0.3 с.
// ─────────────────────────────────────────────────────────────────────────────
class PlayerScreen : RedwaveScreen() {

    private val p get() = gdxGame.player
    private var shownId: String? = null

    private val bg = VfxImage(this, effect = MipBlurEffect())
    private lateinit var cover: ACover
    private lateinit var coverGlow: Image
    private lateinit var title: AMsdfLabel
    private lateinit var artist: AMsdfLabel
    private lateinit var fav: AIconButton
    private lateinit var seek: AProgressBar
    private lateinit var knob: ARect
    private lateinit var knobHalo: ARect
    private lateinit var posL: AMsdfLabel
    private lateinit var leftL: AMsdfLabel
    private lateinit var playIcon: Image
    private lateinit var shuffle: AIconButton
    private lateinit var repeat: AIconButton
    private lateinit var sleepL: AMsdfLabel
    private lateinit var fromL: AMsdfLabel
    private var seeking = false

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        val side = px(20f)

        // ── Фон: розмита обкладинка + затемнення 25 % → 72 % (52 %) → фон (90 %) ──
        content.addActor(bg); bg.setBounds(-px(50f), -px(50f), w + px(100f), h + px(100f)); bg.isVisible = false
        content.addActor(ARect(this, 0f, GameColor.background.cpy().apply { a = 0.25f }, GameColor.background.cpy().apply { a = 0.72f }, angleCss = 180f, mid = 0.52f).apply { setBounds(-px(4f), h * 0.48f - px(3f), w + px(8f), h * 0.52f + px(8f)) })
        content.addActor(ARect(this, 0f, GameColor.background.cpy().apply { a = 0.72f }, GameColor.background, angleCss = 180f, mid = 0.8f).apply { setBounds(-px(4f), -px(4f), w + px(8f), h * 0.48f + px(4f)) })

        var top = h - safeStatusBarUI - px(8f)

        // ── Верх ──
        val close = AIconButton(this, assets.ic_chev_down, bgColor = Color(0f, 0f, 0f, 0.25f), strokeColor = GameColor.white_12).apply { setBounds(side, top - px(38f), px(38f), px(38f)) }
        close.onClick { closePlayer() }
        val more = AIconButton(this, assets.ic_more, bgColor = Color(0f, 0f, 0f, 0.25f), strokeColor = GameColor.white_12).apply { setBounds(w - side - px(38f), top - px(38f), px(38f), px(38f)) }
        more.onClick { p.now?.let { t -> openSheet(TrackMenuSheet(this, t) { tr -> toRingtone(tr) }) } }
        val fromCap = lbl(Copy.Player.PLAYING_FROM.uppercase(), msdf.monoSemi(9.5f, GameColor.white_60, spacing = 18f))
        fromL = lbl(p.playingFrom, msdf.bold(13f))
        fromCap.setPosition((w - fromCap.width) / 2f, top - px(19f) + px(1f)); fromL.setPosition((w - fromL.width) / 2f, top - px(19f) - fromL.height + px(2f))
        content.addActor(close); content.addActor(more); content.addActor(fromCap); content.addActor(fromL)
        top -= px(38f) + px(12f)

        // ── Низ (будуємо знизу): панель дій, керування, перемотка, візуалізатор ──
        var y = safeNavBarUI + px(14f)
        val acts = actionsPanel(w - side * 2).apply { setPosition(side, y) }
        content.addActor(acts); y += acts.height + px(12f)

        val ctrl = controls(w - side * 2).apply { setPosition(side, y) }
        content.addActor(ctrl); y += ctrl.height + px(12f)

        // перемотка: бар 5 + кружок 13 з ореолом, час під баром
        posL = lbl("0:00", msdf.monoSemi(11f, GameColor.muted_A8949B)); leftL = lbl("-0:00", msdf.monoSemi(11f, GameColor.muted_A8949B))
        posL.setPosition(side, y); leftL.setPosition(w - side - leftL.width, y)
        content.addActor(posL); content.addActor(leftL)
        y += posL.height + px(8f)
        seek = AProgressBar(this).apply { setBounds(side, y, w - side * 2, px(5f)) }
        knobHalo = ARect(this, 999f, GameColor.red_35).apply { setSize(px(21f), px(21f)); touchable = Touchable.disabled }
        knob = ARect(this, 999f, Color.WHITE).apply { setSize(px(13f), px(13f)); touchable = Touchable.disabled }
        content.addActor(seek); content.addActor(knobHalo); content.addActor(knob)
        val seekHit = object : AdvancedGroup() {
            override val screen = this@PlayerScreen
            override fun addActorsOnGroup() {}
        }.apply { setBounds(side - px(6f), y - px(14f), w - side * 2 + px(12f), px(33f)) }
        seekHit.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { seeking = true; scrub(x); return true }
            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) { scrub(x) }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                seeking = false; p.seekFraction(((x - px(6f)) / (w - side * 2)).coerceIn(0f, 1f))
            }
        })
        content.addActor(seekHit)
        y += px(5f) + px(12f)

        val viz = AVisualizer(this).apply { setBounds(side, y, w - side * 2, px(58f)) }
        content.addActor(viz); y += viz.height + px(12f)

        // ── Назва / артист / ♥ ──
        fav = AIconButton(this, assets.ic_heart)
        fav.setBounds(w - side - px(38f), y + px(4f), px(38f), px(38f))
        fav.onClick { p.now?.let { t -> gdxGame.model.toggleFavorite(t.id); refreshFav() } }
        artist = lbl("", msdf.regular(13.5f, GameColor.muted_A8949B)).ellipsize(w - side * 2 - px(50f))
        title = lbl("", msdf.bold(23f)).ellipsize(w - side * 2 - px(50f))
        artist.setPosition(side, y); title.setPosition(side, y + artist.height + px(2f))
        content.addActor(fav); content.addActor(artist); content.addActor(title)
        y += artist.height + px(2f) + title.height + px(16f)

        // ── Обкладинка: min(78 %, 268), по центру між верхом і назвою ──
        val avail = top - y
        val cs = minOf(w * 0.78f, px(268f), avail)
        val cy = y + avail / 2f
        coverGlow = Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.25f; setSize(cs * 1.9f, cs * 1.9f); setPosition((w - width) / 2f, cy - height / 2f); touchable = Touchable.disabled }
        cover = ACover(this, px(22f)).apply { setBounds((w - cs) / 2f, cy - cs / 2f, cs, cs); setOrigin(Align.center); isTransform = true }
        content.addActor(coverGlow); content.addActor(cover)

        refreshTrack(force = true)
    }

    // ------------------------------------------------------------------------
    // Блоки
    // ------------------------------------------------------------------------
    private fun controls(w: Float) = object : AdvancedGroup() {
        override val screen = this@PlayerScreen
        override fun addActorsOnGroup() {
            val cy = height / 2f
            shuffle = AIconButton(screen, assets.ic_shuffle, iconColor = GameColor.muted_A8949B, bare = true).apply { setBounds(0f, cy - px(19f), px(38f), px(38f)) }
            shuffle.onClick { p.toggleShuffle() }
            val pw = width; val ph = height; repeat = AIconButton(screen, assets.ic_repeat, iconColor = GameColor.muted_A8949B, bare = true).apply { setBounds(pw - px(38f), cy - px(19f), px(38f), px(38f)) }
            repeat.onClick { p.toggleRepeat() }
            val prev = AIconButton(screen, assets.ic_prev_fill, px(26f), bare = true).apply { setBounds(pw * 0.27f - px(23f), cy - px(23f), px(46f), px(46f)) }
            prev.onClick { p.previous() }
            val next = AIconButton(screen, assets.ic_next_fill, px(26f), bare = true).apply { setBounds(pw * 0.73f - px(23f), cy - px(23f), px(46f), px(46f)) }
            next.onClick { p.next() }
            val play = object : ATap(screen, 0.93f) {
                override fun addContent() {
                    val pw = width; val ph = height; addActor(Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.65f; setBounds(-pw * 0.35f, -ph * 0.6f, pw * 1.7f, ph * 1.5f); touchable = Touchable.disabled })
                    addAndFillActor(ARect(screen, 999f, GameColor.gradTop_FF5468, GameColor.gradBot_E3002B))
                    playIcon = icon(assets.ic_play_fill, px(28f)); playIcon.setPosition((width - playIcon.width) / 2f, (height - playIcon.height) / 2f)
                    addActor(playIcon)
                }
            }.onClick { p.toggle() }
            play.setBounds((width - px(68f)) / 2f, cy - px(34f), px(68f), px(68f))
            addActor(shuffle); addActor(prev); addActor(play); addActor(next); addActor(repeat)
        }
    }.apply { setSize(w, px(68f)) }

    private fun actionsPanel(w: Float) = object : AdvancedGroup() {
        override val screen = this@PlayerScreen
        override fun addActorsOnGroup() {
            addAndFillActor(ARect(screen, px(18f), Color(1f, 1f, 1f, 0.045f), stroke = GameColor.line_white_7))
            val items: List<Pair<TextureRegion, String>> = listOf(
                assets.ic_sliders to Copy.Player.EQUALIZER, assets.ic_scissors to Copy.Player.RINGTONE,
                assets.ic_moon to Copy.Player.SLEEP, assets.ic_share to Copy.Player.SHARE,
            )
            val pad = px(6f); val gap = px(4f)
            val iw = (width - pad * 2 - gap * 3) / 4f
            items.forEachIndexed { i, (ic, label) ->
                val l = lbl(label, msdf.semibold(10.5f))
                if (i == 2) sleepL = l
                val b = object : ATap(screen, 0.95f) {
                    override fun addContent() {
                        val im = icon(ic, px(19f), GameColor.text_FBF1F2)
                        val tot = im.height + px(4f) + l.height * 0.85f
                        im.setPosition((width - im.width) / 2f, (height + tot) / 2f - im.height)
                        l.setPosition((width - l.width) / 2f, im.y - px(4f) - l.height * 0.92f)
                        addActor(im); addActor(l)
                    }
                }.onClick { action(i) }
                b.setBounds(pad + i * (iw + gap), pad, iw, height - pad * 2)
                addActor(b)
            }
        }
    }.apply { setSize(w, px(60f)) }

    private fun action(i: Int) {
        when (i) {
            0 -> openSheet(EqSheet(this))
            1 -> p.now?.let { toRingtone(it) }
            2 -> { p.cycleSleep(); refreshSleep() }
            3 -> p.now?.let { gdxGame.bridge.shareTrack(it) }
        }
    }

    private fun toRingtone(t: Track) {
        gdxGame.ringtoneTrackId = t.id
        animHideScreen { gdxGame.navigationManager.navigateRoot(AppScreen::class.java.name, AppScreen.TAB_RINGTONE) }
    }

    // ------------------------------------------------------------------------
    // Оновлення
    // ------------------------------------------------------------------------
    override fun render(delta: Float) {
        if (::cover.isInitialized) refresh()
        super.render(delta)
    }

    private fun refresh() {
        refreshTrack(false)
        val s = p.snap
        val dur = s.durationMs.takeIf { it > 0 } ?: p.now?.durationMs ?: 0L
        if (!seeking) seek.set(p.progress)
        val kx = seek.x + seek.width * seek.shown
        knob.setPosition(kx - knob.width / 2f, seek.y + seek.height / 2f - knob.height / 2f)
        knobHalo.setPosition(kx - knobHalo.width / 2f, seek.y + seek.height / 2f - knobHalo.height / 2f)
        val pos = if (seeking) (seek.shown * dur).toLong() else s.positionMs
        posL.setText(Fmt.time(pos)); posL.pack()
        leftL.setText("-" + Fmt.time((dur - pos).coerceAtLeast(0))); leftL.pack(); leftL.x = worldWidth - px(20f) - leftL.width
        playIcon.drawable = TextureRegionDrawable(if (p.isPlaying) assets.ic_pause_fill else assets.ic_play_fill)
        shuffle.ic.color.set(if (s.shuffle) GameColor.redHi_FF5A6C else GameColor.muted_A8949B)
        repeat.ic.color.set(if (s.repeatOne) GameColor.redHi_FF5A6C else GameColor.muted_A8949B)
        // «пульс»: scale 1 + bass·0.035, світіння 0.25 + bass·0.4
        val bass = if (p.isPlaying) gdxGame.bridge.spectrum.bass() else 0f
        cover.setScale(1f + bass * 0.035f)
        coverGlow.color.a = 0.25f + bass * 0.4f
    }

    private fun scrub(x: Float) {
        seek.set(((x - px(6f)) / seek.width).coerceIn(0f, 1f), immediate = true)
    }

    private fun refreshTrack(force: Boolean) {
        val t = p.now
        if (!force && t?.id == shownId) return
        shownId = t?.id
        if (t == null) { closePlayer(); return }
        title.setText(t.title); artist.setText(t.artist)
        cover.setTrack(t)
        fromL.setText(p.playingFrom); fromL.pack(); fromL.x = (worldWidth - fromL.width) / 2f
        gdxGame.covers.forTrack(t) { tex ->
            if (shownId != t.id) return@forTrack
            if (tex != null) { bg.drawable = TextureRegionDrawable(ACover.coverRegion(tex, bg.width, bg.height)); bg.isVisible = true }
            else bg.isVisible = false
        }
        refreshFav(); refreshSleep()
    }

    private fun refreshFav() {
        val on = p.now?.let { gdxGame.model.isFavorite(it.id) } ?: false
        fav.ic.color.set(if (on) GameColor.red_FF2E4D else GameColor.text_FBF1F2)
        fav.bg.stroke(if (on) GameColor.red_FF2E4D.cpy().apply { a = 0.4f } else GameColor.line_white_7)
    }

    private fun refreshSleep() {
        val m = p.sleepMinutes
        sleepL.setText(if (m == 0) Copy.Player.SLEEP else Copy.Player.sleepMin(m)); sleepL.pack()
        sleepL.x = (sleepL.parent.width - sleepL.width) / 2f
    }

    private fun closePlayer() {
        animHideScreen { gdxGame.navigationManager.back() }
    }

    // ------------------------------------------------------------------------
    // Анімація входу: slide-up 0.3 с
    // ------------------------------------------------------------------------
    override fun animShowScreen(blockEnd: com.redwave.downloader.game.utils.Block) {
        content.y = -worldHeight * 0.12f
        content.color.a = 0f
        content.addAction(Actions.sequence(
            Actions.parallel(Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow3Out), Actions.fadeIn(TIME_ANIM_SCREEN)),
            Actions.run(blockEnd),
        ))
    }

    override fun animHideScreen(blockEnd: com.redwave.downloader.game.utils.Block) {
        content.touchable = Touchable.disabled
        content.addAction(Actions.sequence(
            Actions.parallel(Actions.moveTo(0f, -worldHeight * 0.08f, 0.2f, Interpolation.pow2In), Actions.fadeOut(0.2f)),
            Actions.run(blockEnd),
        ))
    }
}
