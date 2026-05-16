package com.rbuxdrop.cougame.game.utils.vfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kotlin.math.abs

/**
 * Вбудовані ефекти системи.
 *
 * ─── ЯК ЧИТАТИ ЦЕЙ ФАЙЛ ────────────────────────────────────────────────────
 *
 * Кожен ефект — живий приклад того як писати нові:
 *
 * SINGLE-PASS (один Blit прохід):
 *   • override fragmentShader  → шлях до .glsl
 *   • override setUniforms()   → передаємо параметри
 *   Більше нічого. Базовий VfxEffect сам робить Blit + swap.
 *
 * MULTI-PASS (кілька Blit проходів):
 *   • override fragmentShader  → шлях до .glsl
 *   • override render()        → N разів: Blit.blit(...) + pingPong.swap()
 *   Правило: після кожного blit — обов'язково swap().
 *
 * DUAL-TEXTURE (дві текстури в шейдері):
 *   • override render()        → Blit.blit з uniforms лямбдою що bind-ить unit 1
 *
 * PASS-THROUGH (ефект вимкнений):
 *   • просто return без swap — src залишається незміненим для наступного ефекту
 *
 * ─── ШЕЙДЕР КЕШУЄТЬСЯ АВТОМАТИЧНО ─────────────────────────────────────────
 * val shader: ShaderProgram get() = VfxShaderCache.get(fragmentShader)
 * Не потрібно companion object lazy. VfxShaderCache компілює шейдер один раз.
 */

// ─────────────────────────────────────────────────────────────────────────────
// BlurEffect — Gaussian blur (multi-pass: H + V)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Gaussian blur.
 *
 * Два проходи через ping-pong:
 *   Pass 1: горизонтальне розмиття (u_direction = 1,0)
 *   Pass 2: вертикальне розмиття  (u_direction = 0,1)
 *
 * Математично точний 2D Gaussian: Gaussian2D(x,y) = Gaussian1D(x) × Gaussian1D(y).
 * Більше пасів по діагоналях — артефакт "сітки", не потрібно.
 *
 * radius = 0 → pass-through (жодного Blit, жодного swap).
 */
class BlurEffect(var radius: Float = 8f) : VfxEffect() {

    override val fragmentShader = "shader/blur/gaussianBlurFS.glsl"

