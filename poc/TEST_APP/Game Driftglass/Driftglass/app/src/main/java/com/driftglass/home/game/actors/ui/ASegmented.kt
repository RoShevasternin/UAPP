package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.actor.ellipsize
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

/**
 * .seg: фон white 8 %, радіус 14, відступ 3; вибраний — біла пігулка з темним текстом.
 * Пункт — текст і, за бажанням, іконка над ним (стилі в Studio).
 */
class ASegmented(
    override val screen: AdvancedScreen,
    private val items: List<Pair<String, TextureRegion?>>,
    selected: Int,
    private val textSize: Float = 11f,
    private val onSelect: (Int) -> Unit,
) : AdvancedGroup() {

    var selected = selected
        private set

    override fun addActorsOnGroup() {
        addAndFillActor(ARect(screen, px(14f), GameColor.white_08))
        val pad = px(3f); val gap = px(3f)
        val iw = (width - pad * 2 - gap * (items.size - 1)) / items.size
        items.forEachIndexed { i, (title, region) ->
            val on = i == selected
            val col = if (on) GameColor.ink_071016 else GameColor.white_70
            val tab = object : ATap(screen, 0.96f) {
                override fun addContent() {
                    if (on) addAndFillActor(ARect(screen, px(11f), GameColor.white_92))
                    val t = lbl(title, msdf.semibold(textSize, col)).ellipsize(width - px(4f))
                    t.setAlignment(com.badlogic.gdx.utils.Align.center)
                    val ic = region?.let { icon(it, px(17f), col) }
                    val total = t.height + (ic?.let { it.height + px(3f) } ?: 0f)
                    var y = (height + total) / 2f
                    ic?.let { y -= it.height; it.setPosition((width - it.width) / 2f, y); addActor(it); y -= px(3f) }
                    y -= t.height; t.setPosition((width - t.width) / 2f, y); addActor(t)
                }
            }.onClick { if (selected != i) { selected = i; onSelect(i) } }
            tab.setBounds(pad + i * (iw + gap), pad, iw, height - pad * 2)
            addActor(tab)
        }
    }
}
