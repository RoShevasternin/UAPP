package com.fimer.skintool.game.utils.font.msdf

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Disposable
import com.fimer.skintool.game.utils.disposeAll
import com.fimer.skintool.game.utils.font.msdf.effects.MsdfEffectShader
import com.fimer.skintool.game.utils.font.msdf.effects.StrokeEffect
import com.fimer.skintool.game.utils.font.msdf.effects.DropShadowEffect
import com.fimer.skintool.game.utils.font.msdf.effects.InnerShadowEffect

// ─────────────────────────────────────────────────────────────────────────────
// MsdfManager — єдина точка: шрифти + шейдери шарів.
// Кожен ефект має свій шейдер тут. Додати ефект = shader + factory-метод.
// ─────────────────────────────────────────────────────────────────────────────

class MsdfManager : Disposable {

    val fillShader   = MsdfEffectShader("shader/msdf/msdf_fill.glsl")
    val strokeShader = MsdfEffectShader("shader/msdf/msdf_stroke.glsl")
    val shadowShader = MsdfEffectShader("shader/msdf/msdf_shadow.glsl")
    val innerShader  = MsdfEffectShader("shader/msdf/msdf_inner_shadow.glsl")

    val fontNunitoSans_Black = MsdfFont(
        "font/msdf/NunitoSans-Black.json",
        "font/msdf/NunitoSans-Black.png",
    )
    val fontNunitoSans_Bold = MsdfFont(
        "font/msdf/NunitoSans-Bold.json",
        "font/msdf/NunitoSans-Bold.png",
    )
    val fontNunitoSans_Regular = MsdfFont(
        "font/msdf/NunitoSans-Regular.json",
        "font/msdf/NunitoSans-Regular.png",
    )
    val fontBowlbyOneSC_Regular = MsdfFont(
        "font/msdf/BowlbyOneSC-Regular.json",
        "font/msdf/BowlbyOneSC-Regular.png",
    )

    /** Обведення OUTSIDE. weight у дизайн-px. */
    fun stroke(weight: Float, color: Color) = StrokeEffect(weight, color, strokeShader)

    /** Тінь як у Figma: x,y (y+ = вниз), blur — усе в дизайн-px. Можна кілька. */
    fun dropShadow(x: Float, y: Float, blur: Float, color: Color) = DropShadowEffect(x, y, blur, color, shadowShader)

    /** Внутрішня тінь (Figma Inner shadow): x,y (y+ = вниз), blur у дизайн-px. */
    fun innerShadow(x: Float, y: Float, blur: Float, color: Color) = InnerShadowEffect(x, y, blur, color, innerShader)

    override fun dispose() {
        disposeAll(
            fillShader,
            strokeShader,
            shadowShader,
            innerShader,

            fontNunitoSans_Black,
            fontNunitoSans_Bold,
            fontNunitoSans_Regular,

            fontBowlbyOneSC_Regular,
        )
    }

    // ------------------------------------------------------------------------
    // Type
    // ------------------------------------------------------------------------
//    val GLOBAL_YELLOW by lazy { MsdfStyle(this, fontNunitoBlack, 90f)
//        .stroke(5f, GameColor.yellow)
//        .dropShadow(6f, 6f, 4f, GameColor.yellow)
//    }

}