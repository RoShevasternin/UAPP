package com.racing.funtols.game.actors.panel.outfit.data

import com.racing.funtols.game.actors.panel.outfit.OutfitItem
import com.racing.funtols.game.utils.gdxGame

enum class AccessoriesCategory(val title: String) {
    ALL ("ALL"),
    FACE("FACE"),
    HEAD("HEAD"),
    NECK("NECK"),
}

object AccessoriesData {

    // ⚠ ВИМАГАЄ OUTFIT_ACCESSORIES count = 24 у SpriteManager.kt
    //   (зараз там 18 — з 18 цей файл крашне на listOutfitAccessories[18]).
    //   Розподіл:
    //     [0..7]   — FACE
    //     [8..15]  — HEAD
    //     [16..23] — NECK
    fun items(): List<OutfitItem<AccessoriesCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── FACE ──────────────────────────────────────────────────────────
            OutfitItem("GOGGLES"          , AccessoriesCategory.FACE, a.listOutfitAccessories[0]),
            OutfitItem("CYBORG"           , AccessoriesCategory.FACE, a.listOutfitAccessories[1]),
            OutfitItem("LASERFACE"        , AccessoriesCategory.FACE, a.listOutfitAccessories[2]),
            OutfitItem("CYBERPUNK"        , AccessoriesCategory.FACE, a.listOutfitAccessories[3]),
            OutfitItem("SKI MASK"         , AccessoriesCategory.FACE, a.listOutfitAccessories[4]),
            OutfitItem("PANCAKE"          , AccessoriesCategory.FACE, a.listOutfitAccessories[5]),
            OutfitItem("VAMPIRE"          , AccessoriesCategory.FACE, a.listOutfitAccessories[6]),
            OutfitItem("STAR"             , AccessoriesCategory.FACE, a.listOutfitAccessories[7]),

            // ── HEAD ──────────────────────────────────────────────────────────
            OutfitItem("MUMMY HAND"       , AccessoriesCategory.HEAD, a.listOutfitAccessories[8]),
            OutfitItem("DEATH METAL"      , AccessoriesCategory.HEAD, a.listOutfitAccessories[9]),
            OutfitItem("ULTIMATE VICTORY" , AccessoriesCategory.HEAD, a.listOutfitAccessories[10]),
            OutfitItem("PLATINUM PIRATE"  , AccessoriesCategory.HEAD, a.listOutfitAccessories[11]),
            OutfitItem("HOT JAMS"         , AccessoriesCategory.HEAD, a.listOutfitAccessories[12]),
            OutfitItem("FUZZY BUNNY"      , AccessoriesCategory.HEAD, a.listOutfitAccessories[13]),
            OutfitItem("BIGHEAD SANTA"    , AccessoriesCategory.HEAD, a.listOutfitAccessories[14]),
            OutfitItem("SANTA HAT"        , AccessoriesCategory.HEAD, a.listOutfitAccessories[15]),

            // ── NECK ──────────────────────────────────────────────────────────
            OutfitItem("BLUESTEEL BLING"  , AccessoriesCategory.NECK, a.listOutfitAccessories[16]),
            OutfitItem("BLING \$\$"       , AccessoriesCategory.NECK, a.listOutfitAccessories[17]),
            OutfitItem("THE CROWN OF ROSES", AccessoriesCategory.NECK, a.listOutfitAccessories[18]),
            OutfitItem("DECKLACE"         , AccessoriesCategory.NECK, a.listOutfitAccessories[19]),
            OutfitItem("GOLD HERO"        , AccessoriesCategory.NECK, a.listOutfitAccessories[20]),
            OutfitItem("NERDY COMPUTER"   , AccessoriesCategory.NECK, a.listOutfitAccessories[21]),
            OutfitItem("GOLD TROLL"       , AccessoriesCategory.NECK, a.listOutfitAccessories[22]),
            OutfitItem("BONE"             , AccessoriesCategory.NECK, a.listOutfitAccessories[23]),
        )
    }
}