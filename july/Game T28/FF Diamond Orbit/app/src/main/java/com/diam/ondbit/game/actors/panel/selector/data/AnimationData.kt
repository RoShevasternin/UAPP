package com.diam.ondbit.game.actors.panel.selector.data

import com.diam.ondbit.game.data.ItemData
import com.diam.ondbit.game.utils.gdxGame

object AnimationData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("Hello!"      , a.listAnimations[0]),
            ItemData("LOL"         , a.listAnimations[1]),
            ItemData("Provoke"     , a.listAnimations[2]),
            ItemData("Applause"    , a.listAnimations[3]),

            ItemData("Dab"         , a.listAnimations[4]),
            ItemData("Chicken"     , a.listAnimations[5]),
            ItemData("Arm Wave"    , a.listAnimations[6]),
            ItemData("Shoot Dance" , a.listAnimations[7]),
        )
    }
}