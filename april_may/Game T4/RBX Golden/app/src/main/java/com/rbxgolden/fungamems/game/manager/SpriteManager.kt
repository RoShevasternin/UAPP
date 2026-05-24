package com.rbxgolden.fungamems.game.manager

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
        BACKGROUND(TextureData("textures/loader/background.png")),

        // All
        SELECT_ANIMATION_PACK(TextureData("textures/all/select_animation_pack.png")),
        MEMS                 (TextureData("textures/all/mems.png")),

        // All | panel
        PANEL_MAIN                   (TextureData("textures/all/panel/panel_main.png")),
        PANEL_QUIZ                   (TextureData("textures/all/panel/panel_quiz.png")),
        PANEL_GIFT                   (TextureData("textures/all/panel/panel_gift.png")),
        PANEL_ALL_CLOTHES_ANIMATIONS (TextureData("textures/all/panel/panel_all_clothes_animations.png")),
        PANEL_SETTINGS               (TextureData("textures/all/panel/panel_settings.png")),

        // All | result
        DAILY_RESULT   (TextureData("textures/all/result/daily_result.png")),
        WHEEL_RESULT   (TextureData("textures/all/result/wheel_result.png")),
        SCRATCH_RESULT (TextureData("textures/all/result/scratch_result.png")),

        // All | select
        s1_1(TextureData("textures/all/select/s1_1.png")),
        s1_2(TextureData("textures/all/select/s1_2.png")),
        s1_3(TextureData("textures/all/select/s1_3.png")),
        s1_4(TextureData("textures/all/select/s1_4.png")),
        s1_5(TextureData("textures/all/select/s1_5.png")),

        s2_1(TextureData("textures/all/select/s2_1.png")),
        s2_2(TextureData("textures/all/select/s2_2.png")),
        s2_3(TextureData("textures/all/select/s2_3.png")),

        s3_1(TextureData("textures/all/select/s3_1.png")),
        s3_2(TextureData("textures/all/select/s3_2.png")),

        // All | converter
        INPUT                 (TextureData("textures/all/converter/input.png")),
        PANEL_RESULT          (TextureData("textures/all/converter/panel_result.png")),
        PANEL_SELECT_CONVERTER(TextureData("textures/all/converter/panel_select_converter.png")),

        // All | wheel
        WHEEL_BACK(TextureData("textures/all/wheel/wheel_back.png")),
        WHEEL_FRONT(TextureData("textures/all/wheel/wheel_front.png")),

        // All | scratch
        PANEL_SCRATCH       (TextureData("textures/all/scratch/panel_scratch.png")),
        PANEL_SCRATCH_RESULT(TextureData("textures/all/scratch/panel_scratch_result.png")),

        // All | select_charatcers
        sc_1(TextureData("textures/all/select_charatcers/sc_1.png")),
        sc_2(TextureData("textures/all/select_charatcers/sc_2.png")),

        // All | charatcer
        _1 (TextureData("textures/all/character/1.png")),
        _2 (TextureData("textures/all/character/2.png")),
        _3 (TextureData("textures/all/character/3.png")),
        _4 (TextureData("textures/all/character/4.png")),
        _5 (TextureData("textures/all/character/5.png")),
        _6 (TextureData("textures/all/character/6.png")),
        _7 (TextureData("textures/all/character/7.png")),
        _8 (TextureData("textures/all/character/8.png")),
        _9 (TextureData("textures/all/character/9.png")),
        _10(TextureData("textures/all/character/10.png")),
        _11(TextureData("textures/all/character/11.png")),
        _12(TextureData("textures/all/character/12.png")),
        _13(TextureData("textures/all/character/13.png")),
        _14(TextureData("textures/all/character/14.png")),
        _15(TextureData("textures/all/character/15.png")),
        _16(TextureData("textures/all/character/16.png")),
        _17(TextureData("textures/all/character/17.png")),
        _18(TextureData("textures/all/character/18.png")),
        _19(TextureData("textures/all/character/19.png")),
        _20(TextureData("textures/all/character/20.png")),
        _21(TextureData("textures/all/character/21.png")),
        _22(TextureData("textures/all/character/22.png")),
        _23(TextureData("textures/all/character/23.png")),
        _24(TextureData("textures/all/character/24.png")),

        // All | all_clothes_animations
        //clothing
        CLOTHING_H1(TextureData("textures/all/all_clothes_animations/clothing/clothing_h1.png")),
        CLOTHING_H2(TextureData("textures/all/all_clothes_animations/clothing/clothing_h2.png")),
        CLOTHING_H3(TextureData("textures/all/all_clothes_animations/clothing/clothing_h3.png")),
        CLOTHING_H4(TextureData("textures/all/all_clothes_animations/clothing/clothing_h4.png")),
        CLOTHING_H5(TextureData("textures/all/all_clothes_animations/clothing/clothing_h5.png")),

        CLOTHING_P1(TextureData("textures/all/all_clothes_animations/clothing/clothing_p1.png")),
        CLOTHING_P2(TextureData("textures/all/all_clothes_animations/clothing/clothing_p2.png")),
        CLOTHING_P3(TextureData("textures/all/all_clothes_animations/clothing/clothing_p3.png")),
        CLOTHING_P4(TextureData("textures/all/all_clothes_animations/clothing/clothing_p4.png")),

        // accessories
        ACCESSORIES_H1(TextureData("textures/all/all_clothes_animations/accessories/accessories_h1.png")),
        ACCESSORIES_H2(TextureData("textures/all/all_clothes_animations/accessories/accessories_h2.png")),
        ACCESSORIES_H3(TextureData("textures/all/all_clothes_animations/accessories/accessories_h3.png")),
        ACCESSORIES_H4(TextureData("textures/all/all_clothes_animations/accessories/accessories_h4.png")),

        ACCESSORIES_P1(TextureData("textures/all/all_clothes_animations/accessories/accessories_p1.png")),
        ACCESSORIES_P2(TextureData("textures/all/all_clothes_animations/accessories/accessories_p2.png")),
        ACCESSORIES_P3(TextureData("textures/all/all_clothes_animations/accessories/accessories_p3.png")),

        // animations
        ANIMATIONS_H1(TextureData("textures/all/all_clothes_animations/animations/animations_h1.png")),
        ANIMATIONS_H2(TextureData("textures/all/all_clothes_animations/animations/animations_h2.png")),
        ANIMATIONS_H3(TextureData("textures/all/all_clothes_animations/animations/animations_h3.png")),

        ANIMATIONS_P1(TextureData("textures/all/all_clothes_animations/animations/animations_p1.png")),
        ANIMATIONS_P2(TextureData("textures/all/all_clothes_animations/animations/animations_p2.png")),

        // head_body
        HEAD_BODY_H1(TextureData("textures/all/all_clothes_animations/head_body/head_body_h1.png")),
        HEAD_BODY_H2(TextureData("textures/all/all_clothes_animations/head_body/head_body_h2.png")),
        HEAD_BODY_H3(TextureData("textures/all/all_clothes_animations/head_body/head_body_h3.png")),
        HEAD_BODY_H4(TextureData("textures/all/all_clothes_animations/head_body/head_body_h4.png")),

        HEAD_BODY_P1(TextureData("textures/all/all_clothes_animations/head_body/head_body_p1.png")),
        HEAD_BODY_P2(TextureData("textures/all/all_clothes_animations/head_body/head_body_p2.png")),
        HEAD_BODY_P3(TextureData("textures/all/all_clothes_animations/head_body/head_body_p3.png")),
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}