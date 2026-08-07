package com.diam.ondbit.game.actors.panel.selector.data

import com.diam.ondbit.game.data.ItemData
import com.diam.ondbit.game.utils.gdxGame

object PetsData {

    fun items(): List<ItemData> {
        val a = gdxGame.assetsAll
        return listOf(

            ItemData("Kaktus" , a.listPets[0]),
            ItemData("Fang"   , a.listPets[1]),
            ItemData("Hoot"   , a.listPets[2]),
            ItemData("Finn"   , a.listPets[3]),

            ItemData("Zasil"  , a.listPets[4]),
            ItemData("Arvon"  , a.listPets[5]),
            ItemData("Flash"  , a.listPets[6]),
            ItemData("Yeti"   , a.listPets[7]),
        )
    }
}