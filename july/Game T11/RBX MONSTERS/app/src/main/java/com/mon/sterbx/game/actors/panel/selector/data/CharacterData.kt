package com.mon.sterbx.game.actors.panel.selector.data

import com.mon.sterbx.game.data.ItemData
import com.mon.sterbx.game.utils.gdxGame

object CharacterData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("GOLD GUARDIAN"  , a.listCharacter[0], "Protects hidden treasures and golden RBX rewards"),
            ItemData("FOREST WARDEN"  , a.listCharacter[1], "Nature's champion who discovers lucky rewards"),
            ItemData("BLAZE CHAMPION" , a.listCharacter[2], "Fearless fighter guarding powerful rewards"),

            ItemData("MYSTIC OVERLORD", a.listCharacter[3], "Uses ancient magic to reveal rare treasures"),
            ItemData("LAVA TITAN"     , a.listCharacter[4], "Forged by fire and stronger than stone"),
            ItemData("FROST WYVERN"   , a.listCharacter[5], "Controls the frozen winds with icy power"),
        )
    }
}