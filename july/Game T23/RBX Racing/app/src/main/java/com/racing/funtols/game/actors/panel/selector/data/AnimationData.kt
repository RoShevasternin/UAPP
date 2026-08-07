package com.racing.funtols.game.actors.panel.selector.data

import com.racing.funtols.game.data.ItemData
import com.racing.funtols.game.utils.gdxGame

object AnimationData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("GODLIKE"          , a.listAnimations[0]),
            ItemData("SWISH"            , a.listAnimations[1]),
            ItemData("MONKEY"           , a.listAnimations[2]),
            ItemData("CHA-CHA"          , a.listAnimations[3]),

            ItemData("HEISMAN POSE"     , a.listAnimations[4]),
            ItemData("AIR GUITAR"       , a.listAnimations[5]),
            ItemData("SUPERHERO REVEAL" , a.listAnimations[6]),
            ItemData("HYPE DANCE"       , a.listAnimations[7]),
        )
    }
}