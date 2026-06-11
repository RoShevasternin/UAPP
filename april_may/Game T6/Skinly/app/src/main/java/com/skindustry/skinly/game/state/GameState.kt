package com.skindustry.skinly.game.state

import com.skindustry.skinly.game.data.PlayerData
import kotlinx.coroutines.flow.MutableStateFlow

class GameState {

    val unlockedTShirtFlow = MutableStateFlow(setOf(0))
    val unlockedShirtFlow  = MutableStateFlow(setOf(0))
    val unlockedPantsFlow  = MutableStateFlow(setOf(0))

    // Texture
    val unlockedTextureSolidFlow    = MutableStateFlow(setOf(0))
    val unlockedTextureDenimFlow    = MutableStateFlow(setOf(0))
    val unlockedTextureCammoFlow    = MutableStateFlow(setOf(0))
    val unlockedTextureStripesFlow  = MutableStateFlow(setOf(0))
    val unlockedTextureAcidFlow     = MutableStateFlow(setOf(0))
    val unlockedTextureEmoFlow      = MutableStateFlow(setOf(0))
    val unlockedTextureTartanFlow   = MutableStateFlow(setOf(0))
    val unlockedTexture_70sFlow     = MutableStateFlow(setOf(0))

    // Sticker
    val unlockedStickerFunFlow      = MutableStateFlow(setOf(0))
    val unlockedStickerCatsFlow     = MutableStateFlow(setOf(0))
    val unlockedStickerAnimeFlow    = MutableStateFlow(setOf(0))
    val unlockedStickerPocketsFlow  = MutableStateFlow(setOf(0))
    val unlockedStickerButtonsFlow  = MutableStateFlow(setOf(0))

    fun loadFrom(data: PlayerData) {
        unlockedTShirtFlow.value = data.unlockedTShirt
        unlockedShirtFlow.value  = data.unlockedShirt
        unlockedPantsFlow.value  = data.unlockedPants

        // Texture
        unlockedTextureSolidFlow.value   = data.unlockedTextureSolid
        unlockedTextureDenimFlow.value   = data.unlockedTextureDenim
        unlockedTextureCammoFlow.value   = data.unlockedTextureCammo
        unlockedTextureStripesFlow.value = data.unlockedTextureStripes
        unlockedTextureAcidFlow.value    = data.unlockedTextureAcid
        unlockedTextureEmoFlow.value     = data.unlockedTextureEmo
        unlockedTextureTartanFlow.value  = data.unlockedTextureTartan
        unlockedTexture_70sFlow.value    = data.unlockedTexture_70s

        // Sticker
        unlockedStickerFunFlow.value     = data.unlockedStickerFun
        unlockedStickerCatsFlow.value    = data.unlockedStickerCats
        unlockedStickerAnimeFlow.value   = data.unlockedStickerAnime
        unlockedStickerPocketsFlow.value = data.unlockedStickerPockets
        unlockedStickerButtonsFlow.value = data.unlockedStickerButtons
    }


    fun toPlayerData() = PlayerData(
        unlockedTShirt = unlockedTShirtFlow.value,
        unlockedShirt  = unlockedShirtFlow.value,
        unlockedPants  = unlockedPantsFlow.value,

        // Texture
        unlockedTextureSolid   = unlockedTextureSolidFlow.value,
        unlockedTextureDenim   = unlockedTextureDenimFlow.value,
        unlockedTextureCammo   = unlockedTextureCammoFlow.value,
        unlockedTextureStripes = unlockedTextureStripesFlow.value,
        unlockedTextureAcid    = unlockedTextureAcidFlow.value,
        unlockedTextureEmo     = unlockedTextureEmoFlow.value,
        unlockedTextureTartan  = unlockedTextureTartanFlow.value,
        unlockedTexture_70s    = unlockedTexture_70sFlow.value,

        // Sticker
        unlockedStickerFun     = unlockedStickerFunFlow.value,
        unlockedStickerCats    = unlockedStickerCatsFlow.value,
        unlockedStickerAnime   = unlockedStickerAnimeFlow.value,
        unlockedStickerPockets = unlockedStickerPocketsFlow.value,
        unlockedStickerButtons = unlockedStickerButtonsFlow.value,
    )
}