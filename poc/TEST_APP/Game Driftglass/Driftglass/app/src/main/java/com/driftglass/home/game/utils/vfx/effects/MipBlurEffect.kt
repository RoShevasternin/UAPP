package com.driftglass.home.game.utils.vfx.effects

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.driftglass.home.game.utils.vfx.VfxContext

/** Розмиття через мип-рівень (шейдер ui/mipBlurFS). Текстура має бути з мипмапами. */
class MipBlurEffect(var bias: Float = 5.5f, var saturation: Float = 1.4f, var brightness: Float = 0.55f) : VfxEffect() {
    override val fragmentShader = "shader/ui/mipBlurFS.glsl"
    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_bias", bias)
        shader.setUniformf("u_saturation", saturation)
        shader.setUniformf("u_brightness", brightness)
    }
}
