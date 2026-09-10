package com.rbxgolden.fungamems.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("000000")

    val orange_FE: Color = Color.valueOf("FE8800")
    val yellow_FF: Color = Color.valueOf("FFDD01")
    val gray_5C  : Color = Color.valueOf("5C6070")
    val green_28 : Color = Color.valueOf("28BE41")
    val green_1E : Color = Color.valueOf("1E9A33")   // FREE-кнопка, низ градієнта
    val green_16 : Color = Color.valueOf("167A28")   // FREE-кнопка, натиснута

    val white_22 : Color = Color.WHITE.cpy().apply { a = 0.22f }

    val white_25      : Color = Color.WHITE.cpy().apply { a = 0.25f }
    val background_80 : Color = background.cpy().apply { a = 0.80f }
}