package com.bossrbx.rbxcalculator.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.bossrbx.rbxcalculator.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val logo         = getRegion("logo")
        //val title        = getRegion("title")
        val loader       = getRegion("loader")
        val bottom_title = getRegion("bottom_title")

        val panel_no_wifi = getRegion("panel_no_wifi")
        val retry_def     = getRegion("retry_def")
        val wifi          = getRegion("wifi")
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion =
            SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)

        private fun get9Patch(name: String): NinePatch =
            SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val blue_def = getAllRegion("blue_def")
        val blue_dis = getAllRegion("blue_dis")
        val box_check = getAllRegion("box_check")
        val back_def = getAllRegion("back_def")
        val logo    = getAllRegion("logo")
        val settings_def = getAllRegion("settings_def")
        val rs = getAllRegion("rs")

        //val listPoint = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        val panel_rbx = get9Patch("panel_rbx").apply { this.scale(0.5f, 0.5f) }

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val LIST_LANGUAGE = SpriteManager.EnumTexture.LIST_LANGUAGE.data.texture
        val INPUT         = SpriteManager.EnumTexture.INPUT.data.texture

        // All | popup
        val POPUP = SpriteManager.EnumTexture.POPUP.data.texture

        // All | panel
        val PANEL_MAIN             = SpriteManager.EnumTexture.PANEL_MAIN.data.texture
        val PANEL_SELECT_CONVERTER = SpriteManager.EnumTexture.PANEL_SELECT_CONVERTER.data.texture
        val PANEL_CONVERTER_RESULT = SpriteManager.EnumTexture.PANEL_CONVERTER_RESULT.data.texture
        val PANEL_SETTINGS         = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | scratch
        val PANEL_SCRATCH  = SpriteManager.EnumTexture.PANEL_SCRATCH.data.texture
        val SCRATCH_FRONT  = SpriteManager.EnumTexture.SCRATCH_FRONT.data.texture
        val SCRATCH_RESULT = SpriteManager.EnumTexture.SCRATCH_RESULT.data.texture

        // All | onboarding
        private val ONB_1 = SpriteManager.EnumTexture.ONB_1.data.texture
        private val ONB_2 = SpriteManager.EnumTexture.ONB_2.data.texture
        private val ONB_3 = SpriteManager.EnumTexture.ONB_3.data.texture

        private val ONBOARDING_1 = SpriteManager.EnumTexture.ONBOARDING_1.data.texture
        private val ONBOARDING_2 = SpriteManager.EnumTexture.ONBOARDING_2.data.texture
        private val ONBOARDING_3 = SpriteManager.EnumTexture.ONBOARDING_3.data.texture

        val listOnb = listOf(ONB_1, ONB_2, ONB_3)
        val listOnboarding = listOf(ONBOARDING_1, ONBOARDING_2, ONBOARDING_3)

        // All | daily
        val CLAIM     = SpriteManager.EnumTexture.CLAIM.data.texture
        val CLAIMED   = SpriteManager.EnumTexture.CLAIMED.data.texture
        val CLOSE     = SpriteManager.EnumTexture.CLOSE.data.texture
        val COME_BACK = SpriteManager.EnumTexture.COME_BACK.data.texture

        // All | wheel
        val PANEL_SPIN  = SpriteManager.EnumTexture.PANEL_SPIN.data.texture
        val WHEEL       = SpriteManager.EnumTexture.WHEEL.data.texture
        val WHEEL_FRONT = SpriteManager.EnumTexture.WHEEL_FRONT.data.texture

        // All | flipCard
        val CIRCLE_CARD               = SpriteManager.EnumTexture.CIRCLE_CARD.data.texture
        val SHADOW_FLIP               = SpriteManager.EnumTexture.SHADOW_FLIP.data.texture
        val TEXT_FLIP_CARD            = SpriteManager.EnumTexture.TEXT_FLIP_CARD.data.texture
        val TEXT_FLIP_CONGRATULATIONS = SpriteManager.EnumTexture.TEXT_FLIP_CONGRATULATIONS.data.texture
        val CARD_BOSS                 = SpriteManager.EnumTexture.CARD_BOSS.data.texture
        val CARD_REWARD               = SpriteManager.EnumTexture.CARD_REWARD.data.texture

        // All | quiz
        val PANEL_ANSWER      = SpriteManager.EnumTexture.PANEL_ANSWER.data.texture
        val PANEL_PLAY_QUIZ   = SpriteManager.EnumTexture.PANEL_PLAY_QUIZ.data.texture
        val POPUP_QUIZ_RESULT = SpriteManager.EnumTexture.POPUP_QUIZ_RESULT.data.texture
    }
}