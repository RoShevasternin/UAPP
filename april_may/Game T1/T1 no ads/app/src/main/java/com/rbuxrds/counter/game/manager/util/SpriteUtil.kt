package com.rbuxrds.counter.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.rbuxrds.counter.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val logo  = getRegion("logo")
        val title = getRegion("title")

        //val listLight = listOf(C1, C2, C3, C4, C5, C6).reversed()
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
        private fun getMainRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.MAIN.data.atlas.findRegion(name)
        private fun getCharactersRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.CHARACTERS.data.atlas.findRegion(name)

        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val next_def     = getAllRegion("next_def")
        val next_press   = getAllRegion("next_press")
        val back_def     = getAllRegion("back_def")
        val back_press   = getAllRegion("back_press")
        val hex_check    = getAllRegion("hex_check")
        val long_check   = getAllRegion("long_check")
        val long_def     = getAllRegion("long_def")
        val rbx_panel    = getAllRegion("rbx_panel")
        val wheel_target = getAllRegion("wheel_target")

        // All | daily
        val cosmo_1                        = getAllRegion("cosmo_1")
        val cosmo_2                        = getAllRegion("cosmo_2")
        val cosmo_3                        = getAllRegion("cosmo_3")
        val cosmo_hex                      = getAllRegion("cosmo_hex")
        val cosmo_person                   = getAllRegion("cosmo_person")
        val daily_converter_item           = getAllRegion("daily_converter_item")
        val daily_free_rbx_calculator_item = getAllRegion("daily_free_rbx_calculator_item")
        val ok_def                         = getAllRegion("ok_def")
        val ok_press                       = getAllRegion("ok_press")
        val false_def                      = getAllRegion("false_def")
        val false_press                    = getAllRegion("false_press")
        val panel_level                    = getAllRegion("panel_level")
        val panel_question                 = getAllRegion("panel_question")
        val true_def                       = getAllRegion("true_def")
        val true_press                     = getAllRegion("true_press")

        //val listGlarePanelGame = List(4) { getAllRegion("glare_panel_game_${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------

        val panel_who = get9Patch("panel_who")

        // ------------------------------------------------------------------------------
        // ATLAS MAIN
        // ------------------------------------------------------------------------------
        val accessories_def     = getMainRegion("accessories_def")
        val all_charatcers_def  = getMainRegion("all_charatcers_def")
        val all_clothing_def    = getMainRegion("all_clothing_def")
        val animations_def      = getMainRegion("animations_def")
        val daily_converter_def = getMainRegion("daily_converter_def")
        val daily_new_rbx_def   = getMainRegion("daily_new_rbx_def")
        val dollar_to_rbx_def   = getMainRegion("dollar_to_rbx_def")
        val head_body_def       = getMainRegion("head_body_def")
        val logic_quiz_time_def = getMainRegion("logic_quiz_time_def")
        val meme_for_fun_def    = getMainRegion("meme_for_fun_def")
        val rbx_to_dollar_def   = getMainRegion("rbx_to_dollar_def")
        val redeem_coin_def     = getMainRegion("redeem_coin_def")
        val scratch_win_def     = getMainRegion("scratch_win_def")
        val settings_def        = getMainRegion("settings_def")
        val spin_wheel_def      = getMainRegion("spin_wheel_def")

        // ------------------------------------------------------------------------------
        // ATLAS CHARACTERS
        // ------------------------------------------------------------------------------
        val listCharacters = List(20) { getCharactersRegion("${it.inc()}") }

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // ALL
        val WHEEL = SpriteManager.EnumTexture.WHEEL.data.texture

        // ALL | screen_1
        val SELECT_ANIME = SpriteManager.EnumTexture.SELECT_ANIME.data.texture
        val SELECT_CLOTH = SpriteManager.EnumTexture.SELECT_CLOTH.data.texture
        val SELECT_SKIN  = SpriteManager.EnumTexture.SELECT_SKIN.data.texture

        // ALL | popup
        val POPUP_DAILY_FREE_DONE = SpriteManager.EnumTexture.POPUP_DAILY_FREE_DONE.data.texture
        val POPUP_DAILY_FREE_X    = SpriteManager.EnumTexture.POPUP_DAILY_FREE_X.data.texture
        val POPUP_REDEEM          = SpriteManager.EnumTexture.POPUP_REDEEM.data.texture

        // ALL | panel
        val PANEL_SCRATCH        = SpriteManager.EnumTexture.PANEL_SCRATCH.data.texture
        val PANEL_SCRATCH_RESULT = SpriteManager.EnumTexture.PANEL_SCRATCH_RESULT.data.texture
        val PANEL_REDEEM         = SpriteManager.EnumTexture.PANEL_REDEEM.data.texture
        val PANEL_MEMES_FOR_FUN  = SpriteManager.EnumTexture.PANEL_MEMES_FOR_FUN.data.texture
        val PANEL_SETTINGS       = SpriteManager.EnumTexture.PANEL_SETTINGS.data.texture

        // ALL | redeem
        val REDEEM_COFFER       = SpriteManager.EnumTexture.REDEEM_COFFER.data.texture
        val REDEEM_GLOW         = SpriteManager.EnumTexture.REDEEM_GLOW.data.texture

        // ALL | animations
        val ANIMATIONS_BUNDLES = SpriteManager.EnumTexture.ANIMATIONS_BUNDLES.data.texture
        val ANIMATIONS_EMOTES  = SpriteManager.EnumTexture.ANIMATIONS_EMOTES.data.texture
        val ANIMATIONS_SELECT  = SpriteManager.EnumTexture.ANIMATIONS_SELECT.data.texture

        // ALL | accessories
        val ACCESSORIES_FACE   = SpriteManager.EnumTexture.ACCESSORIES_FACE.data.texture
        val ACCESSORIES_HEAD   = SpriteManager.EnumTexture.ACCESSORIES_HEAD.data.texture
        val ACCESSORIES_NECK   = SpriteManager.EnumTexture.ACCESSORIES_NECK.data.texture
        val ACCESSORIES_SELECT = SpriteManager.EnumTexture.ACCESSORIES_SELECT.data.texture

        // ALL | clothing
        val CLOTHING_SELECT     = SpriteManager.EnumTexture.CLOTHING_SELECT.data.texture
        val COLLECTION_PANTS    = SpriteManager.EnumTexture.COLLECTION_PANTS.data.texture
        val COLLECTION_SHIRTS   = SpriteManager.EnumTexture.COLLECTION_SHIRTS.data.texture
        val COLLECTION_SHOES    = SpriteManager.EnumTexture.COLLECTION_SHOES.data.texture
        val COLLECTION_T_SHIETS = SpriteManager.EnumTexture.COLLECTION_T_SHIETS.data.texture

        // ALL | head_and_body
        val HEAD_AND_BODY_LOOK   = SpriteManager.EnumTexture.HEAD_AND_BODY_LOOK.data.texture
        val HEAD_AND_BODY_SELECT = SpriteManager.EnumTexture.HEAD_AND_BODY_SELECT.data.texture
        val HEAD_AND_BODY_SHAPE  = SpriteManager.EnumTexture.HEAD_AND_BODY_SHAPE.data.texture


    }

}