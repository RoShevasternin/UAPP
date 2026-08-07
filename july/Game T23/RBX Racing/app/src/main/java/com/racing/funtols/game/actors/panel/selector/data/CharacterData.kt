package com.racing.funtols.game.actors.panel.selector.data

import com.racing.funtols.game.data.ItemData
import com.racing.funtols.game.utils.gdxGame

object CharacterData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("ROBLOX GIRL" , a.listCharacter[0]),
            ItemData("ROBLOX BOY"  , a.listCharacter[1]),
            ItemData("WOMAN"       , a.listCharacter[2]),
            ItemData("MAN"         , a.listCharacter[3]),

            ItemData("ROBLOX GIRL" , a.listCharacter[4]),
            ItemData("SKYLER"      , a.listCharacter[5]),
            ItemData("DENNIS"      , a.listCharacter[6]),
            ItemData("LINDSEY"     , a.listCharacter[7]),
        )
    }
}