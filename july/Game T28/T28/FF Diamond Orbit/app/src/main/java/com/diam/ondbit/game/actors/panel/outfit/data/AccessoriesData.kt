package com.diam.ondbit.game.actors.panel.outfit.data

import com.diam.ondbit.game.actors.panel.outfit.OutfitItem
import com.diam.ondbit.game.utils.gdxGame

enum class AccessoriesCategory(val title: String) {
    ALL ("ALL"),
    HEAD("HEAD"),
    MASK("MASK"),
}

object AccessoriesData {

    // Порядок ЗБІГАЄТЬСЯ з порядком у listOutfitAccessories (усього 16 текстур):
    //   [0..7]  — HEAD
    //   [8..15] — MASK
    fun items(): List<OutfitItem<AccessoriesCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── HEAD ──────────────────────────────────────────────────────────
            OutfitItem("Essential Explorer" , AccessoriesCategory.HEAD, a.listOutfitAccessories[0]),
            OutfitItem("The Reel Style"     , AccessoriesCategory.HEAD, a.listOutfitAccessories[1]),
            OutfitItem("OG Hero in Action"  , AccessoriesCategory.HEAD, a.listOutfitAccessories[2]),
            OutfitItem("Project Quality"    , AccessoriesCategory.HEAD, a.listOutfitAccessories[3]),
            OutfitItem("VR Ready"           , AccessoriesCategory.HEAD, a.listOutfitAccessories[4]),
            OutfitItem("Shinjuku Influence" , AccessoriesCategory.HEAD, a.listOutfitAccessories[5]),
            OutfitItem("Reeling Fisher"     , AccessoriesCategory.HEAD, a.listOutfitAccessories[6]),
            OutfitItem("Shibuya Idol"       , AccessoriesCategory.HEAD, a.listOutfitAccessories[7]),

            // ── MASK ──────────────────────────────────────────────────────────
            OutfitItem("Solar Commander"    , AccessoriesCategory.MASK, a.listOutfitAccessories[8]),
            OutfitItem("The Reel Style"     , AccessoriesCategory.MASK, a.listOutfitAccessories[9]),
            OutfitItem("Shinjuku Influence" , AccessoriesCategory.MASK, a.listOutfitAccessories[10]),
            OutfitItem("OG Hero in Action"  , AccessoriesCategory.MASK, a.listOutfitAccessories[11]),
            OutfitItem("Star General"       , AccessoriesCategory.MASK, a.listOutfitAccessories[12]),
            OutfitItem("Wildfire Vagabond"  , AccessoriesCategory.MASK, a.listOutfitAccessories[13]),
            OutfitItem("The Real Style"     , AccessoriesCategory.MASK, a.listOutfitAccessories[14]),
            OutfitItem("Bioforge"           , AccessoriesCategory.MASK, a.listOutfitAccessories[15]),
        )
    }
}