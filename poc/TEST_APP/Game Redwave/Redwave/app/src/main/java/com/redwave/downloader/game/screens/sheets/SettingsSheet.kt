package com.redwave.downloader.game.screens.sheets

import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.actors.ui.AToggle
import com.redwave.downloader.game.screens.OnboardingScreen
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.global.IS_DEBUG
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// SettingsSheet — шестерня на Home (VELDAN, 07.10.2026):
//   • перемикач «Use as Home screen»: увімкнути — системний діалог ролі; вимкнути —
//     системний екран вибору головного застосунку (забрати роль сама апка не може);
//   • Privacy Policy — для людей і модератора Google Play (адреса з RemoteFlags.privacy);
//   • версія; у debug-збірці — скинути онбординг і що прийшло з Remote Config.
// Роль обов'язкова (home_required) і її забрали → на наступному вході екран-вимога (GDXGame.resume).
// ─────────────────────────────────────────────────────────────────────────────
class SettingsSheet(screen: AdvancedScreen) : ASheet(screen) {

    private val cardH = px(64f)
    private val rowH = px(48f)
    private val btnH = px(44f)
    private val required = gdxGame.flags.homeRequired
    private val noteH = if (required) px(24f) else 0f
    private lateinit var status: AMsdfLabel
    private lateinit var toggle: AToggle
    private var lastHome: Boolean? = null
    private var debugLine: AMsdfLabel? = null

    override val contentHeight: Float get() {
        var h = px(26f) + px(14f) + cardH + noteH + px(10f) + rowH + px(12f) + px(16f)
        if (IS_DEBUG) h += px(22f) + px(18f) + cardH + px(8f) + btnH + px(10f) + px(64f)
        return h
    }

