package com.vidtools.framefall.game.screens.sheets

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.HomeBg
import com.vidtools.framefall.game.actors.ui.ASheet
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

/** ⋮ на головному екрані: вбудовані петлі (Orion, Sintel), своя петля зі Studio, «Make a loop in Studio». */
class HomeBgSheet(screen: AdvancedScreen, private val onChanged: () -> Unit) : ASheet(screen) {

    private val rowH = px(52f)
    private val current get() = gdxGame.model.state.homeBg
    private val hasOwn get() = !HomeBg.isBuiltIn(current)
    private val rows get() = 2 + (if (hasOwn) 1 else 0) + 1
    override val contentHeight: Float get() = px(24f) + px(12f) + rows * rowH + (rows - 1) * px(6f)

    override fun buildContent(w: Float) {
        var y = contentHeight
        val title = lbl(Copy.Launcher.BG_TITLE, msdf.bold(17f)); title.setPosition(0f, y - px(22f)); add(title)
        y -= px(24f) + px(12f)
        fun mark(on: Boolean) = if (on) " · ${Copy.Launcher.BG_CURRENT}" else ""
        val list = buildList {
            add(videoRow(screen, null, Copy.Launcher.BG_ORION + mark(current == HomeBg.ORION), "NASA · public domain", asset = "th_orion") { set(HomeBg.ORION) })
            add(videoRow(screen, null, Copy.Launcher.BG_SINTEL + mark(current == HomeBg.SINTEL), "Blender Foundation · CC BY 3.0", asset = "th_sintel") { set(HomeBg.SINTEL) })
            if (hasOwn) add(videoRow(screen, null, Copy.Launcher.BG_YOURS + mark(true), "Made in Studio", asset = null) { close() })
            add(optionRow(screen, assets.ic_wand, Copy.Launcher.BG_MAKE) { close(); gdxGame.openStudio("loop") })
        }
        list.forEach { r -> y -= rowH; r.setBounds(0f, y, w, rowH); add(r); y -= px(6f) }
    }

    private fun set(key: String) {
        gdxGame.model.update { it.copy(homeBg = key) }
        close(); onChanged()
    }
}
