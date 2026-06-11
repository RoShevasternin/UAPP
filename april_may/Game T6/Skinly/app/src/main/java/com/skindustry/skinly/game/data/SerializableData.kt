package com.skindustry.skinly.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    // Індекси відкритих карточок для кожного типу
    // 0 = перша карточка відкрита за замовчуванням
    val unlockedTShirt: Set<Int> = setOf(0),
    val unlockedShirt : Set<Int> = setOf(0),
    val unlockedPants : Set<Int> = setOf(0),

    // Texture
    val unlockedTextureSolid   : Set<Int> = setOf(0),
    val unlockedTextureDenim   : Set<Int> = setOf(0),
    val unlockedTextureCammo   : Set<Int> = setOf(0),
    val unlockedTextureStripes : Set<Int> = setOf(0),
    val unlockedTextureAcid    : Set<Int> = setOf(0),
    val unlockedTextureEmo     : Set<Int> = setOf(0),
    val unlockedTextureTartan  : Set<Int> = setOf(0),
    val unlockedTexture_70s    : Set<Int> = setOf(0),

    // Sticker
    val unlockedStickerFun     : Set<Int> = setOf(0),
    val unlockedStickerCats    : Set<Int> = setOf(0),
    val unlockedStickerAnime   : Set<Int> = setOf(0),
    val unlockedStickerPockets : Set<Int> = setOf(0),
    val unlockedStickerButtons : Set<Int> = setOf(0),

)