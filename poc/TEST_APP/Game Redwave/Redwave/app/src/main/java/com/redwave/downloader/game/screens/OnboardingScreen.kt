package com.redwave.downloader.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.game.actors.ui.AButtonRed
import com.redwave.downloader.game.actors.ui.AChip
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.AEqBars
import com.redwave.downloader.game.actors.ui.AProgressBar
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.screens.base.RedwaveScreen
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px
import kotlin.math.abs

// ─────────────────────────────────────────────────────────────────────────────
// OnboardingScreen — 3 слайди: фото з Ken Burns, скляна картка, що «пливе»,
// крок, заголовок, опис, крапки + «→» (слайди 1–2) або CTA ролі HOME (3).
// Свайп (поріг 45) гортає, «Назад» — попередній слайд.
// Роль HOME обов'язкова (рішення VELDAN 07.10.2026): Skip веде на 3-й слайд, «Maybe later»
// немає; відмова — лишаємось тут з поясненням. KEY_GATE — той самий екран як вимога, коли
// роль забрали (GDXGame.navigateFirst / resume): одразу 3-й слайд.
// ─────────────────────────────────────────────────────────────────────────────
class OnboardingScreen : RedwaveScreen() {

    companion object {
        /** NavigationManager.key: показати лише вимогу ролі (онбординг уже пройдено). */
        const val KEY_GATE = 1
    }

