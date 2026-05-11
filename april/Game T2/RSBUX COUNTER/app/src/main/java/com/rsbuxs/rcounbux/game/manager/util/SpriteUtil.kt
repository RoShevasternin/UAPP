package com.rsbuxs.rcounbux.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.rsbuxs.rcounbux.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val logo   = getRegion("logo")
        val title  = getRegion("title")
        val loader = getRegion("loader")

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

        val back_btn           = getAllRegion("back_btn")
        val gradient_box_check = getAllRegion("gradient_box_check")
        val gradient_box_def   = getAllRegion("gradient_box_def")
        val green_btn          = getAllRegion("green_btn")
        val rbux               = getAllRegion("rbux")
        val logo_mini          = getAllRegion("logo_mini")
        val field_edit         = getAllRegion("field_edit")
        val gray_btn           = getAllRegion("gray_btn")
        val result_n_to_rbx    = getAllRegion("result_n_to_rbx")
        val wheel_target       = getAllRegion("wheel_target")
        val bag                = getAllRegion("bag")
        val glow               = getAllRegion("glow")

        val daily_claim   = getAllRegion("claim")
        val daily_claimed = getAllRegion("claimed")
        val daily_close   = getAllRegion("close")

        val listR = List(3) { getAllRegion("r${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        val panel_who      = get9Patch("panel_who")
        val panel_gradient = get9Patch("panel_gradient").apply { this.scale(0.5f, 0.5f) }

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val LIST_LANGUAGE     = SpriteManager.EnumTexture.LIST_LANGUAGE.data.texture
        val WELCOME           = SpriteManager.EnumTexture.WELCOME.data.texture
        val WELCOME_MINI_GAME = SpriteManager.EnumTexture.WELCOME_MINI_GAME.data.texture
        val RESULT_MINI_GAME  = SpriteManager.EnumTexture.RESULT_MINI_GAME.data.texture
        val WHEEL             = SpriteManager.EnumTexture.WHEEL.data.texture

        // All | panel
        val PANEL_MAIN           = SpriteManager.EnumTexture.PANEL_MAIN.data.texture
        val PANEL_N_TO_RBX       = SpriteManager.EnumTexture.PANEL_N_TO_RBX.data.texture
        val PANEL_BOOST          = SpriteManager.EnumTexture.PANEL_BOOST.data.texture
        val PANEL_QUIZ           = SpriteManager.EnumTexture.PANEL_QUIZ.data.texture
        val PANEL_SCRATCH        = SpriteManager.EnumTexture.PANEL_SCRATCH.data.texture
        val PANEL_SCRATCH_RESULT = SpriteManager.EnumTexture.PANEL_SCRATCH_RESULT.data.texture
        val PANEL_REFERRAL       = SpriteManager.EnumTexture.PANEL_REFERRAL.data.texture
        val PANEL_SETTINGS       = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

    }

}