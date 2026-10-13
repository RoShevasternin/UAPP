package com.vidtools.framefall.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.vfx.VfxImage
import com.vidtools.framefall.game.utils.vfx.effects.RadialRectEffect
import com.vidtools.framefall.game.utils.vfx.effects.RoundImageEffect
import kotlin.math.abs

// ─────────────────────────────────────────────────────────────────────────────
// ACover — мініатюра з заокругленими кутами (.th у прототипі).
// Без кадру — заглушка: радіальний градієнт свого відтінку + гліф плівки ph_glyph.
// Кадр приходить асинхронно (CoverCache) і проявляється fade-ом.
// ─────────────────────────────────────────────────────────────────────────────
class ACover(
    override val screen: AdvancedScreen,
    radius: Float,
) : AdvancedGroup() {

    private val phFx  = RadialRectEffect().apply { this.radius = radius }
    private val ph    = VfxImage(screen, screen.drawerUtil.whiteRegion, phFx)
    private val glyph = Image(assets.ph_glyph).apply { color.a = 0.85f; touchable = Touchable.disabled }
    private val imgFx = RoundImageEffect().apply { this.radius = radius }
    private val img   = VfxImage(screen, effect = imgFx).apply { isVisible = false }

    /** Ключ поточного запиту — відкидаємо застарілі відповіді (рядок перевикористали). */
    private var requestKey: String? = null

    init { touchable = Touchable.disabled }

    var radius: Float
        get() = imgFx.radius
        set(v) { imgFx.radius = v; phFx.radius = v }

    override fun addActorsOnGroup() {
        addAndFillActors(ph, img)
        addActor(glyph)
        layoutGlyph()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        layoutGlyph()
    }

    private fun layoutGlyph() {
        val s = width * 0.44f
        glyph.setBounds((width - s) / 2f, (height - s) / 2f, s, s)
    }

    /** Інший гліф заглушки (аудіо — нота замість плівки). */
    fun setGlyph(region: com.badlogic.gdx.graphics.g2d.TextureRegion) { glyph.drawable = TextureRegionDrawable(region) }

    fun setHueFrom(seed: String) {
        val h = (abs(seed.hashCode()) % 360) / 360f
        phFx.c0.set(hsl(h, 0.85f, 0.52f))
        phFx.c1.set(hsl(h, 0.75f, 0.20f))
        phFx.c2.set(hsl(h, 0.70f, 0.09f))
    }

    fun setVideo(video: Video?) {
        img.isVisible = false; glyph.isVisible = true
        if (video == null) { requestKey = null; setHueFrom("none"); return }
        setHueFrom(video.id)
        // Каталог: вбудований кадр (підібраний вручну, кращий за кадр з 5-ї секунди) — одразу, без диска
        if (video.thumbAsset != null) { gdxGame.covers.forAsset(video.thumbAsset)?.let { show(it, animate = false); requestKey = null; return } }
        val key = "v:${video.id}"
        requestKey = key
        gdxGame.covers.forVideo(video) { tex -> if (requestKey == key) show(tex) }
    }

    /** Вбудований кадр каталогу (assets/textures/thumbs). */
    fun setAsset(name: String) {
        requestKey = null
        setHueFrom(name)
        gdxGame.covers.forAsset(name)?.let { show(it, animate = false) }
    }

    fun setUrl(url: String?, seed: String = url ?: "") {
        img.isVisible = false; glyph.isVisible = true
        setHueFrom(seed)
        if (url == null) { requestKey = null; return }
        val key = "u:$url"
        requestKey = key
        gdxGame.covers.forUrl(url) { tex -> if (requestKey == key) show(tex) }
    }

    /** Статична текстура (банери Discover, фото). Кроп «cover» під пропорцію актора. */
    fun setTexture(tex: Texture) = show(tex, animate = false)

    private fun show(tex: Texture?, animate: Boolean = true) {
        tex ?: return
        img.drawable = TextureRegionDrawable(coverRegion(tex, width, height))
        img.isVisible = true
        glyph.isVisible = false
        if (animate) { img.color.a = 0f; img.addAction(Actions.fadeIn(0.25f)) }
    }

    companion object {
        /** CSS background-size: cover — центрований кроп текстури під пропорцію w×h. */
        fun coverRegion(tex: Texture, w: Float, h: Float): TextureRegion {
            val ta = tex.width.toFloat() / tex.height
            val aa = if (h > 0f) w / h else 1f
            return if (ta > aa) {
                val cw = (tex.height * aa).toInt()
                TextureRegion(tex, (tex.width - cw) / 2, 0, cw, tex.height)
            } else {
                val ch = (tex.width / aa).toInt()
                TextureRegion(tex, 0, (tex.height - ch) / 2, tex.width, ch)
            }
        }

        fun hsl(h: Float, s: Float, l: Float): Color {
            val q = if (l < 0.5f) l * (1 + s) else l + s - l * s
            val p = 2 * l - q
            fun hue(t0: Float): Float {
                var t = t0
                if (t < 0) t += 1f; if (t > 1) t -= 1f
                return when {
                    t < 1f / 6 -> p + (q - p) * 6 * t
                    t < 1f / 2 -> q
                    t < 2f / 3 -> p + (q - p) * (2f / 3 - t) * 6
                    else -> p
                }
            }
            return Color(hue(h + 1f / 3), hue(h), hue(h - 1f / 3), 1f)
        }
    }
}
