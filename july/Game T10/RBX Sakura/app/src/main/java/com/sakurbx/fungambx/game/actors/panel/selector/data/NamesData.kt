package com.sakurbx.fungambx.game.actors.panel.selector.data

import com.sakurbx.fungambx.game.data.ItemData
import com.sakurbx.fungambx.game.utils.gdxGame

object NamesData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("NINJA"      , a.listAnimPack[0]),
            ItemData("KNIGHT"     , a.listAnimPack[1]),
            ItemData("WEREWOLF"   , a.listAnimPack[2]),
            ItemData("SUPER HERO" , a.listAnimPack[3]),

            ItemData("VILLAIN"    , a.listAnimPack[4]),
            ItemData("HARD COUR"  , a.listAnimPack[5]),
            ItemData("ELDER"      , a.listAnimPack[6]),
            ItemData("VAMPIRE"    , a.listAnimPack[7]),
        )
    }
}