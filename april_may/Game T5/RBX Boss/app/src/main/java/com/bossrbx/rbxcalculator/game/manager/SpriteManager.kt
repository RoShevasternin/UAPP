package com.bossrbx.rbxcalculator.game.manager

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
        LIST_LANGUAGE(TextureData("textures/all/list_language.png")),
        INPUT        (TextureData("textures/all/input.png")),

        // All | popup
        POPUP(TextureData("textures/all/popup/popup.png")),

        // All | panel
        PANEL_MAIN             (TextureData("textures/all/panel/panel_main.png")),
        PANEL_SELECT_CONVERTER (TextureData("textures/all/panel/panel_select_converter.png")),
        PANEL_CONVERTER_RESULT (TextureData("textures/all/panel/panel_converter_result.png")),
        PANEL_SETTINGS         (TextureData("textures/all/panel/panel_settings.png")),

        // All | result
        PANEL_SCRATCH (TextureData("textures/all/scratch/panel_scratch.png")),
        SCRATCH_FRONT (TextureData("textures/all/scratch/scratch_front.png")),
        SCRATCH_RESULT(TextureData("textures/all/scratch/scratch_result.png")),

        // All | onboarding
        ONB_1        (TextureData("textures/all/onboarding/onb_1.png")),
        ONB_2        (TextureData("textures/all/onboarding/onb_2.png")),
        ONB_3        (TextureData("textures/all/onboarding/onb_3.png")),

        ONBOARDING_1 (TextureData("textures/all/onboarding/onboarding_1.png")),
        ONBOARDING_2 (TextureData("textures/all/onboarding/onboarding_2.png")),
        ONBOARDING_3 (TextureData("textures/all/onboarding/onboarding_3.png")),

        // All | daily
        CLAIM     (TextureData("textures/all/daily/claim.png")),
        CLAIMED   (TextureData("textures/all/daily/claimed.png")),
        CLOSE     (TextureData("textures/all/daily/close.png")),
        COME_BACK (TextureData("textures/all/daily/come_back.png")),

        // All | wheel
        PANEL_SPIN  (TextureData("textures/all/wheel/panel_spin.png")),
        WHEEL       (TextureData("textures/all/wheel/wheel.png")),
        WHEEL_FRONT (TextureData("textures/all/wheel/wheel_front.png")),

        // All | flipCard
        CIRCLE_CARD               (TextureData("textures/all/flipCard/circle_card.png")),
        SHADOW_FLIP               (TextureData("textures/all/flipCard/shadow_flip.png")),
        TEXT_FLIP_CARD            (TextureData("textures/all/flipCard/text_flip_card.png")),
        TEXT_FLIP_CONGRATULATIONS (TextureData("textures/all/flipCard/text_flip_congratulations.png")),
        CARD_BOSS                 (TextureData("textures/all/flipCard/card_boss.png")),
        CARD_REWARD               (TextureData("textures/all/flipCard/card_reward.png")),

        // All | quiz
        PANEL_ANSWER      (TextureData("textures/all/quiz/panel_answer.png")),
        PANEL_PLAY_QUIZ   (TextureData("textures/all/quiz/panel_play_quiz.png")),
        POPUP_QUIZ_RESULT (TextureData("textures/all/quiz/popup_quiz_result.png")),
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}