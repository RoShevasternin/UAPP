package com.driftglass.home.game.screens.tabs

import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.model.Saver
import com.driftglass.home.core.model.Settings
import com.driftglass.home.game.actors.layout.AColumn
import com.driftglass.home.game.screens.AppScreen
import com.driftglass.home.game.screens.LauncherScreen
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.screens.ui.sectionLabel
import com.driftglass.home.game.screens.ui.settingsRow
import com.driftglass.home.game.utils.PRIVACY_URL
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.global.IS_DEBUG
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px
import com.driftglass.home.util.log

// ─────────────────────────────────────────────────────────────────────────────
// SettingsTab: головний екран (роль, подвійний дотик, паралакс, підписи, сітка),
// батарея (економія), екран блокування (кадр), про апку, debug (AD_MODE). Мови немає — лише англійська.
// Вимкнути роль = системний екран вибору лаунчера (самі ми її не знімаємо — Android не дає).
// ─────────────────────────────────────────────────────────────────────────────
class SettingsTab(app: AppScreen) : TabPage(app) {

    private var lastRole: Boolean? = null

    override fun build(col: AColumn) {
        val s = gdxGame.model.state.settings
        val home = gdxGame.bridge.isDefaultHome()
        lastRole = home
        col.addActor(header(L.settings))

        section(col, L.secHome)
        col.addActor(settingsRow(app, W, L.useHome, if (home) L.roleOn else L.roleOff, toggle = home) {
            if (home) gdxGame.bridge.openHomeAppSettings()
            else gdxGame.bridge.requestDefaultHome { isHome ->
                if (isHome) { gdxGame.toast(L.tRole); gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name) } else rebuild()
            }
        })
        col.addActor(settingsRow(app, W, L.doubleTap, null, toggle = s.doubleTap) { set { it.copy(doubleTap = !it.doubleTap) } })
        col.addActor(settingsRow(app, W, L.parallax, L.parallaxSub, toggle = s.parallax) { set { it.copy(parallax = !it.parallax) } })
        col.addActor(settingsRow(app, W, L.labels, null, toggle = s.labels) { set { it.copy(labels = !it.labels) } })
        col.addActor(settingsRow(app, W, L.grid, L.cols(s.cols), value = "${s.cols}") { set { it.copy(cols = if (it.cols == 4) 5 else 4) } })

        section(col, L.secPower)
        val saverSub = when (s.saver) { Saver.AUTO -> L.saverAuto; Saver.ON -> L.saverOn; Saver.OFF -> L.vOff }
        val saverVal = when (s.saver) { Saver.AUTO -> L.vAuto; Saver.ON -> L.vOn; Saver.OFF -> L.vOff }
        col.addActor(settingsRow(app, W, L.saver, saverSub, value = saverVal) {
            set { it.copy(saver = when (it.saver) { Saver.AUTO -> Saver.ON; Saver.ON -> Saver.OFF; Saver.OFF -> Saver.AUTO }) }
        })

        section(col, L.secLock)
        col.addActor(settingsRow(app, W, L.lockStill, L.lockSub, toggle = s.lockStill) { set { it.copy(lockStill = !it.lockStill) } })

        section(col, L.secAbout)
        col.addActor(settingsRow(app, W, L.privacy, null, value = "›") { gdxGame.bridge.openUrl(PRIVACY_URL) })
        col.addActor(settingsRow(app, W, L.version, gdxGame.bridge.appVersion) {})

        // ── Debug: AD_MODE як у Redwave (реклама на Home, роль обов'язкова, без Uninstall), перемикання перезапускає апку ──
        if (IS_DEBUG) {
            section(col, "DEBUG")
            val adOn = gdxGame.flags.isAdMode
            col.addActor(settingsRow(app, W, "AD_MODE", if (adOn) "On · ad on Home, Home required, no uninstall" else "Off · flags = default", toggle = adOn) {
                gdxGame.toast("Restarting…")
                com.badlogic.gdx.utils.Timer.schedule(object : com.badlogic.gdx.utils.Timer.Task() {
                    override fun run() { gdxGame.bridge.setAdMode(!adOn) }
                }, 0.35f)
            })
            val dl = lbl(gdxGame.bridge.flagsDebug(), msdf.label(10f, GameColor.white_55, spacing = 0f)).apply { setWrap(true); width = W - px(8f); height = prefHeight }
            col.addActor(group(app, W, dl.height + px(6f)) { dl.setPosition(px(4f), 0f); addActor(dl) })
        }
    }

    private fun section(col: AColumn, title: String) {
        val l = sectionLabel(title)
        col.addActor(group(app, W, l.height + px(8f)) { l.setPosition(px(4f), 0f); addActor(l) })
    }

    private fun set(block: (Settings) -> Settings) {
        gdxGame.model.update { it.copy(settings = block(it.settings)) }
        log("Settings → ${gdxGame.model.state.settings}")
        rebuild()
    }

    override fun onResumed() {
        // повернулись із системного вибору лаунчера — роль могла змінитись
        if (lastRole != gdxGame.bridge.isDefaultHome()) { lastRole = gdxGame.bridge.isDefaultHome(); rebuild() }
    }
}
