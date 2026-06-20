package com.treprosure.starbxup.game.actors.panel.outfit.data

import com.treprosure.starbxup.game.actors.panel.outfit.OutfitItem
import com.treprosure.starbxup.game.utils.gdxGame

enum class AnimationsCategory(val title: String) {
    ALL     ("ALL"),
    EMOTIONS("EMOTIONS"),
    BUNDLES ("BUNDLES"),
}

object AnimationsData {

    // texture-и підстав свої з assetsAll
    fun items(): List<OutfitItem<AnimationsCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // EMOTIONS
            OutfitItem("GODLIKE"      , AnimationsCategory.EMOTIONS, a.listAnimations[0]),
            OutfitItem("SWISH"        , AnimationsCategory.EMOTIONS, a.listAnimations[1]),
            OutfitItem("MONKEY"       , AnimationsCategory.EMOTIONS, a.listAnimations[2]),
            OutfitItem("CHA-CHA"      , AnimationsCategory.EMOTIONS, a.listAnimations[3]),
            OutfitItem("HEISMAN POSE" , AnimationsCategory.EMOTIONS, a.listAnimations[4]),
            OutfitItem("AIR GUITAR"   , AnimationsCategory.EMOTIONS, a.listAnimations[5]),

            // BUNDLES
            OutfitItem("STYLISH CLIMB", AnimationsCategory.BUNDLES , a.listAnimations[6]),
            OutfitItem("STYLISH FALL" , AnimationsCategory.BUNDLES , a.listAnimations[7]),
            OutfitItem("STYLISH IDLE" , AnimationsCategory.BUNDLES , a.listAnimations[8]),
            OutfitItem("STYLISH JUMP" , AnimationsCategory.BUNDLES , a.listAnimations[9]),
            OutfitItem("STYLISH RUN"  , AnimationsCategory.BUNDLES , a.listAnimations[10]),
            OutfitItem("NINJA RUN"    , AnimationsCategory.BUNDLES , a.listAnimations[11]),
        )
    }
}