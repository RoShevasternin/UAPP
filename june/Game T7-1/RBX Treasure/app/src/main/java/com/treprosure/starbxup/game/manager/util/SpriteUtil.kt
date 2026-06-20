package com.treprosure.starbxup.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.treprosure.starbxup.game.manager.SpriteManager

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
        val yellow_check   = getAllRegion("yellow_check")
        val yellow_def     = getAllRegion("yellow_def")
        val logo           = getAllRegion("logo")
        val settings_def   = getAllRegion("settings_def")
        val close_f        = getAllRegion("close_f")
        val lose_f         = getAllRegion("lose_f")
        val win_f          = getAllRegion("win_f")
        val quiz_tab_check = getAllRegion("quiz_tab_check")
        val quiz_tab_def   = getAllRegion("quiz_tab_def")
        val claim          = getAllRegion("claim")

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
        val CHAR_BIG_CARD         = SpriteManager.EnumTexture.CHAR_BIG_CARD.data.texture

        // All | popup
        val POPUP = SpriteManager.EnumTexture.POPUP.data.texture

        // All | panel
        val PANEL_CONVERTER        = SpriteManager.EnumTexture.PANEL_CONVERTER.data.texture
        val PANEL_CONVERTER_SELECT = SpriteManager.EnumTexture.PANEL_CONVERTER_SELECT.data.texture
        val PANEL_QUIZ             = SpriteManager.EnumTexture.PANEL_QUIZ.data.texture
        val PANEL_GIFT             = SpriteManager.EnumTexture.PANEL_GIFT.data.texture
        val PANEL_ITEM             = SpriteManager.EnumTexture.PANEL_ITEM.data.texture
        val PANEL_SELECT_OUTFIT    = SpriteManager.EnumTexture.PANEL_SELECT_OUTFIT.data.texture
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | daily
        val CLAIM   = SpriteManager.EnumTexture.CLAIM.data.texture
        val CLAIMED = SpriteManager.EnumTexture.CLAIMED.data.texture
        val CLOSE   = SpriteManager.EnumTexture.CLOSE.data.texture

        // All | wheel
        val BACK       = SpriteManager.EnumTexture.BACK.data.texture
        val FRONT      = SpriteManager.EnumTexture.FRONT.data.texture
        val WHEEL      = SpriteManager.EnumTexture.WHEEL.data.texture
        val WHEEL_DESC = SpriteManager.EnumTexture.WHEEL_DESC.data.texture

        // All | scratch
        val SCRATCH_DESC = SpriteManager.EnumTexture.SCRATCH_DESC.data.texture
        val SCRATCH_MAP  = SpriteManager.EnumTexture.SCRATCH_MAP.data.texture
        val SCRATCH_WIN  = SpriteManager.EnumTexture.SCRATCH_WIN.data.texture

        // All | finds
        val DESC_FINDS     = SpriteManager.EnumTexture.DESC_FINDS.data.texture
        val GET_FREE_FINDS = SpriteManager.EnumTexture.GET_FREE_FINDS.data.texture
        val PANEL_FINDS    = SpriteManager.EnumTexture.PANEL_FINDS.data.texture


        // All | list
        val listHomeContent = SpriteManager.EnumTextureGroup.HOME_CONTENT.data.textures
        val listCharacter   = SpriteManager.EnumTextureGroup.CHARACTER.data.textures
        val listClothing    = SpriteManager.EnumTextureGroup.CLOTHING.data.textures
        val listAccessories = SpriteManager.EnumTextureGroup.ACCESSORIES.data.textures
        val listAnimations  = SpriteManager.EnumTextureGroup.ANIMATIONS.data.textures
        val listHead        = SpriteManager.EnumTextureGroup.HEAD.data.textures

    }
}