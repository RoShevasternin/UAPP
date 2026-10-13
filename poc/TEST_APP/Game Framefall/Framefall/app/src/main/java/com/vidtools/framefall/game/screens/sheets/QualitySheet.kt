package com.vidtools.framefall.game.screens.sheets

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ASheet
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.controller.DownloadController.Submit
import com.vidtools.framefall.game.utils.Fmt
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

/**
 * Сторінка твору (Internet Archive / NASA) → вибір файлу: «360p · 640×360 · MP4 · H.264 · 61.9 MB».
 * Менші — зверху (мобільний інтернет). Тап — у чергу, шторка закривається.
 */
class QualitySheet(screen: AdvancedScreen, private val choose: Submit.Choose, private val onSaved: () -> Unit = {}) : ASheet(screen) {

    private val rowH = px(52f)
    private val files = choose.item.files
    override val contentHeight: Float get() = px(22f) + px(6f) + px(16f) + px(14f) + files.size * rowH + (files.size - 1) * px(6f)

    override fun buildContent(w: Float) {
        var y = contentHeight
        val title = lbl(choose.item.title, msdf.bold(17f)).ellipsize(w)
        title.setPosition(0f, y - px(22f)); add(title); y -= px(22f) + px(6f)
        val sub = lbl("${choose.host} · ${Copy.Quality.SUB}", msdf.regular(12f, GameColor.muted_B3A293)).ellipsize(w)
        sub.setPosition(0f, y - px(16f)); add(sub); y -= px(16f) + px(14f)

        files.forEach { f ->
            y -= rowH
            val row = object : ATap(screen, 0.98f) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, px(14f), GameColor.white_4, stroke = GameColor.line_white_7))
                    val big = lbl(f.label, msdf.bold(15f)); big.setPosition(px(14f), height / 2f - px(1f))
                    val meta = lbl(listOf(f.dim, f.format).filter { it.isNotBlank() }.joinToString(" · "), msdf.regular(11.5f, GameColor.muted_B3A293))
                    meta.setPosition(px(14f), height / 2f - meta.height + px(2f))
                    val ic = icon(assets.ic_download, px(18f), GameColor.accent_FF8A2A)
                    ic.setPosition(width - px(14f) - ic.width, (height - ic.height) / 2f)
                    val size = lbl(if (f.sizeBytes > 0) Fmt.size(f.sizeBytes) else "", msdf.monoSemi(12f, GameColor.apricot_FFC98F))
                    size.setPosition(ic.x - px(10f) - size.width, (height - size.height) / 2f)
                    addActor(big); addActor(meta); addActor(size); addActor(ic)
                }
            }.onClick {
                gdxGame.downloads.saveChosen(choose, f)
                close(); onSaved()
            }
            row.setBounds(0f, y, w, rowH); add(row)
            y -= px(6f)
        }
    }
}
