package com.racing.funtols.game.utils.global

var GLOBAL_SELECTED_CONVERTER_TYPE  = ConverterType.entries.first()

enum class ConverterType(
    val title       : String,
    val fromCurrency: String,
    val toCurrency  : String,
    val coff        : Double,
) {
    FuelCoin_TO_RBX(
        "Fuel Coin to RBX",
        "Fuel Coin",
        "RBX",
        3.0
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

    RBC_TO_RBX(
        "RBC to RBX",
        "RBC",
        "RBX",
        0.0087
    ),

}

var GLOBAL_SELECTED_CHARACTER_INDEX = 0

val GLOBAL_LIST_CHARACTER_NAMES = listOf(
    "ROBLOX GIRL",
    "ROBLOX BOY",
    "WOMAN",
    "MAN",
    "ROSIE",
    "SKYLER",
    "DENNIS",
    "LINDSEY",
)

val GLOBAL_LIST_CHARACTER_DESCRIPTIONS = listOf(
    "Roblox Girl is the classic starter look — a soft pink top, matching pants and a smile that never drops. Simple, friendly and instantly recognizable on any track. A great pick for players who like keeping things clean and classic.",
    "Roblox Boy keeps it simple: bright blue tee, dark jeans and a laid-back grin. He is the everyday racer who shows up, buckles in and gets the job done. Ideal for players who want a no-nonsense classic at the wheel.",
    "Woman stands out with bright ginger hair and a soft cream outfit that somehow stays calm even at full speed. She is steady, focused and never rattled by a close finish. Perfect for players who race with patience and a cool head.",
    "Man comes with neat dark hair, a tan shirt and sleeves ready for work under the hood. Practical and reliable, he treats every lap like a job worth doing properly. A solid choice for players who value consistency over flash.",
    "Rosie turns heads with hot pink hair and a matching pink top that glows against the asphalt. Loud, playful and always first to the start line. Great for players who like bold colors and a bit of attitude.",
    "Skyler shows up in a bright yellow top and light pants, with a grin that says the race is already won. Upbeat and fearless, she treats every heat like a party. Perfect for players who bring big energy to every run.",
    "Dennis wears a worn denim jacket and carries his notes everywhere — he studies every corner before taking it. Quiet, sharp and usually one lap ahead in his head. Ideal for players who prefer smart racing lines over brute speed.",
    "Lindsey rolls in with a denim jacket over a dark red shirt and the calm look of someone who has done this many times. Easy-going off the track, ruthless on it. A great pick for players who like a relaxed style with a competitive streak.",
)