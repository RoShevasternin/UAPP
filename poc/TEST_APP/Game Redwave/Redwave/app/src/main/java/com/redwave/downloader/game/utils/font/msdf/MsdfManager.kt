package com.redwave.downloader.game.utils.font.msdf

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Disposable
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.disposeAll
import com.redwave.downloader.game.utils.font.msdf.effects.DropShadowEffect
import com.redwave.downloader.game.utils.font.msdf.effects.InnerShadowEffect
import com.redwave.downloader.game.utils.font.msdf.effects.MsdfEffectShader
import com.redwave.downloader.game.utils.font.msdf.effects.StrokeEffect
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// MsdfManager — шрифти Redwave + шейдери шарів + каталог стилів (як CSS-класи
// прототипу). Шрифти — ../assets/fonts/README.md.
//
// ⚠️ Archivo — ЛИШЕ фіксовані англійські рядки й цифри (кирилиці немає).
//    Усе від користувача (ID3, імена файлів, RSS, буфер) — Onest.
// ─────────────────────────────────────────────────────────────────────────────

class MsdfManager : Disposable {

    val fillShader   = MsdfEffectShader("shader/msdf/msdf_fill.glsl")
    val strokeShader = MsdfEffectShader("shader/msdf/msdf_stroke.glsl")
    val shadowShader = MsdfEffectShader("shader/msdf/msdf_shadow.glsl")
    val innerShader  = MsdfEffectShader("shader/msdf/msdf_inner_shadow.glsl")

    // ── Display: Archivo Expanded — ЛИШЕ фіксовані англійські рядки, без кирилиці! ──
    val fontArchivo_ExpandedExtraBold = MsdfFont("font/msdf/Archivo-ExpandedExtraBold.json", "font/msdf/Archivo-ExpandedExtraBold.png")
    val fontArchivo_ExpandedBold      = MsdfFont("font/msdf/Archivo-ExpandedBold.json", "font/msdf/Archivo-ExpandedBold.png")

    // ── UI / body: Onest — увесь динамічний текст ──
    val fontOnest_Regular  = MsdfFont("font/msdf/Onest-Regular.json", "font/msdf/Onest-Regular.png")
    val fontOnest_Medium   = MsdfFont("font/msdf/Onest-Medium.json", "font/msdf/Onest-Medium.png")
    val fontOnest_SemiBold = MsdfFont("font/msdf/Onest-SemiBold.json", "font/msdf/Onest-SemiBold.png")
    val fontOnest_Bold     = MsdfFont("font/msdf/Onest-Bold.json", "font/msdf/Onest-Bold.png")

    // ── Mono: JetBrains Mono — таймери, розміри, мітки ──
    val fontJetBrainsMono_Regular  = MsdfFont("font/msdf/JetBrainsMono-Regular.json", "font/msdf/JetBrainsMono-Regular.png")
    val fontJetBrainsMono_SemiBold = MsdfFont("font/msdf/JetBrainsMono-SemiBold.json", "font/msdf/JetBrainsMono-SemiBold.png")

    /** Обведення OUTSIDE. weight у дизайн-px. */
    fun stroke(weight: Float, color: Color) = StrokeEffect(weight, color, strokeShader)

    /** Тінь як у Figma: x,y (y+ = вниз), blur — усе в дизайн-px. Можна кілька. */
    fun dropShadow(x: Float, y: Float, blur: Float, color: Color) = DropShadowEffect(x, y, blur, color, shadowShader)

    /** Внутрішня тінь (Figma Inner shadow): x,y (y+ = вниз), blur у дизайн-px. */
    fun innerShadow(x: Float, y: Float, blur: Float, color: Color) = InnerShadowEffect(x, y, blur, color, innerShader)

    fun style(font: MsdfFont, sizePx: Float, color: Color = GameColor.text_FBF1F2, block: MsdfStyle.() -> Unit = {}) =
        MsdfStyle(this, font, px(sizePx), color).apply(block)

    // ------------------------------------------------------------------------
    // Каталог стилів. Назва = роль; розмір у px прототипу (переводить px()).
    // ------------------------------------------------------------------------
    /** .h-disp: Archivo Expanded 800, letter-spacing −.025em. */
    fun disp(sizePx: Float, color: Color = GameColor.text_FBF1F2) =
        style(fontArchivo_ExpandedExtraBold, sizePx, color) { letterSpacing = -2.5f }

    fun clock(sizePx: Float, color: Color = Color.WHITE) =
        style(fontArchivo_ExpandedBold, sizePx, color) { letterSpacing = -4f }

    fun regular (sizePx: Float, color: Color = GameColor.text_FBF1F2) = style(fontOnest_Regular,  sizePx, color)
    fun medium  (sizePx: Float, color: Color = GameColor.text_FBF1F2) = style(fontOnest_Medium,   sizePx, color)
    fun semibold(sizePx: Float, color: Color = GameColor.text_FBF1F2) = style(fontOnest_SemiBold, sizePx, color)
    fun bold    (sizePx: Float, color: Color = GameColor.text_FBF1F2) = style(fontOnest_Bold,     sizePx, color)

    fun mono    (sizePx: Float, color: Color = GameColor.muted_A8949B, spacing: Float = 0f) =
        style(fontJetBrainsMono_Regular, sizePx, color) { letterSpacing = spacing }
    fun monoSemi(sizePx: Float, color: Color = GameColor.muted_A8949B, spacing: Float = 0f) =
        style(fontJetBrainsMono_SemiBold, sizePx, color) { letterSpacing = spacing }

    override fun dispose() {
        disposeAll(
            fillShader, strokeShader, shadowShader, innerShader,
            fontArchivo_ExpandedExtraBold, fontArchivo_ExpandedBold,
            fontOnest_Regular, fontOnest_Medium, fontOnest_SemiBold, fontOnest_Bold,
            fontJetBrainsMono_Regular, fontJetBrainsMono_SemiBold,
        )
    }
}
