package com.sakurbx.fungambx.game.manager

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
        BACKGROUND_LOADER(TextureData("textures/loader/background_loader.png")),
        STAR             (TextureData("textures/loader/star.png")),

        // ALL
        BACKGROUND_PUPRLE(TextureData("textures/all/background_puprle.png")),
        CHAR_BIG_CARD        (TextureData("textures/all/char_big_card.png")),

        // All | popup
        POPUP       (TextureData("textures/all/popup/popup.png")),
        POPUP_SAKURA(TextureData("textures/all/popup/popup_sakura.png")),
        POPUP_GUESS (TextureData("textures/all/popup/popup_guess.png")),

        // All | box
        ITEM_CHECK     (TextureData("textures/all/box/item_check.png")),
        ITEM_DEF       (TextureData("textures/all/box/item_def.png")),
        ITEM_LONG_CHECK(TextureData("textures/all/box/item_long_check.png")),
        ITEM_LONG_DEF  (TextureData("textures/all/box/item_long_def.png")),

        // All | panel
        PANEL_CONVERTER       (TextureData("textures/all/panel/panel_converter.png")),
        PANEL_CONVERTER_SELECT(TextureData("textures/all/panel/panel_converter_select.png")),
        PANEL_ITEM            (TextureData("textures/all/panel/panel_item.png")),
        PANEL_SELECT_OUTFIT   (TextureData("textures/all/panel/panel_select_outfit.png")),
        PANEL_SETTINGS        (TextureData("textures/all/panel/panel_settings.png")),
        PANEL_DAILY_REWARD    (TextureData("textures/all/panel/panel_daily_reward.png")),

        // All | quiz
        PANEL_QUIZ  (TextureData("textures/all/quiz/panel_quiz.png")),

        // All | wheel
        TARGET     (TextureData("textures/all/wheel/target.png")),
        WHEEL      (TextureData("textures/all/wheel/wheel.png")),
        WHEEL_DESC (TextureData("textures/all/wheel/wheel_desc.png")),

        // All | scratch
        SCRATCH_DESC (TextureData("textures/all/scratch/scratch_desc.png")),
        SCRATCH_HERE (TextureData("textures/all/scratch/scratch_here.png")),
        SCRATCH_WIN  (TextureData("textures/all/scratch/scratch_win.png")),

        // All | quess
        PANEL_QUESS(TextureData("textures/all/guess/panel_quess.png")),
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

        CLOTHING   ("textures/all/selector/clothing"  , "" , 8 , ""),
        ANIMATIONS ("textures/all/selector/animation" , "" , 12, ""),
        CHARACTER  ("textures/all/selector/character" , "" , 8 , ""),
        ANIM_PACK  ("textures/all/selector/anim_pack" , "" , 8 , ""),

        HOME_CONTENT("textures/all/home", "content", 5),

        OUTFIT_CLOTHING   ("textures/all/outfit/clothing"     , "" , 28, ""),
        OUTFIT_ACCESSORIES("textures/all/outfit/accessories"  , "" , 21, ""),
        OUTFIT_ANIMATIONS ("textures/all/outfit/animations"   , "" , 12, ""),
        OUTFIT_HEAD       ("textures/all/outfit/head"         , "" , 18, ""),

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