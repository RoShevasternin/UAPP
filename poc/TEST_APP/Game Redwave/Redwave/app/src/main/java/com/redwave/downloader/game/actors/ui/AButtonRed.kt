package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AButtonRed — .btn-red: градієнт 135° gradTop→gradBot, радіус 15, світіння
// під кнопкою (box-shadow 0 12px 26px -12px red), іконка 18 + Onest 700/15.
// sm-варіант: радіус 12, шрифт 13, іконка 16.
// ─────────────────────────────────────────────────────────────────────────────
class AButtonRed(
    screen: AdvancedScreen,
    text: String,
    private val iconRegion: TextureRegion? = null,
    private val small: Boolean = false,
    private val glow: Boolean = true,
) : ATap(screen) {

    private val bg = ARect(screen, px(if (small) 12f else 15f), GameColor.gradTop_FF5468, GameColor.gradBot_E3002B)
    private val shine = ARect(screen, px(if (small) 12f else 15f), Color(1f, 1f, 1f, 0.16f), Color(1f, 1f, 1f, 0f), angleCss = 180f, mid = 0.5f)
    private val glowImg = Image(assets.GLOW).apply { color.set(GameColor.red_FF2E4D); color.a = 0.55f; touchable = Touchable.disabled }
    val label: AMsdfLabel = lbl(text, msdf.bold(if (small) 13f else 15f, Color.WHITE))
    private val ic = iconRegion?.let { icon(it, px(if (small) 16f else 18f)) }

    override fun addContent() {
        if (glow) {
            addActor(glowImg)
            glowImg.setBounds(width * 0.08f, -height * 0.55f, width * 0.84f, height * 1.25f)
        }
        addAndFillActors(bg, shine)
        addActor(label)
        ic?.let { addActor(it) }
        layoutContent()
    }

    fun setText(text: String) { label.setText(text); label.pack(); layoutContent() }

    private fun layoutContent() {
        val gap = px(8f)
        val w = label.width + (ic?.let { it.width + gap } ?: 0f)
        var x = (width - w) / 2f
        ic?.let { it.setPosition(x, (height - it.height) / 2f); x += it.width + gap }
        label.setPosition(x, (height - label.height) / 2f)
    }
}
