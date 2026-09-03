package com.selftest.mindora.game.utils.font.msdf

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Disposable
import com.selftest.mindora.game.utils.disposeAll
import com.selftest.mindora.game.utils.font.msdf.effects.MsdfEffectShader
import com.selftest.mindora.game.utils.font.msdf.effects.StrokeEffect
import com.selftest.mindora.game.utils.font.msdf.effects.DropShadowEffect
import com.selftest.mindora.game.utils.font.msdf.effects.InnerShadowEffect

// ─────────────────────────────────────────────────────────────────────────────
// MsdfManager — єдина точка: шрифти + шейдери шарів.
// Кожен ефект має свій шейдер тут. Додати ефект = shader + factory-метод.
// ─────────────────────────────────────────────────────────────────────────────

class MsdfManager : Disposable {

    val fillShader   = MsdfEffectShader("shader/msdf/msdf_fill.glsl")
    val strokeShader = MsdfEffectShader("shader/msdf/msdf_stroke.glsl")
    val shadowShader = MsdfEffectShader("shader/msdf/msdf_shadow.glsl")
    val innerShader  = MsdfEffectShader("shader/msdf/msdf_inner_shadow.glsl")

    val fontMontserrat_Regular = MsdfFont(
        "font/msdf/Montserrat-Regular.json",
        "font/msdf/Montserrat-Regular.png",
    )
    val fontMontserrat_Medium = MsdfFont(
        "font/msdf/Montserrat-Medium.json",
        "font/msdf/Montserrat-Medium.png",
    )
    val fontMontserrat_Bold = MsdfFont(
        "font/msdf/Montserrat-Bold.json",
        "font/msdf/Montserrat-Bold.png",
    )
    val fontMontserrat_Italic = MsdfFont(
        "font/msdf/Montserrat-Italic.json",
        "font/msdf/Montserrat-Italic.png",
    )

    val fontKarla_Bold = MsdfFont(
        "font/msdf/Karla-Bold.json",
        "font/msdf/Karla-Bold.png",
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

            fontMontserrat_Regular,
            fontMontserrat_Medium,
            fontMontserrat_Bold,
            fontMontserrat_Italic,

            fontKarla_Bold
        )
    }

    // ------------------------------------------------------------------------
    // Type
    // ------------------------------------------------------------------------
//    val FLYING_COIN by lazy { MsdfStyle(this, fontNunito_Black, 90f)
//        .stroke(5f, GameColor.purple_350080)
//        .dropShadow(6f, 6f, 4f, GameColor.purple_350080)
//    }

}