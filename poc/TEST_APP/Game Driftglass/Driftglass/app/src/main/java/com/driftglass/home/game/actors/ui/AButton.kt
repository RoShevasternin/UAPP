package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.driftglass.home.game.actors.label.AMsdfLabel
import com.driftglass.home.game.utils.Dimens
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AButton — кнопки прототипу (.btn): TEAL — градієнт 135° teal→sky, WHITE — біла,
// GHOST — скло з рамкою. Іконка 16 + Onest 600/14; small — 12.5 і радіус 12.
// ─────────────────────────────────────────────────────────────────────────────
class AButton(
    screen: AdvancedScreen,
    text: String,
    private val kind: Kind = Kind.TEAL,
    iconRegion: TextureRegion? = null,
    private val small: Boolean = false,
) : ATap(screen) {

    enum class Kind { TEAL, WHITE, GHOST }

    private val radius = if (small) px(12f) else Dimens.R_BTN
    private val bg = when (kind) {
        Kind.TEAL  -> ARect(screen, radius, GameColor.teal_5FF2D1, GameColor.sky_7BB8FF)
        Kind.WHITE -> ARect(screen, radius, Color.WHITE)
        Kind.GHOST -> ARect(screen, radius, GameColor.white_10, stroke = GameColor.white_14)
    }
    private val textColor = when (kind) { Kind.TEAL -> GameColor.inkTeal_04110E; Kind.WHITE -> GameColor.ink_071016; Kind.GHOST -> Color.WHITE }
    val label: AMsdfLabel = lbl(text, msdf.semibold(if (small) 12.5f else 14f, textColor))
    private val ic = iconRegion?.let { icon(it, px(16f), textColor) }

    override fun addContent() {
        addAndFillActor(bg)
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
