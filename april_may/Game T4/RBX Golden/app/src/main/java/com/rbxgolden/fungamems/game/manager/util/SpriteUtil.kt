package com.rbxgolden.fungamems.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.rbxgolden.fungamems.game.manager.SpriteManager

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

        val BACKGROUND = SpriteManager.EnumTexture.BACKGROUND.data.texture

    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)

        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val back_def        = getAllRegion("back_def")
        val golden_def      = getAllRegion("golden_def")
        val golden_dis      = getAllRegion("golden_dis")
        val yellow_box      = getAllRegion("yellow_box")
        val yellow_long_box = getAllRegion("yellow_long_box")
        val coin            = getAllRegion("coin")
        val logo            = getAllRegion("logo")
        val settings        = getAllRegion("settings")
        val claim           = getAllRegion("claim")
        val claimed         = getAllRegion("claimed")
        val close           = getAllRegion("close")
        val gray_btn        = getAllRegion("gray_btn")

        //val listPoint = List(3) { getAllRegion("p${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------
        val panel_rbx = get9Patch("panel_rbx").apply { this.scale(0.5f, 0.5f) }

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val SELECT_ANIMATION_PACK = SpriteManager.EnumTexture.SELECT_ANIMATION_PACK.data.texture
        val MEMS                  = SpriteManager.EnumTexture.MEMS.data.texture

        // All | panel
        val PANEL_MAIN                   = SpriteManager.EnumTexture.PANEL_MAIN.data.texture
        val PANEL_QUIZ                   = SpriteManager.EnumTexture.PANEL_QUIZ.data.texture
        val PANEL_GIFT                   = SpriteManager.EnumTexture.PANEL_GIFT.data.texture
        val PANEL_ALL_CLOTHES_ANIMATIONS = SpriteManager.EnumTexture.PANEL_ALL_CLOTHES_ANIMATIONS.data.texture
        val PANEL_SETTINGS               = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // All | result
        val DAILY_RESULT   = SpriteManager.EnumTexture.DAILY_RESULT.data.texture
        val WHEEL_RESULT   = SpriteManager.EnumTexture.WHEEL_RESULT.data.texture

        // All | select
        private val s1_1 = SpriteManager.EnumTexture.s1_1.data.texture
        private val s1_2 = SpriteManager.EnumTexture.s1_2.data.texture
        private val s1_3 = SpriteManager.EnumTexture.s1_3.data.texture
        private val s1_4 = SpriteManager.EnumTexture.s1_4.data.texture
        private val s1_5 = SpriteManager.EnumTexture.s1_5.data.texture

        private val s2_1 = SpriteManager.EnumTexture.s2_1.data.texture
        private val s2_2 = SpriteManager.EnumTexture.s2_2.data.texture
        private val s2_3 = SpriteManager.EnumTexture.s2_3.data.texture

        private val s3_1 = SpriteManager.EnumTexture.s3_1.data.texture
        private val s3_2 = SpriteManager.EnumTexture.s3_2.data.texture

        val listS1 = listOf(s1_1, s1_2, s1_3, s1_4, s1_5)
        val listS2 = listOf(s2_1, s2_2, s2_3)
        val listS3 = listOf(s3_1, s3_2)

        // All | converter
        val INPUT                  = SpriteManager.EnumTexture.INPUT.data.texture
        val PANEL_RESULT           = SpriteManager.EnumTexture.PANEL_RESULT.data.texture
        val PANEL_SELECT_CONVERTER = SpriteManager.EnumTexture.PANEL_SELECT_CONVERTER.data.texture

        // All | wheel
        val WHEEL_BACK  = SpriteManager.EnumTexture.WHEEL_BACK.data.texture
        val WHEEL_FRONT = SpriteManager.EnumTexture.WHEEL_FRONT.data.texture

        // All | scratch
        val PANEL_SCRATCH        = SpriteManager.EnumTexture.PANEL_SCRATCH.data.texture
        val PANEL_SCRATCH_RESULT = SpriteManager.EnumTexture.PANEL_SCRATCH_RESULT.data.texture

        // All | select_charatcers
        private val sc_1 = SpriteManager.EnumTexture.sc_1.data.texture
        private val sc_2 = SpriteManager.EnumTexture.sc_2.data.texture

        val listSC = listOf(sc_1, sc_2)

        // All | character
        private val _1  = SpriteManager.EnumTexture._1.data.texture
        private val _2  = SpriteManager.EnumTexture._2.data.texture
        private val _3  = SpriteManager.EnumTexture._3.data.texture
        private val _4  = SpriteManager.EnumTexture._4.data.texture
        private val _5  = SpriteManager.EnumTexture._5.data.texture
        private val _6  = SpriteManager.EnumTexture._6.data.texture
        private val _7  = SpriteManager.EnumTexture._7.data.texture
        private val _8  = SpriteManager.EnumTexture._8.data.texture
        private val _9  = SpriteManager.EnumTexture._9.data.texture
        private val _10 = SpriteManager.EnumTexture._10.data.texture
        private val _11 = SpriteManager.EnumTexture._11.data.texture
        private val _12 = SpriteManager.EnumTexture._12.data.texture
        private val _13 = SpriteManager.EnumTexture._13.data.texture
        private val _14 = SpriteManager.EnumTexture._14.data.texture
        private val _15 = SpriteManager.EnumTexture._15.data.texture
        private val _16 = SpriteManager.EnumTexture._16.data.texture
        private val _17 = SpriteManager.EnumTexture._17.data.texture
        private val _18 = SpriteManager.EnumTexture._18.data.texture
        private val _19 = SpriteManager.EnumTexture._19.data.texture
        private val _20 = SpriteManager.EnumTexture._20.data.texture
        private val _21 = SpriteManager.EnumTexture._21.data.texture
        private val _22 = SpriteManager.EnumTexture._22.data.texture
        private val _23 = SpriteManager.EnumTexture._23.data.texture
        private val _24 = SpriteManager.EnumTexture._24.data.texture

        val listCharacter = listOf(
            _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12,
            _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23, _24,
        )

        // All | all_clothes_animations
        // clothing
        private val CLOTHING_H1 = SpriteManager.EnumTexture.CLOTHING_H1.data.texture
        private val CLOTHING_H2 = SpriteManager.EnumTexture.CLOTHING_H2.data.texture
        private val CLOTHING_H3 = SpriteManager.EnumTexture.CLOTHING_H3.data.texture
        private val CLOTHING_H4 = SpriteManager.EnumTexture.CLOTHING_H4.data.texture
        private val CLOTHING_H5 = SpriteManager.EnumTexture.CLOTHING_H5.data.texture

        private val CLOTHING_P1 = SpriteManager.EnumTexture.CLOTHING_P1.data.texture
        private val CLOTHING_P2 = SpriteManager.EnumTexture.CLOTHING_P2.data.texture
        private val CLOTHING_P3 = SpriteManager.EnumTexture.CLOTHING_P3.data.texture
        private val CLOTHING_P4 = SpriteManager.EnumTexture.CLOTHING_P4.data.texture

        val listClothingHeader = listOf(CLOTHING_H1, CLOTHING_H2, CLOTHING_H3, CLOTHING_H4, CLOTHING_H5)
        val listClothingPanel  = listOf(CLOTHING_P1, CLOTHING_P2, CLOTHING_P3, CLOTHING_P4)

        // accessories
        private val ACCESSORIES_H1 = SpriteManager.EnumTexture.ACCESSORIES_H1.data.texture
        private val ACCESSORIES_H2 = SpriteManager.EnumTexture.ACCESSORIES_H2.data.texture
        private val ACCESSORIES_H3 = SpriteManager.EnumTexture.ACCESSORIES_H3.data.texture
        private val ACCESSORIES_H4 = SpriteManager.EnumTexture.ACCESSORIES_H4.data.texture

        private val ACCESSORIES_P1 = SpriteManager.EnumTexture.ACCESSORIES_P1.data.texture
        private val ACCESSORIES_P2 = SpriteManager.EnumTexture.ACCESSORIES_P2.data.texture
        private val ACCESSORIES_P3 = SpriteManager.EnumTexture.ACCESSORIES_P3.data.texture

        val listAccessoriesHeader = listOf(ACCESSORIES_H1, ACCESSORIES_H2, ACCESSORIES_H3, ACCESSORIES_H4)
        val listAccessoriesPanel  = listOf(ACCESSORIES_P1, ACCESSORIES_P2, ACCESSORIES_P3)

        // animations
        private val ANIMATIONS_H1 = SpriteManager.EnumTexture.ANIMATIONS_H1.data.texture
        private val ANIMATIONS_H2 = SpriteManager.EnumTexture.ANIMATIONS_H2.data.texture
        private val ANIMATIONS_H3 = SpriteManager.EnumTexture.ANIMATIONS_H3.data.texture

        private val ANIMATIONS_P1 = SpriteManager.EnumTexture.ANIMATIONS_P1.data.texture
        private val ANIMATIONS_P2 = SpriteManager.EnumTexture.ANIMATIONS_P2.data.texture

        val listAnimationsHeader = listOf(ANIMATIONS_H1, ANIMATIONS_H2, ANIMATIONS_H3)
        val listAnimationsPanel  = listOf(ANIMATIONS_P1, ANIMATIONS_P2)
        
        // head_body
        private val HEAD_BODY_H1 = SpriteManager.EnumTexture.HEAD_BODY_H1.data.texture
        private val HEAD_BODY_H2 = SpriteManager.EnumTexture.HEAD_BODY_H2.data.texture
        private val HEAD_BODY_H3 = SpriteManager.EnumTexture.HEAD_BODY_H3.data.texture
        private val HEAD_BODY_H4 = SpriteManager.EnumTexture.HEAD_BODY_H4.data.texture

        private val HEAD_BODY_P1 = SpriteManager.EnumTexture.HEAD_BODY_P1.data.texture
        private val HEAD_BODY_P2 = SpriteManager.EnumTexture.HEAD_BODY_P2.data.texture
        private val HEAD_BODY_P3 = SpriteManager.EnumTexture.HEAD_BODY_P3.data.texture

        val listHead_BodyHeader = listOf(HEAD_BODY_H1, HEAD_BODY_H2, HEAD_BODY_H3, HEAD_BODY_H4)
        val listHead_BodyPanel  = listOf(HEAD_BODY_P1, HEAD_BODY_P2, HEAD_BODY_P3)
    }

}