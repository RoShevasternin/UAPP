package com.bossrbx.rbxcalculator.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("000000")

    val blue_335FFF : Color = Color.valueOf("335FFF")
    val gray_333333 : Color = Color.valueOf("333333")
    val gray_808080 : Color = Color.valueOf("808080")
    val gray_171717 : Color = Color.valueOf("171717")
    val green_55BF40: Color = Color.valueOf("55BF40")

    val white_25      : Color = Color.WHITE.cpy().apply { a = 0.25f }
    val white_50      : Color = Color.WHITE.cpy().apply { a = 0.50f }
    val background_90 : Color = background.cpy().apply { a = 0.90f }
}