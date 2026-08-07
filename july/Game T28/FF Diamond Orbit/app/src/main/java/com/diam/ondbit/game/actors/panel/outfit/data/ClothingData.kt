package com.diam.ondbit.game.actors.panel.outfit.data

import com.diam.ondbit.game.actors.panel.outfit.OutfitItem
import com.diam.ondbit.game.utils.gdxGame

enum class ClothingCategory(val title: String) {
    ALL   ("ALL"),
    TOP   ("TOP"),
    BOTTOM("BOTTOM"),
    SHOES ("SHOES"),
}

object ClothingData {

    // Порядок ЗБІГАЄТЬСЯ з порядком у listOutfitClothing:
    //   [0..7]   — TOP
    //   [8..15]  — BOTTOM
    //   [16..23] — SHOES
    fun items(): List<OutfitItem<ClothingCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── TOP ───────────────────────────────────────────────────────────
            OutfitItem("The Cobra"          , ClothingCategory.TOP, a.listOutfitClothing[0]),
            OutfitItem("Uncharted Seas"     , ClothingCategory.TOP, a.listOutfitClothing[1]),
            OutfitItem("The Paradox"        , ClothingCategory.TOP, a.listOutfitClothing[2]),
            OutfitItem("Solar Commander"    , ClothingCategory.TOP, a.listOutfitClothing[3]),
            OutfitItem("Capt. Punisher"     , ClothingCategory.TOP, a.listOutfitClothing[4]),
            OutfitItem("Kings Sword"        , ClothingCategory.TOP, a.listOutfitClothing[5]),
            OutfitItem("Project Cosmos"     , ClothingCategory.TOP, a.listOutfitClothing[6]),
            OutfitItem("Frostfire"          , ClothingCategory.TOP, a.listOutfitClothing[7]),

            // ── BOTTOM ────────────────────────────────────────────────────────
            OutfitItem("Reeling Angler"     , ClothingCategory.BOTTOM, a.listOutfitClothing[8]),
            OutfitItem("Rampage"            , ClothingCategory.BOTTOM, a.listOutfitClothing[9]),
            OutfitItem("Uncharted Seas"     , ClothingCategory.BOTTOM, a.listOutfitClothing[10]),
            OutfitItem("Staple Wanderer"    , ClothingCategory.BOTTOM, a.listOutfitClothing[11]),
            OutfitItem("The Reel Style"     , ClothingCategory.BOTTOM, a.listOutfitClothing[12]),
            OutfitItem("Bandit"             , ClothingCategory.BOTTOM, a.listOutfitClothing[13]),
            OutfitItem("Shinjuku Influence" , ClothingCategory.BOTTOM, a.listOutfitClothing[14]),
            OutfitItem("The Paradox"        , ClothingCategory.BOTTOM, a.listOutfitClothing[15]),

            // ── SHOES ─────────────────────────────────────────────────────────
            OutfitItem("The Paradox"        , ClothingCategory.SHOES, a.listOutfitClothing[16]),
            OutfitItem("Frostfire"          , ClothingCategory.SHOES, a.listOutfitClothing[17]),
            OutfitItem("Essential Explorer" , ClothingCategory.SHOES, a.listOutfitClothing[18]),
            OutfitItem("BOOYAH Day"         , ClothingCategory.SHOES, a.listOutfitClothing[19]),
            OutfitItem("Essential 2 Elite"  , ClothingCategory.SHOES, a.listOutfitClothing[20]),
            OutfitItem("Reeling Fisher"     , ClothingCategory.SHOES, a.listOutfitClothing[21]),
            OutfitItem("Cyber Blast"        , ClothingCategory.SHOES, a.listOutfitClothing[22]),
            OutfitItem("Capt. Punisher"     , ClothingCategory.SHOES, a.listOutfitClothing[23]),
        )
    }
}