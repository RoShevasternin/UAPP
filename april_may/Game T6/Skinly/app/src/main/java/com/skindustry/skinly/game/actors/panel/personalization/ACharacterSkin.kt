package com.skindustry.skinly.game.actors.panel.personalization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.vfx.VfxImage
import com.skindustry.skinly.game.utils.vfx.effects.SkinClothingEffect

/**
 * Персонаж з персоналізацією одягу — два шари.
 *
 * Шар 1 (setClothing)  — базовий одяг, заповнює жовту зону F2CC0D
 * Шар 2 (setClothing2) — верхній одяг, малюється поверх шару 1
 *
 * ```kotlin
 * val character = ACharacterSkin(screen, assetsAll.char_p1_1)
 * character.setBounds(100f, 200f, 400f, 600f)
 *
 * // Базовий одяг — червоний патерн:
 * character.setClothing(assetsAll.pattern_stripes, scale = 2f)
 *
 * // Верхній шар — логотип поверх:
 * character.setClothing2(assetsAll.overlay_logo, scale = 1f)
 *
 * // Змінити тільки верхній шар:
 * character.setClothing2(assetsAll.overlay_star)
 *
 * // Зняти верхній шар (базовий залишається):
 * character.clearClothing2()
 *
 * // Зняти все:
 * character.clearClothing()
 * ```
 */
class ACharacterSkin(
    override val screen: AdvancedScreen,
    characterRegion    : Texture,
) : VfxImage(screen, characterRegion, SkinClothingEffect()) {

    private val clothingEffect get() = effect as SkinClothingEffect

    // ─── Шар 1: базовий одяг ────────────────────────────────────────────────

    fun setClothing(
        texture    : Texture,
        scale      : Float = 1f,
        keepShading: Boolean = true,
    ) {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        clothingEffect.clothingTexture = texture
        clothingEffect.clothScaleX     = scale
        clothingEffect.clothScaleY     = scale
        clothingEffect.keepShading     = if (keepShading) 1f else 0f
    }

    fun setClothingScale(scaleX: Float, scaleY: Float) {
        clothingEffect.clothScaleX = scaleX
        clothingEffect.clothScaleY = scaleY
    }

    fun setClothingOffset(offsetX: Float, offsetY: Float) {
        clothingEffect.clothOffsetX = offsetX
        clothingEffect.clothOffsetY = offsetY
    }

    /** Зняти базовий одяг (верхній теж прибирається) */
    fun clearClothing() {
        clothingEffect.clothingTexture  = null
        clothingEffect.clothing2Texture = null
    }

    // ─── Шар 2: верхній одяг ────────────────────────────────────────────────

    fun setClothing2(
        texture: Texture,
        scale  : Float = 1f,
    ) {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        clothingEffect.clothing2Texture = texture
        clothingEffect.cloth2ScaleX     = scale
        clothingEffect.cloth2ScaleY     = scale
    }

    fun setClothing2Scale(scaleX: Float, scaleY: Float) {
        clothingEffect.cloth2ScaleX = scaleX
        clothingEffect.cloth2ScaleY = scaleY
    }

    fun setClothing2Offset(offsetX: Float, offsetY: Float) {
        clothingEffect.cloth2OffsetX = offsetX
        clothingEffect.cloth2OffsetY = offsetY
    }

    /** Зняти тільки верхній шар (базовий залишається) */
    fun clearClothing2() {
        clothingEffect.clothing2Texture = null
    }

    // ─── Персонаж ───────────────────────────────────────────────────────────

    fun setCharacter(region: TextureRegion) { drawable = TextureRegionDrawable(region) }
    fun setCharacter(texture: Texture)      { drawable = TextureRegionDrawable(texture) }

    fun setTolerance(value: Float) { clothingEffect.tolerance = value }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------

    // Захоплює персонажа з одягом у Pixmap
    fun captureToPixmap(captureSize: Int = 1024): Pixmap {
        val fbo   = FrameBuffer(Pixmap.Format.RGBA8888, captureSize, captureSize, false)
        val batch = screen.stageUI.batch
        val cam   = OrthographicCamera(captureSize.toFloat(), captureSize.toFloat())
        cam.setToOrtho(false, captureSize.toFloat(), captureSize.toFloat())
        cam.update()

        // Зберігаємо поточні розмір/позицію
        val oldX = x; val oldY = y
        val oldW = width; val oldH = height

        // Розтягуємо актор на весь буфер
        setPosition(0f, 0f)
        setSize(captureSize.toFloat(), captureSize.toFloat())

        fbo.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = cam.combined
        batch.begin()
        draw(batch, 1f)
        batch.end()

        val pixmap = Pixmap.createFromFrameBuffer(0, 0, captureSize, captureSize)
        fbo.end()

        // Повертаємо актор як був
        setPosition(oldX, oldY)
        setSize(oldW, oldH)
        fbo.dispose()

        return pixmap.flipY()
    }

    private fun Pixmap.flipY(): Pixmap {
        val flipped = Pixmap(width, height, format)
        for (y in 0 until height) {
            for (x in 0 until width) {
                flipped.drawPixel(x, height - 1 - y, getPixel(x, y))
            }
        }
        dispose()
        return flipped
    }
}