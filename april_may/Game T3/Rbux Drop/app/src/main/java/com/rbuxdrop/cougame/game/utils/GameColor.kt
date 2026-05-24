package com.rbuxdrop.cougame.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("19171C")
    val gary_7F    : Color = Color.valueOf("7F738C")
    val purple_3D  : Color = Color.valueOf("993DF5")
    val green_4B   : Color = Color.valueOf("14B830")

    val white_25      : Color = Color.WHITE.cpy().apply { a = 0.25f }
    val background_80 : Color = background.cpy().apply { a = 0.80f }
}