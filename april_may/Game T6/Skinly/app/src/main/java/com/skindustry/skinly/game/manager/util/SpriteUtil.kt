package com.skindustry.skinly.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.skindustry.skinly.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val bottom_title  = getRegion("bottom_title")
        val glow          = getRegion("glow")
        val loader        = getRegion("loader")
        val logo          = getRegion("logo")
        val title         = getRegion("title")

        val panel_no_wifi = getRegion("panel_no_wifi")
        val retry_def     = getRegion("retry_def")
        val wifi          = getRegion("wifi")

        val BACKGROUND = SpriteManager.EnumTexture.BACKGROUND.data.texture
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion =
            SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)

        private fun get9Patch(name: String): NinePatch =
            SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val back_def          = getAllRegion("back_def")
        val logo              = getAllRegion("logo")
        val orange_def        = getAllRegion("orange_def")
        val orange_dis        = getAllRegion("orange_dis")
        val settings_def      = getAllRegion("settings_def")
        val point_black       = getAllRegion("point_black")
        val point_gray        = getAllRegion("point_gray")
        val home_check        = getAllRegion("home_check")
        val home_def          = getAllRegion("home_def")
        val skin_book_check   = getAllRegion("skin_book_check")
        val skin_book_def     = getAllRegion("skin_book_def")
        val lock              = getAllRegion("lock")
        val eraser_def        = getAllRegion("eraser_def")
        val share_def         = getAllRegion("share_def")
        val close_def         = getAllRegion("close_def")
        val lock_white        = getAllRegion("lock_white")
        val texture_check     = getAllRegion("texture_check")
        val texture_def       = getAllRegion("texture_def")
        val sticker_check     = getAllRegion("sticker_check")
        val sticker_def       = getAllRegion("sticker_def")
        val bot_texture_check = getAllRegion("bot_texture_check")
        val bot_texture_def   = getAllRegion("bot_texture_def")
        val share_whats       = getAllRegion("share_whats")
        val share_insta       = getAllRegion("share_insta")
        val share_meta        = getAllRegion("share_meta")
        val share_otherr      = getAllRegion("share_otherr")
        val music_off         = getAllRegion("music_off")
        val music_on          = getAllRegion("music_on")

        //val listPoint = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        fun get9PatchScaled(name: String, scale: Int = 1) = get9Patch(name).apply { this.scale(1f / scale, 1f / scale) }

        val panel_box_check   = get9PatchScaled("panel_box_check", 3)
        val panel_box_def     = get9PatchScaled("panel_box_def", 3)
        val filter_item_check = get9PatchScaled("filter_item_check", 3)
        val filter_item_def   = get9PatchScaled("filter_item_def", 3)

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val FRAME_SKIN = SpriteManager.EnumTexture.FRAME_SKIN.data.texture

        // All | popup
        val POPUP_UNLOCK = SpriteManager.EnumTexture.POPUP_UNLOCK.data.texture

        // All | panel
        val PANEL_SETTINGS = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | onboarding
        val listOnboarding = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures

        // All | blokcy
        val BLOKCY_CARD = SpriteManager.EnumTexture.BLOKCY_CARD.data.texture

        val listBlokcy = SpriteManager.EnumTextureGroup.BLOKCY.data.textures

        // All | homeSelect
        val MINI_CARD = SpriteManager.EnumTexture.MINI_CARD.data.texture

        val listP1 = SpriteManager.EnumTextureGroup.P1.data.textures
        val listP2 = SpriteManager.EnumTextureGroup.P2.data.textures
        val listP3 = SpriteManager.EnumTextureGroup.P3.data.textures

        // All | skinBook
        val listSB1 = SpriteManager.EnumTextureGroup.SB1.data.textures
        val listSB2 = SpriteManager.EnumTextureGroup.SB2.data.textures
        val listSB3 = SpriteManager.EnumTextureGroup.SB3.data.textures

        // All | personalization
        //val tTexture = SpriteManager.EnumTexture.tTexture.data.texture

        // Texture
        val listTextureSolid   = SpriteManager.EnumTextureGroup.PERS_SOLID.data.textures
        val listTextureDenim   = SpriteManager.EnumTextureGroup.PERS_DENIM.data.textures
        val listTextureCammo   = SpriteManager.EnumTextureGroup.PERS_CAMMO.data.textures
        val listTextureStripes = SpriteManager.EnumTextureGroup.PERS_STRIPES.data.textures
        val listTextureAcid    = SpriteManager.EnumTextureGroup.PERS_ACID.data.textures
        val listTextureEmo     = SpriteManager.EnumTextureGroup.PERS_EMO.data.textures
        val listTextureTartan  = SpriteManager.EnumTextureGroup.PERS_TARTAN.data.textures
        val listTexture_70s    = SpriteManager.EnumTextureGroup.PERS_70s.data.textures

        // Sticker
        val listStickerFun     = SpriteManager.EnumTextureGroup.PERS_FUN.data.textures
        val listStickerCats    = SpriteManager.EnumTextureGroup.PERS_CATS.data.textures
        val listStickerAnime   = SpriteManager.EnumTextureGroup.PERS_ANIME.data.textures
        val listStickerPockets = SpriteManager.EnumTextureGroup.PERS_POCKETS.data.textures
        val listStickerButtons = SpriteManager.EnumTextureGroup.PERS_BUTTONS.data.textures
    }
}