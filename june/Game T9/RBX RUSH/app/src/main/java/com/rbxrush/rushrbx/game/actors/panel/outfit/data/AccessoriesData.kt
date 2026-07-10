package com.rbxrush.rushrbx.game.actors.panel.outfit.data

import com.rbxrush.rushrbx.game.actors.panel.outfit.OutfitItem
import com.rbxrush.rushrbx.game.utils.gdxGame

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
            OutfitItem("GOGGLES"               , AccessoriesCategory.FACE, a.listOutfitAccessories[0]),
            OutfitItem("CYBORG"                , AccessoriesCategory.FACE, a.listOutfitAccessories[1]),
            OutfitItem("LASERFACE"             , AccessoriesCategory.FACE, a.listOutfitAccessories[2]),
            OutfitItem("CYBERPUNK"             , AccessoriesCategory.FACE, a.listOutfitAccessories[3]),
            OutfitItem("SKI MASK"              , AccessoriesCategory.FACE, a.listOutfitAccessories[4]),
            OutfitItem("PANCAKE"               , AccessoriesCategory.FACE, a.listOutfitAccessories[5]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , AccessoriesCategory.FACE, a.listOutfitAccessories[6]),

            // HEAD
            OutfitItem("MUMMY HAND"            , AccessoriesCategory.HEAD, a.listOutfitAccessories[7]),
            OutfitItem("DEATH METAL"           , AccessoriesCategory.HEAD, a.listOutfitAccessories[8]),
            OutfitItem("ULTIMATE VICTORY"      , AccessoriesCategory.HEAD, a.listOutfitAccessories[9]),
            OutfitItem("PLATINUM PIRATE"       , AccessoriesCategory.HEAD, a.listOutfitAccessories[10]),
            OutfitItem("HOT JAMS"              , AccessoriesCategory.HEAD, a.listOutfitAccessories[11]),
            OutfitItem("LADY LIBERTY"          , AccessoriesCategory.HEAD, a.listOutfitAccessories[12]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , AccessoriesCategory.HEAD, a.listOutfitAccessories[13]),

            // NECK
            OutfitItem("BLUESTEEL BLING"       , AccessoriesCategory.NECK, a.listOutfitAccessories[14]),
            OutfitItem("BLING $$"              , AccessoriesCategory.NECK, a.listOutfitAccessories[15]),
            OutfitItem("THE CROWN OF ROSES"    , AccessoriesCategory.NECK, a.listOutfitAccessories[16]),
            OutfitItem("DECKLACE"              , AccessoriesCategory.NECK, a.listOutfitAccessories[17]),
            OutfitItem("GOLD HERO"             , AccessoriesCategory.NECK, a.listOutfitAccessories[18]),
            OutfitItem("NERDY COMPUTER"        , AccessoriesCategory.NECK, a.listOutfitAccessories[19]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , AccessoriesCategory.NECK, a.listOutfitAccessories[20]),
        )
    }
}