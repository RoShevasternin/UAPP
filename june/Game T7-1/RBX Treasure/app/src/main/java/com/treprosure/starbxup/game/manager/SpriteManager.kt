package com.treprosure.starbxup.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class SpriteManager(var assetManager: AssetManager) {

    var loadableAtlasList    = mutableListOf<AtlasData>()
    var loadableTexturesList = mutableListOf<TextureData>()
    var loadableGroupList    = mutableListOf<TextureGroupData>()

    // ------------------------------------------------------------------------
    // Atlas
    // ------------------------------------------------------------------------
    fun loadAtlas() {
        loadableAtlasList.onEach { assetManager.load(it.path, TextureAtlas::class.java) }
    }

    fun initAtlas() {
        loadableAtlasList.onEach { it.atlas = assetManager[it.path, TextureAtlas::class.java] }
        loadableAtlasList.clear()
    }

    // ------------------------------------------------------------------------
    // Texture
    // ------------------------------------------------------------------------
    fun loadTexture() {
        loadableTexturesList.onEach { assetManager.load(it.path, Texture::class.java) }
    }

    fun initTexture() {
        loadableTexturesList.onEach { it.texture = assetManager[it.path, Texture::class.java] }
        loadableTexturesList.clear()
    }

    // ------------------------------------------------------------------------
    // TextureGroup
    // ------------------------------------------------------------------------
    fun loadGroups() {
        loadableGroupList.onEach { group -> group.paths.forEach { assetManager.load(it, Texture::class.java) } }
    }

    fun initGroups() {
        loadableGroupList.onEach { group -> group.textures = group.paths.map { assetManager[it, Texture::class.java] } }
        loadableGroupList.clear()
    }

    // ------------------------------------------------------------------------
    // Util
    // ------------------------------------------------------------------------
    fun initAll() {
        initAtlas()
        initTexture()
        initGroups()
    }

    // ------------------------------------------------------------------------
    // EnumAtlas
    // ------------------------------------------------------------------------
    enum class EnumAtlas(val data: AtlasData) {
        LOADER  (AtlasData("atlas/loader.atlas")),
        ALL     (AtlasData("atlas/all.atlas")),
        _9_PATCH(AtlasData("atlas/9_patch.atlas")),
    }

    // ------------------------------------------------------------------------
    // EnumTexture
    // ------------------------------------------------------------------------
    enum class EnumTexture(val data: TextureData) {
        // Loader
        BACKGROUND(TextureData("textures/loader/background.png")),

        // ALL
        BACKGROUND_ALL(TextureData("textures/all/background_all.png")),
        CHAR_BIG_CARD (TextureData("textures/all/char_big_card.png")),

        // All | popup
        POPUP(TextureData("textures/all/popup/popup.png")),

        // All | panel
        PANEL_CONVERTER       (TextureData("textures/all/panel/panel_converter.png")),
        PANEL_CONVERTER_SELECT(TextureData("textures/all/panel/panel_converter_select.png")),
        PANEL_QUIZ            (TextureData("textures/all/panel/panel_quiz.png")),
        PANEL_GIFT            (TextureData("textures/all/panel/panel_gift.png")),
        PANEL_ITEM            (TextureData("textures/all/panel/panel_item.png")),
        PANEL_SELECT_OUTFIT   (TextureData("textures/all/panel/panel_select_outfit.png")),
        PANEL_SETTINGS        (TextureData("textures/all/panel/panel_settings.png")),

        // All | daily
        CLAIM  (TextureData("textures/all/daily/claim.png")),
        CLAIMED(TextureData("textures/all/daily/claimed.png")),
        CLOSE  (TextureData("textures/all/daily/close.png")),

        // All | wheel
        BACK       (TextureData("textures/all/wheel/back.png")),
        FRONT      (TextureData("textures/all/wheel/front.png")),
        WHEEL      (TextureData("textures/all/wheel/wheel.png")),
        WHEEL_DESC (TextureData("textures/all/wheel/wheel_desc.png")),

        // All | scratch
        SCRATCH_DESC (TextureData("textures/all/scratch/scratch_desc.png")),
        SCRATCH_MAP  (TextureData("textures/all/scratch/scratch_map.png")),
        SCRATCH_WIN  (TextureData("textures/all/scratch/scratch_win.png")),

        // All | finds
        DESC_FINDS    (TextureData("textures/all/finds/desc_finds.png")),
        GET_FREE_FINDS(TextureData("textures/all/finds/get_free_finds.png")),
        PANEL_FINDS   (TextureData("textures/all/finds/panel_finds.png")),
    }

    // ------------------------------------------------------------------------
    // EnumTextureGroup
    // ------------------------------------------------------------------------
    enum class EnumTextureGroup(
        private val folder: String,
        private val prefix: String,
        private val count : Int,
        private val separator: String = "_",
    ) {
        HOME_CONTENT("textures/all/home"      , "content"   , 7),
        CHARACTER   ("textures/all/character" , "char"      , 7),

        CLOTHING   ("textures/all/outfit/clothing"     , "" , 28, ""),
        ACCESSORIES("textures/all/outfit/accessories"  , "" , 21, ""),
        ANIMATIONS ("textures/all/outfit/animations"   , "" , 12, ""),
        HEAD       ("textures/all/outfit/head"         , "" , 18, ""),

        ;
        val data: TextureGroupData by lazy {
            TextureGroupData((1..count).map { "$folder/$prefix$separator$it.png" })
        }
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

    data class TextureGroupData(val paths: List<String>) {
        lateinit var textures: List<Texture>
    }

}