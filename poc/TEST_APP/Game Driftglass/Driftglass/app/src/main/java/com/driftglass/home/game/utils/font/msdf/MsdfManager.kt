package com.driftglass.home.game.utils.font.msdf

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Disposable
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.disposeAll
import com.driftglass.home.game.utils.font.msdf.effects.DropShadowEffect
import com.driftglass.home.game.utils.font.msdf.effects.InnerShadowEffect
import com.driftglass.home.game.utils.font.msdf.effects.MsdfEffectShader
import com.driftglass.home.game.utils.font.msdf.effects.StrokeEffect
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// MsdfManager — шрифти Driftglass + шейдери шарів + каталог стилів (як CSS прототипу).
// Усі шрифти з кирилицею (УКР / РУС / ENG): Geologica — заголовки й годинник,
// Onest — інтерфейс, JetBrains Mono — дрібні мітки. Джерела — ../assets/fonts.
// ─────────────────────────────────────────────────────────────────────────────
class MsdfManager : Disposable {

    val fillShader   = MsdfEffectShader("shader/msdf/msdf_fill.glsl")
    val strokeShader = MsdfEffectShader("shader/msdf/msdf_stroke.glsl")
    val shadowShader = MsdfEffectShader("shader/msdf/msdf_shadow.glsl")
    val innerShader  = MsdfEffectShader("shader/msdf/msdf_inner_shadow.glsl")

    // ── Display: Geologica ──
    val fontGeologica_Light     = MsdfFont("font/msdf/Geologica-Light.json", "font/msdf/Geologica-Light.png")
    val fontGeologica_Bold      = MsdfFont("font/msdf/Geologica-Bold.json", "font/msdf/Geologica-Bold.png")
    val fontGeologica_ExtraBold = MsdfFont("font/msdf/Geologica-ExtraBold.json", "font/msdf/Geologica-ExtraBold.png")

    // ── UI: Onest ──
    val fontOnest_Regular  = MsdfFont("font/msdf/Onest-Regular.json", "font/msdf/Onest-Regular.png")
    val fontOnest_Medium   = MsdfFont("font/msdf/Onest-Medium.json", "font/msdf/Onest-Medium.png")
    val fontOnest_SemiBold = MsdfFont("font/msdf/Onest-SemiBold.json", "font/msdf/Onest-SemiBold.png")
    val fontOnest_Bold     = MsdfFont("font/msdf/Onest-Bold.json", "font/msdf/Onest-Bold.png")

    // ── Mono: JetBrains Mono — мітки «ЗА БАЖАННЯМ», заголовки секцій ──
    val fontJetBrainsMono_SemiBold = MsdfFont("font/msdf/JetBrainsMono-SemiBold.json", "font/msdf/JetBrainsMono-SemiBold.png")

    fun stroke(weight: Float, color: Color) = StrokeEffect(weight, color, strokeShader)
    fun dropShadow(x: Float, y: Float, blur: Float, color: Color) = DropShadowEffect(x, y, blur, color, shadowShader)
    fun innerShadow(x: Float, y: Float, blur: Float, color: Color) = InnerShadowEffect(x, y, blur, color, innerShader)

    fun style(font: MsdfFont, sizePx: Float, color: Color = GameColor.text, block: MsdfStyle.() -> Unit = {}) =
        MsdfStyle(this, font, px(sizePx), color).apply(block)

    // ------------------------------------------------------------------------
    // Каталог стилів. Розмір — у px прототипу (переводить px()).
    // ------------------------------------------------------------------------
    /** Заголовки екранів і онбордингу: Geologica 800, letter-spacing −.03em. */
    fun disp(sizePx: Float, color: Color = GameColor.text) = style(fontGeologica_ExtraBold, sizePx, color) { letterSpacing = -3f }
    /** Заголовки карток: Geologica 700. */
    fun title(sizePx: Float, color: Color = GameColor.text) = style(fontGeologica_Bold, sizePx, color) { letterSpacing = -2f }
    /** Годинник лаунчера: Geologica 300, −.04em. */
    fun clock(sizePx: Float, color: Color = Color.WHITE) = style(fontGeologica_Light, sizePx, color) { letterSpacing = -4f }

    fun regular (sizePx: Float, color: Color = GameColor.text) = style(fontOnest_Regular,  sizePx, color)
    fun medium  (sizePx: Float, color: Color = GameColor.text) = style(fontOnest_Medium,   sizePx, color)
    fun semibold(sizePx: Float, color: Color = GameColor.text) = style(fontOnest_SemiBold, sizePx, color)
    fun bold    (sizePx: Float, color: Color = GameColor.text) = style(fontOnest_Bold,     sizePx, color)

    /** Мітки капсом: JetBrains Mono 600 з розрідкою. */
    fun label(sizePx: Float, color: Color = GameColor.dim, spacing: Float = 14f) =
        style(fontJetBrainsMono_SemiBold, sizePx, color) { letterSpacing = spacing }

    override fun dispose() {
        disposeAll(
            fillShader, strokeShader, shadowShader, innerShader,
            fontGeologica_Light, fontGeologica_Bold, fontGeologica_ExtraBold,
            fontOnest_Regular, fontOnest_Medium, fontOnest_SemiBold, fontOnest_Bold,
            fontJetBrainsMono_SemiBold,
        )
    }
}
