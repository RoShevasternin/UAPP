package com.mon.sterbx.game.utils.global

var GLOBAL_SELECTED_CONVERTER_TYPE  = ConverterType.entries.first()

enum class ConverterType(
    val title       : String,
    val fromCurrency: String,
    val toCurrency  : String,
    val coff        : Double,
) {
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

    RBX_TO_DOLLAR(
        "RBX to Dollar",
        "RBX",
        "Dollar",
        0.00073
    ),

    DOLLAR_TO_RBX(
        "Dollar to RBX",
        "Dollar",
        "RBX",
        1369.86
    ),
}

var GLOBAL_SELECTED_CHARACTER_INDEX = 0

val GLOBAL_LIST_CHARACTER_NAMES = listOf(
    "GOLD GUARDIAN",
    "FOREST WARDEN",
    "BLAZE CHAMPION",
    "MYSTIC OVERLORD",
    "LAVA TITAN",
    "FROST WYVERN",
)

val GLOBAL_LIST_CHARACTER_DESCRIPTIONS = listOf(
    "Gold Guardian is a mighty blocky warrior forged from solid gold and crowned with sharp horns. He stands watch over hidden vaults and never lets a treasure slip away. Perfect for players who want a bold, unstoppable protector at their side.",
    "Forest Warden is a living guardian of the deep woods, armored in bark, moss, and glowing emerald runes. Calm and patient, he senses every secret hidden among the roots. A great pick for players who value wisdom and quiet strength.",
    "Blaze Champion is a fearless fighter wrapped in molten armor with embers burning across his fists. He charges into every challenge headfirst and never backs down. Ideal for players who like raw power and an aggressive playstyle.",
    "Mystic Overlord is a mysterious sorcerer glowing with violet energy and ancient carved symbols. He bends forgotten magic to uncover what others cannot see. Perfect for players drawn to mystery, magic, and rare discoveries.",
    "Lava Titan is a colossal beast born deep inside a volcano, his cracked stone body still burning from within. Slow but unstoppable, he crushes everything that stands in his path. A solid choice for players who love brute force and heavy hits.",
    "Frost Wyvern is a winged predator of the frozen peaks, wrapped in shimmering ice and icy blue flame. He rules the cold winds and strikes before anyone hears him coming. Great for players who prefer speed, precision, and a cool-headed approach.",
)