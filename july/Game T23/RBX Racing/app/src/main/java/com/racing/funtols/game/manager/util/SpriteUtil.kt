package com.racing.funtols.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.racing.funtols.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val bottom_title  = getRegion("bottom_title")
        val loader        = getRegion("loader")

        val panel_no_wifi = getRegion("panel_no_wifi")
        val retry_def     = getRegion("retry_def")
        val wifi          = getRegion("wifi")
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val back_def        = getAllRegion("back_def")
        val red_def         = getAllRegion("red_def")
        val true_def        = getAllRegion("true_def")
        val false_def       = getAllRegion("false_def")
        val claim_def       = getAllRegion("claim_def")
        val settings_def    = getAllRegion("settings_def")
        val logo            = getAllRegion("logo")
        val claim           = getAllRegion("claim")
        val claimed         = getAllRegion("claimed")
        val close           = getAllRegion("close")
        val converter_check = getAllRegion("converter_check")
        val converter_def   = getAllRegion("converter_def")
        val card            = getAllRegion("card")

        val listMatch = List(6) { getAllRegion("card_${it.inc()}") }
        val listPlate = List(8) { getAllRegion("plate_${it.inc()}") }

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
        val TURBO_DESC = SpriteManager.EnumTexture.TURBO_DESC.data.texture
        val PLATE_DESC = SpriteManager.EnumTexture.PLATE_DESC.data.texture
        val PICK_DESC  = SpriteManager.EnumTexture.PICK_DESC.data.texture
        val ITEM_CHAR  = SpriteManager.EnumTexture.ITEM_CHAR.data.texture
        val BIG_CHAR   = SpriteManager.EnumTexture.BIG_CHAR.data.texture

        val BOOST = SpriteManager.EnumTexture.BOOST.data.texture

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
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture
        val PANEL_DAILY_REWARD     = SpriteManager.EnumTexture.PANEL_DAILY_REWARD.data.texture
        val PANEL_SELECT_OUTFIT    = SpriteManager.EnumTexture.PANEL_SELECT_OUTFIT.data.texture

        // All | pick
        val FAIL = SpriteManager.EnumTexture.FAIL.data.texture
        val FUEL = SpriteManager.EnumTexture.FUEL.data.texture
        val WIN  = SpriteManager.EnumTexture.WIN.data.texture



        // All | list
        val listOnboarding  = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures

        val listClothing    = SpriteManager.EnumTextureGroup.CLOTHING.data.textures
        val listAnimations  = SpriteManager.EnumTextureGroup.ANIMATIONS.data.textures
        val listCharacter   = SpriteManager.EnumTextureGroup.CHARACTER.data.textures

        val listHomeContent  = SpriteManager.EnumTextureGroup.HOME_CONTENT.data.textures

        val listOutfitClothing    = SpriteManager.EnumTextureGroup.OUTFIT_CLOTHING.data.textures
        val listOutfitAccessories = SpriteManager.EnumTextureGroup.OUTFIT_ACCESSORIES.data.textures
        val listOutfitAnimations  = SpriteManager.EnumTextureGroup.OUTFIT_ANIMATIONS.data.textures
        val listOutfitHead        = SpriteManager.EnumTextureGroup.OUTFIT_HEAD.data.textures

    }
}