    private val gate = gdxGame.navigationManager.key == KEY_GATE
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
        showSlide(if (gate) Copy.Onboarding.SLIDES.lastIndex else 0, animate = false)
    }

    private fun go(i: Int) {
        val n = i.coerceIn(0, Copy.Onboarding.SLIDES.lastIndex)
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
        val w = worldWidth; val h = worldHeight
        val last = i == Copy.Onboarding.SLIDES.lastIndex
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
        if (!last) {
            val skip = object : ATap(this) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, 999f, GameColor.skip_0A0506, stroke = GameColor.white_18))
                    val l = lbl(Copy.Onboarding.SKIP, msdf.bold(12.5f, Color.WHITE))
                    addActor(l); l.setPosition((width - l.width) / 2f, (height - l.height) / 2f)
                }
            }.onClick { go(Copy.Onboarding.SLIDES.lastIndex) }
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
        if (last) {
            val hint = lbl(if (declined) Copy.Onboarding.HOME_DECLINED else Copy.Onboarding.HOME_REQUIRED,
                msdf.regular(12.5f, if (declined) GameColor.pink_FF8A98 else GameColor.muted_A8949B), com.badlogic.gdx.utils.Align.center).apply {
                setWrap(true); width = w - side * 2; height = prefHeight
            }
            hint.setPosition(side, y); g.addActor(hint); y += hint.height + px(12f)
            val setHome = AButtonRed(this, Copy.Onboarding.SET_HOME, assets.ic_home).apply { setSize(w - side * 2, px(48f)) }
            setHome.onClick { askRole() }
            setHome.setPosition(side, y); g.addActor(setHome); y += setHome.height + px(12f)
            val dots = dots(i); dots.setPosition(side, y); g.addActor(dots); y += dots.height + px(18f)
        } else {
            val next = object : ATap(this, 0.94f) {
                override fun addContent() {
                    val pw = width; val ph = height; addActor(Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.6f; setBounds(-pw * 0.3f, -ph * 0.55f, pw * 1.6f, ph * 1.4f) })
                    addAndFillActor(ARect(screen, 999f, GameColor.gradTop_FF5468, GameColor.gradBot_E3002B))
                    val ic = icon(assets.ic_chev_right, px(24f)); addActor(ic); ic.setPosition((width - ic.width) / 2f, (height - ic.height) / 2f)
                }
            }.onClick { go(index + 1) }
            next.setSize(px(60f), px(60f))
            next.setPosition(w - side - next.width, y)
            g.addActor(next)
            val dots = dots(i); dots.setPosition(side, y + (next.height - dots.height) / 2f); g.addActor(dots)
            y += next.height + px(8f) + px(10f)
        }

        val body = lbl(data.body, msdf.regular(14f, GameColor.muted_A8949B)).apply {
            setLineHeight(118f)
            setWrap(true)
            width = minOf(px(232f), w - side * 2)
            height = prefHeight
        }
        body.setPosition(side, y); g.addActor(body); y += body.height + px(10f)

        val accent = lbl(data.accent, msdf.disp(31f, GameColor.redHi_FF5A6C))
        accent.setPosition(side, y); g.addActor(accent); y += accent.height * 0.92f
        val line1 = lbl(data.line1, msdf.disp(31f))
        line1.setPosition(side, y); g.addActor(line1); y += line1.height + px(10f)

        val stepRed = lbl("0${i + 1}", msdf.monoSemi(11.5f, GameColor.redHi_FF5A6C, spacing = 14f))
        val stepRest = lbl(" / 03", msdf.monoSemi(11.5f, GameColor.muted_A8949B, spacing = 14f))
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
                val d = ARect(screen, 999f, if (on) GameColor.red_FF2E4D else GameColor.handleOff)
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
        val pad = px(14f)
        val inner = cw - pad * 2
        val ch = when (i) { 0 -> px(13f + 18f + 9f + 6f + 9f + 14f + 13f); 1 -> px(13f + 26f + 9f + 44f + 13f); else -> px(13f + 16f + 7f + 20f + 7f + 16f + 9f + 34f + 13f) }

        return object : AdvancedGroup() {
            override val screen = this@OnboardingScreen
            override fun addActorsOnGroup() {
                addAndFillActor(ARect(screen, px(20f), GameColor.glass_160A0E, stroke = GameColor.white_14))
                var top = height - px(13f)
                when (i) {
                    0 -> {
                        val ic = icon(assets.ic_link, px(16f), GameColor.redHi_FF5A6C)
                        val url = lbl("cdn.lofi-loops.net/night-drive.mp3", msdf.monoSemi(12f, GameColor.flText_FFD5CC)).ellipsize(inner - px(24f))
                        top -= px(18f)
                        ic.setPosition(pad, top + (px(18f) - ic.height) / 2f); url.setPosition(pad + px(24f), top + (px(18f) - url.height) / 2f)
                        addActor(ic); addActor(url)
                        top -= px(9f) + px(6f)
                        val bar = LoopBar(screen).apply { setBounds(pad, top, inner, px(6f)) }
                        addActor(bar)
                        top -= px(9f) + px(14f)
                        val meta = lbl("MP3 · 4.8 MB", msdf.semibold(11f, GameColor.muted_A8949B))
                        val pct = lbl(Copy.Onboarding.FLOAT_DOWNLOADING, msdf.semibold(11f, GameColor.pink_FF8A98))
                        meta.setPosition(pad, top); pct.setPosition(width - pad - pct.width, top)
                        addActor(meta); addActor(pct)
                    }
                    1 -> {
                        val chip = AChip(screen, Copy.Onboarding.FLOAT_NO_WIFI, AChip.Kind.GREEN, assets.ic_wifi_off)
                        top -= chip.height; chip.setPosition(pad, top); addActor(chip)
                        top -= px(9f) + px(44f)
                        var x = pad
                        listOf("128" to "tracks", "2.4" to "GB saved").forEach { (b, s) ->
                            val big = lbl(b, msdf.style(gdxGame.msdfManager.fontArchivo_ExpandedExtraBold, 22f) { letterSpacing = -1f })
                            val small = lbl(s, msdf.regular(11f, GameColor.muted_A8949B))
                            small.setPosition(x, top); big.setPosition(x, top + small.height * 0.9f)
                            addActor(big); addActor(small)
                            x += maxOf(big.width, small.width) + px(16f)
                        }
                        val pw = width; val ph = height; val eq = AEqBars(screen, px(3f), px(2f), GameColor.red_FF2E4D).apply { setBounds(pw - pad - px(30f), top + px(6f), px(30f), px(26f)) }
                        addActor(eq)
                    }
                    else -> {
                        top -= px(16f)
                        val ic = icon(assets.ic_clip, px(14f), GameColor.pink_FF8A98)
                        val ey = lbl(Copy.Onboarding.FLOAT_CLIP_EYEBROW.uppercase(), msdf.style(gdxGame.msdfManager.fontJetBrainsMono_SemiBold, 10.5f, GameColor.pink_FF8A98) { letterSpacing = 10f })
                        ic.setPosition(pad, top + (px(16f) - ic.height) / 2f); ey.setPosition(pad + px(20f), top + (px(16f) - ey.height) / 2f)
                        addActor(ic); addActor(ey)
                        top -= px(7f) + px(20f)
                        val b = lbl(Copy.Onboarding.FLOAT_CLIP_TITLE, msdf.bold(15f)); b.setPosition(pad, top); addActor(b)
                        top -= px(7f) + px(16f)
                        val u = lbl("drive.google.com/file/d/1A2b3C…", msdf.mono(11.5f, GameColor.muted_A8949B)).ellipsize(inner)
                        u.setPosition(pad, top); addActor(u)
                        top -= px(9f) + px(34f)
                        val btn = AButtonRed(screen, Copy.Home.DOWNLOAD, assets.ic_download, small = true, glow = false).apply { setBounds(pad, top, inner, px(34f)); touchable = com.badlogic.gdx.scenes.scene2d.Touchable.disabled }
                        addActor(btn)
                    }
                }
            }
        }.apply { setSize(cw, ch) }
    }

    /** .fl-bar: смуга 6, заповнення 6 % → 100 % за 3.2 с по колу (80–100 % — пауза на повній). */
    private class LoopBar(screen: RedwaveScreen) : AdvancedGroup() {
        override val screen = screen
        private val bar = AProgressBar(screen, GameColor.red_FF2E4D, GameColor.coral_FF9A7A, GameColor.white_12).apply { smooth = false }
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
                showSlide(Copy.Onboarding.SLIDES.lastIndex, animate = false)
                gdxGame.toast(Copy.Onboarding.HOME_DECLINED)
            }
        }
    }

    /** Лише з наданою роллю: далі — звичайний перший екран (AppScreen). */
    private fun finish() {
        gdxGame.model.update { it.copy(onboarded = true) }
        animHideScreen {
            gdxGame.navigateFirst()
            com.redwave.downloader.game.utils.runGDX { gdxGame.toast(Copy.Role.NOW_HOME) }   // уже на новому екрані
        }
    }

    override fun onBackPressed() {
        if (index > 0) go(index - 1) else gdxGame.navigationManager.exit()
    }
}