    // Multi-pass: override render() а не setUniforms()
    override fun render(pingPong: PingPong, ctx: VfxContext) {
        if (radius <= 0f) return  // pass-through — src не змінюється

        // Pass 1: горизонтальний
        Blit.blit(pingPong.src, pingPong.dst, shader) { s ->
            s.setUniformf("u_direction",  1f, 0f)
            s.setUniformf("u_groupSize",  ctx.bufferW.toFloat(), ctx.bufferH.toFloat())
            s.setUniformf("u_blurAmount", radius)
        }
        pingPong.swap()  // результат H-pass тепер в src

        // Pass 2: вертикальний
        Blit.blit(pingPong.src, pingPong.dst, shader) { s ->
            s.setUniformf("u_direction",  0f, 1f)
            s.setUniformf("u_groupSize",  ctx.bufferW.toFloat(), ctx.bufferH.toFloat())
            s.setUniformf("u_blurAmount", radius)
        }
        pingPong.swap()  // результат V-pass тепер в src

        // Pass 3: діагональний 1
        Blit.blit(pingPong.src, pingPong.dst, shader) { s ->
            s.setUniformf("u_direction",  0.383f,  0.924f)
            s.setUniformf("u_groupSize",  ctx.bufferW.toFloat(), ctx.bufferH.toFloat())
            s.setUniformf("u_blurAmount", radius)
        }
        pingPong.swap()  // результат D1-pass тепер в src

        // Pass 4: діагональний 2
        Blit.blit(pingPong.src, pingPong.dst, shader) { s ->
            s.setUniformf("u_direction",  0.924f,  0.383f)
            s.setUniformf("u_groupSize",  ctx.bufferW.toFloat(), ctx.bufferH.toFloat())
            s.setUniformf("u_blurAmount", radius)
        }
        pingPong.swap()  // результат D2-pass тепер в src = фінальний blur
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MaskEffect — alpha masking (dual-texture)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Маскування через alpha-текстуру.
 *
 * maskFS.glsl очікує:
 *   uniform sampler2D u_texture;  ← контент (texture unit 0, bind-ить Blit)
 *   uniform sampler2D u_mask;     ← маска  (texture unit 1, bind-имо вручну)
 *
 * maskTexture = null → pass-through.
 */
class MaskEffect(var maskTexture: Texture? = null) : VfxEffect() {

    override val fragmentShader = "shader/mask/maskFS.glsl"

    override fun render(pingPong: PingPong, ctx: VfxContext) {
        val mask = maskTexture ?: return  // pass-through

        Blit.blit(pingPong.src, pingPong.dst, shader) { s ->
            s.setUniformi("u_texture", 0)          // unit 0 вже bind-нутий Blit (src)

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
            mask.bind(1)
            s.setUniformi("u_mask", 1)             // unit 1 = маска

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)  // повертаємо активний unit
        }
        pingPong.swap()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HslEffect — Hue/Saturation/Luminance (single-pass)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Перефарбування через HSL. Зберігає luminance (яскравість) оригіналу.
 *
 * Для GRAYSCALE текстури (Figma → Saturation = -100):
 *   • один спрайт покриває будь-який колір
 *   • бліки = білі пікселі → залишаються білими
 *   • тіні = темні пікселі → залишаються темними
 *
 * Для VfxGroup — застосовується до всього вмісту групи після рендеру в FBO.
 * Для VfxImage — застосовується прямо через batch.shader, без FBO.
 */
class HslEffect(
    hue       : Float = 0f,
    saturation: Float = 1f,
    luminance : Float = 0f,
) : VfxEffect() {

    override val fragmentShader = "shader/hslColor/hslColorFS.glsl"

    var hue       : Float = hue
    var saturation: Float = saturation
    var luminance : Float = luminance

    // ─── Зручні константи hue ───────────────────────────────────────────────
    object Hue {
        const val RED    = 0.00f
        const val ORANGE = 0.08f
        const val YELLOW = 0.15f
        const val GREEN  = 0.33f
        const val CYAN   = 0.50f
        const val BLUE   = 0.62f
        const val PURPLE = 0.75f
        const val PINK   = 0.88f
    }

    /** Встановити колір через HEX прямо з Figma. "F97316", "#A855F7" тощо */
    fun setColor(hex: String, luminance: Float = 0f) {
        val c = Color.valueOf(hex.trimStart('#').let { if (it.length == 6) "${it}FF" else it })
        val (h, s, _) = rgbToHsl(c.r, c.g, c.b)
        this.hue       = h
        this.saturation = s
        this.luminance  = luminance
    }

    /** Встановити колір через LibGDX Color */
    fun setColor(color: Color, luminance: Float = 0f) {
        val (h, s, _) = rgbToHsl(color.r, color.g, color.b)
        this.hue       = h
        this.saturation = s
        this.luminance  = luminance
    }

    // Single-pass: override тільки setUniforms(), render() в базовому класі
    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_hue",        hue)
        shader.setUniformf("u_saturation", saturation)
        shader.setUniformf("u_luminance",  luminance)
    }

    private fun rgbToHsl(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
        val maxC = maxOf(r, g, b); val minC = minOf(r, g, b)
        val l    = (maxC + minC) / 2f; val d = maxC - minC
        if (d < 0.001f) return Triple(0f, 0f, l)
        val s = d / (1f - abs(2f * l - 1f))
        val h = when (maxC) {
            r    -> (((g - b) / d) % 6f) / 6f
            g    -> ((b - r) / d + 2f) / 6f
            else -> ((r - g) / d + 4f) / 6f
        }.let { if (it < 0f) it + 1f else it }
        return Triple(h, s, l)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SaturationEffect — насиченість (single-pass)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Регулює насиченість кольорів.
 * saturation: 0 = grayscale, 1 = оригінал, >1 = oversaturated.
 * isEnabled = false (оригінал) → автоматичний pass-through.
 */
class SaturationEffect(var saturation: Float = 1f, var alpha: Float = 1f) : VfxEffect() {

    override val fragmentShader = "shader/saturation/saturationFS.glsl"
    override val isEnabled      get() = saturation != 1f || alpha != 1f

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_saturation", saturation)
        shader.setUniformf("u_alpha",      alpha)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CircleProgressEffect — кільцевий прогрес-бар (single-pass)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Кільцевий прогрес-бар з градієнтом і round caps.
 * progress: -100.0..100.0 (від'ємне = зворотний напрямок).
 */
class CircleProgressEffect(
    var progress   : Float = 50f,
    var startAngle : Float = 0f,
    var innerEmpty : Float = 70f,
    var roundness  : Float = 1f,
    var colorStart : Color = Color.WHITE,
    var colorEnd   : Color = Color.WHITE,
) : VfxEffect() {

    override val fragmentShader = "shader/circleProgress/circleProgressFS.glsl"

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_progress",   progress)
        shader.setUniformf("u_startAngle", startAngle)
        shader.setUniformf("u_innerEmpty", innerEmpty)
        shader.setUniformf("u_roundness",  roundness)
        shader.setUniformf("u_colorStart", colorStart.r, colorStart.g, colorStart.b, colorStart.a)
        shader.setUniformf("u_colorEnd",   colorEnd.r,   colorEnd.g,   colorEnd.b,   colorEnd.a)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BagCoinsEffect — заливка знизу вгору (single-pass)
// ─────────────────────────────────────────────────────────────────────────────

/** Заповнення текстури кольором знизу вгору. fillPercent: 0..100 */
class BagCoinsEffect(var fillPercent: Float = 0f) : VfxEffect() {

    override val fragmentShader = "shader/bagCoins/bagCoinsFS.glsl"

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_fillPercent", fillPercent)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LavaProgressEffect — lava distortion (single-pass)
// ─────────────────────────────────────────────────────────────────────────────

/** Liquid/lava distortion ефект */
class LavaProgressEffect(
    var time       : Float = 0f,
    var edgeDeform : Float = 1f,
    var finishFlash: Float = 0f,
) : VfxEffect() {

    override val fragmentShader = "shader/lavaProgress/lavaProgressFS.glsl"

    override fun setUniforms(shader: ShaderProgram, ctx: VfxContext) {
        shader.setUniformf("u_time",        time)
        shader.setUniformf("u_edgeDeform",  edgeDeform)
        shader.setUniformf("u_finishFlash", finishFlash)
    }
}