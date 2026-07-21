package com.mon.sterbx.game.actors.panel.selector.data

import com.mon.sterbx.game.data.ItemData
import com.mon.sterbx.game.utils.gdxGame

object AnimationData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("GODLIKE"      , a.listAnimations[0], "Hover with your arms raised in a legendary pose"),
            ItemData("SWISH"        , a.listAnimations[1], "Strike a stylish pose with smooth arm movements"),
            ItemData("MONKEY"       , a.listAnimations[2], "Act like a monkey with funny dance moves"),

            ItemData("CHA-CHA"      , a.listAnimations[3], "Dance the classic cha-cha with smooth steps"),
            ItemData("HEISMAN POSE" , a.listAnimations[4], "Show off the legendary Heisman stance."),
            ItemData("AIR GUITAR"   , a.listAnimations[5], "Rock out with an epic air guitar solo"),
        )
    }
}