package com.driftglass.home.game.wallpaper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.driftglass.home.core.model.Style
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.util.log
import java.io.ByteArrayOutputStream

// ═════════════════════════════════════════════════════════════════════════════
//  WallpaperRenderer — шейдери шпалер (assets/shader/wallpaper: common.glsl + <style>.frag,
//  той самий GLSL ES 1.00, що в прототипі) і рендер у FrameBuffer.
//
//  Чому FBO, а не одразу на екран: liquid/aurora — це ~15 fbm на піксель; на 1080×2400
//  Redmi не тягне 60 fps. Малюємо в буфер 0.5 від екрана (0.35 у режимі економії) з лінійним
//  фільтром — плавні градієнти від цього не страждають, а пікселів учетверо менше.
//
//  Мініатюри (Огляд, «Моє скло», слоти циклу дня) — той самий шейдер у маленький FBO
//  ОДИН раз (час t = 8 + seed·3, як у прототипі), далі кеш.
//
//  Викликати лише з GL-потоку і ПОЗА batch.begin/end сцени (актори роблять це в act()).
// ═════════════════════════════════════════════════════════════════════════════
class WallpaperRenderer : Disposable {

    private val vert   = Gdx.files.internal("shader/defaultVS.glsl").readString()
    private val common = Gdx.files.internal("shader/wallpaper/common.glsl").readString()
    private val programs = HashMap<Style, ShaderProgram>()

    private val batch = SpriteBatch(4)
    /** 1×1 біла текстура з повними UV 0..1 — шейдер рахує піксель з v_texCoords. */
    private val white: Texture = Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
        setColor(Color.WHITE); fill()
        Texture(this).also { dispose() }
    }
    private val full = TextureRegion(white)

    private val thumbs = HashMap<String, FrameBuffer>()

    // ------------------------------------------------------------------------
    // Шейдери
    // ------------------------------------------------------------------------
    fun program(style: Style): ShaderProgram = programs.getOrPut(style) {
        val frag = common + "\n" + Gdx.files.internal("shader/wallpaper/${style.shader}.frag").readString()
        ShaderProgram(vert, frag).also { if (!it.isCompiled) log("wallpaper shader ${style.shader}: ${it.log}") }
    }

    /** Кольори палітри 0..1: c1..c4 по 3 float. */
    class Uniforms {
        val colors = FloatArray(12)
        var time = 0f
        var scale = 1f
        var grain = 0.05f
        var seed = 0f
        var parX = 0f
        var parY = 0f

        fun setFrom(w: Wallpaper) {
            for (i in 0 until 4) w.pal.rgb(i).copyInto(colors, i * 3)
            scale = w.scale; grain = w.grain; seed = w.seed
        }
    }

    private fun applyUniforms(sp: ShaderProgram, u: Uniforms, wPx: Float, hPx: Float) {
        sp.setUniformf("u_res", wPx, hPx)
        sp.setUniformf("u_time", u.time)
        sp.setUniformf("u_c1", u.colors[0], u.colors[1], u.colors[2])
        sp.setUniformf("u_c2", u.colors[3], u.colors[4], u.colors[5])
        sp.setUniformf("u_c3", u.colors[6], u.colors[7], u.colors[8])
        sp.setUniformf("u_c4", u.colors[9], u.colors[10], u.colors[11])
        sp.setUniformf("u_scale", u.scale)
        sp.setUniformf("u_grain", u.grain)
        sp.setUniformf("u_seed", u.seed)
        sp.setUniformf("u_par", u.parX, u.parY)
    }

    /**
     * Намалювати шпалери в [fbo] на весь його розмір. [resW]/[resH] — «логічна» роздільність
     * для шейдера (пікселі актора на екрані): зерно й розмір візерунка не залежать від масштабу FBO.
     */
    fun renderTo(fbo: FrameBuffer, style: Style, u: Uniforms, resW: Float, resH: Float) {
        val sp = program(style)
        fbo.begin()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.projectionMatrix.setToOrtho2D(0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
        batch.shader = sp
        batch.disableBlending()
        batch.begin()
        applyUniforms(sp, u, resW, resH)
        batch.setColor(1f, 1f, 1f, 1f)
        batch.draw(full, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
        batch.end()
        batch.enableBlending()
        batch.shader = null
        fbo.end()
    }

    // ------------------------------------------------------------------------
    // Мініатюри
    // ------------------------------------------------------------------------
    /** Мініатюра [w] розміром [wPx]×[hPx] пікселів (кеш на сесію). Регіон уже перевернутий для scene2d. */
    fun thumb(w: Wallpaper, wPx: Int, hPx: Int): TextureRegion {
        val key = "${w.style}|${w.palette}|${w.scale}|${w.grain}|${w.seed}|${wPx}x$hPx"
        val fbo = thumbs.getOrPut(key) {
            FrameBuffer(Pixmap.Format.RGBA8888, wPx.coerceAtLeast(2), hPx.coerceAtLeast(2), false).also { fb ->
                fb.colorBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                val u = Uniforms().apply { setFrom(w); time = 8f + w.seed * 3f; grain = w.grain * 0.8f }
                // логічна роздільність — як у мініатюри прототипу (144×256 при ширині 160 px)
                renderTo(fb, w.style, u, wPx.toFloat(), hPx.toFloat())
            }
        }
        return TextureRegion(fbo.colorBufferTexture).apply { flip(false, true) }
    }

    /**
     * Нерухомий кадр для WallpaperManager: [wPx]×[hPx] (екран телефона), PNG-байти.
     * Пікселі читаємо в GL-потоці; кодування PNG — в [encode] (можна з фону: Pixmap — звичайна пам'ять).
     */
    fun stillPixmap(w: Wallpaper, time: Float, wPx: Int, hPx: Int): Pixmap {
        val fb = FrameBuffer(Pixmap.Format.RGBA8888, wPx, hPx, false)
        val u = Uniforms().apply { setFrom(w); this.time = time }
        renderTo(fb, w.style, u, wPx.toFloat(), hPx.toFloat())
        fb.begin()
        val bytes = ScreenUtils.getFrameBufferPixels(0, 0, wPx, hPx, true)
        fb.end()
        fb.dispose()
        val pm = Pixmap(wPx, hPx, Pixmap.Format.RGBA8888)
        BufferUtils.copy(bytes, 0, pm.pixels, bytes.size)
        return pm
    }

    fun encode(pm: Pixmap): ByteArray = ByteArrayOutputStream().use { out ->
        val png = PixmapIO.PNG(pm.width * pm.height * 4)
        try { png.setFlipY(false); png.setCompression(6); png.write(out, pm) } finally { png.dispose() }
        out.toByteArray()
    }

    override fun dispose() {
        programs.values.forEach { it.dispose() }
        programs.clear()
        thumbs.values.forEach { it.dispose() }
        thumbs.clear()
        batch.dispose()
        white.dispose()
    }
}
