package com.skindustry.skinly.game.actors.panel.homeSelect

import com.badlogic.gdx.graphics.Texture
import com.skindustry.skinly.game.utils.SelectedHomeType
import com.skindustry.skinly.game.utils.gdxGame

object SkinRepository {
    fun getCards(type: SelectedHomeType): List<Texture> {
        val a = gdxGame.assetsAll
        return when (type) {
            SelectedHomeType.T_SHIRT -> a.listP1
            SelectedHomeType.SHIRT   -> a.listP2
            SelectedHomeType.PANTS   -> a.listP3
        }
    }
}