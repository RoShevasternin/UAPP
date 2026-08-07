package com.diam.ondbit.game.manager

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
        ITEM_CHAR (TextureData("textures/all/item_char.png")),

        BOOST(TextureData("textures/all/boost.png")),

        // All | popup
        POPUP (TextureData("textures/all/popup/popup.png")),

        // All | map
        MAP (TextureData("textures/all/map/map.png")),

        // All | panel
        PANEL_CONVERTER       (TextureData("textures/all/panel/panel_converter.png")),
        PANEL_CONVERTER_SELECT(TextureData("textures/all/panel/panel_converter_select.png")),
        PANEL_SETTINGS        (TextureData("textures/all/panel/panel_settings.png")),
        PANEL_DAILY_REWARD    (TextureData("textures/all/panel/panel_daily_reward.png")),
        PANEL_SELECT_OUTFIT    (TextureData("textures/all/panel/panel_select_outfit.png")),

        // All | quiz
        DESC_QUIZ (TextureData("textures/all/quiz/desc_quiz.png")),
        PANEL_QUIZ(TextureData("textures/all/quiz/panel_quiz.png")),

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

        CHARACTER  ("textures/all/selector/character" , "" , 8, ""),
        PETS       ("textures/all/selector/pets"      , "" , 8, ""),
        ANIMATIONS ("textures/all/selector/animation" , "" , 8, ""),

        HOME_CONTENT("textures/all/home", "content", 4),

        MAP("textures/all/map", "", 6, ""),

        BIG_CHARACTERS("textures/all/big_characters", "", 8, ""),

        OUTFIT_GEAR       ("textures/all/outfit/gear"         , "" , 32, ""),
        OUTFIT_CLOTHING   ("textures/all/outfit/clothing"     , "" , 24, ""),
        OUTFIT_EMOTES     ("textures/all/outfit/emotes"       , "" , 25, ""),
        OUTFIT_ACCESSORIES("textures/all/outfit/accessories"  , "" , 16, ""),

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