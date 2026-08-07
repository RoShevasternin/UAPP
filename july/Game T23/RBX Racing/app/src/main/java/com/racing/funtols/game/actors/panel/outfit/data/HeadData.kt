package com.racing.funtols.game.actors.panel.outfit.data

import com.racing.funtols.game.actors.panel.outfit.OutfitItem
import com.racing.funtols.game.utils.gdxGame

enum class HeadCategory(val title: String) {
    ALL        ("ALL"),
    FACE_LOOK  ("FACE LOOK"),
    FACE_SHAPES("FACE SHAPES"),
    BODY_SHAPE ("BODY SHAPE"),
}

object HeadData {

    // Порядок ЗБІГАЄТЬСЯ з порядком у listOutfitHead:
    //   [0..7]   — FACE LOOK   (вирази облич)
    //   [8..15]  — FACE SHAPES (форми голови)
    //   [16..23] — BODY SHAPE  (частини тіла)
    fun items(): List<OutfitItem<HeadCategory>> {
        val a = gdxGame.assetsAll
        return listOf(

            // ── FACE LOOK ─────────────────────────────────────────────────────
            OutfitItem("O.O"              , HeadCategory.FACE_LOOK, a.listOutfitHead[0]),
            OutfitItem("CLASSIC GOOF"     , HeadCategory.FACE_LOOK, a.listOutfitHead[1]),
            OutfitItem("GLEE"             , HeadCategory.FACE_LOOK, a.listOutfitHead[2]),
            OutfitItem("CHILL"            , HeadCategory.FACE_LOOK, a.listOutfitHead[3]),
            OutfitItem("CHECK IT"         , HeadCategory.FACE_LOOK, a.listOutfitHead[4]),
            OutfitItem("EXISTENTIAL ANGST", HeadCategory.FACE_LOOK, a.listOutfitHead[5]),
            OutfitItem("CLASSIC VAMPIRE"  , HeadCategory.FACE_LOOK, a.listOutfitHead[6]),
            OutfitItem("WINKY"            , HeadCategory.FACE_LOOK, a.listOutfitHead[7]),

            // ── FACE SHAPES ───────────────────────────────────────────────────
            OutfitItem("BLOCKHEAD"        , HeadCategory.FACE_SHAPES, a.listOutfitHead[8]),
            OutfitItem("ROUNDY"           , HeadCategory.FACE_SHAPES, a.listOutfitHead[9]),
            OutfitItem("TRIM"             , HeadCategory.FACE_SHAPES, a.listOutfitHead[10]),
            OutfitItem("OCTOBLOX"         , HeadCategory.FACE_SHAPES, a.listOutfitHead[11]),
            OutfitItem("ROX BOX"          , HeadCategory.FACE_SHAPES, a.listOutfitHead[12]),
            OutfitItem("ERASER HEAD"      , HeadCategory.FACE_SHAPES, a.listOutfitHead[13]),
            OutfitItem("BARREL"           , HeadCategory.FACE_SHAPES, a.listOutfitHead[14]),
            OutfitItem("STAR"             , HeadCategory.FACE_SHAPES, a.listOutfitHead[15]),

            // ── BODY SHAPE ────────────────────────────────────────────────────
            OutfitItem("TORSO 1"          , HeadCategory.BODY_SHAPE, a.listOutfitHead[16]),
            OutfitItem("LEFT LEG 1"       , HeadCategory.BODY_SHAPE, a.listOutfitHead[17]),
            OutfitItem("RIGHT LEG 1"      , HeadCategory.BODY_SHAPE, a.listOutfitHead[18]),
            OutfitItem("LEFT ARM 1"       , HeadCategory.BODY_SHAPE, a.listOutfitHead[19]),
            OutfitItem("RIGHT ARM 1"      , HeadCategory.BODY_SHAPE, a.listOutfitHead[20]),
            OutfitItem("TORSO 2"          , HeadCategory.BODY_SHAPE, a.listOutfitHead[21]),
            OutfitItem("LEFT LEG 2"       , HeadCategory.BODY_SHAPE, a.listOutfitHead[22]),
            OutfitItem("RIGHT LEG 2"      , HeadCategory.BODY_SHAPE, a.listOutfitHead[23]),
        )
    }
}