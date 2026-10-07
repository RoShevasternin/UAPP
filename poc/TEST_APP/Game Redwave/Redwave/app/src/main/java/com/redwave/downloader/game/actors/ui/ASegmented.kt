package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

/**
 * .seg: фон card, рамка, радіус 14, відступ 4; вибраний — card2 + рамка redHi 30 %.
 * Підпис 700/12.5 muted (вибраний — білий) + лічильник mono 600/11 з альфою .75.
 */
class ASegmented(
    override val screen: AdvancedScreen,
    private val items: List<Pair<String, String?>>,
    selected: Int,
    private val onSelect: (Int) -> Unit,
) : AdvancedGroup() {

    var selected = selected
        private set

    override fun addActorsOnGroup() {
        addAndFillActor(ARect(screen, px(14f), GameColor.card_170F12, stroke = GameColor.line_white_7))
        val pad = px(4f); val gap = px(4f)
        val iw = (width - pad * 2 - gap * (items.size - 1)) / items.size
        items.forEachIndexed { i, (title, count) ->
            val on = i == selected
            val tab = object : ATap(screen, 0.97f) {
                override fun addContent() {
                    if (on) addAndFillActor(ARect(screen, px(10f), GameColor.card2_21161A, stroke = GameColor.redHi_30))
                    val t = lbl(title, msdf.bold(12.5f, if (on) Color.WHITE else GameColor.muted_A8949B))
                    val c = count?.let { lbl(it, msdf.monoSemi(11f, (if (on) Color.WHITE else GameColor.muted_A8949B).cpy().apply { a = 0.75f })) }
                    val w = t.width + (c?.let { it.width + px(4f) } ?: 0f)
                    t.setPosition((width - w) / 2f, (height - t.height) / 2f); addActor(t)
                    c?.let { it.setPosition(t.x + t.width + px(4f), (height - it.height) / 2f); addActor(it) }
                }
            }.onClick { if (selected != i) { selected = i; onSelect(i) } }
            tab.setBounds(pad + i * (iw + gap), pad, iw, height - pad * 2)
            addActor(tab)
        }
    }
}
