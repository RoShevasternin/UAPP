package com.diam.ondbit.game.actors.panel.outfit.data

import com.diam.ondbit.game.actors.panel.outfit.OutfitItem
import com.diam.ondbit.game.utils.gdxGame

enum class EmotesCategory(val title: String) {
    ALL      ("ALL"),
    DEFAULT  ("DEFAULT"),
    TRANSFORM("TRANSFORM"),
    GROUP    ("GROUP"),
    SUPER    ("SUPER"),
}

object EmotesData {

    // Порядок ЗБІГАЄТЬСЯ з порядком у listOutfitEmotes (усього 25 текстур):
    //   [0..7]   — DEFAULT
    //   [8..15]  — TRANSFORM
    //   [16..23] — GROUP
    //   [24]     — SUPER
    fun items(): List<OutfitItem<EmotesCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── DEFAULT ───────────────────────────────────────────────────────
            OutfitItem("Hello!"              , EmotesCategory.DEFAULT, a.listOutfitEmotes[0]),
            OutfitItem("LOL"                 , EmotesCategory.DEFAULT, a.listOutfitEmotes[1]),
            OutfitItem("Provoke"             , EmotesCategory.DEFAULT, a.listOutfitEmotes[2]),
            OutfitItem("Applause"            , EmotesCategory.DEFAULT, a.listOutfitEmotes[3]),
            OutfitItem("Dab"                 , EmotesCategory.DEFAULT, a.listOutfitEmotes[4]),
            OutfitItem("Chicken"             , EmotesCategory.DEFAULT, a.listOutfitEmotes[5]),
            OutfitItem("Arm Wave"            , EmotesCategory.DEFAULT, a.listOutfitEmotes[6]),
            OutfitItem("Shoot Dance"         , EmotesCategory.DEFAULT, a.listOutfitEmotes[7]),

            // ── TRANSFORM ─────────────────────────────────────────────────────
            OutfitItem("Rampage Look"        , EmotesCategory.TRANSFORM, a.listOutfitEmotes[8]),
            OutfitItem("Cannibal Hunter"     , EmotesCategory.TRANSFORM, a.listOutfitEmotes[9]),
            OutfitItem("Devil Trigger"       , EmotesCategory.TRANSFORM, a.listOutfitEmotes[10]),
            OutfitItem("Scorpio Look"        , EmotesCategory.TRANSFORM, a.listOutfitEmotes[11]),
            OutfitItem("Frostfire Look"      , EmotesCategory.TRANSFORM, a.listOutfitEmotes[12]),
            OutfitItem("Last Paradox"        , EmotesCategory.TRANSFORM, a.listOutfitEmotes[13]),
            OutfitItem("Ninja's Ascension"   , EmotesCategory.TRANSFORM, a.listOutfitEmotes[14]),
            OutfitItem("Aurora Look"         , EmotesCategory.TRANSFORM, a.listOutfitEmotes[15]),

            // ── GROUP ─────────────────────────────────────────────────────────
            OutfitItem("Bend of Time"        , EmotesCategory.GROUP, a.listOutfitEmotes[16]),
            OutfitItem("Speed and Style"     , EmotesCategory.GROUP, a.listOutfitEmotes[17]),
            OutfitItem("Skyrage Skedaddle"   , EmotesCategory.GROUP, a.listOutfitEmotes[18]),
            OutfitItem("Goodies Time"        , EmotesCategory.GROUP, a.listOutfitEmotes[19]),
            OutfitItem("Brisk Gallop"        , EmotesCategory.GROUP, a.listOutfitEmotes[20]),
            OutfitItem("Cyclone Skate"       , EmotesCategory.GROUP, a.listOutfitEmotes[21]),
            OutfitItem("Dragon Rider"        , EmotesCategory.GROUP, a.listOutfitEmotes[22]),
            OutfitItem("Charge!"             , EmotesCategory.GROUP, a.listOutfitEmotes[23]),

            // ── SUPER ─────────────────────────────────────────────────────────
            OutfitItem("Toad Summon"         , EmotesCategory.SUPER, a.listOutfitEmotes[24]),
        )
    }
}