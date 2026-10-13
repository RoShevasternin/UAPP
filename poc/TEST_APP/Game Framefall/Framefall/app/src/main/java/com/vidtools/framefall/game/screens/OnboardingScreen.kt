package com.vidtools.framefall.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.game.actors.ui.AButtonAccent
import com.vidtools.framefall.game.actors.ui.AChip
import com.vidtools.framefall.game.actors.ui.ACover
import com.vidtools.framefall.game.actors.ui.AProgressBar
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px
import kotlin.math.abs

// ─────────────────────────────────────────────────────────────────────────────
// OnboardingScreen — 3 слайди (кадр фільму з Ken Burns, скляна картка, що «пливе», крок,
// заголовок, опис, крапки + «→») і 4-й екран — роль HOME («Make Framefall your Home screen»:
// прев'ю відеофону, три пункти, CTA). Свайп (поріг 45) гортає, «Назад» — попередній.
// Роль: AppFlags.homeRequired з картки сервера (app_open = custom*, рішення VELDAN 12.10.2026):
//   true  — лише «Set as Home screen»; відмова — лишаємось тут з поясненням;
//   false — під нею вторинна текстова «Maybe later» (видна, але не кричить) → апка без ролі.
// Skip веде на екран ролі. KEY_GATE — той самий екран як вимога, коли роль обов'язкова, а її
// немає (GDXGame.navigateFirst / resume).
// ─────────────────────────────────────────────────────────────────────────────
class OnboardingScreen : FramefallScreen() {

    companion object {
        /** NavigationManager.key: показати лише вимогу ролі (онбординг уже пройдено). */
        const val KEY_GATE = 1
        /** Індекс екрана ролі — одразу після слайдів. */
        private val ROLE = Copy.Onboarding.SLIDES.size
    }

    private val gate = gdxGame.navigationManager.key == KEY_GATE
    val isGate get() = gate
    /** Чи обов'язкова роль — з картки сервера (AppFlags); змінився на льоту → onFlagsChanged перебудовує слайд. */
    private var required = gdxGame.flags.homeRequired
    private var index = 0
    /** Користувач відмовив у системному діалозі — підказка стає червоною. */
    private var declined = false
    private var slide: AdvancedGroup? = null

    private val bottomPad get() = safeNavBarUI + px(22f)

