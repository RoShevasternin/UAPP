package com.rbxrush.rushrbx.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("1160BC")

    val black_2C2C2C  : Color = Color.valueOf("2C2C2C")
    val gray_817E78   : Color = Color.valueOf("817E78")
    val yellow_FACA4F : Color = Color.valueOf("FACA4F")

    val green_3DC44B : Color = Color.valueOf("3DC44B")   // FREE-кнопка, верх градієнта
    val green_2A9C36 : Color = Color.valueOf("2A9C36")   // FREE-кнопка, низ градієнта

    val white_22 : Color = Color.WHITE.cpy().apply { a = 0.22f }
    val white_55 : Color = Color.WHITE.cpy().apply { a = 0.55f }

    val black_60: Color = Color.BLACK.cpy().apply { a = 0.80f }
}