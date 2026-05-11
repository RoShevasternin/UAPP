package com.rbuxrds.counter.game.actors.label

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

class ALabelAutoFont(
    override val screen   : AdvancedScreen,
    text: String = "",
    private val labelStyle: LabelStyle,
    private val maxFontScale: Float = 5f,
    private val minFontScale: Float = 0.1f,
    private val fitMode: FitMode = FitMode.WIDTH,
    val isWrapWidth : Boolean = false,
    val isWrapHeight: Boolean = false,
) : AdvancedGroup() {

    val label = Label(text, labelStyle)
    private val glyphLayout = GlyphLayout()

    private var computedTextWidth  = 0f
    private var computedTextHeight = 0f

    private var isAdjusting = false

    // ------------------------------------------------------------------------
    // Prefs
    // ------------------------------------------------------------------------

    override fun getPrefWidth()  = if (isWrapWidth)  computedTextWidth  else super.getPrefWidth()
    override fun getPrefHeight() = if (isWrapHeight) computedTextHeight else super.getPrefHeight()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addAndFillActor(label)
        label.setAlignment(Align.center)
    }

    override fun sizeChanged() {
        super.sizeChanged()
        label.setSize(width, height)
        recalculate()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setText(newText: CharSequence?) {
        label.setText(newText)
        recalculate()
    }

    private fun recalculate() {
        if (isAdjusting) return
        isAdjusting = true

        adjustFontSize()

        computedTextWidth  = glyphLayout.width
        computedTextHeight = glyphLayout.height

        // Виставляємо свій розмір одразу — не чекаємо батька
        if (isWrapWidth  && computedTextWidth  > 0f) width  = computedTextWidth
        if (isWrapHeight && computedTextHeight > 0f) height = computedTextHeight

        if (isWrapWidth || isWrapHeight) invalidateHierarchy()

        isAdjusting = false
    }

    private fun adjustFontSize() {
        val fitSize = when (fitMode) {
            FitMode.WIDTH  -> width
            FitMode.HEIGHT -> height
            FitMode.MIN    -> minOf(width, height)
        }
        if (fitSize <= 0f) return

        // Зберігаємо оригінальний scale шрифту — не мутуємо назавжди
        val savedScaleX = labelStyle.font.data.scaleX
        val savedScaleY = labelStyle.font.data.scaleY

        var lo   = minFontScale
        var hi   = maxFontScale
        var best = minFontScale

        repeat(12) {
            val mid = (lo + hi) / 2f
            labelStyle.font.data.setScale(mid)
            glyphLayout.setText(labelStyle.font, label.text)

            val glyphSize = when (fitMode) {
                FitMode.WIDTH  -> glyphLayout.width
                FitMode.HEIGHT -> glyphLayout.height
                FitMode.MIN    -> maxOf(glyphLayout.width, glyphLayout.height)
            }

            if (glyphSize <= fitSize) { best = mid; lo = mid } else hi = mid
        }

        // Відновлюємо шарений шрифт — не залишаємо сліди
        labelStyle.font.data.setScale(savedScaleX, savedScaleY)

        // Застосовуємо scale тільки до цього label — не глобально
        label.setFontScale(best)

        // Міряємо фінальний розмір для isWrapWidth/isWrapHeight
        labelStyle.font.data.setScale(best)
        glyphLayout.setText(labelStyle.font, label.text)
        computedTextWidth  = glyphLayout.width
        computedTextHeight = glyphLayout.height
        labelStyle.font.data.setScale(savedScaleX, savedScaleY)
    }

    // ------------------------------------------------------------------------
    // Enum
    // ------------------------------------------------------------------------

    enum class FitMode { WIDTH, HEIGHT, MIN }
}