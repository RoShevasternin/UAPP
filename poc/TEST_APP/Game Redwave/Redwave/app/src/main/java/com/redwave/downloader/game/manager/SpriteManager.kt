package com.redwave.downloader.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class SpriteManager(var assetManager: AssetManager) {

    var loadableAtlasList    = mutableListOf<AtlasData>()
    var loadableTexturesList = mutableListOf<TextureData>()
    var loadableGroupList    = mutableListOf<TextureGroupData>()

    /**
     * Фото й світіння малюються з даунскейлом (720 px → ~300 px) — без мипмап
     * лінійний фільтр дає «сходи» і мерехтіння при Ken Burns.
     */
    private val textureParams = TextureLoader.TextureParameter().apply {
        genMipMaps = true
        minFilter  = Texture.TextureFilter.MipMapLinearLinear
        magFilter  = Texture.TextureFilter.Linear
    }

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
        loadableTexturesList.onEach { assetManager.load(it.path, Texture::class.java, textureParams) }
    }

    fun initTexture() {
        loadableTexturesList.onEach { it.texture = assetManager[it.path, Texture::class.java] }
        loadableTexturesList.clear()
    }

    // ------------------------------------------------------------------------
    // TextureGroup
    // ------------------------------------------------------------------------
    fun loadGroups() {
        loadableGroupList.onEach { group -> group.paths.forEach { assetManager.load(it, Texture::class.java, textureParams) } }
    }

    fun initGroups() {
        loadableGroupList.onEach { group -> group.textures = group.paths.map { assetManager[it, Texture::class.java] } }
        loadableGroupList.clear()
    }

    // ------------------------------------------------------------------------
    // Util
    // ------------------------------------------------------------------------
    fun loadAll() {
        loadableAtlasList.addAll(EnumAtlas.entries.map { it.data })
        loadableTexturesList.addAll(EnumTexture.entries.map { it.data })
        loadableGroupList.addAll(EnumTextureGroup.entries.map { it.data })
        loadAtlas()
        loadTexture()
        loadGroups()
    }

    fun initAll() {
        initAtlas()
        initTexture()
        initGroups()
    }

    // ------------------------------------------------------------------------
    // EnumAtlas
    // ------------------------------------------------------------------------
    enum class EnumAtlas(val data: AtlasData) {
        /** Іконки 96 px, білі — пакує таск `:app:packAtlas` (gdx-tools). */
        ALL(AtlasData("atlas/all.atlas")),
    }

    // ------------------------------------------------------------------------
    // EnumTexture
    // ------------------------------------------------------------------------
    enum class EnumTexture(val data: TextureData) {
        LOGO       (TextureData("textures/logo.png")),
        GLOW       (TextureData("textures/fx/glow_radial.png")),
        FADE_V     (TextureData("textures/fx/fade_vertical.png")),
        LAUNCHER_BG(TextureData("textures/launcher/launcher_bg.jpg")),
        FEAT_LIVE  (TextureData("textures/discover/feat_live.jpg")),
        FEAT_NIGHT (TextureData("textures/discover/feat_night.jpg")),
        FEAT_VINYL (TextureData("textures/discover/feat_vinyl.jpg")),
    }

    // ------------------------------------------------------------------------
    // EnumTextureGroup
    // ------------------------------------------------------------------------
    enum class EnumTextureGroup(
        private val folder: String,
        private val prefix: String,
        private val count : Int,
        private val ext   : String = "jpg",
    ) {
        ONBOARDING("textures/onboarding", "onboarding", 3),
        ;
        val data: TextureGroupData by lazy {
            TextureGroupData((1..count).map { "$folder/${prefix}_$it.$ext" })
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
