package com.coinsclub.funrbx.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.coinsclub.funrbx.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val bottom_title  = getRegion("bottom_title")
        val loader        = getRegion("loader")
        val logo          = getRegion("logo")

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

        val back_def       = getAllRegion("back_def")
        val yellow_def     = getAllRegion("yellow_def")
        val logo           = getAllRegion("logo")
        val settings_def   = getAllRegion("settings_def")
        val close_f        = getAllRegion("close_f")
        val lose_f         = getAllRegion("lose_f")
        val win_f          = getAllRegion("win_f")
        val claim          = getAllRegion("claim")
        val claimed        = getAllRegion("claimed")
        val claimed_circle = getAllRegion("claimed_circle")
        val close          = getAllRegion("close")
        val rbx            = getAllRegion("rbx")
        val icon_get_prize = getAllRegion("icon_get_prize")

        //val listPoint = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        fun get9PatchScaled(name: String, scale: Int = 1) = get9Patch(name).apply { this.scale(1f / scale, 1f / scale) }

        val tab_check = get9PatchScaled("tab_check", 3)
        val tab_def   = get9PatchScaled("tab_def", 3)

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val BACKGROUND_ALL        = SpriteManager.EnumTexture.BACKGROUND_ALL.data.texture
        val BACKGROUND_ONBOARDING = SpriteManager.EnumTexture.BACKGROUND_ONBOARDING.data.texture
        val CHAR_BIG_CARD         = SpriteManager.EnumTexture.CHAR_BIG_CARD.data.texture

        // All | popup
        val POPUP = SpriteManager.EnumTexture.POPUP.data.texture

        // All | box
        val ITEM_CHECK      = SpriteManager.EnumTexture.ITEM_CHECK.data.texture
        val ITEM_DEF        = SpriteManager.EnumTexture.ITEM_DEF.data.texture
        val ITEM_LONG_CHECK = SpriteManager.EnumTexture.ITEM_LONG_CHECK.data.texture
        val ITEM_LONG_DEF   = SpriteManager.EnumTexture.ITEM_LONG_DEF.data.texture

        // All | panel
        val PANEL_CONVERTER        = SpriteManager.EnumTexture.PANEL_CONVERTER.data.texture
        val PANEL_CONVERTER_SELECT = SpriteManager.EnumTexture.PANEL_CONVERTER_SELECT.data.texture
        val PANEL_FREE             = SpriteManager.EnumTexture.PANEL_FREE.data.texture
        val PANEL_ITEM             = SpriteManager.EnumTexture.PANEL_ITEM.data.texture
        val PANEL_SELECT_OUTFIT    = SpriteManager.EnumTexture.PANEL_SELECT_OUTFIT.data.texture
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | quiz
        val QUIZ_CIRCLE_FALSE = SpriteManager.EnumTexture.QUIZ_CIRCLE_FALSE.data.texture
        val QUIZ_CIRCLE_TRUE  = SpriteManager.EnumTexture.QUIZ_CIRCLE_TRUE.data.texture
        val QUIZ_FALSE        = SpriteManager.EnumTexture.QUIZ_FALSE.data.texture
        val QUIZ_PANEL        = SpriteManager.EnumTexture.QUIZ_PANEL.data.texture
        val QUIZ_TRUE         = SpriteManager.EnumTexture.QUIZ_TRUE.data.texture
        val PANEL_QUIZ        = SpriteManager.EnumTexture.PANEL_QUIZ.data.texture

        // All | home
        val CONTENT_3_CLAIM = SpriteManager.EnumTexture.CONTENT_3_CLAIM.data.texture
        val CONTENT_3_WAIT  = SpriteManager.EnumTexture.CONTENT_3_WAIT.data.texture

        // All | wheel
        val BACK       = SpriteManager.EnumTexture.BACK.data.texture
        val FRONT      = SpriteManager.EnumTexture.FRONT.data.texture
        val WHEEL      = SpriteManager.EnumTexture.WHEEL.data.texture
        val WHEEL_DESC = SpriteManager.EnumTexture.WHEEL_DESC.data.texture

        // All | scratch
        val SCRATCH_DESC = SpriteManager.EnumTexture.SCRATCH_DESC.data.texture
        val SCRATCH_HERE = SpriteManager.EnumTexture.SCRATCH_HERE.data.texture
        val SCRATCH_WIN  = SpriteManager.EnumTexture.SCRATCH_WIN.data.texture

        // All | guess
        val DESC_QUESS     = SpriteManager.EnumTexture.DESC_QUESS.data.texture
        val GET_FREE_GUESS = SpriteManager.EnumTexture.GET_FREE_GUESS.data.texture


        // All | list
        val listOnboarding  = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures
        val listClothing    = SpriteManager.EnumTextureGroup.CLOTHING.data.textures
        val listAnimations  = SpriteManager.EnumTextureGroup.ANIMATIONS.data.textures
        val listCharacter   = SpriteManager.EnumTextureGroup.CHARACTER.data.textures

        val listHomeContent = SpriteManager.EnumTextureGroup.HOME_CONTENT.data.textures

        val listOutfitClothing    = SpriteManager.EnumTextureGroup.OUTFIT_CLOTHING.data.textures
        val listOutfitAccessories = SpriteManager.EnumTextureGroup.OUTFIT_ACCESSORIES.data.textures
        val listOutfitAnimations  = SpriteManager.EnumTextureGroup.OUTFIT_ANIMATIONS.data.textures
        val listOutfitHead        = SpriteManager.EnumTextureGroup.OUTFIT_HEAD.data.textures

    }
}