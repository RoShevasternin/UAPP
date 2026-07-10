package com.rbxrush.rushrbx.game.actors.panel.selector.data

import com.rbxrush.rushrbx.game.data.ItemData
import com.rbxrush.rushrbx.game.utils.gdxGame

object ClothingData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("FLORAL BUTTON DOWN"    , a.listClothing[0]),
            ItemData("GRAMMY GOLDEN T-SHIRT" , a.listClothing[1]),
            ItemData("BLOUSE - TAN DOTTED"   , a.listClothing[2]),

            ItemData("ROBLOX T-SHIRT"        , a.listClothing[3]),
            ItemData("ROBLOX JERSEY"         , a.listClothing[4]),
            ItemData("BASIC T-SHIRT - WHITE" , a.listClothing[5]),

            ItemData("REF OUTFIT - T-SHIRT"  , a.listClothing[6]),
            ItemData("GRAMMY VIOLET T-SHIRT" , a.listClothing[7]),
            ItemData("LA TRIBU T-SHIRT"      , a.listClothing[8]),

            ItemData("MASTERCARD T-SHIRT"    , a.listClothing[9]),
            ItemData("T-SHIRT TYE DYE"       , a.listClothing[10]),
            ItemData("ROBLOX T-SHIRT GRAY"   , a.listClothing[11]),
        )
    }
}