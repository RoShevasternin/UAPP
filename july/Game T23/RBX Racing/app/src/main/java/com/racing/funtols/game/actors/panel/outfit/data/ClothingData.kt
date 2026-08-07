package com.racing.funtols.game.actors.panel.outfit.data

import com.racing.funtols.game.actors.panel.outfit.OutfitItem
import com.racing.funtols.game.utils.gdxGame

enum class ClothingCategory(val title: String) {
    ALL     ("ALL"),
    T_SHIRTS("T-SHIRTS"),
    SHIRTS  ("SHIRTS"),
    PANTS   ("PANTS"),
    SHOES   ("SHOES"),
}

object ClothingData {

    // Порядок ЗБІГАЄТЬСЯ з порядком у listOutfitClothing:
    //   [0..7]   — T-SHIRTS
    //   [8..15]  — SHIRTS
    //   [16..23] — PANTS
    //   [24..31] — SHOES
    fun items(): List<OutfitItem<ClothingCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── T-SHIRTS ──────────────────────────────────────────────────────
            OutfitItem("CAMO TEE - GREEN"      , ClothingCategory.T_SHIRTS, a.listOutfitClothing[0]),
            OutfitItem("GRAMMY GOLDEN T-SHIRT" , ClothingCategory.T_SHIRTS, a.listOutfitClothing[1]),
            OutfitItem("BLOUSE - TAN DOTTED"   , ClothingCategory.T_SHIRTS, a.listOutfitClothing[2]),
            OutfitItem("BLOCK TEE - BLACK"     , ClothingCategory.T_SHIRTS, a.listOutfitClothing[3]),
            OutfitItem("BLOCK JERSEY"          , ClothingCategory.T_SHIRTS, a.listOutfitClothing[4]),
            OutfitItem("BASIC T-SHIRT - WHITE" , ClothingCategory.T_SHIRTS, a.listOutfitClothing[5]),
            OutfitItem("REF OUTFIT - T-SHIRT"  , ClothingCategory.T_SHIRTS, a.listOutfitClothing[6]),
            OutfitItem("GRAMMY VIOLET T-SHIRT" , ClothingCategory.T_SHIRTS, a.listOutfitClothing[7]),

            // ── SHIRTS ────────────────────────────────────────────────────────
            OutfitItem("JEAN JACKET"           , ClothingCategory.SHIRTS, a.listOutfitClothing[8]),
            OutfitItem("GEM STONE - DARK"      , ClothingCategory.SHIRTS, a.listOutfitClothing[9]),
            OutfitItem("GEM STONE - LIGHT"     , ClothingCategory.SHIRTS, a.listOutfitClothing[10]),
            OutfitItem("KNIT SWEATER - PINK"   , ClothingCategory.SHIRTS, a.listOutfitClothing[11]),
            OutfitItem("FLANNEL - GREEN"       , ClothingCategory.SHIRTS, a.listOutfitClothing[12]),
            OutfitItem("POLO SHIRT - BLACK"    , ClothingCategory.SHIRTS, a.listOutfitClothing[13]),
            OutfitItem("ZIP HOODIE - GREY"     , ClothingCategory.SHIRTS, a.listOutfitClothing[14]),
            OutfitItem("ZIP HOODIE - BLUE"     , ClothingCategory.SHIRTS, a.listOutfitClothing[15]),

            // ── PANTS ─────────────────────────────────────────────────────────
            OutfitItem("BAGGY JEAN SHORTS"     , ClothingCategory.PANTS, a.listOutfitClothing[16]),
            OutfitItem("CHIFFON SKIRT - PINK"  , ClothingCategory.PANTS, a.listOutfitClothing[17]),
            OutfitItem("LONG RUFFLE SKIRT"     , ClothingCategory.PANTS, a.listOutfitClothing[18]),
            OutfitItem("TENNIS SKIRT - WHITE"  , ClothingCategory.PANTS, a.listOutfitClothing[19]),
            OutfitItem("ASTRONAUT PANTS - RED" , ClothingCategory.PANTS, a.listOutfitClothing[20]),
            OutfitItem("ASTRONAUT PANTS"       , ClothingCategory.PANTS, a.listOutfitClothing[21]),
            OutfitItem("NATURE ARCHER PANTS"   , ClothingCategory.PANTS, a.listOutfitClothing[22]),
            OutfitItem("CYBERPUNK PANTS"       , ClothingCategory.PANTS, a.listOutfitClothing[23]),

            // ── SHOES ─────────────────────────────────────────────────────────
            OutfitItem("SPEEDY SHOES"          , ClothingCategory.SHOES, a.listOutfitClothing[24]),
            OutfitItem("DEATH RUN"             , ClothingCategory.SHOES, a.listOutfitClothing[25]),
            OutfitItem("NERF SHOES"            , ClothingCategory.SHOES, a.listOutfitClothing[26]),
            OutfitItem("EDGY SCI-FI"           , ClothingCategory.SHOES, a.listOutfitClothing[27]),
            OutfitItem("SATIN POINT"           , ClothingCategory.SHOES, a.listOutfitClothing[28]),
            OutfitItem("CANVAS SHOES - BLACK"  , ClothingCategory.SHOES, a.listOutfitClothing[29]),
            OutfitItem("GREEN SNEAKERS"        , ClothingCategory.SHOES, a.listOutfitClothing[30]),
            OutfitItem("BLUE SNEAKERS"         , ClothingCategory.SHOES, a.listOutfitClothing[31]),
        )
    }
}