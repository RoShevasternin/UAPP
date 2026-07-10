package com.sakurbx.fungambx.game.actors.panel.outfit.data

import com.sakurbx.fungambx.game.actors.panel.outfit.OutfitItem
import com.sakurbx.fungambx.game.utils.gdxGame

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
            OutfitItem("KIMONO"                        , ClothingCategory.T_SHIRTS, a.listOutfitClothing[0]),
            OutfitItem("GRAMMY GOLDEN T-SHIRT"         , ClothingCategory.T_SHIRTS, a.listOutfitClothing[1]),
            OutfitItem("BLOUSE - TAN DOTTED"           , ClothingCategory.T_SHIRTS, a.listOutfitClothing[2]),
            OutfitItem("ROBLOX T-SHIRT"                , ClothingCategory.T_SHIRTS, a.listOutfitClothing[3]),
            OutfitItem("BASIC T-SHIRT - WHITE"         , ClothingCategory.T_SHIRTS, a.listOutfitClothing[4]),
            OutfitItem("REF OUTFIT - T-SHIRT"          , ClothingCategory.T_SHIRTS, a.listOutfitClothing[5]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.T_SHIRTS, a.listOutfitClothing[6]),

            // SHIRTS
            OutfitItem("JEAN JACKET"                   , ClothingCategory.SHIRTS  , a.listOutfitClothing[7]),
            OutfitItem("2 BADDIES GEM STONE JACKET"    , ClothingCategory.SHIRTS  , a.listOutfitClothing[8]),
            OutfitItem("REFLECTING LEATHER MOTO JACKET", ClothingCategory.SHIRTS  , a.listOutfitClothing[9]),
            OutfitItem("KNIT SWEATER - PINK"           , ClothingCategory.SHIRTS  , a.listOutfitClothing[10]),
            OutfitItem("FLANNEL - GREEN"               , ClothingCategory.SHIRTS  , a.listOutfitClothing[11]),
            OutfitItem("ROBLOX POLO SHIRT"             , ClothingCategory.SHIRTS  , a.listOutfitClothing[12]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.SHIRTS  , a.listOutfitClothing[13]),

            // PANTS
            OutfitItem("BAGGY JEAN SHORTS - BLACK"     , ClothingCategory.PANTS   , a.listOutfitClothing[14]),
            OutfitItem("CHIFFON SKIRT - LIGHT PINK"    , ClothingCategory.PANTS   , a.listOutfitClothing[15]),
            OutfitItem("LONG RUFFLE SKIRT - RED"       , ClothingCategory.PANTS   , a.listOutfitClothing[16]),
            OutfitItem("TENNIS-WHITE"                  , ClothingCategory.PANTS   , a.listOutfitClothing[17]),
            OutfitItem("ASTRONAUT PANTS RED"           , ClothingCategory.PANTS   , a.listOutfitClothing[18]),
            OutfitItem("ASTRONAUT PANTS"               , ClothingCategory.PANTS   , a.listOutfitClothing[19]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.PANTS   , a.listOutfitClothing[20]),

            // SHOES
            OutfitItem("SPEEDY SHOES"                  , ClothingCategory.SHOES   , a.listOutfitClothing[21]),
            OutfitItem("DEATH RUN"                     , ClothingCategory.SHOES   , a.listOutfitClothing[22]),
            OutfitItem("NERF SHOES"                    , ClothingCategory.SHOES   , a.listOutfitClothing[23]),
            OutfitItem("EDGY SCI-FI"                   , ClothingCategory.SHOES   , a.listOutfitClothing[24]),
            OutfitItem("SATIN POINT"                   , ClothingCategory.SHOES   , a.listOutfitClothing[25]),
            OutfitItem("CANVAS SHOES - BLACK & PURPLE" , ClothingCategory.SHOES   , a.listOutfitClothing[26]),
            OutfitItem("GRAMMY VIOLET T-SHIRT"         , ClothingCategory.SHOES   , a.listOutfitClothing[27]),
        )
    }
}