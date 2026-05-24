package com.rbxgolden.fungamems.game.utils

enum class ConverterType(
    val title   : String,
    val currency: String,
    val coff    : Double,
    ) {
    DAILY_FREE_RBX("", "", 0.0),
    BC_TO_RBX     ("BC to RBX"     , "RBX"    , 0.073),
    TBC_TO_RBX    ("TBC to RBX"    , "RBX"    , 3.0),
    OBC_TO_RBX    ("OBC to RBX"    , "RBX"    , 1.7),
    RBX_TO_DOLLAR ("RBX to Dollar" , "Dollar" , 0.05),
    DOLLAR_TO_RBX ("Dollar to RBX" , "RBX"    , 1.86),
}

var GLOBAL_SELECTED_CONVERTER_TYPE  = ConverterType.entries.first()
var GLOBAL_SELECTED_CHARACTER_INDEX = 0