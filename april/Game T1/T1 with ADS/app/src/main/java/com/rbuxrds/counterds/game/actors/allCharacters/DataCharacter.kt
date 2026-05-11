package com.rbuxrds.counterds.game.actors.allCharacters

import com.badlogic.gdx.graphics.g2d.TextureRegion

data class DataCharacter(
    val name       : String,
    val region     : TextureRegion,
    val description: String,
)