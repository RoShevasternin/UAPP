package com.skindustry.skinly.game.manager

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
        FRAME_SKIN(TextureData("textures/all/frame_skin.png")),

        // All | popup
        POPUP_UNLOCK(TextureData("textures/all/popup/popup_unlock.png")),

        // All | panel
        PANEL_SETTINGS(TextureData("textures/all/panel/panel_settings.png")),

        // All | blokcy
        BLOKCY_CARD(TextureData("textures/all/blokcy/blokcy_card.png")),

        // All | homeSelect
        MINI_CARD(TextureData("textures/all/homeSelect/mini_card.png")),

        // All | personalization
        //tTexture(TextureData("textures/all/personalization/Texture.png")),
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
        ONBOARDING("textures/all/onboarding", "onboarding", 3),
        BLOKCY    ("textures/all/blokcy",     "blokcy",     3),

        P1("textures/all/homeSelect", "p1", 7),
        P2("textures/all/homeSelect", "p2", 7),
        P3("textures/all/homeSelect", "p3", 7),

        SB1("textures/all/skinBook", "sb1", 7),
        SB2("textures/all/skinBook", "sb2", 7),
        SB3("textures/all/skinBook", "sb3", 7),

        // Texture
        PERS_SOLID  ("textures/all/personalization/texture/solid"   , "", 108, ""),
        PERS_DENIM  ("textures/all/personalization/texture/denim"   , "", 9  , ""),
        PERS_CAMMO  ("textures/all/personalization/texture/cammo"   , "", 9  , ""),
        PERS_STRIPES("textures/all/personalization/texture/stripes" , "", 5  , ""),
        PERS_ACID   ("textures/all/personalization/texture/acid"    , "", 17 , ""),
        PERS_EMO    ("textures/all/personalization/texture/emo"     , "", 12 , ""),
        PERS_TARTAN ("textures/all/personalization/texture/tartan"  , "", 10 , ""),
        PERS_70s    ("textures/all/personalization/texture/a70s"    , "", 10 , ""),

        // Sticker
        PERS_FUN     ("textures/all/personalization/sticker/fun"     , "", 12, ""),
        PERS_CATS    ("textures/all/personalization/sticker/cats"    , "", 17, ""),
        PERS_ANIME   ("textures/all/personalization/sticker/anime"   , "", 25, ""),
        PERS_POCKETS ("textures/all/personalization/sticker/pockets" , "", 16, ""),
        PERS_BUTTONS ("textures/all/personalization/sticker/buttons" , "", 18, ""),

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