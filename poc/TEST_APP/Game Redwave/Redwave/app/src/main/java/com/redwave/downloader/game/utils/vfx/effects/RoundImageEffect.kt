package com.redwave.downloader.game.utils.vfx.effects

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.redwave.downloader.game.utils.vfx.VfxContext

/** Текстура з заокругленими кутами (шейдер ui/roundImageFS). Радіус — у world-юнітах. */
class RoundImageEffect : VfxEffect() {

    override val fragmentShader = "shader/ui/roundImageFS.glsl"

    var radius  = 12f
    var aaWidth = 1.2f

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_size", ctx.width, ctx.height)
        shader.setUniformf("u_radius", radius)
        shader.setUniformf("u_aa", aaWidth)
    }
}
