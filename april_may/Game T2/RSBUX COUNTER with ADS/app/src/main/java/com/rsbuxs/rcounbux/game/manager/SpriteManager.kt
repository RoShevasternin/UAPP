package com.rsbuxs.rcounbux.game.manager

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
        LOADER  (AtlasData("atlas/loader.atlas")),
        ALL     (AtlasData("atlas/all.atlas")),

        _9_PATCH(AtlasData("atlas/9_patch.atlas")),
    }

    enum class EnumTexture(val data: TextureData) {
        // Loader
        //BACKGROUND(TextureData("textures/loader/background.png")),

        // All
        LIST_LANGUAGE     (TextureData("textures/all/list_language.png")),
        WELCOME           (TextureData("textures/all/welcome.png")),
        WELCOME_MINI_GAME (TextureData("textures/all/welcome_mini_game.png")),
        RESULT_MINI_GAME  (TextureData("textures/all/result_mini_game.png")),
        WHEEL             (TextureData("textures/all/wheel.png")),

        // All | panel
        PANEL_MAIN            (TextureData("textures/all/panel/panel_main.png")),
        PANEL_N_TO_RBX        (TextureData("textures/all/panel/panel_n_to_rbx.png")),
        PANEL_BOOST           (TextureData("textures/all/panel/panel_boost.png")),
        PANEL_QUIZ            (TextureData("textures/all/panel/panel_quiz.png")),
        PANEL_SCRATCH         (TextureData("textures/all/panel/panel_scratch.png")),
        PANEL_SCRATCH_RESULT  (TextureData("textures/all/panel/panel_scratch_result.png")),
        PANEL_REFERRAL        (TextureData("textures/all/panel/panel_referral.png")),
        PANEL_SETTINGS        (TextureData("textures/all/panel/panel_settings.png")),
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}