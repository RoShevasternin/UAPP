package com.sakurbx.fungambx.game.actors.panel.selector.data

import com.sakurbx.fungambx.game.data.ItemData
import com.sakurbx.fungambx.game.utils.gdxGame

object CharacterData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("SAKURA"             , a.listCharacter[0]),
            ItemData("AKARI"              , a.listCharacter[1]),
            ItemData("ROBLOX GIRL"        , a.listCharacter[2]),
            ItemData("ROBLOX BOY"         , a.listCharacter[3]),

            ItemData("WOMAN"              , a.listCharacter[4]),
            ItemData("MAN"                , a.listCharacter[5]),
            ItemData("ROBLOX GIRL"        , a.listCharacter[6]),
            ItemData("SKYLER"             , a.listCharacter[7]),
        )
    }
}