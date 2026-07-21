package com.mon.sterbx.game.actors.panel.outfit.data

import com.mon.sterbx.game.actors.panel.outfit.OutfitItem
import com.mon.sterbx.game.utils.gdxGame

enum class HeadCategory(val title: String) {
    ALL        ("ALL"),
    FACE_SHAPES("FACE SHAPES"),
    FACE_LOOK  ("FACE LOOK"),
    BODY_SHAPE ("BODY SHAPE"),
}

object HeadData {

    fun items(): List<OutfitItem<HeadCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── FACE SHAPES ──
            OutfitItem("BLOCKHEAD"  , HeadCategory.FACE_SHAPES, a.listOutfitHead[0], "The classic blocky look everyone loves"),
            OutfitItem("ROUNDY"     , HeadCategory.FACE_SHAPES, a.listOutfitHead[1], "Smooth and simple for a friendly look"),
            OutfitItem("TRIM"       , HeadCategory.FACE_SHAPES, a.listOutfitHead[2], "Clean edges with a modern style"),
            OutfitItem("OCTOBLOX"   , HeadCategory.FACE_SHAPES, a.listOutfitHead[3], "Stand out with a unique geometric shape"),
            OutfitItem("ROX BOX"    , HeadCategory.FACE_SHAPES, a.listOutfitHead[4], "Bold, blocky, and ready for adventure"),
            OutfitItem("ERASER HEAD", HeadCategory.FACE_SHAPES, a.listOutfitHead[5], "Minimalist design with a classic feel"),

            // ── FACE LOOK ──
            OutfitItem("O.O"              , HeadCategory.FACE_LOOK, a.listOutfitHead[6],  "Keep a calm and mysterious expression"),
            OutfitItem("CLASSIC GOOF"     , HeadCategory.FACE_LOOK, a.listOutfitHead[7],  "Show your playful and goofy side"),
            OutfitItem("GLEE"             , HeadCategory.FACE_LOOK, a.listOutfitHead[8],  "Smile with pure happiness and joy"),
            OutfitItem("CHILL"            , HeadCategory.FACE_LOOK, a.listOutfitHead[9],  "Stay relaxed in every adventure"),
            OutfitItem("CHECK IT"         , HeadCategory.FACE_LOOK, a.listOutfitHead[10], "Look confident with a clever grin"),
            OutfitItem("EXISTENTIAL ANGST", HeadCategory.FACE_LOOK, a.listOutfitHead[11], "Wear a face full of deep thoughts"),

            // ── BODY SHAPE ──
            OutfitItem("TORSO 1"    , HeadCategory.BODY_SHAPE, a.listOutfitHead[12], "Build your style from the center"),
            OutfitItem("LEFT LEG 1" , HeadCategory.BODY_SHAPE, a.listOutfitHead[13], "Complete your look one step at a time"),
            OutfitItem("RIGHT LEG 1", HeadCategory.BODY_SHAPE, a.listOutfitHead[14], "Walk with confidence and style"),
            OutfitItem("LEFT ARM 1" , HeadCategory.BODY_SHAPE, a.listOutfitHead[15], "Add strength to your unique outfit"),
            OutfitItem("RIGHT ARM 1", HeadCategory.BODY_SHAPE, a.listOutfitHead[16], "Finish your look with perfect details"),
            OutfitItem("TORSO 2"    , HeadCategory.BODY_SHAPE, a.listOutfitHead[17], "Upgrade your avatar with a fresh design"),
        )
    }
}