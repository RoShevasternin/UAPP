package com.redwave.downloader.game.screens.sheets

import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.actors.ui.AButtonGhost
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.screens.OnboardingScreen
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.global.IS_DEBUG
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// SettingsSheet — шестерня на Home: стан ролі HOME + «Change» (системний вибір
// головного застосунку), версія. У debug-збірці — секція Debug: повернути
// системний лаунчер (VELDAN, 07.10.2026) і скинути онбординг.
// Роль забрали → при поверненні в апку GDXGame.resume покаже екран-вимогу.
// ─────────────────────────────────────────────────────────────────────────────
class SettingsSheet(screen: AdvancedScreen) : ASheet(screen) {

    private val cardH = px(64f)
    private val btnH = px(44f)
    private lateinit var status: AMsdfLabel
    private var lastHome: Boolean? = null

    override val contentHeight: Float get() {
        var h = px(26f) + px(14f) + cardH + px(12f) + px(16f)
        if (IS_DEBUG) h += px(22f) + px(18f) + btnH * 2 + px(8f)
        return h
    }

    override fun buildContent(w: Float) {
        var y = contentHeight
        val title = lbl(Copy.Settings.TITLE, msdf.bold(17f))
        y -= px(26f); title.setPosition(0f, y + (px(26f) - title.height) / 2f); add(title)

        // ── Головний екран ──
        y -= px(14f) + cardH
        val card = object : AdvancedGroup() {
            override val screen = this@SettingsSheet.screen
            override fun addActorsOnGroup() {
                addAndFillActor(ARect(screen, px(16f), GameColor.white_4, stroke = GameColor.line_white_7))
                val ic = icon(assets.ic_home, px(20f), GameColor.text_FBF1F2); ic.setPosition(px(14f), (height - ic.height) / 2f); addActor(ic)
                val change = AButtonGhost(screen, Copy.Settings.CHANGE, small = true)
                change.setSize(change.label.width + px(26f), px(34f)); change.setPosition(width - px(12f) - change.width, (height - change.height) / 2f)
                change.onClick { gdxGame.bridge.openHomeAppSettings() }
                addActor(change)
                val t = lbl(Copy.Settings.HOME_TITLE, msdf.bold(14f))
                status = lbl("", msdf.regular(12f, GameColor.muted_A8949B))
                t.setPosition(px(46f), height / 2f + px(1f)); status.setPosition(px(46f), height / 2f - status.height - px(1f))
                addActor(t); addActor(status)
                refresh()
            }
        }.apply { setBounds(0f, y, w, cardH) }
        add(card)

        // ── Версія ──
        y -= px(12f) + px(16f)
        val ver = lbl(Copy.Settings.version(gdxGame.bridge.appVersion), msdf.mono(11f, GameColor.muted_A8949B))
        ver.setPosition(0f, y); add(ver)

        // ── Debug ──
        if (IS_DEBUG) {
            y -= px(22f) + px(18f)
            val ey = lbl(Copy.Settings.DEBUG.uppercase(), msdf.monoSemi(10.5f, GameColor.pink_FF8A98, spacing = 10f))
            ey.setPosition(0f, y + px(4f)); add(ey)
            y -= btnH
            val chooser = AButtonGhost(screen, Copy.Settings.DEBUG_CHOOSER, assets.ic_home).apply { setBounds(0f, y, w, btnH) }
            chooser.onClick { gdxGame.bridge.openHomeAppSettings() }
            add(chooser)
            y -= px(8f) + btnH
            val reset = AButtonGhost(screen, Copy.Settings.DEBUG_RESET, assets.ic_x).apply { setBounds(0f, y, w, btnH) }
            reset.onClick {
                close()
                gdxGame.model.update { it.copy(onboarded = false) }
                gdxGame.navigationManager.navigateRoot(OnboardingScreen::class.java.name)
            }
            add(reset)
        }
    }

    private var acc = 0f

    override fun act(delta: Float) {
        super.act(delta)
        acc += delta
        if (acc >= 1f && ::status.isInitialized) { acc = 0f; refresh() }   // RoleManager — binder-виклик, не щокадру
    }

    /** Стан ролі — живий: повернулись із системних налаштувань → підпис оновився. */
    private fun refresh() {
        val home = gdxGame.bridge.isDefaultHome()
        if (home == lastHome) return
        lastHome = home
        status.setText(if (home) Copy.Settings.HOME_ON else Copy.Settings.HOME_OFF)
        status.setTextColor(if (home) GameColor.ok_3DDC97 else GameColor.pink_FF8A98)
        status.pack()
    }
}
