package com.vidtools.framefall.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AButtonAccent — .btn-p: градієнт 135° бурштин → полум'я, радіус 15, світіння
// під кнопкою (box-shadow помаранчевий), темна іконка 18 + Onest 700/15 (onAccent).
// sm-варіант: радіус 12, шрифт 13, іконка 16.
// ─────────────────────────────────────────────────────────────────────────────
class AButtonAccent(
    screen: AdvancedScreen,
    text: String,
    private val iconRegion: TextureRegion? = null,
    private val small: Boolean = false,
    private val glow: Boolean = true,
) : ATap(screen) {

    private val bg = ARect(screen, px(if (small) 12f else 15f), GameColor.gradTop_FFC94D, GameColor.gradBot_F2511B)
    private val shine = ARect(screen, px(if (small) 12f else 15f), Color(1f, 1f, 1f, 0.22f), Color(1f, 1f, 1f, 0f), angleCss = 180f, mid = 0.5f)
    private val glowImg = Image(assets.GLOW).apply { color.set(GameColor.accent_FF8A2A); color.a = 0.55f; touchable = Touchable.disabled }
    val label: AMsdfLabel = lbl(text, msdf.bold(if (small) 13f else 15f, GameColor.onAccent_1A0E05))
    private val ic = iconRegion?.let { icon(it, px(if (small) 16f else 18f), GameColor.onAccent_1A0E05) }

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

    fun setText(text: String, region: TextureRegion? = null) {
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