    override fun buildContent(w: Float) {
        var y = contentHeight
        val title = lbl(Copy.Settings.TITLE, msdf.bold(17f))
        y -= px(26f); title.setPosition(0f, y + (px(26f) - title.height) / 2f); add(title)

        // ── Головний екран: перемикач ──
        y -= px(14f) + cardH
        val card = object : ATap(screen, 0.985f) {
            override fun addContent() {
                addAndFillActor(ARect(screen, px(16f), GameColor.white_4, stroke = GameColor.line_white_7))
                val ic = icon(assets.ic_home, px(20f), GameColor.text_FBF1F2); ic.setPosition(px(14f), (height - ic.height) / 2f); addActor(ic)
                toggle = AToggle(screen, gdxGame.bridge.isDefaultHome())
                toggle.setPosition(width - px(14f) - toggle.width, (height - toggle.height) / 2f)
                addActor(toggle)
                val t = lbl(Copy.Settings.HOME_TITLE, msdf.bold(14f))
                status = lbl("", msdf.regular(12f, GameColor.muted_A8949B))
                t.setPosition(px(46f), height / 2f + px(1f)); status.setPosition(px(46f), height / 2f - status.height - px(1f))
                addActor(t); addActor(status)
                refresh()
            }
        }.onClick { switchHome() }
        card.setBounds(0f, y, w, cardH)
        add(card)
        if (required) {
            y -= noteH
            val note = lbl(Copy.Settings.HOME_REQUIRED_NOTE, msdf.regular(11.5f, GameColor.muted_A8949B))
            note.setPosition(px(4f), y + (noteH - note.height) / 2f - px(2f)); add(note)
        }

        // ── Privacy Policy ──
        y -= px(10f) + rowH
        val privacy = object : ATap(screen, 0.985f) {
            override fun addContent() {
                addAndFillActor(ARect(screen, px(14f), GameColor.white_4))
                val ic = icon(assets.ic_shield, px(18f), GameColor.text_FBF1F2); ic.setPosition(px(14f), (height - ic.height) / 2f)
                val l = lbl(Copy.Settings.PRIVACY, msdf.semibold(14f)); l.setPosition(px(46f), (height - l.height) / 2f)
                val ch = icon(assets.ic_chev_right, px(16f), GameColor.muted_A8949B); ch.setPosition(width - px(14f) - ch.width, (height - ch.height) / 2f)
                addActor(ic); addActor(l); addActor(ch)
            }
        }.onClick { gdxGame.bridge.openUrl(gdxGame.flags.privacy) }
        privacy.setBounds(0f, y, w, rowH)
        add(privacy)

        // ── Версія ──
        y -= px(12f) + px(16f)
        val ver = lbl(Copy.Settings.version(gdxGame.bridge.appVersion), msdf.mono(11f, GameColor.muted_A8949B))
        ver.setPosition(0f, y); add(ver)

        // ── Debug ──
        if (IS_DEBUG) {
            y -= px(22f) + px(18f)
            val ey = lbl(Copy.Settings.DEBUG.uppercase(), msdf.monoSemi(10.5f, GameColor.pink_FF8A98, spacing = 10f))
            ey.setPosition(0f, y + px(4f)); add(ey)
            // AD_MODE: «чорний» режим поверх Remote Config, перемикання перезапускає апку
            y -= cardH
            val adOn = gdxGame.bridge.isAdMode()
            val adTgl = AToggle(screen, adOn)
            val ad = object : ATap(screen, 0.985f) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, px(16f), GameColor.white_4, stroke = GameColor.line_white_7))
                    val t = lbl(Copy.Settings.DEBUG_AD_MODE, msdf.monoSemi(13f, GameColor.pink_FF8A98))
                    val st = lbl(if (adOn) Copy.Settings.DEBUG_AD_ON else Copy.Settings.DEBUG_AD_OFF, msdf.regular(11.5f, GameColor.muted_A8949B))
                    t.setPosition(px(14f), height / 2f + px(1f)); st.setPosition(px(14f), height / 2f - st.height - px(1f))
                    adTgl.setPosition(width - px(14f) - adTgl.width, (height - adTgl.height) / 2f)
                    addActor(t); addActor(st); addActor(adTgl)
                }
            }.onClick {
                adTgl.isOn = !adOn
                gdxGame.toast("Restarting…")
                com.badlogic.gdx.utils.Timer.schedule(object : com.badlogic.gdx.utils.Timer.Task() {
                    override fun run() { gdxGame.bridge.setAdMode(!adOn) }
                }, 0.35f)
            }
            ad.setBounds(0f, y, w, cardH); add(ad)
            y -= px(8f) + btnH
            val reset = AButtonGhost(screen, Copy.Settings.DEBUG_RESET, assets.ic_x).apply { setBounds(0f, y, w, btnH) }
            reset.onClick {
                close()
                gdxGame.model.update { it.copy(onboarded = false) }
                gdxGame.navigationManager.navigateRoot(OnboardingScreen::class.java.name)
            }
            add(reset)
            y -= px(10f) + px(64f)
            val dl = lbl(debugText(), msdf.mono(10.5f, GameColor.muted_A8949B)).apply {
                setWrap(true); width = w; height = px(64f)
                setAlignment(com.badlogic.gdx.utils.Align.topLeft)
            }
            dl.setPosition(0f, y); add(dl)
            debugLine = dl
        }
    }

    /** Remote Config + стан реклами (лічильник треків, останній показ, чи завантажено). */
    private fun debugText() = gdxGame.bridge.remoteFlagsDebug() + "\n" + gdxGame.bridge.adsDebug()

    /** Увімкнути — діалог ролі; вимкнути — системний вибір головного застосунку (роль забирає лише система). */
    private fun switchHome() {
        if (gdxGame.bridge.isDefaultHome()) {
            gdxGame.toast(Copy.Settings.PICK_PREVIOUS)
            gdxGame.bridge.openHomeAppSettings()
        } else {
            gdxGame.bridge.requestDefaultHome { isHome -> if (isHome) gdxGame.toast(Copy.Role.NOW_HOME); refresh() }
        }
    }

    private var acc = 0f

    override fun act(delta: Float) {
        super.act(delta)
        acc += delta
        if (acc >= 1f && ::status.isInitialized) {        // RoleManager — binder-виклик, не щокадру
            acc = 0f; refresh()
            debugLine?.setText(debugText())
        }
    }

    /** Стан ролі — живий: повернулись із системних налаштувань → перемикач і підпис оновились. */
    private fun refresh() {
        val home = gdxGame.bridge.isDefaultHome()
        if (home == lastHome) return
        lastHome = home
        if (toggle.isOn != home) toggle.isOn = home
        status.setText(if (home) Copy.Settings.HOME_ON else Copy.Settings.HOME_OFF)
        status.setTextColor(if (home) GameColor.ok_3DDC97 else GameColor.muted_A8949B)
        status.pack()
    }
}
