package com.rbuxdrop.cougame.game.manager

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
        SEPARATOR         (TextureData("textures/all/separator.png")),

        // All | panel
        PANEL_MAIN             (TextureData("textures/all/panel/panel_main.png")),
        PANEL_SELECT_CONVERTER (TextureData("textures/all/panel/panel_select_converter.png")),
        PANEL_RESULT           (TextureData("textures/all/panel/panel_result.png")),
        PANEL_HOW_QUIZ         (TextureData("textures/all/panel/panel_how_quiz.png")),
        PANEL_PLAY_QUIZ        (TextureData("textures/all/panel/panel_play_quiz.png")),
        PANEL_SETTINGS         (TextureData("textures/all/panel/panel_settings.png")),

        // All | onboarding
        ONB_1(TextureData("textures/all/onboarding/onb_1.png")),
        ONB_2(TextureData("textures/all/onboarding/onb_2.png")),
        ONB_3(TextureData("textures/all/onboarding/onb_3.png")),

        // All | daily
        CLAIM       (TextureData("textures/all/daily/claim.png")),
        CLAIMED     (TextureData("textures/all/daily/claimed.png")),
        CLOSE       (TextureData("textures/all/daily/close.png")),
        DAILY_RESULT(TextureData("textures/all/daily/daily_result.png")),

        // All | wheel
        WHEEL_RESULT (TextureData("textures/all/wheel/wheel_result.png")),
        WHEEL        (TextureData("textures/all/wheel/wheel.png")),

        // All | scratch
        PANEL_SCRATCH        (TextureData("textures/all/scratch/panel_scratch.png")),
        SCRATCH_RESULT       (TextureData("textures/all/scratch/scratch_result.png")),
        PANEL_SCRATCH_RESULT (TextureData("textures/all/scratch/panel_scratch_result.png")),

        // All | flip
        FLIP_CARD        (TextureData("textures/all/flip/flip_card.png")),
        FLIP_RESULT      (TextureData("textures/all/flip/flip_result.png")),
        FLIP_CARD_RESULT (TextureData("textures/all/flip/flip_card_result.png")),

        // All | tips
        PANEL_SELECT_TIPS (TextureData("textures/all/tips/panel_select_tips.png")),
        S1                (TextureData("textures/all/tips/s1.png")),
        S2                (TextureData("textures/all/tips/s2.png")),
        S3                (TextureData("textures/all/tips/s3.png")),
        S4                (TextureData("textures/all/tips/s4.png")),
        S5                (TextureData("textures/all/tips/s5.png")),
        S6                (TextureData("textures/all/tips/s6.png")),
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}