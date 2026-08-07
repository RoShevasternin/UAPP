package com.racing.funtols.game.manager

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

        // ALL
        TURBO_DESC(TextureData("textures/all/turbo_desc.png")),
        PLATE_DESC(TextureData("textures/all/plate_desc.png")),
        PICK_DESC (TextureData("textures/all/pick_desc.png")),
        ITEM_CHAR (TextureData("textures/all/item_char.png")),
        BIG_CHAR  (TextureData("textures/all/big_char.png")),

        BOOST(TextureData("textures/all/boost.png")),

        // All | popup
        POPUP (TextureData("textures/all/popup/popup.png")),

        // All | box
        ITEM_CHECK     (TextureData("textures/all/box/item_check.png")),
        ITEM_DEF       (TextureData("textures/all/box/item_def.png")),
        ITEM_LONG_CHECK(TextureData("textures/all/box/item_long_check.png")),
        ITEM_LONG_DEF  (TextureData("textures/all/box/item_long_def.png")),

        // All | panel
        PANEL_CONVERTER       (TextureData("textures/all/panel/panel_converter.png")),
        PANEL_CONVERTER_SELECT(TextureData("textures/all/panel/panel_converter_select.png")),
        PANEL_SETTINGS        (TextureData("textures/all/panel/panel_settings.png")),
        PANEL_DAILY_REWARD    (TextureData("textures/all/panel/panel_daily_reward.png")),
        PANEL_SELECT_OUTFIT    (TextureData("textures/all/panel/panel_select_outfit.png")),

        // All | pick
        FAIL (TextureData("textures/all/pick/fail.png")),
        FUEL (TextureData("textures/all/pick/fuel.png")),
        WIN  (TextureData("textures/all/pick/win.png")),

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
        ONBOARDING  ("textures/all/onboarding", "onboarding", 3),

        CLOTHING   ("textures/all/selector/clothing"  , "" , 8, ""),
        ANIMATIONS ("textures/all/selector/animation" , "" , 8, ""),
        CHARACTER  ("textures/all/selector/character" , "" , 8, ""),

        HOME_CONTENT("textures/all/home", "content", 5),

        OUTFIT_CLOTHING   ("textures/all/outfit/clothing"     , "" , 32, ""),
        OUTFIT_ACCESSORIES("textures/all/outfit/accessories"  , "" , 24, ""),
        OUTFIT_ANIMATIONS ("textures/all/outfit/animations"   , "" , 16, ""),
        OUTFIT_HEAD       ("textures/all/outfit/head"         , "" , 24, ""),

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