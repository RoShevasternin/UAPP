package com.coinsclub.funrbx.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("FFFFFF")

    val white_FFF5E3  : Color = Color.valueOf("FFF5E3")
    val white_D9D4EB  : Color = Color.valueOf("D9D4EB")
    val purple_421870 : Color = Color.valueOf("421870")
    val yellow_DFA008 : Color = Color.valueOf("DFA008")
    val gray_837B9D   : Color = Color.valueOf("837B9D")
    val gray_A5A5A6   : Color = Color.valueOf("A5A5A6")
    val green_6EF033  : Color = Color.valueOf("6EF033")

    val green_3DC44B : Color = Color.valueOf("3DC44B")   // FREE-кнопка, верх градієнта
    val green_2A9C36 : Color = Color.valueOf("2A9C36")   // FREE-кнопка, низ градієнта

    val white_22 : Color = Color.WHITE.cpy().apply { a = 0.22f }
    val white_55 : Color = Color.WHITE.cpy().apply { a = 0.55f }

    val black_60: Color = Color.BLACK.cpy().apply { a = 0.80f }
}