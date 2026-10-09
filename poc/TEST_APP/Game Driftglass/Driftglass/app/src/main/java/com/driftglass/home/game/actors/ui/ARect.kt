package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.vfx.VfxImage
import com.driftglass.home.game.utils.vfx.effects.GradientRectEffect

// ─────────────────────────────────────────────────────────────────────────────
// ARect — картка/кнопка/чип: заокруглений прямокутник з суцільною або
// градієнтною заливкою і рамкою (CSS background + border + border-radius).
//
//   ARect(screen, px(20f), GameColor.card_170F12, stroke = GameColor.line_white_7)
//   ARect(screen, px(15f), GameColor.gradTop_FF5468, GameColor.gradBot_E3002B)   // --grad 135°
// ─────────────────────────────────────────────────────────────────────────────
class ARect(
    override val screen: AdvancedScreen,
    radius: Float,
    colA: Color,
    colB: Color = colA,
    angleCss: Float = 135f,
    stroke: Color? = null,
    strokeWidth: Float = 1.1f,
    mid: Float = 1f,
    start: Float = 0f,
) : VfxImage(screen) {

    val fx = GradientRectEffect()

    init {
        drawable = TextureRegionDrawable(screen.drawerUtil.whiteRegion)
        effect   = fx
        fx.radius = radius
        fx.colA.set(colA)
        fx.colB.set(colB)
        fx.angleCss = angleCss
        fx.mid = mid
        fx.start = start
        if (stroke != null) { fx.strokeWidth = strokeWidth; fx.strokeColor.set(stroke) }
    }

    var radius: Float
        get() = fx.radius
        set(v) { fx.radius = v }

    fun fill(a: Color, b: Color = a): ARect { fx.colA.set(a); fx.colB.set(b); return this }

    fun stroke(c: Color?, width: Float = 1.1f): ARect {
        if (c == null) fx.strokeWidth = 0f else { fx.strokeWidth = width; fx.strokeColor.set(c) }
        return this
    }
}
