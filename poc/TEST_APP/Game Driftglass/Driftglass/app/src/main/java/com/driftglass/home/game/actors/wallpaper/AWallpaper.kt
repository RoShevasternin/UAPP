package com.driftglass.home.game.actors.wallpaper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Disposable
import com.driftglass.home.core.logic.PowerPolicy
import com.driftglass.home.core.model.Style
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.wallpaper.WallpaperRenderer

// ─────────────────────────────────────────────────────────────────────────────
// AWallpaper — живі шпалери як актор (Home, онбординг, перегляд, Studio).
//
//  • кадр рендериться в act() у власний FBO (поза batch сцени), draw() лише малює текстуру;
//  • зміна шпалер того ж стилю — палітра плавно перетікає (~0.5 с, як у прототипі);
//    інший стиль — короткий fade через темну основу;
//  • паралакс — з акселерометра (GDXGame.parallax), якщо ввімкнено в налаштуваннях;
//  • режим економії — 30 fps (GDXGame), половина швидкості й менший FBO.
// ─────────────────────────────────────────────────────────────────────────────
class AWallpaper(private val screen: AdvancedScreen) : Actor(), Disposable {

    private val renderer get() = gdxGame.wallpapers

    private var wallpaper: Wallpaper? = null
    private var style = Style.LIQUID
    private var pendingStyle: Wallpaper? = null
    private val u = WallpaperRenderer.Uniforms()
    private val target = FloatArray(12)
    private var fade = 1f          // 1 — видно; при зміні стилю йде до 0 і назад
    private var speed = 1f

    private var fbo: FrameBuffer? = null
    private var region: TextureRegion? = null

    /** false — кадр заморожено (напр. поверх відкрита шторка, що закриває все). */
    var animate = true

    init { touchable = Touchable.disabled }

    // ------------------------------------------------------------------------
    // Що показувати
    // ------------------------------------------------------------------------
    /** Поставити шпалери. [instant] — без переходу (перший показ екрана). */
    fun show(w: Wallpaper, instant: Boolean = false) {
        val prev = wallpaper
        if (prev == w) return
        if (prev == null || instant) {
            apply(w); u.colors.indices.forEach { u.colors[it] = target[it] }
            fade = 1f; pendingStyle = null
            u.time = 6f + w.seed * 3f
            return
        }
        if (w.style != style) pendingStyle = w else apply(w)
    }

    /** Studio: ті самі шпалери, але змінились ручки — без переходу, одразу. */
    fun live(w: Wallpaper) {
        if (wallpaper?.style != w.style) { show(w); return }
        apply(w)
    }

    private fun apply(w: Wallpaper) {
        wallpaper = w
        style = w.style
        for (i in 0 until 4) w.pal.rgb(i).copyInto(target, i * 3)
        u.scale = w.scale; u.grain = w.grain; u.seed = w.seed
        speed = w.speed
    }

    // ------------------------------------------------------------------------
    // Кадр
    // ------------------------------------------------------------------------
    override fun act(delta: Float) {
        super.act(delta)
        if (wallpaper == null || !isVisible || stage == null) return
        val game = gdxGame
        val saver = game.isSaverOn

        // перехід між стилями: fade out → зміна → fade in
        val ps = pendingStyle
        if (ps != null) {
            fade -= delta / 0.16f
            if (fade <= 0f) { fade = 0f; apply(ps); u.colors.indices.forEach { u.colors[it] = target[it] }; pendingStyle = null }
        } else if (fade < 1f) fade = (fade + delta / 0.25f).coerceAtMost(1f)

        val k = (delta * 5f).coerceAtMost(1f)
        for (i in 0 until 12) u.colors[i] += (target[i] - u.colors[i]) * k

        if (animate) u.time += delta * speed * (if (saver) PowerPolicy.SPEED_SAVER else 1f)
        val par = game.parallax
        u.parX = MathUtils.lerp(u.parX, par.x, k); u.parY = MathUtils.lerp(u.parY, par.y, k)

        // розмір актора в пікселях екрана → FBO (масштаб 0.5 / 0.35)
        val sx = Gdx.graphics.width / screen.worldWidth
        val sy = Gdx.graphics.height / screen.worldHeight
        val resW = width * sx; val resH = height * sy
        val scale = if (saver) 0.35f else 0.5f
        val fw = (resW * scale).toInt().coerceAtLeast(2); val fh = (resH * scale).toInt().coerceAtLeast(2)
        val cur = fbo
        if (cur == null || cur.width != fw || cur.height != fh) {
            cur?.dispose()
            fbo = FrameBuffer(Pixmap.Format.RGBA8888, fw, fh, false).also {
                it.colorBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                region = TextureRegion(it.colorBufferTexture).apply { flip(false, true) }
            }
        }
        renderer.renderTo(fbo!!, style, u, resW, resH)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val r = region ?: return
        val c = color
        batch.setColor(c.r * fade, c.g * fade, c.b * fade, c.a * parentAlpha)
        batch.draw(r, x, y, width, height)
        batch.setColor(1f, 1f, 1f, 1f)
    }

    /** Поточна фаза анімації — щоб нерухомий кадр на екран блокування збігся з тим, що бачить людина. */
    val time: Float get() = u.time

    override fun dispose() {
        fbo?.dispose(); fbo = null; region = null
    }
}
