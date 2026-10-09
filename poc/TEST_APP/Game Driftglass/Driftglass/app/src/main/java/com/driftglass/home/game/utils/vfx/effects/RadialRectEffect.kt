package com.driftglass.home.game.utils.vfx.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.driftglass.home.game.utils.vfx.VfxContext

/** Заглушка обкладинки: радіальний 3-кольоровий градієнт у заокругленому прямокутнику. */
class RadialRectEffect : VfxEffect() {

    override val fragmentShader = "shader/ui/radialRoundRectFS.glsl"

    var radius = 12f
    var aaWidth = 1.2f
    val c0 = Color(Color.WHITE)
    val c1 = Color(Color.GRAY)
    val c2 = Color(Color.BLACK)
    var centerX = 0.25f
    var centerY = 0.15f
    var radiusX = 1.2f
    var radiusY = 0.9f

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_size", ctx.width, ctx.height)
        shader.setUniformf("u_radius", radius)
        shader.setUniformf("u_aa", aaWidth)
        shader.setUniformf("u_center", centerX, centerY)
        shader.setUniformf("u_radii", radiusX, radiusY)
        shader.setUniformf("u_c0", c0)
        shader.setUniformf("u_c1", c1)
        shader.setUniformf("u_c2", c2)
    }
}
