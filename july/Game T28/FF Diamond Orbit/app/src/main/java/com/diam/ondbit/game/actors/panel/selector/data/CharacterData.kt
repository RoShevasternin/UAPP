package com.diam.ondbit.game.actors.panel.selector.data

import com.diam.ondbit.game.data.ItemData
import com.diam.ondbit.game.utils.gdxGame

object CharacterData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("Ray"    , a.listCharacter[0]),
            ItemData("Morse"  , a.listCharacter[1]),
            ItemData("Nero"   , a.listCharacter[2]),
            ItemData("Rin"    , a.listCharacter[3]),

            ItemData("Oscar"  , a.listCharacter[4]),
            ItemData("Kassie" , a.listCharacter[5]),
            ItemData("Kairos" , a.listCharacter[6]),
            ItemData("Ryden"  , a.listCharacter[7]),
        )
    }
}