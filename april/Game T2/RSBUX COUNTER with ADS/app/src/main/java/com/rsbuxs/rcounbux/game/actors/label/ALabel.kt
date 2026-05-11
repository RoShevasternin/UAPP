package com.rsbuxs.rcounbux.game.actors.label

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.rsbuxs.rcounbux.game.utils.actor.addAndFillActor
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontGenerator
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import kotlin.math.roundToInt

class ALabel(
    override val screen   : AdvancedScreen,
    private val text      : String,
    private val color     : Color = Color.WHITE,
    private val parameter : FontParameter,
    private val generator : FontGenerator,
) : AdvancedGroup() {

    private lateinit var label: Label
    private var localFont: BitmapFont? = null

    fun getLabelOrNull(): Label? = if (::label.isInitialized) label else null

    override fun getPrefWidth(): Float {
        return getLabelOrNull()?.prefWidth ?: width
    }

    override fun getPrefHeight(): Float {
        return getLabelOrNull()?.prefHeight ?: height
    }

    override fun addActorsOnGroup() {
        val pxPerUnit  = screen.scalerUItoScreen.toActual(1f).roundToInt().toFloat()
        val sizePx     = (parameter.size * pxPerUnit).roundToInt().coerceAtLeast(4)  // ← мінімум 4, захист від density=0

        // Отримуємо базовий шрифт з кешу (генерується тільки один раз)
        val cachedFont = generator.generateFont(parameter.copy().setSize(sizePx))

        // Власний wrapper зі своїм scale — не забруднює кеш
        localFont = BitmapFont(cachedFont.data, cachedFont.regions, false).also {
            it.data.setScale(1f / pxPerUnit)
        }

        label = Label(text, LabelStyle(localFont, color))
        addAndFillActor(label)
    }

    fun setText(value: String)   { getLabelOrNull()?.setText(value) }
    fun setAlignment(align: Int) { getLabelOrNull()?.setAlignment(align) }
    fun setLabelColor(color: Color) {
        val label = getLabelOrNull() ?: return

        val currentColor = label.style.fontColor

        // якщо вже такий самий колір — нічого не робимо
        if (currentColor != null && currentColor == color) return

        label.style = Label.LabelStyle(label.style.font, color)
    }

    override fun dispose() {
        localFont?.dispose()  // dispose wrapper, не кешований шрифт
        super.dispose()
    }
}