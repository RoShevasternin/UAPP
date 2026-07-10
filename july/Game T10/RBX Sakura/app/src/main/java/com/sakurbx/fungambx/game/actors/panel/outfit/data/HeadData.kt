package com.sakurbx.fungambx.game.actors.panel.outfit.data

import com.sakurbx.fungambx.game.actors.panel.outfit.OutfitItem
import com.sakurbx.fungambx.game.utils.gdxGame

enum class HeadCategory(val title: String) {
    ALL        ("ALL"),
    FACE_SHAPES("FACE SHAPES"),
    FACE_LOOK  ("FACE LOOK"),
    BODY_SHAPE ("BODY SHAPE"),
}

object HeadData {

    // texture-и підстав свої з assetsAll
    fun items(): List<OutfitItem<HeadCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // FACE SHAPES
            OutfitItem("BLOCKHEAD"        , HeadCategory.FACE_SHAPES, a.listOutfitHead[0]),
            OutfitItem("ROUNDY"           , HeadCategory.FACE_SHAPES, a.listOutfitHead[1]),
            OutfitItem("TRIM"             , HeadCategory.FACE_SHAPES, a.listOutfitHead[2]),
            OutfitItem("OCTOBLOX"         , HeadCategory.FACE_SHAPES, a.listOutfitHead[3]),
            OutfitItem("ROX BOX"          , HeadCategory.FACE_SHAPES, a.listOutfitHead[4]),
            OutfitItem("ERASER HEAD"      , HeadCategory.FACE_SHAPES, a.listOutfitHead[5]),

            // FACE LOOK
            OutfitItem("O.O"              , HeadCategory.FACE_LOOK  , a.listOutfitHead[6]),
            OutfitItem("CLASSIC GOOF"     , HeadCategory.FACE_LOOK  , a.listOutfitHead[7]),
            OutfitItem("GLEE"             , HeadCategory.FACE_LOOK  , a.listOutfitHead[8]),
            OutfitItem("CHILL"            , HeadCategory.FACE_LOOK  , a.listOutfitHead[9]),
            OutfitItem("CHECK IT"         , HeadCategory.FACE_LOOK  , a.listOutfitHead[10]),
            OutfitItem("EXISTENTIAL ANGST", HeadCategory.FACE_LOOK  , a.listOutfitHead[11]),

            // BODY SHAPE
            OutfitItem("TORSO 1"          , HeadCategory.BODY_SHAPE , a.listOutfitHead[12]),
            OutfitItem("LEFT LEG 1"       , HeadCategory.BODY_SHAPE , a.listOutfitHead[13]),
            OutfitItem("RIGHT LEG 1"      , HeadCategory.BODY_SHAPE , a.listOutfitHead[14]),
            OutfitItem("LEFT ARM 1"       , HeadCategory.BODY_SHAPE , a.listOutfitHead[15]),
            OutfitItem("RIGHT ARM 1"      , HeadCategory.BODY_SHAPE , a.listOutfitHead[16]),
            OutfitItem("TORSO 2"          , HeadCategory.BODY_SHAPE , a.listOutfitHead[17]),
        )
    }
}