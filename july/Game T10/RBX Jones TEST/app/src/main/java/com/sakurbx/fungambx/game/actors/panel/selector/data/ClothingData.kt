package com.sakurbx.fungambx.game.actors.panel.selector.data

import com.sakurbx.fungambx.game.data.ItemData
import com.sakurbx.fungambx.game.utils.gdxGame

object ClothingData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("KIMONO PINK"           , a.listClothing[0]),
            ItemData("GRAMMY GOLDEN T-SHIRT" , a.listClothing[1]),
            ItemData("BLOUSE - TAN DOTTED"   , a.listClothing[2]),
            ItemData("NINJA T-SHIRT"         , a.listClothing[3]),

            ItemData("BASIC T-SHIRT - WHITE" , a.listClothing[4]),
            ItemData("KIMONO BLACK"          , a.listClothing[5]),
            ItemData("DRAGON T-SHIRT"        , a.listClothing[6]),
            ItemData("JAPAN WHITE T-SHIRT"   , a.listClothing[7]),
        )
    }
}