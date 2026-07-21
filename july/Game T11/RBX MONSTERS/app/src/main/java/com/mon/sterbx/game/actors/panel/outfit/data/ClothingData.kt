package com.mon.sterbx.game.actors.panel.outfit.data

import com.mon.sterbx.game.actors.panel.outfit.OutfitItem
import com.mon.sterbx.game.utils.gdxGame

enum class ClothingCategory(val title: String) {
    ALL     ("ALL"),
    T_SHIRTS("T-SHIRTS"),
    SHIRTS  ("SHIRTS"),
    PANTS   ("PANTS"),
    SHOES   ("SHOES"),
}

object ClothingData {

    fun items(): List<OutfitItem<ClothingCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── T-SHIRTS ──
            OutfitItem("FOREST WARDEN TEE"  , ClothingCategory.T_SHIRTS, a.listOutfitClothing[0],  "Wear the power of nature"),
            OutfitItem("FROST WYVERN TEE"   , ClothingCategory.T_SHIRTS, a.listOutfitClothing[1],  "Bring icy magic wherever you go"),
            OutfitItem("MYSTIC OVERLORD TEE", ClothingCategory.T_SHIRTS, a.listOutfitClothing[2],  "Wrap yourself in ancient magic"),
            OutfitItem("ROBLOX T-SHIRT"     , ClothingCategory.T_SHIRTS, a.listOutfitClothing[3],  "A stylish tee inspired by the world of Roblox"),
            OutfitItem("ROBLOX JERSEY"      , ClothingCategory.T_SHIRTS, a.listOutfitClothing[4],  "Casual black T-shirt with a vibrant Roblox design"),
            OutfitItem("ROBLOX JERSEY"      , ClothingCategory.T_SHIRTS, a.listOutfitClothing[5],  "Casual black T-shirt with a vibrant Roblox design"),

            // ── SHIRTS ──
            OutfitItem("CHAMPION HOODIE"       , ClothingCategory.SHIRTS, a.listOutfitClothing[6],  "Unleash the power of blazing fire"),
            OutfitItem("GOLD GUARDIAN HOODIE"  , ClothingCategory.SHIRTS, a.listOutfitClothing[7],  "Protect every adventure with golden power"),
            OutfitItem("MYSTIC OVERLORD HOODIE", ClothingCategory.SHIRTS, a.listOutfitClothing[8],  "Wrap yourself in ancient magic"),
            OutfitItem("SHADOW ZIP HOODIE"     , ClothingCategory.SHIRTS, a.listOutfitClothing[9],  "Master the shadows with mystical energy"),
            OutfitItem("2 BADDIES GEM STONE"   , ClothingCategory.SHIRTS, a.listOutfitClothing[10], "Shine with a cosmic futuristic style"),
            OutfitItem("FLANNEL - GREEN"       , ClothingCategory.SHIRTS, a.listOutfitClothing[11], "Classic flannel for everyday adventures"),

            // ── PANTS ──
            OutfitItem("BLAZE CHAMPION JOGGERS", ClothingCategory.PANTS, a.listOutfitClothing[12], "Move fast and burn bright"),
            OutfitItem("WARDEN JOGGERS"        , ClothingCategory.PANTS, a.listOutfitClothing[13], "Comfort rooted in nature"),
            OutfitItem("FROST WYVERN JOGGERS"  , ClothingCategory.PANTS, a.listOutfitClothing[14], "Stay cool on every adventure"),
            OutfitItem("BAGGY JEAN SHORTS"     , ClothingCategory.PANTS, a.listOutfitClothing[15], "Relaxed denim for easy days"),
            OutfitItem("ASTRONAUT PANTS"       , ClothingCategory.PANTS, a.listOutfitClothing[16], "Gear built for deep space missions"),
            OutfitItem("MONSTER DENIM"         , ClothingCategory.PANTS, a.listOutfitClothing[17], "Classic jeans for every adventure"),

            // ── SHOES ──
            OutfitItem("SPEEDY SHOES"    , ClothingCategory.SHOES, a.listOutfitClothing[18], "Run faster with blazing speed"),
            OutfitItem("DEATH RUN"       , ClothingCategory.SHOES, a.listOutfitClothing[19], "Leave your rivals far behind"),
            OutfitItem("NERF SHOES"      , ClothingCategory.SHOES, a.listOutfitClothing[20], "Step into every battle with confidence"),
            OutfitItem("EDGY SCI-FI"     , ClothingCategory.SHOES, a.listOutfitClothing[21], "Future-ready boots for every mission"),
            OutfitItem("CANVAS SHOES"    , ClothingCategory.SHOES, a.listOutfitClothing[22], "Classic sneakers with a colorful style"),
            OutfitItem("NEON SNEAKERS"   , ClothingCategory.SHOES, a.listOutfitClothing[23], "Bright sneakers made to stand out"),
        )
    }
}