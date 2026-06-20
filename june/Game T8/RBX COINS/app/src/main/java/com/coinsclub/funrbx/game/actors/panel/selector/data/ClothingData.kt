package com.coinsclub.funrbx.game.actors.panel.selector.data

import com.coinsclub.funrbx.game.data.ItemData
import com.coinsclub.funrbx.game.utils.gdxGame

object ClothingData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("FLORAL BUTTON DOWN"    , a.listClothing[0]),
            ItemData("GRAMMY GOLDEN T-SHIRT" , a.listClothing[1]),
            ItemData("BLOUSE - TAN DOTTED"   , a.listClothing[2]),
            ItemData("ROBLOX T-SHIRT"        , a.listClothing[3]),
            ItemData("BASIC T-SHIRT - WHITE" , a.listClothing[4]),
            ItemData("REF OUTFIT - T-SHIRT"  , a.listClothing[5]),
            ItemData("GRAMMY VIOLET T-SHIRT" , a.listClothing[6]),
            ItemData("LA TRIBU T-SHIRT"      , a.listClothing[7]),
        )
    }
}