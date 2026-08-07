package com.diam.ondbit.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.diam.ondbit.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val bottom_title  = getRegion("bottom_title")
        val logo          = getRegion("logo")
        val progress      = getRegion("progress")
        val progress_back = getRegion("progress_back")
        val pers          = getRegion("pers")

        val panel_no_wifi = getRegion("panel_no_wifi")
        val retry_def     = getRegion("retry_def")
        val wifi          = getRegion("wifi")

        val BACKGROUND = SpriteManager.EnumTexture.BACKGROUND.data.texture
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val back_def        = getAllRegion("back_def")
        val yellow_def      = getAllRegion("yellow_def")
        val yellow_dis      = getAllRegion("yellow_dis")
        val true_def        = getAllRegion("true_def")
        val false_def       = getAllRegion("false_def")
        val settings_def    = getAllRegion("settings_def")
        val claim           = getAllRegion("claim")
        val claimed         = getAllRegion("claimed")
        val close           = getAllRegion("close")
        val item_check      = getAllRegion("item_check")
        val item_def        = getAllRegion("item_def")
        val progress        = getAllRegion("progress")
        val progress_back   = getAllRegion("progress_back")
        val di              = getAllRegion("di")
        val scratch_25      = getAllRegion("scratch_25")
        val scratch_here    = getAllRegion("scratch_here")
        val scratch_zero    = getAllRegion("scratch_zero")

        val listP     = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        fun get9PatchScaled(name: String, scale: Int = 1) = get9Patch(name).apply { this.scale(1f / scale, 1f / scale) }

        val tab_check     = get9PatchScaled("tab_check", 3)
        val tab_def       = get9PatchScaled("tab_def", 3)
        val panel_balance = get9PatchScaled("panel_balance", 3)

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val ITEM_CHAR  = SpriteManager.EnumTexture.ITEM_CHAR.data.texture

        val BOOST = SpriteManager.EnumTexture.BOOST.data.texture

        // All | popup
        val POPUP = SpriteManager.EnumTexture.POPUP.data.texture

        // All | map
        val MAP = SpriteManager.EnumTexture.MAP.data.texture

        // All | panel
        val PANEL_CONVERTER        = SpriteManager.EnumTexture.PANEL_CONVERTER.data.texture
        val PANEL_CONVERTER_SELECT = SpriteManager.EnumTexture.PANEL_CONVERTER_SELECT.data.texture
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture
        val PANEL_DAILY_REWARD     = SpriteManager.EnumTexture.PANEL_DAILY_REWARD.data.texture
        val PANEL_SELECT_OUTFIT    = SpriteManager.EnumTexture.PANEL_SELECT_OUTFIT.data.texture

        // All | quiz
        val DESC_QUIZ  = SpriteManager.EnumTexture.DESC_QUIZ.data.texture
        val PANEL_QUIZ = SpriteManager.EnumTexture.PANEL_QUIZ.data.texture



        // All | list
        val listOnboarding  = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures

        val listCharacter   = SpriteManager.EnumTextureGroup.CHARACTER.data.textures
        val listPets        = SpriteManager.EnumTextureGroup.PETS.data.textures
        val listAnimations  = SpriteManager.EnumTextureGroup.ANIMATIONS.data.textures

        val listHomeContent  = SpriteManager.EnumTextureGroup.HOME_CONTENT.data.textures

        val listMap = SpriteManager.EnumTextureGroup.MAP.data.textures

        val listBigCharacters = SpriteManager.EnumTextureGroup.BIG_CHARACTERS.data.textures

        val listOutfitGear        = SpriteManager.EnumTextureGroup.OUTFIT_GEAR.data.textures
        val listOutfitClothing    = SpriteManager.EnumTextureGroup.OUTFIT_CLOTHING.data.textures
        val listOutfitEmotes      = SpriteManager.EnumTextureGroup.OUTFIT_EMOTES.data.textures
        val listOutfitAccessories = SpriteManager.EnumTextureGroup.OUTFIT_ACCESSORIES.data.textures

    }
}