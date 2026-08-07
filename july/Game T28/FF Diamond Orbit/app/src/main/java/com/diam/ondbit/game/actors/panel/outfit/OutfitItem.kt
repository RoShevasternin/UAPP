package com.diam.ondbit.game.actors.panel.outfit

import com.badlogic.gdx.graphics.Texture

data class OutfitItem<C>(
    val name     : String,
    val category : C,
    val texture  : Texture,
)