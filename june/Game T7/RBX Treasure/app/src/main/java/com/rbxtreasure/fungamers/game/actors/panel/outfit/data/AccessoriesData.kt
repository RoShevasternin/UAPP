package com.rbxtreasure.fungamers.game.actors.panel.outfit.data

import com.rbxtreasure.fungamers.game.actors.panel.outfit.OutfitItem
import com.rbxtreasure.fungamers.game.utils.gdxGame

enum class AccessoriesCategory(val title: String) {
    ALL ("ALL"),
    FACE("FACE"),
    HEAD("HEAD"),
    NECK("NECK"),
}

object AccessoriesData {

    // texture-и підстав свої з assetsAll
    fun items(): List<OutfitItem<AccessoriesCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // FACE
            OutfitItem("GOGGLES"               , AccessoriesCategory.FACE, a.listAccessories[0]),
            OutfitItem("CYBORG"                , AccessoriesCategory.FACE, a.listAccessories[1]),
            OutfitItem("LASERFACE"             , AccessoriesCategory.FACE, a.listAccessories[2]),
            OutfitItem("CYBERPUNK"             , AccessoriesCategory.FACE, a.listAccessories[3]),
            OutfitItem("SKI MASK"              , AccessoriesCategory.FACE, a.listAccessories[4]),
            OutfitItem("PANCAKE"               , AccessoriesCategory.FACE, a.listAccessories[5]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , AccessoriesCategory.FACE, a.listAccessories[6]),

            // HEAD
            OutfitItem("MUMMY HAND"            , AccessoriesCategory.HEAD, a.listAccessories[7]),
            OutfitItem("DEATH METAL"           , AccessoriesCategory.HEAD, a.listAccessories[8]),
            OutfitItem("ULTIMATE VICTORY"      , AccessoriesCategory.HEAD, a.listAccessories[9]),
            OutfitItem("PLATINUM PIRATE"       , AccessoriesCategory.HEAD, a.listAccessories[10]),
            OutfitItem("HOT JAMS"              , AccessoriesCategory.HEAD, a.listAccessories[11]),
            OutfitItem("LADY LIBERTY"          , AccessoriesCategory.HEAD, a.listAccessories[12]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , AccessoriesCategory.HEAD, a.listAccessories[13]),

            // NECK
            OutfitItem("BLUESTEEL BLING"       , AccessoriesCategory.NECK, a.listAccessories[14]),
            OutfitItem("BLING $$"              , AccessoriesCategory.NECK, a.listAccessories[15]),
            OutfitItem("THE CROWN OF ROSES"    , AccessoriesCategory.NECK, a.listAccessories[16]),
            OutfitItem("DECKLACE"              , AccessoriesCategory.NECK, a.listAccessories[17]),
            OutfitItem("GOLD HERO"             , AccessoriesCategory.NECK, a.listAccessories[18]),
            OutfitItem("NERDY COMPUTER"        , AccessoriesCategory.NECK, a.listAccessories[19]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , AccessoriesCategory.NECK, a.listAccessories[20]),
        )
    }
}