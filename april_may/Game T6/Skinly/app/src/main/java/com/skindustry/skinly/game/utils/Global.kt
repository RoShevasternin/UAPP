package com.skindustry.skinly.game.utils

val GLOBAL_listDesc = listOf(
    "What type of clothing do you\n" + "want to create?",
    "What type of accessories do you want to add to your avatar?",
    "What type of clothing would you like to design for your Roblox character?",
)

val GLOBAL_listSelectorItem_1 = listOf(
    "Pants",
    "Shirt",
    "T-Shirt",
    "Jackets",
    "Sweaters",
    "Hoodies",
)
val GLOBAL_listSelectorItem_2 = listOf(
    "Hats",
    "Glasses",
    "Backpacks",
    "Masks",
    "Scarves",
    "Belts",
)
val GLOBAL_listSelectorItem_3 = listOf(
    "Casual Wear",
    "Sportwear",
    "Military or Tactical Gear",
    "Seasonal Outfits",
    "Formal Wear",
)

val GLOBAL_listTitleBlokcy = listOf("T-shirt", "Shirt", "Pants")

enum class SelectedHomeType(
    val title: String,
) {
    T_SHIRT("T-shirt"),
    SHIRT  ("Shirt"),
    PANTS  ("Pants"),
}

var GLOBAL_selectedHomeType = SelectedHomeType.T_SHIRT
var GLOBAL_selectedPersonageIndex = 0

var GLOBAL_sharedSkinPath: String = ""