package com.vidtools.framefall.game.screens.sheets

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.actors.AScrollPane
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.ui.ASheet
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

/** Studio → Change: вибір відео з бібліотеки (лише відео, найновіші зверху). */
class PickVideoSheet(screen: AdvancedScreen, private val onPick: (Video) -> Unit) : ASheet(screen) {

    private val list = gdxGame.model.videos
    private val rowH = px(52f)
    override val contentHeight: Float get() = px(24f) + px(12f) + (list.size.coerceAtMost(6) * (rowH + px(6f)))

    override fun buildContent(w: Float) {
        val title = lbl(Copy.Studio.PICK_TITLE, msdf.bold(17f))
        title.setPosition(0f, contentHeight - px(22f)); add(title)
        val col = AColumn(screen, px(6f), padTop = 0f, padBottom = 0f, padX = 0f)
        col.setSize(w, 1f)
        list.forEach { v ->
            col.addActor(videoRow(screen, v, v.title, videoInfo(v)) { close(); onPick(v) }.apply { setSize(w, rowH) })
        }
        val sc = AScrollPane(col)
        sc.setBounds(0f, 0f, w, contentHeight - px(24f) - px(12f))
        add(sc)
    }
}
