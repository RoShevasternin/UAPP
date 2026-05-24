package com.bossrbx.rbxcalculator.game.utils

enum class ConverterType(
    val title       : String,
    val fromCurrency: String,
    val toCurrency  : String,
    val coff        : Double,
) {

    USD_TO_RBX(
        "USD to RBX",
        "USD",
        "RBX",
        1369.86
    ),

    RBX_TO_USD(
        "RBX to USD",
        "RBX",
        "USD",
        0.00073
    ),

    DOLLAR_TO_RBX(
        "Dollar to RBX",
        "Dollar",
        "RBX",
        1369.86
    ),

    RBX_TO_DOLLAR(
        "RBX to Dollar",
        "RBX",
        "Dollar",
        0.00073
    ),

    BC_TO_RBX(
        "BC to RBX",
        "BC",
        "RBX",
        15.0
    ),

    TBC_TO_RBX(
        "TBC to RBX",
        "TBC",
        "RBX",
        25.0
    ),

    OBC_TO_RBX(
        "OBC to RBX",
        "OBC",
        "RBX",
        40.0
    ),
}

var GLOBAL_SELECTED_CONVERTER_TYPE  = ConverterType.entries.first()