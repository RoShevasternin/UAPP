package com.rbuxrds.counterds.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("151619")

    val gray_5C6070 : Color = Color.valueOf("5C6070")
    val gray_23252A : Color = Color.valueOf("23252A")
    val blue_335FFF : Color = Color.valueOf("335FFF")
    val green_22C55E: Color = Color.valueOf("22C55E")   // FREE-кнопка, верх градієнта
    val green_16A34A: Color = Color.valueOf("16A34A")   // FREE-кнопка, низ градієнта
    val green_0E7A34: Color = Color.valueOf("0E7A34")   // FREE-кнопка, натиснута

    val white_22 : Color = Color.WHITE.cpy().apply { a = 0.22f }

    val white_55 : Color = Color.WHITE.cpy().apply { a = 0.55f }
    val black_80 : Color = Color.BLACK.cpy().apply { a = 0.80f }
}