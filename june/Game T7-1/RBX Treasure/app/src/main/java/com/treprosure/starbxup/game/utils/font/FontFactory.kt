package com.treprosure.starbxup.game.utils.font

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import kotlin.math.roundToInt

object FontFactory {

    fun create(
        screen    : AdvancedScreen,
        parameter : FontParameter,
        generator : FontGenerator,
        color     : Color = Color.WHITE,
    ): Label.LabelStyle {
        val pxPerUnit = screen.scalerUItoScreen.toActual(1f).roundToInt().toFloat()
        val sizePx    = (parameter.size * pxPerUnit).roundToInt().coerceAtLeast(4)

        val cached = generator.generateFont(parameter.copy().setSize(sizePx))
        val font   = BitmapFont(cached.data, cached.regions, false).also { it.data.setScale(1f / pxPerUnit) }

        // Реєструємо в екрані — dispose відбудеться автоматично разом з екраном
        screen.disposableSet.add(font)

        return Label.LabelStyle(font, color)
    }
}