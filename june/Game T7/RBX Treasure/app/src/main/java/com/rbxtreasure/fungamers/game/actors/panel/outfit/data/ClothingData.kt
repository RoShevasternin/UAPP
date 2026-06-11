package com.rbxtreasure.fungamers.game.actors.panel.outfit.data

import com.rbxtreasure.fungamers.game.actors.panel.outfit.OutfitItem
import com.rbxtreasure.fungamers.game.utils.gdxGame

enum class ClothingCategory(val title: String) {
    ALL     ("ALL"),
    T_SHIRTS("T-SHIRTS"),
    SHIRTS  ("SHIRTS"),
    PANTS   ("PANTS"),
    SHOES   ("SHOES"),
}

object ClothingData {

    // texture-и підстав свої з assetsAll
    fun items(): List<OutfitItem<ClothingCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // T-SHIRTS
            OutfitItem("FLORAL BUTTON DOWN"            , ClothingCategory.T_SHIRTS, a.listClothing[0]),
            OutfitItem("GRAMMY GOLDEN T-SHIRT"         , ClothingCategory.T_SHIRTS, a.listClothing[1]),
            OutfitItem("BLOUSE - TAN DOTTED"           , ClothingCategory.T_SHIRTS, a.listClothing[2]),
            OutfitItem("ROBLOX T-SHIRT"                , ClothingCategory.T_SHIRTS, a.listClothing[3]),
            OutfitItem("BASIC T-SHIRT - WHITE"         , ClothingCategory.T_SHIRTS, a.listClothing[4]),
            OutfitItem("REF OUTFIT - T-SHIRT"          , ClothingCategory.T_SHIRTS, a.listClothing[5]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.T_SHIRTS, a.listClothing[6]),

            // SHIRTS
            OutfitItem("JEAN JACKET"                   , ClothingCategory.SHIRTS  , a.listClothing[7]),
            OutfitItem("2 BADDIES GEM STONE JACKET"    , ClothingCategory.SHIRTS  , a.listClothing[8]),
            OutfitItem("REFLECTING LEATHER MOTO JACKET", ClothingCategory.SHIRTS  , a.listClothing[9]),
            OutfitItem("KNIT SWEATER - PINK"           , ClothingCategory.SHIRTS  , a.listClothing[10]),
            OutfitItem("FLANNEL - GREEN"               , ClothingCategory.SHIRTS  , a.listClothing[11]),
            OutfitItem("ROBLOX POLO SHIRT"             , ClothingCategory.SHIRTS  , a.listClothing[12]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.SHIRTS  , a.listClothing[13]),

            // PANTS
            OutfitItem("BAGGY JEAN SHORTS - BLACK"     , ClothingCategory.PANTS   , a.listClothing[14]),
            OutfitItem("CHIFFON SKIRT - LIGHT PINK"    , ClothingCategory.PANTS   , a.listClothing[15]),
            OutfitItem("LONG RUFFLE SKIRT - RED"       , ClothingCategory.PANTS   , a.listClothing[16]),
            OutfitItem("TENNIS-WHITE"                  , ClothingCategory.PANTS   , a.listClothing[17]),
            OutfitItem("ASTRONAUT PANTS RED"           , ClothingCategory.PANTS   , a.listClothing[18]),
            OutfitItem("ASTRONAUT PANTS"               , ClothingCategory.PANTS   , a.listClothing[19]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.PANTS   , a.listClothing[20]),

            // SHOES
            OutfitItem("SPEEDY SHOES"                  , ClothingCategory.SHOES   , a.listClothing[21]),
            OutfitItem("DEATH RUN"                     , ClothingCategory.SHOES   , a.listClothing[22]),
            OutfitItem("NERF SHOES"                    , ClothingCategory.SHOES   , a.listClothing[23]),
            OutfitItem("EDGY SCI-FI"                   , ClothingCategory.SHOES   , a.listClothing[24]),
            OutfitItem("SATIN POINT"                   , ClothingCategory.SHOES   , a.listClothing[25]),
            OutfitItem("CANVAS SHOES - BLACK & PURPLE" , ClothingCategory.SHOES   , a.listClothing[26]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.SHOES   , a.listClothing[27]),
        )
    }
}