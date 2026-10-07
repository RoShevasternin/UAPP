package com.redwave.downloader.game.utils.vfx.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.redwave.downloader.game.utils.vfx.VfxContext

// ─────────────────────────────────────────────────────────────────────────────
// GradientRectEffect — заокруглений прямокутник з лінійним градієнтом
// (CSS linear-gradient) + обводка окремим кольором. Шейдер ui/gradRoundRectFS.
//
//   angleCss — кут як у CSS: 90 = зліва направо, 180 = згори вниз, 135 = діагональ.
//   start    — до цієї частки чистий colA («colA 35%, …» → 0.35).
//   mid      — частка, де вже чистий colB («…, colB 55%» → 0.55).
// ─────────────────────────────────────────────────────────────────────────────
class GradientRectEffect : VfxEffect() {

    override val fragmentShader = "shader/ui/gradRoundRectFS.glsl"

    var radius      = 16f
    var aaWidth     = 1.2f
    val colA        = Color(Color.WHITE)
    val colB        = Color(Color.WHITE)
    var angleCss    = 135f
    var start       = 0f
    var mid         = 1f
    var strokeWidth = 0f
    val strokeColor = Color(0f, 0f, 0f, 0f)

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        // CSS: 0deg = вгору, 90deg = вправо. У шейдері y — вгору.
        val rad = (angleCss) * MathUtils.degreesToRadians
        shader.setUniformf("u_size", ctx.width, ctx.height)
        shader.setUniformf("u_radius", radius)
        shader.setUniformf("u_aa", aaWidth)
        shader.setUniformf("u_colA", colA)
        shader.setUniformf("u_colB", colB)
        shader.setUniformf("u_dir", MathUtils.sin(rad), MathUtils.cos(rad))
        shader.setUniformf("u_start", start)
        shader.setUniformf("u_mid", mid)
        shader.setUniformf("u_strokeWidth", strokeWidth)
        shader.setUniformf("u_strokeColor", strokeColor)
    }
}
