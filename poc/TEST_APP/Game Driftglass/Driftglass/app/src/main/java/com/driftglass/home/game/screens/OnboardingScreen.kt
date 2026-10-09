package com.driftglass.home.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.model.Catalog
import com.driftglass.home.game.actors.ui.AButton
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.wallpaper.AWallpaper
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.screens.ui.glass
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.screens.ui.tap
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.global.IS_DEBUG
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px
import com.driftglass.home.util.log

// ─────────────────────────────────────────────────────────────────────────────
// OnboardingScreen — 3 сторінки над живими шпалерами (Northern Hush / Ember Drift /
// Sahara Dusk), далі — чесне пояснення ролі «Додому» з «Можливо, пізніше».
// Роль просимо лише кнопкою; системний діалог показує Android (RoleManager).
// AD_MODE (AppFlags.homeRequired): без «Maybe later», а вже пройдений онбординг без ролі
// одразу показує пояснення як екран-вимогу; debug — кнопка AD_MODE OFF зліва вгорі.
// ─────────────────────────────────────────────────────────────────────────────
class OnboardingScreen : DgScreen() {

    private val pages = listOf(
        Triple({ L.onb1t }, { L.onb1d }, "northern-hush"),
        Triple({ L.onb2t }, { L.onb2d }, "ember-drift"),
        Triple({ L.onb3t }, { L.onb3d }, "sahara-dusk"),
    )
    private var page = 0
    private lateinit var wall: AWallpaper
    private var panel: AdvancedGroup? = null
    private val required = gdxGame.flags.homeRequired
    /** Онбординг уже пройдено, але роль обов'язкова й її немає — лише пояснення. */
    private val requirementOnly = required && gdxGame.model.state.onboarded

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        wall = AWallpaper(this).apply { setBounds(0f, 0f, w, h) }
        disposableSet += wall
        content.addActor(wall)
        // linear-gradient(180deg, transparent 38%, rgba(4,7,10,.82))
        content.addActor(ARect(this, 0f, Color(0.016f, 0.027f, 0.04f, 0f), Color(0.016f, 0.027f, 0.04f, 0.85f), angleCss = 180f, start = 0.38f).apply {
            setBounds(0f, 0f, w, h); touchable = com.badlogic.gdx.scenes.scene2d.Touchable.disabled
        })
        if (requirementOnly) showExplain(instant = true) else showPage(0, instant = true)
        if (required && IS_DEBUG && gdxGame.bridge.flags().isAdMode) addAdModeOff()
    }

    /** Debug: вийти з AD_MODE прямо з екрана-вимоги (до Settings без ролі не дістатись). */
    private fun addAdModeOff() {
        val l = lbl("AD_MODE OFF", msdf.label(11f, GameColor.danger_FF8A8A))
        val off = tap(this, l.width + px(28f), px(30f)) {
            addAndFillActor(ARect(this@OnboardingScreen, 999f, GameColor.black_55, stroke = GameColor.white_20))
            l.setPosition((width - l.width) / 2f, (height - l.height) / 2f); addActor(l)
        }.apply { setOnClickListener { gdxGame.toast("Restarting…"); gdxGame.bridge.setAdMode(false) } }
        off.setPosition(px(16f), worldHeight - safeStatusBarUI - px(10f) - off.height)
        content.addActor(off)
    }

    private fun showPage(i: Int, instant: Boolean = false) {
        page = i
        wall.show(Catalog.byId(pages[i].third)!!, instant)
        val w = worldWidth
        val side = px(20f)
        val cw = w - side * 2
        val title = lbl(pages[i].first(), msdf.disp(32f)).apply { setWrap(true); width = cw; height = prefHeight }
        val body = lbl(pages[i].second(), msdf.regular(14.5f, GameColor.muted)).apply { setWrap(true); width = cw; height = prefHeight }
        val btnH = px(48f)
        val total = px(7f) + px(14f) + title.height + px(12f) + body.height + px(18f) + btnH
        val p = group(this, w, total + safeNavBarUI + px(30f)) {
            var y = height
            y -= px(7f)
            val dots = group(this@OnboardingScreen, px(60f), px(7f)) {
                var x = 0f
                for (k in 0..2) {
                    val dw = if (k == i) px(22f) else px(7f)
                    addActor(ARect(this@OnboardingScreen, 999f, if (k == i) Color.WHITE else GameColor.white_35).apply { setBounds(x, 0f, dw, px(7f)) })
                    x += dw + px(6f)
                }
            }
            dots.setPosition(side, y); addActor(dots)
            y -= px(14f) + title.height; title.setPosition(side, y); addActor(title)
            y -= px(12f) + body.height; body.setPosition(side, y); addActor(body)
            y -= px(18f) + btnH
            val btn = AButton(this@OnboardingScreen, if (i < 2) L.next else L.cont, AButton.Kind.WHITE)
            btn.setBounds(side, y, cw, btnH)
            btn.setOnClickListener { if (page < 2) showPage(page + 1) else showExplain() }
            addActor(btn)
        }
        replacePanel(p)
    }

    private fun replacePanel(p: AdvancedGroup) {
        panel?.let { old -> old.touchable = com.badlogic.gdx.scenes.scene2d.Touchable.disabled; old.addAction(Actions.sequence(Actions.fadeOut(0.15f), Actions.removeActor())) }
        panel = p
        p.setPosition(0f, 0f)
        content.addActor(p)
        p.color.a = 0f; p.y = -px(14f)
        p.addAction(Actions.parallel(Actions.fadeIn(0.3f), Actions.moveTo(0f, 0f, 0.35f, Interpolation.pow3Out)))
    }

    /** Чесне пояснення: що буде, як повернути, «Можливо, пізніше». */
    private fun showExplain(instant: Boolean = false) {
        page = 3
        wall.show(Catalog.byId("lagoon-glass")!!, instant)
        val w = worldWidth
        val side = px(14f)
        val cw = w - side * 2
        val pad = px(20f)
        val iw = cw - pad * 2
        val title = lbl(L.exTitle, msdf.title(21f)).apply { setWrap(true); width = iw; height = prefHeight }
        val lines = listOf(L.ex1, L.ex2, L.ex3).map { t ->
            lbl(t, msdf.regular(13f, GameColor.white_92.cpy().apply { a = 0.82f })).apply { setWrap(true); width = iw - px(31f); height = prefHeight }
        }
        val btnH = px(46f)
        val laterH = if (required) 0f else px(36f)
        val cardH = pad + px(44f) + px(12f) + title.height + px(12f) + lines.sumOf { (it.height + px(12f)).toDouble() }.toFloat() + btnH + px(6f) + laterH + px(12f)
        val p = group(this, w, cardH + safeNavBarUI + px(22f)) {
            val card = group(this@OnboardingScreen, cw, cardH) {
                addAndFillActor(glass(this@OnboardingScreen, px(26f)))
                var y = height - pad - px(44f)
                val hb = group(this@OnboardingScreen, px(44f), px(44f)) {
                    addAndFillActor(ARect(this@OnboardingScreen, px(14f), GameColor.white_08, stroke = GameColor.white_14))
                    val ic = icon(assets.ic_home, px(20f), GameColor.teal_5FF2D1); ic.setPosition((width - ic.width) / 2f, (height - ic.height) / 2f); addActor(ic)
                }
                hb.setPosition(pad, y); addActor(hb)
                val opt = lbl(if (required) L.required else L.optional, msdf.label(10f, GameColor.teal_5FF2D1, spacing = 12f))
                opt.setPosition(width - pad - opt.width, y + (px(44f) - opt.height) / 2f); addActor(opt)
                y -= px(12f) + title.height; title.setPosition(pad, y); addActor(title)
                y -= px(12f)
                lines.forEach { l ->
                    y -= l.height
                    val ic = icon(assets.ic_check, px(18f), GameColor.teal_5FF2D1); ic.setPosition(pad, y + l.height - ic.height); addActor(ic)
                    l.setPosition(pad + px(31f), y); addActor(l)
                    y -= px(12f)
                }
                y -= btnH
                val set = AButton(this@OnboardingScreen, L.setHome, AButton.Kind.TEAL)
                set.setBounds(pad, y, iw, btnH); set.setOnClickListener { askRole() }; addActor(set)
                if (!required) {
                    y -= px(6f) + laterH
                    val later = tap(this@OnboardingScreen, iw, laterH) {
                        val l = lbl(L.later, msdf.semibold(14f, GameColor.white_70)); l.setPosition((width - l.width) / 2f, (height - l.height) / 2f); addActor(l)
                    }.apply { setOnClickListener { later() } }
                    later.setPosition(pad, y); addActor(later)
                }
            }
            card.setPosition(side, safeNavBarUI + px(22f)); addActor(card)
        }
        replacePanel(p)
    }

    private fun askRole() {
        gdxGame.bridge.requestDefaultHome { isHome ->
            log("onboarding: role result isHome=$isHome")
            if (isHome) {
                gdxGame.model.update { it.copy(onboarded = true) }
                gdxGame.toast(L.tRole)
                gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name)
            } else if (required) {
                gdxGame.toast(L.needHome)
            }
        }
    }

    private fun later() {
        log("onboarding: Maybe later — працюємо без ролі")
        gdxGame.model.update { it.copy(onboarded = true) }
        gdxGame.navigationManager.navigateRoot(AppScreen::class.java.name)
    }

    override fun onBackPressed() {
        when {
            requirementOnly                -> gdxGame.bridge.moveToBack()   // екран-вимога: лише сховати
            page == 3                      -> showPage(2)
            page > 0 && panel != null      -> showPage(page - 1)
            else                           -> super.onBackPressed()
        }
    }

    @Suppress("unused") private val unusedImage: Image? = null
}
