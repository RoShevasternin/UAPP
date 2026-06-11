package com.rbxtreasure.fungamers.game.utils.global

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
    "ROBLOX Girls",
    "ROBLOX Boy",
    "Woman",
    "Man",
    "ROBLOX Girl",
    "Skyler",
    "Dennis",
)

val GLOBAL_LIST_CHARACTER_DESCRIPTIONS = listOf(

    "ROBLOX Girl is a cheerful and friendly character with a bright personality and classic appearance. Her simple style makes her perfect for beginners and casual adventures. Great for roleplaying, social games, and exploring new worlds. Pairs well with colorful outfits and fun accessories. A timeless choice for every player.",

    "ROBLOX Boy is an energetic and adventurous character ready for any challenge. Known for his classic look and positive attitude, he fits perfectly into action games and multiplayer adventures. Works great with sporty outfits and cool accessories. A reliable companion for exploring exciting worlds.",

    "Woman is a stylish and confident character with a modern appearance and elegant design. Perfect for city roleplays, social experiences, and creative adventures. Her fashionable look pairs well with trendy accessories and unique customization options. Often chosen for realistic roleplaying experiences.",

    "Man is a versatile and dependable character with a clean and casual appearance. Great for everyday adventures, roleplay games, and exploration experiences. His balanced design makes him easy to customize with different outfits and styles. A solid choice for players who enjoy classic looks.",

    "ROBLOX Girl is a playful and expressive character with a fun and colorful style. Her unique appearance makes her stand out in social games and multiplayer worlds. Perfect for creative roleplays and friendly adventures. Looks great with fashionable accessories and vibrant outfits.",

    "Skyler is a lively and optimistic character known for her youthful energy and cheerful appearance. She enjoys exploring new places, meeting friends, and taking part in exciting adventures. Her bright style works perfectly in social experiences and roleplaying games. A great choice for fun and creative gameplay.",

    "Dennis is a bold and adventurous character with a confident attitude and iconic appearance. Popular among players who enjoy action-packed experiences and exciting challenges. His strong personality makes him a standout choice for multiplayer adventures. Perfect for exploring, competing, and having fun with friends."

)