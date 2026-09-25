package com.racing.funtols.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background   : Color = Color.valueOf("101010")
    val black_101010 : Color = Color.valueOf("101010")
    val black_1A1A1A : Color = Color.valueOf("1A1A1A")

    val green_3DC44B : Color = Color.valueOf("3DC44B")   // FREE-кнопка, верх градієнта
    val green_2A9C36 : Color = Color.valueOf("2A9C36")   // FREE-кнопка, низ градієнта

    val white_22 : Color = Color.WHITE.cpy().apply { a = 0.22f }
    val white_55 : Color = Color.WHITE.cpy().apply { a = 0.55f }
    val white_77 : Color = Color.WHITE.cpy().apply { a = 0.77f }
    val black_70 : Color = Color.BLACK.cpy().apply { a = 0.70f }
}