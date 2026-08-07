package com.racing.funtols.game.utils.font.msdf

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Disposable
import com.racing.funtols.game.utils.disposeAll
import com.racing.funtols.game.utils.font.msdf.effects.MsdfEffectShader
import com.racing.funtols.game.utils.font.msdf.effects.StrokeEffect
import com.racing.funtols.game.utils.font.msdf.effects.DropShadowEffect
import com.racing.funtols.game.utils.font.msdf.effects.InnerShadowEffect

// ─────────────────────────────────────────────────────────────────────────────
// MsdfManager — єдина точка: шрифти + шейдери шарів.
// Кожен ефект має свій шейдер тут. Додати ефект = shader + factory-метод.
// ─────────────────────────────────────────────────────────────────────────────

class MsdfManager : Disposable {

    val fillShader   = MsdfEffectShader("shader/msdf/msdf_fill.glsl")
    val strokeShader = MsdfEffectShader("shader/msdf/msdf_stroke.glsl")
    val shadowShader = MsdfEffectShader("shader/msdf/msdf_shadow.glsl")
    val innerShader  = MsdfEffectShader("shader/msdf/msdf_inner_shadow.glsl")

    val fontBarlow_Bold = MsdfFont(
        "font/msdf/Barlow-Bold.json",
        "font/msdf/Barlow-Bold.png",
    )
    val fontBarlow_Regular = MsdfFont(
        "font/msdf/Barlow-Regular.json",
        "font/msdf/Barlow-Regular.png",
    )

    val fontTitilliumWeb_BoldItalic = MsdfFont(
        "font/msdf/TitilliumWeb-BoldItalic.json",
        "font/msdf/TitilliumWeb-BoldItalic.png",
    )

    val fontPoppins_Medium = MsdfFont(
        "font/msdf/Poppins-Medium.json",
        "font/msdf/Poppins-Medium.png",
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

            fontBarlow_Bold,
            fontBarlow_Regular,

            fontTitilliumWeb_BoldItalic,

            fontPoppins_Medium,
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