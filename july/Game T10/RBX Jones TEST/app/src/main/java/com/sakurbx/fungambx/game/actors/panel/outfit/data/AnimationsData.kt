package com.sakurbx.fungambx.game.actors.panel.outfit.data

import com.sakurbx.fungambx.game.actors.panel.outfit.OutfitItem
import com.sakurbx.fungambx.game.utils.gdxGame
import kotlin.collections.get

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
            OutfitItem("GODLIKE"      , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[0]),
            OutfitItem("SWISH"        , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[1]),
            OutfitItem("MONKEY"       , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[2]),
            OutfitItem("CHA-CHA"      , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[3]),
            OutfitItem("HEISMAN POSE" , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[4]),
            OutfitItem("AIR GUITAR"   , AnimationsCategory.EMOTIONS, a.listOutfitAnimations[5]),

            // BUNDLES
            OutfitItem("STYLISH CLIMB", AnimationsCategory.BUNDLES , a.listOutfitAnimations[6]),
            OutfitItem("STYLISH FALL" , AnimationsCategory.BUNDLES , a.listOutfitAnimations[7]),
            OutfitItem("STYLISH IDLE" , AnimationsCategory.BUNDLES , a.listOutfitAnimations[8]),
            OutfitItem("STYLISH JUMP" , AnimationsCategory.BUNDLES , a.listOutfitAnimations[9]),
            OutfitItem("STYLISH RUN"  , AnimationsCategory.BUNDLES , a.listOutfitAnimations[10]),
            OutfitItem("NINJA RUN"    , AnimationsCategory.BUNDLES , a.listOutfitAnimations[11]),
        )
    }
}