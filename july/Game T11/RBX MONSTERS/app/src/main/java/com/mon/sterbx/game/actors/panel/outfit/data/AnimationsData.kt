package com.mon.sterbx.game.actors.panel.outfit.data

import com.mon.sterbx.game.actors.panel.outfit.OutfitItem
import com.mon.sterbx.game.utils.gdxGame

enum class AnimationsCategory(val title: String) {
    ALL     ("ALL"),
    EMOTIONS("EMOTIONS"),
    BUNDLES ("BUNDLES"),
}

object AnimationsData {

    fun items(): List<OutfitItem<AnimationsCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── EMOTIONS ──
            OutfitItem("GODLIKE"      , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[0], "Hover with your arms raised in a legendary pose"),
            OutfitItem("SWISH"        , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[1], "Strike a stylish pose with smooth arm movements"),
            OutfitItem("MONKEY"       , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[2], "Act like a monkey with funny dance moves"),
            OutfitItem("CHA-CHA"      , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[3], "Dance the classic cha-cha with smooth steps"),
            OutfitItem("HEISMAN POSE" , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[4], "Show off the legendary Heisman stance"),
            OutfitItem("AIR GUITAR"   , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[5], "Rock out with an epic air guitar solo"),

            // ── BUNDLES ──
            OutfitItem("STYLISH CLIMB", AnimationsCategory.BUNDLES, a.listOutfitAnimations[6],  "Climb with confidence and show off your style"),
            OutfitItem("STYLISH FALL" , AnimationsCategory.BUNDLES, a.listOutfitAnimations[7],  "Fall with a fun pose and unique animation"),
            OutfitItem("STYLISH IDLE" , AnimationsCategory.BUNDLES, a.listOutfitAnimations[8],  "Stand out with a cool idle animation"),
            OutfitItem("STYLISH JUMP" , AnimationsCategory.BUNDLES, a.listOutfitAnimations[9],  "Jump high with a stylish victory pose"),
            OutfitItem("STYLISH RUN"  , AnimationsCategory.BUNDLES, a.listOutfitAnimations[10], "Run in style and impress everyone around"),
            OutfitItem("NINJA RUN"    , AnimationsCategory.BUNDLES, a.listOutfitAnimations[11], "Move like a ninja with lightning-fast speed"),
        )
    }
}