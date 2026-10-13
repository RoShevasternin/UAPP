package com.vidtools.framefall.game.actors.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.vidtools.framefall.game.platform.VideoPlayer
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.vfx.VfxShaderCache

// ─────────────────────────────────────────────────────────────────────────────
// AVideo — кадр VideoPlayer на сцені GDX (OES-текстура через shader/video/oes*).
//
//   COVER   — заповнити актор, зайве обрізати (фон головного екрана, мініатюри);
//   CONTAIN — вписати цілком, поля прозорі (плеєр, Studio).
// Поки першого кадру немає — постер (мініатюра) з тим самим fit, щоб не «блимало» чорним.
// Плеєр актор не звільняє: ним володіє екран (release() у hide()).
// ─────────────────────────────────────────────────────────────────────────────
class AVideo(
    private val screen: AdvancedScreen,
    var player: VideoPlayer?,
    var fit: Fit = Fit.COVER,
    var radius: Float = 0f,
) : Actor() {

    enum class Fit { COVER, CONTAIN }

    /** COVER: яку частину кадру лишити по вертикалі, 0 — верх, 0.5 — центр, 1 — низ. */
    var focusY = 0.5f
    /** Мініатюра до першого кадру (і для аудіо, де кадрів немає). */
    var poster: Texture? = null
    /** Прямокутник, який реально зайняв кадр (CONTAIN) — для оверлеїв поверх відео. */
    val drawnX get() = dx
    val drawnY get() = dy
    val drawnW get() = dw
    val drawnH get() = dh
    private var dx = 0f; private var dy = 0f; private var dw = 0f; private var dh = 0f

    init { touchable = Touchable.disabled }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val p = player
        val frame = p?.frame()
        if (frame == null || p.videoWidth == 0) { drawPoster(batch, parentAlpha); return }

        val va = p.videoWidth.toFloat() / p.videoHeight
        val aa = width / height
        var u = 0f; var v = 0f; var u2 = 1f; var v2 = 1f
        dx = x; dy = y; dw = width; dh = height
        when (fit) {
            // координати текстури GL: v = 0 — низ кадру, тому фокус «від верху» віддзеркалюємо
            Fit.COVER -> if (va > aa) { val k = aa / va; u = (1 - k) / 2; u2 = 1 - u } else { val k = va / aa; v = (1 - k) * (1 - focusY); v2 = v + k }
            Fit.CONTAIN -> if (va > aa) { dh = width / va; dy = y + (height - dh) / 2 } else { dw = height * va; dx = x + (width - dw) / 2 }
        }

        val sh = shader()
        batch.shader = sh
        sh.setUniformMatrix("u_stMatrix", frame.matrix)
        sh.setUniformf("u_uvRect", u, v, u2, v2)
        sh.setUniformf("u_size", dw, dh)
        sh.setUniformf("u_radius", radius.coerceAtMost(minOf(dw, dh) / 2f))
        sh.setUniformf("u_aa", screen.worldWidth / Gdx.graphics.width.coerceAtLeast(1))
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        batch.draw(frame.texture, dx, dy, dw, dh, u, v, u2, v2)
        batch.shader = null
        batch.setColor(1f, 1f, 1f, 1f)
    }

    private fun drawPoster(batch: Batch, parentAlpha: Float) {
        val t = poster ?: return
        val ta = t.width.toFloat() / t.height
        val aa = width / height
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        when (fit) {
            Fit.COVER -> {
                // кроп текстури під пропорцію актора (як ACover.coverRegion)
                if (ta > aa) { val cw = t.height * aa; batch.draw(t, x, y, width, height, ((t.width - cw) / 2).toInt(), 0, cw.toInt(), t.height, false, false) }
                else { val ch = t.width / aa; batch.draw(t, x, y, width, height, 0, ((t.height - ch) * focusY).toInt(), t.width, ch.toInt(), false, false) }
            }
            Fit.CONTAIN -> {
                if (ta > aa) { val h = width / ta; batch.draw(t, x, y + (height - h) / 2, width, h) } else { val w = height * ta; batch.draw(t, x + (width - w) / 2, y, w, height) }
            }
        }
        batch.setColor(1f, 1f, 1f, 1f)
    }

    companion object {
        private val VERT by lazy { Gdx.files.internal("shader/video/oesVS.glsl").readString() }
        /** Через VfxShaderCache: новий GL-контекст (GDXGame.create) скидає кеш — програма перекомпілюється. */
        fun shader(): ShaderProgram = VfxShaderCache.get("shader/video/oesFS.glsl", VERT)
    }
}
