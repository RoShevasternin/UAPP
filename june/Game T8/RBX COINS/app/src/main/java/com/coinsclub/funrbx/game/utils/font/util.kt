package com.coinsclub.funrbx.game.utils.font

import com.badlogic.gdx.graphics.Color

fun FontParameter.setBorderAndShadow(
    border     : Float = 3f,
    borderColor: Color = Color.BLACK,
    shadowX    : Int   = 4,
    shadowY    : Int   = 2,
    shadowColor: Color = Color.BLACK
): FontParameter {
    return setBorder(border, Color.BLACK).setShadow(shadowX, shadowY, Color.BLACK)
}