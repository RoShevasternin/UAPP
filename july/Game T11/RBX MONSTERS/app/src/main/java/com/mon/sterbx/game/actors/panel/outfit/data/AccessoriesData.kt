package com.mon.sterbx.game.actors.panel.outfit.data

import com.mon.sterbx.game.actors.panel.outfit.OutfitItem
import com.mon.sterbx.game.utils.gdxGame

enum class AccessoriesCategory(val title: String) {
    ALL ("ALL"),
    FACE("FACE"),
    HEAD("HEAD"),
    NECK("NECK"),
}

object AccessoriesData {

    fun items(): List<OutfitItem<AccessoriesCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── FACE ──
            OutfitItem("GOGGLES"   , AccessoriesCategory.FACE, a.listOutfitAccessories[0], "See every adventure with crystal-clear vision"),
            OutfitItem("CYBORG"    , AccessoriesCategory.FACE, a.listOutfitAccessories[1], "Upgrade your look with futuristic cyber gear"),
            OutfitItem("LASERFACE" , AccessoriesCategory.FACE, a.listOutfitAccessories[2], "Power up with advanced robotic technology"),
            OutfitItem("CYBERPUNK" , AccessoriesCategory.FACE, a.listOutfitAccessories[3], "Complete your outfit with a futuristic visor"),
            OutfitItem("SKI MASK"  , AccessoriesCategory.FACE, a.listOutfitAccessories[4], "Hide your identity with a bold new style"),
            OutfitItem("PANCAKE"   , AccessoriesCategory.FACE, a.listOutfitAccessories[5], "Wear a funny mask and stand out from the crowd"),

            // ── HEAD ──
            OutfitItem("MUMMY HAND"      , AccessoriesCategory.HEAD, a.listOutfitAccessories[6],  "Add a spooky touch to your outfit"),
            OutfitItem("DEATH METAL"     , AccessoriesCategory.HEAD, a.listOutfitAccessories[7],  "Rock every game with powerful beats"),
            OutfitItem("ULTIMATE VICTORY", AccessoriesCategory.HEAD, a.listOutfitAccessories[8],  "Wear the symbol of true champions"),
            OutfitItem("PLATINUM PIRATE" , AccessoriesCategory.HEAD, a.listOutfitAccessories[9],  "Sail into battle with a fearless skull"),
            OutfitItem("HOT JAMS"        , AccessoriesCategory.HEAD, a.listOutfitAccessories[10], "Turn up the heat with fiery music"),
            OutfitItem("LADY LIBERTY"    , AccessoriesCategory.HEAD, a.listOutfitAccessories[11], "Stand out with a bold spiked crown"),

            // ── NECK ──
            OutfitItem("BLUESTEEL BLING"   , AccessoriesCategory.NECK, a.listOutfitAccessories[12], "Shine with a premium steel necklace"),
            OutfitItem("BLING \$\$"        , AccessoriesCategory.NECK, a.listOutfitAccessories[13], "Show off your wealth with golden style"),
            OutfitItem("THE CROWN OF ROSES", AccessoriesCategory.NECK, a.listOutfitAccessories[14], "Bloom with elegance and natural beauty"),
            OutfitItem("DECKLACE"          , AccessoriesCategory.NECK, a.listOutfitAccessories[15], "Complete your outfit with a unique necklace"),
            OutfitItem("GOLD HERO"         , AccessoriesCategory.NECK, a.listOutfitAccessories[16], "Wear the chain of a true champion"),
            OutfitItem("NERDY COMPUTER"    , AccessoriesCategory.NECK, a.listOutfitAccessories[17], "Add a fun tech-inspired accessory"),
        )
    }
}