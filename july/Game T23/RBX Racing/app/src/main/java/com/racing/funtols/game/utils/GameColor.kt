package com.racing.funtols.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background   : Color = Color.valueOf("101010")
    val black_101010 : Color = Color.valueOf("101010")
    val black_1A1A1A : Color = Color.valueOf("1A1A1A")

    val white_77 : Color = Color.WHITE.cpy().apply { a = 0.77f }
    val black_70 : Color = Color.BLACK.cpy().apply { a = 0.70f }
}