    override fun buildContent() {
        // Свайп по всьому екрану
        content.addListener(object : InputListener() {
            var sx = 0f; var sy = 0f
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { sx = x; sy = y; return true }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                val dx = x - sx
                if (abs(dx) > px(45f) && abs(dx) > abs(y - sy)) go(index + if (dx < 0) 1 else -1)
            }
        })
        showSlide(if (gate) ROLE else 0, animate = false)
    }

    private fun go(i: Int) {
        val n = i.coerceIn(if (gate) ROLE else 0, ROLE)
        if (n != index) showSlide(n, animate = true)
    }

    private fun showSlide(i: Int, animate: Boolean) {
        index = i
        slide?.let { old -> old.addAction(Actions.sequence(Actions.fadeOut(0.15f), Actions.removeActor())) }
        val s = object : AdvancedGroup() {
            override val screen = this@OnboardingScreen
            override fun addActorsOnGroup() { buildSlide(this, i) }
        }
        content.addActor(s)
        s.setSize(worldWidth, worldHeight)
        if (animate) {
            s.color.a = 0f; s.y = -px(10f)
            s.addAction(Actions.parallel(Actions.fadeIn(0.32f), Actions.moveTo(0f, 0f, 0.32f, Interpolation.pow3Out)))
        }
        slide = s
    }

    // ------------------------------------------------------------------------
    // Слайд
    // ------------------------------------------------------------------------
    private fun buildSlide(g: AdvancedGroup, i: Int) {
        if (i == ROLE) { buildRole(g); return }
        val w = worldWidth; val h = worldHeight
        val data = Copy.Onboarding.SLIDES[i]

        // ── Фото 62 % висоти, Ken Burns 1.14 → 1 за 14 с ──
        val photoH = h * 0.62f
        val photo = Image(TextureRegionDrawable(ACover.coverRegion(assets.listOnboarding[i], w, photoH))).apply {
            setBounds(0f, h - photoH, w, photoH)
            setOrigin(Align.center)
            setScale(1.14f)
            addAction(Actions.scaleTo(1f, 1f, 14f, Interpolation.pow2Out))
        }
        g.addActor(photo)
        // ── Фейд у фон: від 28 до 63 % висоти ──
        g.addActor(Image(assets.FADE_V).apply {
            color.set(GameColor.background)
            setBounds(0f, h - h * 0.63f, w, h * 0.35f)
        })
        g.addActor(Image(screenWhite()).apply { color.set(GameColor.background); setBounds(0f, 0f, w, h * 0.37f + 1f) })

        // ── Skip ──
        run {
            val skip = object : ATap(this) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, 999f, GameColor.skip_0A0705, stroke = GameColor.white_18))
                    val l = lbl(Copy.Onboarding.SKIP, msdf.bold(12.5f, Color.WHITE))
                    addActor(l); l.setPosition((width - l.width) / 2f, (height - l.height) / 2f)
                }
            }.onClick { go(ROLE) }
            val sl = lbl(Copy.Onboarding.SKIP, msdf.bold(12.5f))
            skip.setSize(sl.width + px(28f), px(30f))
            skip.setPosition(w - px(16f) - skip.width, h - safeStatusBarUI - px(10f) - skip.height)
            g.addActor(skip)
        }

        // ── Скляна картка (33 % від верху) ──
        val card = floatCard(i)
        card.setPosition((w - card.width) / 2f, h - h * 0.33f - card.height)
        card.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f, px(6f), 2.25f, Interpolation.sine), Actions.moveBy(0f, -px(6f), 2.25f, Interpolation.sine))))
        g.addActor(card)

        // ── Низ: крок, заголовок, опис, футер (будуємо знизу вгору) ──
        val side = px(22f)
        var y = bottomPad
        val next = object : ATap(this, 0.94f) {
            override fun addContent() {
                val pw = width; val ph = height; addActor(Image(assets.GLOW).apply { color.set(GameColor.accent_FF8A2A); color.a = 0.6f; setBounds(-pw * 0.3f, -ph * 0.55f, pw * 1.6f, ph * 1.4f) })
                addAndFillActor(ARect(screen, 999f, GameColor.gradTop_FFC94D, GameColor.gradBot_F2511B))
                val ic = icon(assets.ic_chev_right, px(24f), GameColor.onAccent_1A0E05); addActor(ic); ic.setPosition((width - ic.width) / 2f, (height - ic.height) / 2f)
            }
        }.onClick { go(index + 1) }
        next.setSize(px(60f), px(60f))
        next.setPosition(w - side - next.width, y)
        g.addActor(next)
        val dots = dots(i); dots.setPosition(side, y + (next.height - dots.height) / 2f); g.addActor(dots)
        y += next.height + px(8f) + px(10f)

        val body = lbl(data.body, msdf.regular(14f, GameColor.muted_B3A293)).apply {
            setLineHeight(118f)
            setWrap(true)
            width = minOf(px(232f), w - side * 2)
            height = prefHeight
        }
        body.setPosition(side, y); g.addActor(body); y += body.height + px(10f)

        val accent = lbl(data.accent, msdf.disp(31f, GameColor.amber_FFC94D))
        accent.setPosition(side, y); g.addActor(accent); y += accent.height * 0.92f
        val line1 = lbl(data.line1, msdf.disp(31f))
        line1.setPosition(side, y); g.addActor(line1); y += line1.height + px(10f)

        val stepRed = lbl("0${i + 1}", msdf.monoSemi(11.5f, GameColor.amber_FFC94D, spacing = 14f))
        val stepRest = lbl(" / 03", msdf.monoSemi(11.5f, GameColor.muted_B3A293, spacing = 14f))
        stepRed.setPosition(side, y); stepRest.setPosition(side + stepRed.width, y)
        g.addActor(stepRed); g.addActor(stepRest)
    }

    private fun screenWhite() = drawerUtil.whiteRegion

    /** .dots: активна 24×7 червона, інші 7×7 білі 22 %, gap 6. */
    private fun dots(active: Int) = object : AdvancedGroup() {
        override val screen = this@OnboardingScreen
        override fun addActorsOnGroup() {
            var x = 0f
            for (k in Copy.Onboarding.SLIDES.indices) {
                val on = k == active
                val d = ARect(screen, 999f, if (on) GameColor.accent_FF8A2A else GameColor.handleOff)
                d.setBounds(x, 0f, px(if (on) 24f else 7f), px(7f))
                addActor(d)
                x += d.width + px(6f)
            }
        }
    }.apply { setSize(px(24f + 7f + 7f + 12f), px(7f)) }

    // ------------------------------------------------------------------------
    // Скляні картки
    // ------------------------------------------------------------------------
    private fun floatCard(i: Int): AdvancedGroup {
        val cw = minOf(px(290f), worldWidth - px(44f))
        val pad = px(12f)
        val inner = cw - pad * 2
        val ch = when (i) { 0 -> px(12f + 48f + 10f + 36f + 12f); 1 -> px(118f); else -> px(12f + 46f + 12f) }

        return object : AdvancedGroup() {
            override val screen = this@OnboardingScreen
            override fun addActorsOnGroup() {
                if (i != 1) addAndFillActor(ARect(screen, px(20f), GameColor.glass_171009, stroke = GameColor.white_14))
                when (i) {
                    // Картка скопійованого лінка: кадр + «Copied link · archive.org» + Save video
                    0 -> {
                        val ph = height
                        val th = com.vidtools.framefall.game.actors.ui.AVideoThumb(screen, px(10f), showDuration = false).apply { setBounds(pad, ph - pad - px(48f), px(86f), px(48f)) }
                        th.setAsset("th_ed", 0); addActor(th)
                        val mx = pad + px(86f + 10f); val mw = width - mx - pad
                        val cap = lbl(Copy.Onboarding.FLOAT_COPIED, msdf.monoSemi(9.5f, GameColor.ccEye_FFD59A)).ellipsize(mw)
                        val t = lbl(Copy.Onboarding.FLOAT_TITLE, msdf.bold(14f)).ellipsize(mw)
                        val m = lbl(Copy.Onboarding.FLOAT_META, msdf.regular(11f, GameColor.muted_B3A293)).ellipsize(mw)
                        val cy = th.y + th.height / 2f
                        t.setPosition(mx, cy - t.height / 2f); cap.setPosition(mx, t.y + t.height - px(3f)); m.setPosition(mx, t.y - m.height + px(3f))
                        addActor(cap); addActor(t); addActor(m)
                        val btn = AButtonAccent(screen, Copy.Onboarding.FLOAT_SAVE, assets.ic_download, small = true, glow = false).apply { setBounds(pad, pad, inner, px(36f)); touchable = com.badlogic.gdx.scenes.scene2d.Touchable.disabled }
                        addActor(btn)
                    }
                    // Годинник поверх живого фону
                    1 -> {
                        val now = java.util.Date()
                        val c = lbl(java.text.SimpleDateFormat("H:mm", java.util.Locale.US).format(now), msdf.clock(54f).apply { dropShadow(0f, 2f, 10f, Color(0f, 0f, 0f, 0.5f)) })
                        val d = lbl(java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.US).format(now), msdf.regular(13f, GameColor.white_75))
                        d.setPosition((width - d.width) / 2f, px(8f)); c.setPosition((width - c.width) / 2f, d.y + d.height)
                        addActor(c); addActor(d)
                    }
                    // Смуга кадрів з ручками (Studio)
                    else -> {
                        val sw = inner; val sh = px(46f)
                        val strip = com.vidtools.framefall.game.actors.ui.ACover(screen, px(8f)).apply { setBounds(pad, pad, sw, sh) }
                        addActor(strip); strip.setAsset("th_llama")
                        addActor(ARect(screen, px(8f), Color(0f, 0f, 0f, 0.6f)).apply { setBounds(pad, pad, sw * 0.22f, sh) })
                        addActor(ARect(screen, px(8f), Color(0f, 0f, 0f, 0.6f)).apply { setBounds(pad + sw * 0.7f, pad, sw * 0.3f, sh) })
                        addActor(ARect(screen, px(3f), GameColor.amber_FFC94D).apply { setBounds(pad + sw * 0.22f - px(5f), pad - px(2f), px(10f), sh + px(4f)) })
                        addActor(ARect(screen, px(3f), GameColor.amber_FFC94D).apply { setBounds(pad + sw * 0.7f - px(5f), pad - px(2f), px(10f), sh + px(4f)) })
                    }
                }
            }
        }.apply { setSize(cw, ch) }
    }

    // ------------------------------------------------------------------------
    // Екран ролі HOME
    // ------------------------------------------------------------------------
    private fun buildRole(g: AdvancedGroup) {
        val w = worldWidth; val h = worldHeight; val side = px(22f)
        g.addActor(Image(screenWhite()).apply { color.set(GameColor.background); setBounds(0f, 0f, w, h) })
        g.addActor(Image(assets.GLOW).apply { color.set(GameColor.accent_FF8A2A); color.a = 0.22f; setBounds(-w * 0.3f, h * 0.45f, w * 1.6f, h * 0.7f) })

        var y = h - safeStatusBarUI - px(22f)
        val ey = lbl(Copy.Onboarding.ROLE_EYEBROW.uppercase(), msdf.monoSemi(11f, GameColor.amber_FFC94D, spacing = 14f))
        y -= ey.height; ey.setPosition(side, y); g.addActor(ey)
        val t1 = lbl(Copy.Onboarding.ROLE_TITLE_1, msdf.disp(27f)); val t2 = lbl(Copy.Onboarding.ROLE_TITLE_2, msdf.disp(27f))
        y -= px(6f) + t1.height; t1.setPosition(side, y); y -= t2.height * 0.92f; t2.setPosition(side, y)
        g.addActor(t1); g.addActor(t2)

        // Прев'ю: петля Orion у картці з плашкою «Copied a video link · Save»
        val pw = w - side * 2; val ph = minOf(pw * 0.56f, h * 0.24f)
        y -= px(16f) + ph
        rolePlayer?.release()
        val pl = gdxGame.bridge.createVideoPlayer().also { rolePlayer = it }
        val vid = com.vidtools.framefall.game.actors.ui.AVideo(this, pl, com.vidtools.framefall.game.actors.ui.AVideo.Fit.COVER, radius = px(18f)).apply { setBounds(side, y, pw, ph); poster = assets.LAUNCHER_BG; focusY = 0.12f }
        g.addActor(vid)
        pl.open(gdxGame.bridge.builtInBackground(com.vidtools.framefall.core.model.HomeBg.ORION), autoplay = true, loop = true, muted = true)
        val pillH = px(30f)
        g.addActor(ARect(this, 999f, Color(0f, 0f, 0f, 0.55f), stroke = GameColor.white_14).apply { setBounds(side + px(10f), y + px(10f), pw - px(20f), pillH) })
        val pillL = lbl(Copy.Onboarding.FLOAT_ROLE, msdf.semibold(11.5f)); pillL.setPosition(side + px(22f), y + px(10f) + (pillH - pillL.height) / 2f); g.addActor(pillL)
        g.addActor(icon(assets.ic_download, px(15f), Color.WHITE).apply { setPosition(side + pw - px(34f), y + px(10f) + (pillH - height) / 2f) })

        // Три пункти
        val icons = listOf(assets.ic_clip, assets.ic_film, assets.ic_play_fill)
        Copy.Onboarding.ROLE_POINTS.forEachIndexed { k, (title, body) ->
            val tx = side + px(42f); val tw = w - tx - side
            val b = lbl(body, msdf.regular(12.5f, GameColor.muted_B3A293)).apply { setWrap(true); width = tw; height = prefHeight }
            val t = lbl(title, msdf.bold(14f))
            y -= px(14f) + t.height
            t.setPosition(tx, y); y -= b.height - px(2f); b.setPosition(tx, y)
            val box = ARect(this, px(9f), GameColor.accent_14).apply { setBounds(side, t.y + t.height - px(30f), px(30f), px(30f)) }
            val ic = icon(icons[k], px(15f), GameColor.amber_FFC94D); ic.setPosition(box.x + (px(30f) - ic.width) / 2f, box.y + (px(30f) - ic.height) / 2f)
            g.addActor(box); g.addActor(ic); g.addActor(t); g.addActor(b)
        }

        // ── Низ: підказка, «Maybe later», CTA ──
        var by = bottomPad
        val hintText = when {
            required && declined -> Copy.Onboarding.HOME_DECLINED
            required             -> Copy.Onboarding.HOME_REQUIRED
            declined             -> Copy.Onboarding.HOME_DECLINED_OPTIONAL
            else                 -> Copy.Onboarding.HOME_OPTIONAL
        }
        val hint = lbl(hintText, msdf.regular(12f, if (declined && required) GameColor.err_FF7A6B else GameColor.muted_B3A293), Align.center).apply {
            setWrap(true); width = w - side * 2; height = prefHeight
        }
        hint.setPosition(side, by); g.addActor(hint); by += hint.height + px(4f)
        if (!required) {
            val later = object : ATap(this, 0.96f) {
                override fun addContent() {
                    val l = lbl(Copy.Onboarding.MAYBE_LATER, msdf.semibold(14f, GameColor.muted_B3A293))
                    addActor(l); l.setPosition((width - l.width) / 2f, (height - l.height) / 2f)
                }
            }.onClick { skipRole() }
            later.setBounds((w - px(180f)) / 2f, by, px(180f), px(44f))
            g.addActor(later); by += later.height + px(2f)
        } else by += px(8f)
        val setHome = AButtonAccent(this, Copy.Onboarding.SET_HOME, assets.ic_home).apply { setBounds(side, by, w - side * 2, px(50f)) }
        setHome.onClick { askRole() }
        g.addActor(setHome)

        // Debug: вийти з AD_MODE прямо з екрана-вимоги (до Settings без ролі не дістатись)
        if (com.vidtools.framefall.game.utils.global.IS_DEBUG && gdxGame.bridge.isAdMode()) {
            val off = object : ATap(this) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, 999f, GameColor.skip_0A0705, stroke = GameColor.white_18))
                    val l = lbl("AD_MODE OFF", msdf.monoSemi(11f, GameColor.err_FF7A6B))
                    addActor(l); l.setPosition((width - l.width) / 2f, (height - l.height) / 2f)
                }
            }.onClick { gdxGame.toast("Restarting…"); gdxGame.bridge.setAdMode(false) }
            off.setSize(px(118f), px(30f))
            off.setPosition(w - side - off.width, h - safeStatusBarUI - px(10f) - off.height)
            g.addActor(off)
        }
    }

    private var rolePlayer: com.vidtools.framefall.game.platform.VideoPlayer? = null

    override fun hide() { rolePlayer?.release(); rolePlayer = null; super.hide() }
    override fun dispose() { rolePlayer?.release(); rolePlayer = null; super.dispose() }

    /** .fl-bar: смуга 6, заповнення 6 % → 100 % за 3.2 с по колу (80–100 % — пауза на повній). */
    private class LoopBar(screen: FramefallScreen) : AdvancedGroup() {
        override val screen = screen
        private val bar = AProgressBar(screen, GameColor.accent_FF8A2A, GameColor.coral_FFB15C, GameColor.white_12).apply { smooth = false }
        private var t = 0f
        override fun addActorsOnGroup() { addAndFillActor(bar) }
        override fun act(delta: Float) {
            super.act(delta)
            t = (t + delta) % 3.2f
            val p = (t / (3.2f * 0.8f)).coerceAtMost(1f)
            bar.set(0.06f + 0.94f * Interpolation.smooth.apply(p), immediate = true)
        }
    }

    // ------------------------------------------------------------------------
    // Дії
    // ------------------------------------------------------------------------
    private fun askRole() {
        gdxGame.bridge.requestDefaultHome { isHome ->
            if (isHome) finish()
            else {
                declined = true
                showSlide(ROLE, animate = false)
                if (required) gdxGame.toast(Copy.Onboarding.HOME_DECLINED)
            }
        }
    }

    /** «Maybe later» (лише home_required = false): онбординг пройдено, роль — пізніше в Settings. */
    private fun skipRole() {
        gdxGame.model.update { it.copy(onboarded = true) }
        animHideScreen { gdxGame.navigateFirst() }
    }

    /** Режим змінився, поки ми тут (GDXGame.onFlagsChanged): показати / сховати «Maybe later». */
    fun onFlagsChanged() {
        val r = gdxGame.flags.homeRequired
        if (r == required) return
        required = r
        if (index == ROLE) showSlide(index, animate = false)
    }

    /** З наданою роллю: далі — звичайний перший екран (AppScreen). */
    private fun finish() {
        gdxGame.model.update { it.copy(onboarded = true) }
        animHideScreen {
            gdxGame.navigateFirst()
            com.vidtools.framefall.game.utils.runGDX { gdxGame.toast(Copy.Role.NOW_HOME) }   // уже на новому екрані
        }
    }

    override fun onBackPressed() {
        if (index > 0 && !gate) go(index - 1) else gdxGame.navigationManager.exit()
    }
}
