package com.rbuxdrop.cougame.game.actors.label

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.rbuxdrop.cougame.game.utils.actor.addAndFillActor
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedGroup
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontGenerator
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import kotlin.math.roundToInt

class ALabel(
    override val screen   : AdvancedScreen,
    private val text      : String,
    private val color     : Color = Color.WHITE,
    private val parameter : FontParameter,
    private val generator : FontGenerator,
) : AdvancedGroup() {

    // Label одразу з дефолтним порожнім стилем
    val label = Label(text, LabelStyle(BitmapFont(), color))

    override fun getPrefWidth()  = label.prefWidth
    override fun getPrefHeight() = label.prefHeight

    override fun addActorsOnGroup() {
        val pxPerUnit = screen.scalerUItoScreen.toActual(1f).roundToInt().toFloat()
        val sizePx    = (parameter.size * pxPerUnit).roundToInt().coerceAtLeast(4)

        val cachedFont = generator.generateFont(parameter.copy().setSize(sizePx))
        val font = BitmapFont(cachedFont.data, cachedFont.regions, false).also {
            it.data.setScale(1f / pxPerUnit)
        }

        // Просто міняємо стиль вже існуючого label
        label.style = LabelStyle(font, color)
        addAndFillActor(label)
    }

    fun setText(value: String)      { label.setText(value) }
    fun setAlignment(align: Int)    { label.setAlignment(align) }
    fun setLabelColor(color: Color) {
        if (label.style.fontColor == color) return
        label.style = LabelStyle(label.style.font, color)
    }

    override fun dispose() {
        label.style.font.dispose()
        super.dispose()
    }
}