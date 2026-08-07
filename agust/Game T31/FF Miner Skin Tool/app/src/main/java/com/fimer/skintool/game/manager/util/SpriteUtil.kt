package com.fimer.skintool.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.fimer.skintool.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val bottom_title  = getRegion("bottom_title")
        val loader        = getRegion("loader")
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
        val settings_def    = getAllRegion("settings_def")
        val yellow_def      = getAllRegion("yellow_def")
        val yellow_dis      = getAllRegion("yellow_dis")
        val claim           = getAllRegion("claim")
        val claimed         = getAllRegion("claimed")
        val close           = getAllRegion("close")

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        fun get9PatchScaled(name: String, scale: Int = 1) = get9Patch(name).apply { this.scale(1f / scale, 1f / scale) }

        val panel_balance = get9PatchScaled("panel_balance", 3)
        val panel_tips    = get9PatchScaled("panel_tips", 3)

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
//        val AA  = SpriteManager.EnumTexture.AA.data.texture

        // All | popup
        val POPUP = SpriteManager.EnumTexture.POPUP.data.texture

        // All | panel
        val PANEL_PROTOCOL     = SpriteManager.EnumTexture.PANEL_PROTOCOL.data.texture
        val PANEL_DAILY_REWARD = SpriteManager.EnumTexture.PANEL_DAILY_REWARD.data.texture
        val TIPS               = SpriteManager.EnumTexture.TIPS.data.texture
        val FREE               = SpriteManager.EnumTexture.FREE.data.texture
        val PANEL_SETTINGS     = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | emotes
        val EMOTES       = SpriteManager.EnumTexture.EMOTES.data.texture
        val PANEL_EMOTES = SpriteManager.EnumTexture.PANEL_EMOTES.data.texture

        // All | weapon
        val WEAPON = SpriteManager.EnumTexture.WEAPON.data.texture

        // All | parashutes
        val PARASHUTES = SpriteManager.EnumTexture.PARASHUTES.data.texture

        // All | vehicles
        val VEHICLES = SpriteManager.EnumTexture.VEHICLES.data.texture

        val BUNDLES = SpriteManager.EnumTexture.BUNDLES.data.texture

        val PETS = SpriteManager.EnumTexture.PETS.data.texture

        val CHAR = SpriteManager.EnumTexture.CHAR.data.texture

        // All | calculator
        val CALCULATOR = SpriteManager.EnumTexture.CALCULATOR.data.texture
        val INPUT      = SpriteManager.EnumTexture.INPUT.data.texture
        val RESULT     = SpriteManager.EnumTexture.RESULT.data.texture


        // All | list
        val listOnboarding     = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures
        val listHomeContent    = SpriteManager.EnumTextureGroup.HOME_CONTENT.data.textures
        val listSelectContent  = SpriteManager.EnumTextureGroup.SELECT_CONTENT.data.textures

        val listItemEmotes     = SpriteManager.EnumTextureGroup.ITEM_EMOTES.data.textures
        val listItemWeapon     = SpriteManager.EnumTextureGroup.ITEM_WEAPON.data.textures
        val listItemParachutes = SpriteManager.EnumTextureGroup.ITEM_PARASHUTES.data.textures
        val listItemVehicles   = SpriteManager.EnumTextureGroup.ITEM_VEHICLES.data.textures
        val listItemBundles    = SpriteManager.EnumTextureGroup.ITEM_BUNDLES.data.textures
        val listItemPets       = SpriteManager.EnumTextureGroup.ITEM_PETS.data.textures
        val listItemChar       = SpriteManager.EnumTextureGroup.ITEM_CHAR.data.textures

    }
}