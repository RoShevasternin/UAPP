package com.skindustry.skinly.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("FFFFFF")

    val gray_818181 : Color = Color.valueOf("818181")
    val gray_F2F2F2 : Color = Color.valueOf("F2F2F2")

    val white_25: Color = Color.WHITE.cpy().apply { a = 0.25f }
    val black_80: Color = Color.BLACK.cpy().apply { a = 0.80f }
}