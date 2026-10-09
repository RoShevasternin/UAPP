package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.px

/** .iconbtn: квадрат 38 з радіусом 12, скло white 8 % + рамка 14 %, іконка 18. */
class AIconButton(
    screen: AdvancedScreen,
    region: TextureRegion,
    iconColor: Color = Color.WHITE,
    private val iconSize: Float = px(18f),
    radius: Float = px(12f),
) : ATap(screen, 0.92f) {

    val bg = ARect(screen, radius, GameColor.white_08, stroke = GameColor.white_14)
    val ic: Image = icon(region, iconSize, iconColor)

    override fun addContent() {
        addAndFillActor(bg)
        addActor(ic)
        ic.setPosition((width - iconSize) / 2f, (height - iconSize) / 2f)
    }

    fun setIcon(region: TextureRegion, color: Color? = null) {
        ic.drawable = TextureRegionDrawable(region)
        color?.let { ic.color.set(it) }
    }
}
