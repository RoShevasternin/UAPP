package com.skindustry.skinly.game.model

import com.skindustry.skinly.game.model.PlayerModel.StickerType.*
import com.skindustry.skinly.game.model.PlayerModel.TextureType.*
import com.skindustry.skinly.game.state.GameState
import com.skindustry.skinly.game.utils.SelectedHomeType
import com.skindustry.skinly.game.utils.gdxGame
import kotlinx.coroutines.CoroutineScope

class PlayerModel(
    private val gameState: GameState,
    private val scope    : CoroutineScope,
) {

    // ------------------------------------------------------------------------
    // getUnlocked
    // ------------------------------------------------------------------------
    fun getUnlocked(type: SelectedHomeType): Set<Int> = when (type) {
        SelectedHomeType.T_SHIRT -> gameState.unlockedTShirtFlow.value
        SelectedHomeType.SHIRT   -> gameState.unlockedShirtFlow.value
        SelectedHomeType.PANTS   -> gameState.unlockedPantsFlow.value
    }

    fun getUnlockedTexture(type: TextureType): Set<Int> = when (type) {
        SOLID   -> gameState.unlockedTextureSolidFlow.value
        DENIM   -> gameState.unlockedTextureDenimFlow.value
        CAMMO   -> gameState.unlockedTextureCammoFlow.value
        STRIPES -> gameState.unlockedTextureStripesFlow.value
        ACID    -> gameState.unlockedTextureAcidFlow.value
        EMO     -> gameState.unlockedTextureEmoFlow.value
        TARTAN  -> gameState.unlockedTextureTartanFlow.value
        _70S    -> gameState.unlockedTexture_70sFlow.value
    }

    fun getUnlockedSticker(type: StickerType): Set<Int> = when (type) {
        FUN     -> gameState.unlockedStickerFunFlow.value
        CATS    -> gameState.unlockedStickerCatsFlow.value
        ANIME   -> gameState.unlockedStickerAnimeFlow.value
        POCKETS -> gameState.unlockedStickerPocketsFlow.value
        BUTTONS -> gameState.unlockedStickerButtonsFlow.value
    }

    // ------------------------------------------------------------------------
    // unlockCard
    // ------------------------------------------------------------------------
    fun unlockCard(type: SelectedHomeType, index: Int) {
        when (type) {
            SelectedHomeType.T_SHIRT -> gameState.unlockedTShirtFlow.value += index
            SelectedHomeType.SHIRT   -> gameState.unlockedShirtFlow.value  += index
            SelectedHomeType.PANTS   -> gameState.unlockedPantsFlow.value  += index
        }
    }

    fun unlockCardTexture(type: TextureType, index: Int) {
        when (type) {
            SOLID   -> gameState.unlockedTextureSolidFlow.value    += index
            DENIM   -> gameState.unlockedTextureDenimFlow.value    += index
            CAMMO   -> gameState.unlockedTextureCammoFlow.value    += index
            STRIPES -> gameState.unlockedTextureStripesFlow.value  += index
            ACID    -> gameState.unlockedTextureAcidFlow.value     += index
            EMO     -> gameState.unlockedTextureEmoFlow.value      += index
            TARTAN  -> gameState.unlockedTextureTartanFlow.value   += index
            _70S    -> gameState.unlockedTexture_70sFlow.value     += index
        }
    }

    fun unlockCardSticker(type: StickerType, index: Int) {
        when (type) {
            FUN     -> gameState.unlockedStickerFunFlow.value      += index
            CATS    -> gameState.unlockedStickerCatsFlow.value     += index
            ANIME   -> gameState.unlockedStickerAnimeFlow.value    += index
            POCKETS -> gameState.unlockedStickerPocketsFlow.value  += index
            BUTTONS -> gameState.unlockedStickerButtonsFlow.value  += index
        }
    }

    enum class TextureType(
        val label: String
    ) {
        SOLID  ("Solid"),
        DENIM  ("Denim"),
        CAMMO  ("Cammo"),
        STRIPES("Stripes"),
        ACID   ("Acid"),
        EMO    ("Emo"),
        TARTAN ("Tartan"),
        _70S   ("70s"),

        ;
        // Список текстур для цього типу
        val textures get() = when (this) {
            SOLID   -> gdxGame.assetsAll.listTextureSolid
            DENIM   -> gdxGame.assetsAll.listTextureDenim
            CAMMO   -> gdxGame.assetsAll.listTextureCammo
            STRIPES -> gdxGame.assetsAll.listTextureStripes
            ACID    -> gdxGame.assetsAll.listTextureAcid
            EMO     -> gdxGame.assetsAll.listTextureEmo
            TARTAN  -> gdxGame.assetsAll.listTextureTartan
            _70S    -> gdxGame.assetsAll.listTexture_70s
        }
    }

    enum class StickerType(
        val label: String
    ) {
        FUN    ("Fun"),
        CATS   ("Cats"),
        ANIME  ("Anime"),
        POCKETS("Pockets"),
        BUTTONS("Buttons"),

        ;
        // Список текстур для цього типу
        val textures get() = when (this) {
            FUN     -> gdxGame.assetsAll.listStickerFun
            CATS    -> gdxGame.assetsAll.listStickerCats
            ANIME   -> gdxGame.assetsAll.listStickerAnime
            POCKETS -> gdxGame.assetsAll.listStickerPockets
            BUTTONS -> gdxGame.assetsAll.listStickerButtons
        }
    }
}