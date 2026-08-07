package com.racing.funtols.game.actors.panel.outfit.data

import com.racing.funtols.game.actors.panel.outfit.OutfitItem
import com.racing.funtols.game.utils.gdxGame

enum class AnimationsCategory(val title: String) {
    ALL     ("ALL"),
    EMOTIONS("EMOTES"),
    BUNDLES ("BUNDLES"),
}

object AnimationsData {

    // listOutfitAnimations містить 12 текстур (OUTFIT_ANIMATIONS count = 12):
    //   [0..5]  — EMOTES  (білі силуети)
    //   [6..11] — BUNDLES (оранжевий персонаж)
    fun items(): List<OutfitItem<AnimationsCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── EMOTES ────────────────────────────────────────────────────────
            OutfitItem("GODLIKE"          , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[0]),
            OutfitItem("SWISH"            , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[1]),
            OutfitItem("MONKEY"           , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[2]),
            OutfitItem("CHA-CHA"          , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[3]),
            OutfitItem("HEISMAN POSE"     , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[4]),
            OutfitItem("AIR GUITAR"       , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[5]),

            // ── BUNDLES ───────────────────────────────────────────────────────
            OutfitItem("GODLIKE"          , AnimationsCategory.BUNDLES, a.listOutfitAnimations[6]),
            OutfitItem("SWISH"            , AnimationsCategory.BUNDLES, a.listOutfitAnimations[7]),
            OutfitItem("MONKEY"           , AnimationsCategory.BUNDLES, a.listOutfitAnimations[8]),
            OutfitItem("CHA-CHA"          , AnimationsCategory.BUNDLES, a.listOutfitAnimations[9]),
            OutfitItem("HEISMAN POSE"     , AnimationsCategory.BUNDLES, a.listOutfitAnimations[10]),
            OutfitItem("AIR GUITAR"       , AnimationsCategory.BUNDLES, a.listOutfitAnimations[11]),
        )
    }
}