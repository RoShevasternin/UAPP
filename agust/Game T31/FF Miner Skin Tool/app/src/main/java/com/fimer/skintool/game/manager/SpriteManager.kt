package com.fimer.skintool.game.manager

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

//        // ALL
//        AA (TextureData("textures/all/aa.png")),

        // All | popup
        POPUP (TextureData("textures/all/popup/popup.png")),

        // All | panel
        PANEL_DAILY_REWARD    (TextureData("textures/all/panel/panel_daily_reward.png")),
        PANEL_PROTOCOL        (TextureData("textures/all/panel/panel_protocol.png")),
        TIPS                  (TextureData("textures/all/panel/tips.png")),
        FREE                  (TextureData("textures/all/panel/free.png")),
        PANEL_SETTINGS        (TextureData("textures/all/panel/panel_settings.png")),

        // All | emotes
        EMOTES      (TextureData("textures/all/emotes/emotes.png")),
        PANEL_EMOTES(TextureData("textures/all/emotes/panel_emotes.png")),

        // All | weapon
        WEAPON (TextureData("textures/all/weapon/weapon.png")),

        // All | parashutes
        PARASHUTES (TextureData("textures/all/parashutes/parashutes.png")),

        // All | vehicles
        VEHICLES (TextureData("textures/all/vehicles/vehicles.png")),

        BUNDLES (TextureData("textures/all/bundles/bundles.png")),

        PETS (TextureData("textures/all/pets/pets.png")),

        CHAR (TextureData("textures/all/char/char.png")),

        // All | calculator
        CALCULATOR(TextureData("textures/all/calculator/calculator.png")),
        INPUT     (TextureData("textures/all/calculator/input.png")),
        RESULT    (TextureData("textures/all/calculator/result.png")),
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
        ONBOARDING    ("textures/all/onboarding", "onboarding", 3),
        HOME_CONTENT  ("textures/all/home"      , "content"   , 2),
        SELECT_CONTENT("textures/all/select"    , "select"    , 4),

        ITEM_EMOTES    ("textures/all/emotes/item"    , "", 6, ""),
        ITEM_WEAPON    ("textures/all/weapon/item"    , "", 6, ""),
        ITEM_PARASHUTES("textures/all/parashutes/item", "", 6, ""),
        ITEM_VEHICLES  ("textures/all/vehicles/item"  , "", 6, ""),
        ITEM_BUNDLES   ("textures/all/bundles/item"   , "", 6, ""),
        ITEM_PETS      ("textures/all/pets/item"      , "", 6, ""),
        ITEM_CHAR      ("textures/all/char/item"      , "", 6, ""),

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