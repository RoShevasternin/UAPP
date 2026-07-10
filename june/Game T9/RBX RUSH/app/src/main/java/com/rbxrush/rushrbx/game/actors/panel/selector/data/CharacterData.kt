package com.rbxrush.rushrbx.game.actors.panel.selector.data

import com.rbxrush.rushrbx.game.data.ItemData
import com.rbxrush.rushrbx.game.utils.gdxGame

object CharacterData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("ROBLOX GIRL"        , a.listCharacter[0]),
            ItemData("ROBLOX BOY"         , a.listCharacter[1]),
            ItemData("WOMAN"              , a.listCharacter[2]),
            ItemData("MAN"                , a.listCharacter[3]),

            ItemData("ROBLOX GIRL"        , a.listCharacter[4]),
            ItemData("SKYLER"             , a.listCharacter[5]),
            ItemData("DENNIS"             , a.listCharacter[6]),
            ItemData("LINDSEY"            , a.listCharacter[7]),

            ItemData("KENNETH"            , a.listCharacter[8]),
            ItemData("CINDY"              , a.listCharacter[9]),
            ItemData("KNIGHTS OF REDCLIFF", a.listCharacter[10]),
            ItemData("DROP DEAD TEDD"     , a.listCharacter[11]),
        )
    }
}