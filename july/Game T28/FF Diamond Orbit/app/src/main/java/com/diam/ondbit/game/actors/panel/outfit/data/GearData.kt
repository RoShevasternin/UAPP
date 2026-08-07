package com.diam.ondbit.game.actors.panel.outfit.data

import com.diam.ondbit.game.actors.panel.outfit.OutfitItem
import com.diam.ondbit.game.utils.gdxGame

enum class GearCategory(val title: String) {
    ALL      ("ALL"),
    SKYBOARD ("SKYBOARD"),
    PARACHUTE("PARACHUTE"),
    VEHICLE  ("VEHICLE SKIN"),
    BACKPACK ("BACKPACK"),
}

object GearData {

    // Порядок ЗБІГАЄТЬСЯ з порядком у listOutfitGear:
    //   [0..7]   — SKYBOARD
    //   [8..15]  — PARACHUTE
    //   [16..23] — VEHICLE SKIN
    //   [24..31] — BACKPACK
    fun items(): List<OutfitItem<GearCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── SKYBOARD ──────────────────────────────────────────────────────
            OutfitItem("Default Skyboard"   , GearCategory.SKYBOARD, a.listOutfitGear[0]),
            OutfitItem("Cherry Blossom"     , GearCategory.SKYBOARD, a.listOutfitGear[1]),
            OutfitItem("The Artist"         , GearCategory.SKYBOARD, a.listOutfitGear[2]),
            OutfitItem("Burning Wreckage"   , GearCategory.SKYBOARD, a.listOutfitGear[3]),
            OutfitItem("Purple Sky"         , GearCategory.SKYBOARD, a.listOutfitGear[4]),
            OutfitItem("Clinks"             , GearCategory.SKYBOARD, a.listOutfitGear[5]),
            OutfitItem("Death From Above"   , GearCategory.SKYBOARD, a.listOutfitGear[6]),
            OutfitItem("Green Star"         , GearCategory.SKYBOARD, a.listOutfitGear[7]),

            // ── PARACHUTE ─────────────────────────────────────────────────────
            OutfitItem("Default Parachute"  , GearCategory.PARACHUTE, a.listOutfitGear[8]),
            OutfitItem("Green"              , GearCategory.PARACHUTE, a.listOutfitGear[9]),
            OutfitItem("Camouflage - Red"   , GearCategory.PARACHUTE, a.listOutfitGear[10]),
            OutfitItem("Camouflage - Olive" , GearCategory.PARACHUTE, a.listOutfitGear[11]),
            OutfitItem("Camouflage - Sand"  , GearCategory.PARACHUTE, a.listOutfitGear[12]),
            OutfitItem("Skull"              , GearCategory.PARACHUTE, a.listOutfitGear[13]),
            OutfitItem("Colored"            , GearCategory.PARACHUTE, a.listOutfitGear[14]),
            OutfitItem("Sky"                , GearCategory.PARACHUTE, a.listOutfitGear[15]),

            // ── VEHICLE SKIN ──────────────────────────────────────────────────
            OutfitItem("Default Pickup"     , GearCategory.VEHICLE, a.listOutfitGear[16]),
            OutfitItem("Jeep"               , GearCategory.VEHICLE, a.listOutfitGear[17]),
            OutfitItem("Tuk Tuk"            , GearCategory.VEHICLE, a.listOutfitGear[18]),
            OutfitItem("Amphibian"          , GearCategory.VEHICLE, a.listOutfitGear[19]),
            OutfitItem("Monster Truck"      , GearCategory.VEHICLE, a.listOutfitGear[20]),
            OutfitItem("Default Motorcycle" , GearCategory.VEHICLE, a.listOutfitGear[21]),
            OutfitItem("Sports Car"         , GearCategory.VEHICLE, a.listOutfitGear[22]),
            OutfitItem("Road Rager"         , GearCategory.VEHICLE, a.listOutfitGear[23]),

            // ── BACKPACK ──────────────────────────────────────────────────────
            OutfitItem("Default Backpack"   , GearCategory.BACKPACK, a.listOutfitGear[24]),
            OutfitItem("Backpack - Cyber"   , GearCategory.BACKPACK, a.listOutfitGear[25]),
            OutfitItem("Backpack - Bunny"   , GearCategory.BACKPACK, a.listOutfitGear[26]),
            OutfitItem("Unleash Inhibition" , GearCategory.BACKPACK, a.listOutfitGear[27]),
            OutfitItem("Puppy Lover"        , GearCategory.BACKPACK, a.listOutfitGear[28]),
            OutfitItem("Brother Chicken"    , GearCategory.BACKPACK, a.listOutfitGear[29]),
            OutfitItem("Sakura"             , GearCategory.BACKPACK, a.listOutfitGear[30]),
            OutfitItem("Cyan Illusion"      , GearCategory.BACKPACK, a.listOutfitGear[31]),
        )
    }
}