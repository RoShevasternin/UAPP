package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.px

/**
 * .icb: коло 38, рамка line_white_7, фон white 4 %, іконка 20.
 * bare = true — без кола (кнопки керування плеєра).
 */
class AIconButton(
    screen: AdvancedScreen,
    region: TextureRegion,
    private val iconSize: Float = px(20f),
    iconColor: Color = GameColor.text_FBF1F2,
    private val bare: Boolean = false,
    bgColor: Color = GameColor.white_4,
    strokeColor: Color = GameColor.line_white_7,
) : ATap(screen, 0.92f) {

    val bg = ARect(screen, 999f, bgColor, stroke = strokeColor)
    val ic: Image = icon(region, iconSize, iconColor)
    /** Червона крапка-бейдж (дзвіночок на Home). */
    var badge: ARect? = null

    override fun addContent() {
        if (!bare) addAndFillActor(bg)
        addActor(ic)
        ic.setPosition((width - iconSize) / 2f, (height - iconSize) / 2f)
        badge?.let { addActor(it) }
    }

    fun withBadge(): AIconButton {
        badge = ARect(screen, 999f, GameColor.red_FF2E4D, stroke = GameColor.background, strokeWidth = px(2f)).apply {
            setSize(px(11f), px(11f))
            setPosition(px(38f - 9f - 9f), px(38f - 8f - 9f))
        }
        return this
    }

    fun setIcon(region: TextureRegion, color: Color? = null) {
        ic.drawable = TextureRegionDrawable(region)
        color?.let { ic.color.set(it) }
    }
}
