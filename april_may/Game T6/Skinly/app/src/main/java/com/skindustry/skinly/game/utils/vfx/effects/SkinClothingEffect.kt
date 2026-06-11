package com.skindustry.skinly.game.utils.vfx.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.skindustry.skinly.game.utils.vfx.VfxContext
import com.skindustry.skinly.game.utils.vfx.VfxEffect

/**
 * Color-key replacement з двома шарами одягу.
 *
 * Шар 1 (clothing)  — базовий одяг, заповнює жовту зону
 * Шар 2 (clothing2) — верхній одяг, малюється поверх шару 1
 *
 * Обидва шари незалежні — можна мати тільки шар 1, тільки шар 2, обидва, або жодного.
 * Без одягу → персонаж як є (жовта зона видна).
 *
 * Texture slots:
 *   0 — персонаж (SpriteBatch bind автоматично)
 *   1 — шар 1 базовий одяг
 *   2 — шар 2 верхній одяг
 */
class SkinClothingEffect(
    var clothingTexture : Texture? = null,
    var clothScaleX     : Float = 1f,
    var clothScaleY     : Float = 1f,
    var clothOffsetX    : Float = 0f,
    var clothOffsetY    : Float = 0f,

    var clothing2Texture: Texture? = null,
    var cloth2ScaleX    : Float = 1f,
    var cloth2ScaleY    : Float = 1f,
    var cloth2OffsetX   : Float = 0f,
    var cloth2OffsetY   : Float = 0f,

    var tolerance       : Float = 0.25f,
    var keepShading     : Float = 1f,
) : VfxEffect() {

    override val fragmentShader = "shader/skinClothing/skinClothingFS.glsl"

    private val keyColor = Color.valueOf("F2CC0DFF")

    fun setKeyColor(hex: String) {
        val c = Color.valueOf(hex.trimStart('#').let { if (it.length == 6) "${it}FF" else it })
        keyColor.set(c)
    }

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_keyColor",    keyColor.r, keyColor.g, keyColor.b)
        shader.setUniformf("u_tolerance",   tolerance)
        shader.setUniformf("u_keepShading", keepShading)

        // ─── Шар 1 ──────────────────────────────────────────────────────
        shader.setUniformf("u_clothScale",  clothScaleX, clothScaleY)
        shader.setUniformf("u_clothOffset", clothOffsetX, clothOffsetY)

        val cloth1 = clothingTexture
        if (cloth1 != null) {
            shader.setUniformf("u_hasClothing", 1f)
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
            cloth1.bind(1)
            shader.setUniformi("u_clothing", 1)
        } else {
            shader.setUniformf("u_hasClothing", 0f)
        }

        // ─── Шар 2 ──────────────────────────────────────────────────────
        shader.setUniformf("u_cloth2Scale",  cloth2ScaleX, cloth2ScaleY)
        shader.setUniformf("u_cloth2Offset", cloth2OffsetX, cloth2OffsetY)

        val cloth2 = clothing2Texture
        if (cloth2 != null) {
            shader.setUniformf("u_hasClothing2", 1f)
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE2)
            cloth2.bind(2)
            shader.setUniformi("u_clothing2", 2)
        } else {
            shader.setUniformf("u_hasClothing2", 0f)
        }

        // Повертаємо активний unit
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }
}