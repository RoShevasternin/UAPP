package com.rbuxrds.counter.game.actors.vfx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.vfx.HslEffect
import com.rbuxrds.counter.game.utils.vfx.VfxImage

/**
 * HSL перефарбування через VfxImage — БЕЗ FBO.
 * batch.shader напряму, нуль FBO операцій.
 * API збережено: setColorShader(hex), setColorShader(color), drawable.
 */
class AHslImage(
    override val screen: AdvancedScreen,
    drawable: Drawable? = null,
) : VfxImage(screen, drawable, HslEffect()) {

    constructor(screen: AdvancedScreen, region : TextureRegion) : this(screen,
        TextureRegionDrawable(region)
    )
    constructor(screen: AdvancedScreen, texture: Texture) : this(screen,
        TextureRegionDrawable(texture)
    )
    constructor(screen: AdvancedScreen, patch  : NinePatch) : this(screen, NinePatchDrawable(patch))

    private val hsl: HslEffect get() = effect as HslEffect

    fun setColorShader(hex: String, luminance: Float = 0f)   { hsl.setColor(hex, luminance) }
    fun setColorShader(color: Color, luminance: Float = 0f)  { hsl.setColor(color, luminance) }
    fun setHsl(hue: Float, saturation: Float = 1f, luminance: Float = 0f) {
        hsl.hue = hue; hsl.saturation = saturation; hsl.luminance = luminance
    }

    object Hue {
        const val RED    = HslEffect.Hue.RED
        const val ORANGE = HslEffect.Hue.ORANGE
        const val YELLOW = HslEffect.Hue.YELLOW
        const val GREEN  = HslEffect.Hue.GREEN
        const val CYAN   = HslEffect.Hue.CYAN
        const val BLUE   = HslEffect.Hue.BLUE
        const val PURPLE = HslEffect.Hue.PURPLE
        const val PINK   = HslEffect.Hue.PINK
    }
}