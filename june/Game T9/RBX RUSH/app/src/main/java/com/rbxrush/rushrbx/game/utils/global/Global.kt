package com.rbxrush.rushrbx.game.utils.global

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

    "ROBLOX GIRL",
    "ROBLOX BOY",
    "WOMAN",

    "MAN",
    "ROBLOX GIRL",
    "SKYLER",

    "DENNIS",
    "LINDSEY",
    "KENNETH",

    "CINDY",
    "KNIGHTS OF REDCLIFF",
    "DROP DEAD TEDD",
)

val GLOBAL_LIST_CHARACTER_DESCRIPTIONS = listOf(

    "ROBLOX Girl is a cheerful and friendly character with a bright personality and classic appearance. Her simple style makes her perfect for beginners and casual adventures. Great for roleplaying, social games, and exploring new worlds. A timeless choice for every player.",

    "ROBLOX Boy is an energetic and adventurous character ready for any challenge. Known for his classic look and positive attitude, he fits perfectly into action games and multiplayer adventures. A reliable companion for exploring exciting worlds.",

    "Woman is a stylish and confident character with a modern appearance and elegant design. Perfect for city roleplays, social experiences, and creative adventures. Her fashionable look pairs well with trendy accessories and customization options.",

    "Man is a versatile and dependable character with a clean and casual appearance. Great for everyday adventures, roleplay games, and exploration experiences. A solid choice for players who enjoy classic and simple styles.",

    "ROBLOX Girl is a playful and expressive character with a fun and colorful style. Her unique appearance makes her stand out in social games and multiplayer worlds. Perfect for creative roleplays and friendly adventures.",

    "Skyler is a lively and optimistic character known for her youthful energy and cheerful appearance. She enjoys exploring new places, meeting friends, and taking part in exciting adventures. A great choice for fun gameplay.",

    "Dennis is a bold and adventurous character with a confident attitude and iconic appearance. Popular among players who enjoy action-packed experiences and exciting challenges. Perfect for competing and exploring with friends.",

    "Lindsey is a smart and fashionable character with a confident personality and modern style. She enjoys social adventures, creative roleplaying, and discovering exciting new places. Ideal for players who love customization.",

    "Kenneth is a determined and athletic character with a strong sense of teamwork and leadership. He enjoys overcoming challenges, helping friends, and exploring new adventures. Great for action-packed experiences.",

    "Cindy is a kind-hearted and creative character who loves making new friends and discovering exciting places. Her cheerful personality and colorful style make her perfect for social games and roleplaying adventures.",

    "Knights of Redcliff is a legendary warrior known for courage, honor, and strength. Equipped with impressive armor and a heroic presence, this character is perfect for epic battles, quests, and fantasy adventures.",

    "Drop Dead Tedd is a mysterious and spooky character with a dark appearance and unique personality. Perfect for horror-themed adventures, nighttime explorations, and players who enjoy standing out from the crowd."
)