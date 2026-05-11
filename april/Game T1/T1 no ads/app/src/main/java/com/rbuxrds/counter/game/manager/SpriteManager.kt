package com.rbuxrds.counter.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class SpriteManager(var assetManager: AssetManager) {

    var loadableAtlasList   = mutableListOf<AtlasData>()
    var loadableTexturesList   = mutableListOf<TextureData>()

    fun loadAtlas() {
        loadableAtlasList.onEach { assetManager.load(it.path, TextureAtlas::class.java) }
    }

    fun initAtlas() {
        loadableAtlasList.onEach { it.atlas = assetManager[it.path, TextureAtlas::class.java] }
        loadableAtlasList.clear()
    }

    // Texture
    fun loadTexture() {
        loadableTexturesList.onEach { assetManager.load(it.path, Texture::class.java) }
    }

    fun initTexture() {
        loadableTexturesList.onEach { it.texture = assetManager[it.path, Texture::class.java] }
        loadableTexturesList.clear()
    }

    fun initAtlasAndTexture() {
        initAtlas()
        initTexture()
    }


    enum class EnumAtlas(val data: AtlasData) {
        LOADER    (AtlasData("atlas/loader.atlas")),
        ALL       (AtlasData("atlas/all.atlas")),
        MAIN      (AtlasData("atlas/main.atlas")),
        CHARACTERS(AtlasData("atlas/characters.atlas")),

        _9_PATCH(AtlasData("atlas/9_patch.atlas")),
    }

    enum class EnumTexture(val data: TextureData) {

        // Loader
        //BACKGROUND(TextureData("textures/loader/background.png")),

        // All
        WHEEL(TextureData("textures/all/wheel.png")),

        // All | screen_1
        SELECT_ANIME (TextureData("textures/all/screen_1/select_anime.png")),
        SELECT_CLOTH (TextureData("textures/all/screen_1/select_cloth.png")),
        SELECT_SKIN  (TextureData("textures/all/screen_1/select_skin.png")),

        // All | popup
        POPUP_DAILY_FREE_DONE(TextureData("textures/all/popup/popup_daily_free_done.png")),
        POPUP_DAILY_FREE_X   (TextureData("textures/all/popup/popup_daily_free_x.png")),
        POPUP_REDEEM         (TextureData("textures/all/popup/popup_redeem.png")),

        // All | panel
        PANEL_SCRATCH       (TextureData("textures/all/panel/panel_scratch.png")),
        PANEL_SCRATCH_RESULT(TextureData("textures/all/panel/panel_scratch_result.png")),
        PANEL_REDEEM        (TextureData("textures/all/panel/panel_redeem.png")),
        PANEL_MEMES_FOR_FUN (TextureData("textures/all/panel/panel_memes_for_fun.png")),
        PANEL_SETTINGS      (TextureData("textures/all/panel/panel_settings.png")),

        // All | redeem
        REDEEM_COFFER(TextureData("textures/all/redeem/redeem_coffer.png")),
        REDEEM_GLOW  (TextureData("textures/all/redeem/redeem_glow.png")),

        // All | animations
        ANIMATIONS_BUNDLES(TextureData("textures/all/animations/animations_bundles.png")),
        ANIMATIONS_EMOTES (TextureData("textures/all/animations/animations_emotes.png")),
        ANIMATIONS_SELECT (TextureData("textures/all/animations/animations_select.png")),

        // All | accessories
        ACCESSORIES_FACE  (TextureData("textures/all/accessories/accessories_face.png")),
        ACCESSORIES_HEAD  (TextureData("textures/all/accessories/accessories_head.png")),
        ACCESSORIES_NECK  (TextureData("textures/all/accessories/accessories_neck.png")),
        ACCESSORIES_SELECT(TextureData("textures/all/accessories/accessories_select.png")),

        // ALL | clothing
        CLOTHING_SELECT    (TextureData("textures/all/clothing/clothing_select.png")),
        COLLECTION_PANTS   (TextureData("textures/all/clothing/collection_pants.png")),
        COLLECTION_SHIRTS  (TextureData("textures/all/clothing/collection_shirts.png")),
        COLLECTION_SHOES   (TextureData("textures/all/clothing/collection_shoes.png")),
        COLLECTION_T_SHIETS(TextureData("textures/all/clothing/collection_t_shiets.png")),

        // ALL | head_and_body
        HEAD_AND_BODY_LOOK  (TextureData("textures/all/head_and_body/head_and_body_look.png")),
        HEAD_AND_BODY_SELECT(TextureData("textures/all/head_and_body/head_and_body_select.png")),
        HEAD_AND_BODY_SHAPE (TextureData("textures/all/head_and_body/head_and_body_shape.png")),
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}