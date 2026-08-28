package com.selftest.mindora.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("000000")

    val purple_9979FF : Color = Color.valueOf("9979FF")
    val purple_0F003E : Color = Color.valueOf("0F003E")
    val pink_E4D5FF   : Color = Color.valueOf("E4D5FF")
    val gray_7C7C7C   : Color = Color.valueOf("7C7C7C")
    val gray_3D3D3D   : Color = Color.valueOf("3D3D3D")

    val black_0A001D_80 : Color = Color.valueOf("0A001D").apply { a = 0.80f }

    val white_70 : Color = Color.WHITE.cpy().apply { a = 0.70f }
    val white_80 : Color = Color.WHITE.cpy().apply { a = 0.80f }
}