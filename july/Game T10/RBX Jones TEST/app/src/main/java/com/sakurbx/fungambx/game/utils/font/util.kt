package com.sakurbx.fungambx.game.utils.font

import com.badlogic.gdx.graphics.Color
import com.sakurbx.fungambx.game.utils.GameColor

fun FontParameter.setDoubleShadow(
    colorB: Color = GameColor.purple_4F0063,
    colorS: Color = GameColor.purple_E6A5FF,
): FontParameter {
    setBorder(0.5f, colorB)
    setShadow(1, 1, colorS)

    return this
}