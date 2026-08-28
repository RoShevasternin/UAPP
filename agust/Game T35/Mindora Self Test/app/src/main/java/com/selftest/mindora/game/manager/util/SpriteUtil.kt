package com.selftest.mindora.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.selftest.mindora.game.manager.SpriteManager
import com.selftest.mindora.game.utils.TextureEmpty

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name) ?: error("Регіон '$name' відсутній в atlas/loader.atlas — перепакуй атлас")

        val bottom_title  = getRegion("bottom_title")
        val icon          = getRegion("icon")
        val loader        = getRegion("loader")
        val logo          = getRegion("logo")

        val panel_no_wifi = getRegion("panel_no_wifi")
        val wifi          = getRegion("wifi")
        val retry_def     = getRegion("retry_def")

        val BACKGROUND = SpriteManager.EnumTexture.BACKGROUND.data.texture
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name) ?: error("Регіон '$name' відсутній в atlas/all.atlas — перепакуй атлас")

        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name) ?: error("Регіон '$name' відсутній в atlas/_9_patch.atlas — перепакуй атлас")

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val btn_def                 = getAllRegion("btn_def")
        val btn_press               = getAllRegion("btn_press")
        val medium_dis              = getAllRegion("medium_dis")
        val medium_def              = getAllRegion("medium_def")
        val check                   = getAllRegion("check")
        val settings_btn            = getAllRegion("settings_btn")
        val lumens_big              = getAllRegion("lumens_big")
        val watch_ad                = getAllRegion("watch_ad")
        val lock                    = getAllRegion("lock")
        val progress_mask_portrait  = getAllRegion("progress_mask_portrait")
        val claim                   = getAllRegion("claim")
        val claimed                 = getAllRegion("claimed")
        val close                   = getAllRegion("close")
        val double_def              = getAllRegion("double_def")
        val back_btn                = getAllRegion("back_btn")
        val fire                    = getAllRegion("fire")

        // Card Test
        val test_card_done = getAllRegion("test_card_done")
        val card_test_dis  = getAllRegion("card_test_dis")
        val card_test_ena  = getAllRegion("card_test_ena")
        val test_btn_again = getAllRegion("test_btn_again")
        val test_btn_dis   = getAllRegion("test_btn_dis")
        val test_btn_ena   = getAllRegion("test_btn_ena")
        val test_btn_open  = getAllRegion("test_btn_open")

        val listIcDis = List(5) { getAllRegion("ic_dis_${it.inc()}") }
        val listIcEna = List(5) { getAllRegion("ic_ena_${it.inc()}") }


        val listP = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        private fun get9PatchScaled(name: String, scale: Int = 1) = get9Patch(name).apply { this.scale(1f / scale, 1f / scale) }

        val panel_balance = get9PatchScaled("panel_balance", 3)

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // TEST
        //val bg_test    = SpriteManager.EnumTexture.bg_test.data.texture

        // ALL
        val LIGHT = TextureEmpty //SpriteManager.EnumTexture.LIGHT.data.texture

        // All | panel
        val PANEL_STREAK = SpriteManager.EnumTexture.PANEL_STREAK.data.texture

        // All | popup
        val POPUP_START = SpriteManager.EnumTexture.POPUP_START.data.texture
        val POPUP       = SpriteManager.EnumTexture.POPUP.data.texture
        val POPUP_MORE  = SpriteManager.EnumTexture.POPUP_MORE.data.texture

        // All | item
        val ITEM_PORTRAIT = SpriteManager.EnumTexture.ITEM_PORTRAIT.data.texture
        val ITEM_DAILY    = SpriteManager.EnumTexture.ITEM_DAILY.data.texture
        val ITEM_INSIGHT  = SpriteManager.EnumTexture.ITEM_INSIGHT.data.texture


        // All | list
        val listOnboarding = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures
    }

}