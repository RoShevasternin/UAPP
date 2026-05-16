package com.rbuxdrop.cougame.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.rbuxdrop.cougame.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val logo         = getRegion("logo")
        val title        = getRegion("title")
        val loader       = getRegion("loader")
        val bottom_title = getRegion("bottom_title")

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
        val box_check       = getAllRegion("box_check")
        val box_def         = getAllRegion("box_def")
        val purple_btn      = getAllRegion("purple_btn")
        val logo_rbs        = getAllRegion("logo_rbs")
        val settings_btn    = getAllRegion("settings_btn")
        val title           = getAllRegion("title")
        val input           = getAllRegion("input")
        val count_now_def   = getAllRegion("count_now_def")
        val count_now_press = getAllRegion("count_now_press")
        val wheel_target    = getAllRegion("wheel_target")
        val panel_question  = getAllRegion("panel_question")

        val listPoint = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        val panel_who = get9Patch("panel_who")
        val panel_rbx = get9Patch("panel_rbx").apply { this.scale(0.5f, 0.5f) }

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val LIST_LANGUAGE     = SpriteManager.EnumTexture.LIST_LANGUAGE.data.texture
        val SEPARATOR         = SpriteManager.EnumTexture.SEPARATOR.data.texture

        // All | panel
        val PANEL_MAIN             = SpriteManager.EnumTexture.PANEL_MAIN.data.texture
        val PANEL_SELECT_CONVERTER = SpriteManager.EnumTexture.PANEL_SELECT_CONVERTER.data.texture
        val PANEL_RESULT           = SpriteManager.EnumTexture.PANEL_RESULT.data.texture
        val PANEL_HOW_QUIZ         = SpriteManager.EnumTexture.PANEL_HOW_QUIZ.data.texture
        val PANEL_PLAY_QUIZ        = SpriteManager.EnumTexture.PANEL_PLAY_QUIZ.data.texture
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | onboarding
        private val ONB_1 = SpriteManager.EnumTexture.ONB_1.data.texture
        private val ONB_2 = SpriteManager.EnumTexture.ONB_2.data.texture
        private val ONB_3 = SpriteManager.EnumTexture.ONB_3.data.texture

        val listOnboarding = listOf(ONB_1, ONB_2, ONB_3)

        // All | daily
        val CLAIM        = SpriteManager.EnumTexture.CLAIM.data.texture
        val CLAIMED      = SpriteManager.EnumTexture.CLAIMED.data.texture
        val CLOSE        = SpriteManager.EnumTexture.CLOSE.data.texture
        val DAILY_RESULT = SpriteManager.EnumTexture.DAILY_RESULT.data.texture

        // All | wheel
        val WHEEL_RESULT = SpriteManager.EnumTexture.WHEEL_RESULT.data.texture
        val WHEEL        = SpriteManager.EnumTexture.WHEEL.data.texture

        // All | scratch
        val PANEL_SCRATCH        = SpriteManager.EnumTexture.PANEL_SCRATCH.data.texture
        val SCRATCH_RESULT       = SpriteManager.EnumTexture.SCRATCH_RESULT.data.texture
        val PANEL_SCRATCH_RESULT = SpriteManager.EnumTexture.PANEL_SCRATCH_RESULT.data.texture

        // All | flip
        val FLIP_CARD        = SpriteManager.EnumTexture.FLIP_CARD.data.texture
        val FLIP_RESULT      = SpriteManager.EnumTexture.FLIP_RESULT.data.texture
        val FLIP_CARD_RESULT = SpriteManager.EnumTexture.FLIP_CARD_RESULT.data.texture

        // All | tips
        val PANEL_SELECT_TIPS = SpriteManager.EnumTexture.PANEL_SELECT_TIPS.data.texture
        private val S1        = SpriteManager.EnumTexture.S1.data.texture
        private val S2        = SpriteManager.EnumTexture.S2.data.texture
        private val S3        = SpriteManager.EnumTexture.S3.data.texture
        private val S4        = SpriteManager.EnumTexture.S4.data.texture
        private val S5        = SpriteManager.EnumTexture.S5.data.texture
        private val S6        = SpriteManager.EnumTexture.S6.data.texture

        val listTips = listOf(S1, S2, S3, S4, S5, S6)


    }

}