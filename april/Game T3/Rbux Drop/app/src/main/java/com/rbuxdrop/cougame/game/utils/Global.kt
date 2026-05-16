package com.rbuxdrop.cougame.game.utils

enum class ConverterType(
    val title   : String,
    val currency: String,
    val coff    : Double,
    ) {
    RBX_DOLLAR ("RBX > Dollar" , "Dollar" , 0.00073),
    DOLLAR_RBX ("Dollar > RBX" , "RBX"    , 1369.86),
    BC_RBX     ("BC > RBX"     , "RBX"    , 15.0),
    TBC_RBX    ("TBC > RBX"    , "RBX"    , 35.0),
    OBC_RBX    ("OBC > RBX"    , "RBX"    , 60.0),
}

enum class TipsType(
    val title: String,
) {
    _1("Use RBX Calculator"),
    _2("Daily Bonuses"),
    _3("Spin the Wheel"),
    _4("Scratch Card"),
    _5("Complete Quizzes"),
    _6("Play Flip Card"),
}

var GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries.first()
var GLOBAL_SELECTED_TIPS_TYPE      = TipsType.entries.first()