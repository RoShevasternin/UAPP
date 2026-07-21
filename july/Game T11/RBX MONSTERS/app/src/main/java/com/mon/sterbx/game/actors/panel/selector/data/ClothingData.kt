package com.mon.sterbx.game.actors.panel.selector.data

import com.mon.sterbx.game.data.ItemData
import com.mon.sterbx.game.utils.gdxGame

object ClothingData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("FOREST WARDEN TEE" , a.listClothing[0], "Wear the power of nature"),
            ItemData("FROST WYVERN TEE"  , a.listClothing[1], "Bring icy magic wherever you go"),
            ItemData("MYSTIC OVERLORD"   , a.listClothing[2], "Wrap yourself in ancient magic"),

            ItemData("ROBLOX T-SHIRT"    , a.listClothing[3], "A stylish tee inspired by the world of Roblox"),
            ItemData("ROBLOX JERSEY"     , a.listClothing[4], "Casual black T-shirt with a vibrant Roblox design"),
            ItemData("ROBLOX JERSEY"     , a.listClothing[5], "Casual black T-shirt with a vibrant Roblox design"),
        )
    }
}