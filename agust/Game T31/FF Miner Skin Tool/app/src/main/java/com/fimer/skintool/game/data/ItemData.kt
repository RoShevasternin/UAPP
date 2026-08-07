package com.fimer.skintool.game.data

import com.badlogic.gdx.graphics.Texture

data class ItemData(
    val name     : String,
    val texture  : Texture,
    val desc     : String = "",
)