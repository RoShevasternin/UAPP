package com.rsbuxs.rcounbux.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("000000")
    val green_06   : Color = Color.valueOf("06E02A")
    val green_81   : Color = Color.valueOf("81EB33")
    val gray_40    : Color = Color.valueOf("404040")

    val white_25 : Color = Color.WHITE.cpy().apply { a = 0.25f }
    val white_10 : Color = Color.WHITE.cpy().apply { a = 0.10f }
    val black_80 : Color = Color.BLACK.cpy().apply { a = 0.80f }
}