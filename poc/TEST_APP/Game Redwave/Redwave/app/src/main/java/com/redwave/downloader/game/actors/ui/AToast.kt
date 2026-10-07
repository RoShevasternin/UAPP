package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

/** .toast: пігулка внизу, Onest 500/12.5 білим, 2.4 с, fade. Ширину рахує від тексту (≤ 92 %). */
class AToast(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val bg = ARect(screen, px(20f), GameColor.toast_282023)
    private val label: AMsdfLabel = lbl("", msdf.medium(12.5f, Color.WHITE))

    init {
        touchable = Touchable.disabled
        isVisible = false
        setSize(px(100f), px(34f))
    }

    override fun addActorsOnGroup() {
        addAndFillActor(bg)
        addActor(label)
    }

    /** [bottom] — відстань від низу сцени до низу тосту. */
    fun show(text: String, worldW: Float, bottom: Float) {
        label.setEllipsis(false)
        label.setText(text); label.pack()
        val maxW = worldW * 0.92f - px(28f)
        if (label.width > maxW) label.ellipsize(maxW)
        setSize(label.width + px(28f), px(34f))
        bg.setSize(width, height)
        label.setPosition(px(14f), (height - label.height) / 2f)
        setPosition((worldW - width) / 2f, bottom)
        toFront()
        clearActions()
        isVisible = true
        color.a = 0f
        addAction(Actions.sequence(
            Actions.parallel(Actions.fadeIn(0.18f), Actions.moveBy(0f, px(6f), 0.18f, Interpolation.pow2Out)),
            Actions.delay(2.4f),
            Actions.fadeOut(0.25f),
            Actions.visible(false),
        ))
    }
}
