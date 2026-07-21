package com.mon.sterbx.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.mon.sterbx.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val bottom_title  = getRegion("bottom_title")
        val logo          = getRegion("logo")
        val progress      = getRegion("progress")
        val progress_bg   = getRegion("progress_bg")

        val panel_no_wifi = getRegion("panel_no_wifi")
        val retry_def     = getRegion("retry_def")
        val wifi          = getRegion("wifi")

        //val listLeaf = List(12) { getRegion("${it.inc()}") }
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val back_def   = getAllRegion("back_def")
        val icon_btn_3 = back_def
        val orange_def = getAllRegion("orange_def")
        val true_def   = getAllRegion("true_def")
        val false_def  = getAllRegion("false_def")

        val claim_def      = getAllRegion("claim_def")
        val settings_def   = getAllRegion("settings_def")
        val close_def      = getAllRegion("close_def")
        val claim          = getAllRegion("claim")
        val claimed        = getAllRegion("claimed")
        val close          = getAllRegion("close")
        val d7_claim       = getAllRegion("d7_claim")
        val d7_claimed     = getAllRegion("d7_claimed")
        val d7_close       = getAllRegion("d7_close")

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
        val BACKGROUND_YELLOW = SpriteManager.EnumTexture.BACKGROUND_YELLOW.data.texture

        // All | popup
        val POPUP = SpriteManager.EnumTexture.POPUP.data.texture

        // All | box
        val ITEM_CHECK      = SpriteManager.EnumTexture.ITEM_CHECK.data.texture
        val ITEM_DEF        = SpriteManager.EnumTexture.ITEM_DEF.data.texture
        val ITEM_LONG_CHECK = SpriteManager.EnumTexture.ITEM_LONG_CHECK.data.texture
        val ITEM_LONG_DEF   = SpriteManager.EnumTexture.ITEM_LONG_DEF.data.texture

        // All | panel
        val PANEL_CONVERTER        = SpriteManager.EnumTexture.PANEL_CONVERTER.data.texture
        val PANEL_CONVERTER_2      = SpriteManager.EnumTexture.PANEL_CONVERTER_2.data.texture
        val PANEL_CONVERTER_SELECT = SpriteManager.EnumTexture.PANEL_CONVERTER_SELECT.data.texture
        val PANEL_ITEM             = SpriteManager.EnumTexture.PANEL_ITEM.data.texture
        val PANEL_SELECT_OUTFIT    = SpriteManager.EnumTexture.PANEL_SELECT_OUTFIT.data.texture
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture
        val PANEL_DAILY_REWARD     = SpriteManager.EnumTexture.PANEL_DAILY_REWARD.data.texture
        val PANEL_FREE             = SpriteManager.EnumTexture.PANEL_FREE.data.texture

        // All | quiz
        val PANEL_QUIZ = SpriteManager.EnumTexture.PANEL_QUIZ.data.texture
        val MONSTER    = SpriteManager.EnumTexture.MONSTER.data.texture

        // All | wheel
        val TARGET     = SpriteManager.EnumTexture.TARGET.data.texture
        val WHEEL      = SpriteManager.EnumTexture.WHEEL.data.texture

        // All | scratch
        val SCRATCH_HERE = SpriteManager.EnumTexture.SCRATCH_HERE.data.texture
        val SCRATCH_WIN  = SpriteManager.EnumTexture.SCRATCH_WIN.data.texture

        // All | guess
        val FAIL  = SpriteManager.EnumTexture.FAIL.data.texture
        val GUESS = SpriteManager.EnumTexture.GUESS.data.texture
        val WIN   = SpriteManager.EnumTexture.WIN.data.texture


        // All | list
        val listOnboarding  = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures
        val listClothing    = SpriteManager.EnumTextureGroup.CLOTHING.data.textures
        val listAnimations  = SpriteManager.EnumTextureGroup.ANIMATIONS.data.textures
        val listCharacter   = SpriteManager.EnumTextureGroup.CHARACTER.data.textures

        val listHomeContent  = SpriteManager.EnumTextureGroup.HOME_CONTENT.data.textures
        val listCharacterBig = SpriteManager.EnumTextureGroup.CHARACTER_BIG.data.textures

        val listOutfitClothing    = SpriteManager.EnumTextureGroup.OUTFIT_CLOTHING.data.textures
        val listOutfitAccessories = SpriteManager.EnumTextureGroup.OUTFIT_ACCESSORIES.data.textures
        val listOutfitAnimations  = SpriteManager.EnumTextureGroup.OUTFIT_ANIMATIONS.data.textures
        val listOutfitHead        = SpriteManager.EnumTextureGroup.OUTFIT_HEAD.data.textures

    }
}