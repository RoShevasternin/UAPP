package com.sakurbx.fungambx.game.utils.global

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
    "SAKURA",
    "AKARI",
    "ROBLOX GIRL",
    "ROBLOX BOY",
    "WOMAN",
    "MAN",
    "ROBLOX GIRL",
    "SKYLER",
)

val GLOBAL_LIST_CHARACTER_DESCRIPTIONS = listOf(

    "Sakura is a graceful and gentle character dressed in a beautiful cherry-blossom kimono. Inspired by traditional Japanese style, she brings elegance and calm to every adventure. Perfect for players who love charming, artistic, and unique looks.",

    "Akari is a fearless ninja warrior cloaked in dark armor and ready for action. Swift, silent, and skilled, she thrives in stealth missions and intense battles. A perfect pick for players who love mystery and martial-arts adventures.",

    "Roblox Girl is a cheerful and friendly character with a bright personality and classic appearance. Her simple style makes her perfect for beginners and casual adventures. Great for roleplaying, social games, and exploring new worlds.",

    "Roblox Boy is an energetic and adventurous character ready for any challenge. Known for his classic look and positive attitude, he fits perfectly into action games and multiplayer adventures. A reliable companion for exploring new worlds.",

    "Woman is a stylish and confident character with a modern appearance and elegant design. Perfect for city roleplays, social experiences, and creative adventures. Her fashionable look pairs well with trendy accessories and customization.",

    "Man is a versatile and dependable character with a clean and casual appearance. Great for everyday adventures, roleplay games, and exploration experiences. A solid choice for players who enjoy classic and simple styles.",

    "Roblox Girl is a playful and expressive character with a fun and colorful style. Her unique appearance makes her stand out in social games and multiplayer worlds. Perfect for creative roleplays and friendly adventures.",

    "Skyler is a lively and optimistic character known for her youthful energy and cheerful appearance. She enjoys exploring new places, meeting friends, and taking part in exciting adventures. A great choice for fun gameplay.",

)