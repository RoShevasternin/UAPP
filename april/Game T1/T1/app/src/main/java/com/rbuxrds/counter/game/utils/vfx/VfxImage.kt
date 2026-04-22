package com.rbuxrds.counter.game.utils.vfx

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

/**
 * Зображення з шейдерним ефектом — БЕЗ FBO.
 *
 * Для per-pixel ефектів (HSL, saturation, bagCoins, lavaProgress, circleProgress)
 * де шейдер трансформує один піксель без потреби "бачити" сусідів.
 *
 * ─── Чому effect.batchShader а не effect.shader ──────────────────────────────
 *
 * SpriteBatch передає вершини у WORLD COORDINATES (наприклад x=500, y=1200).
 * Потрібен vertex shader з u_projTrans щоб перетворити world → NDC.
 *
 * Blit.VERT (effect.shader): gl_Position = a_position (без матриці)
 *   → вершини x=500,y=1200 → за межами NDC (-1..1) → все вирізається → невидимо!
 *
 * BATCH_VERT (effect.batchShader): gl_Position = u_projTrans * a_position
 *   → SpriteBatch підставляє правильну матрицю → нормальний рендер ✓
 *
 * ─── Використання ─────────────────────────────────────────────────────────────
 * ```kotlin
 * val cube = VfxImage(screen, region, HslEffect())
 * cube.setSize(200f, 200f)
 *
 * // Зміна ефекту:
 * (cube.effect as? HslEffect)?.setColor("22D3EE")
 *
 * // Без ефекту — звичайний Image:
 * cube.effect = null
 * ```
 */
open class VfxImage(
    override val screen: AdvancedScreen,
    drawable           : Drawable?  = null,
    var effect         : VfxEffect? = null,
) : AdvancedGroup() {

    constructor(screen: AdvancedScreen, region : TextureRegion, effect: VfxEffect? = null) :
            this(screen, TextureRegionDrawable(region), effect)

    constructor(screen: AdvancedScreen, texture: Texture, effect: VfxEffect? = null) :
            this(screen, TextureRegionDrawable(texture), effect)

    constructor(screen: AdvancedScreen, patch  : NinePatch, effect: VfxEffect? = null) :
            this(screen, NinePatchDrawable(patch), effect)

    private val image = Image()

    var drawable: Drawable?
        get()      = image.drawable
        set(value) { image.drawable = value }

    init { if (drawable != null) image.drawable = drawable }

    override fun addActorsOnGroup() {
        addAndFillActor(image)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null) return

        val fx = effect
        if (fx == null)    { super.draw(batch, parentAlpha); return }
        if (!fx.isEnabled) return

        val sp  = fx.batchShader
        val ctx = VfxContext(
            width, height,
            width.toInt().coerceAtLeast(1),
            height.toInt().coerceAtLeast(1)
        )

        batch.shader = sp
        fx.setUniforms(sp, ctx)

        // UV bounds → BATCH_VERT нормалізує до v_localUV 0..1
        val region = (image.drawable as? TextureRegionDrawable)?.region
        if (region != null) {
            sp.setUniformf("u_uvMin", region.u,  region.v)
            sp.setUniformf("u_uvMax", region.u2, region.v2)
        } else {
            sp.setUniformf("u_uvMin", 0f, 0f)
            sp.setUniformf("u_uvMax", 1f, 1f)
        }

        super.draw(batch, parentAlpha)
        batch.shader = null
    }
}