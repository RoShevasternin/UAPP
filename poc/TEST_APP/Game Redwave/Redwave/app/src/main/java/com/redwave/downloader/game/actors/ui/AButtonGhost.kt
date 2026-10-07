package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

/** .btn-ghost: рамка white 14 %, фон white 4 %, Onest 700/14.5. */
class AButtonGhost(
    screen: AdvancedScreen,
    text: String,
    private val iconRegion: TextureRegion? = null,
    private val small: Boolean = false,
) : ATap(screen) {

    private val bg = ARect(screen, px(if (small) 12f else 15f), GameColor.white_4, stroke = GameColor.white_14)
    val label: AMsdfLabel = lbl(text, msdf.bold(if (small) 13f else 14.5f))
    private val ic = iconRegion?.let { icon(it, px(16f), GameColor.text_FBF1F2) }

    override fun addContent() {
        addAndFillActor(bg)
        addActor(label)
        ic?.let { addActor(it) }
        layoutContent()
    }

    fun setContent(text: String, region: TextureRegion? = null) {
        label.setText(text); label.pack()
        if (region != null) ic?.drawable = com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(region)
        layoutContent()
    }

    private fun layoutContent() {
        val gap = px(8f)
        val w = label.width + (ic?.let { it.width + gap } ?: 0f)
        var x = (width - w) / 2f
        ic?.let { it.setPosition(x, (height - it.height) / 2f); x += it.width + gap }
        label.setPosition(x, (height - label.height) / 2f)
    }
}
