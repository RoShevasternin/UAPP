package com.selftest.mindora.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.selftest.mindora.game.actors.button.base.AButtonTexture.Style
import com.selftest.mindora.game.utils.TextureEmpty
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------

    object Texture {
        val NONE get() = Style(default = TextureRegionDrawable(TextureEmpty.region))

//        val SETTINGS
//            get() = Style(
//                default = TextureRegionDrawable(gdxGame.assetsAll.settings_def),
//                pressed = TextureRegionDrawable(gdxGame.assetsAll.settings_press),
//                disabled = TextureRegionDrawable(gdxGame.assetsAll.settings_press),
//            )
    }

    // ------------------------------------------------------------------------
    // AButtonAnim.Style
    // ------------------------------------------------------------------------

    object Anim {
        val NONE get() = AButtonAnim.Style(TextureRegionDrawable(TextureEmpty.region))

        val RETRY    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsLoader.retry_def))
        val SETTINGS get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.settings_btn))
        val BACK     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.back_btn))
        val DOUBLE   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.double_def))
    }

    // All ------------------------------------------------------------------------
    //val DAILY_CONVERTER_ITEM           get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_converter_item))
    //val DAILY_FREE_RBX_CALCULATOR_ITEM get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_free_rbx_calculator_item))

    // ------------------------------------------------------------------------
    // AButtonAnimTexture.Style
    // ------------------------------------------------------------------------
    object AnimTexture {
        val NONE get() = AButtonAnimTexture.Style(TextureRegionDrawable(TextureEmpty.region))

        val MAIN get() = AButtonAnimTexture.Style(
            default  = TextureRegionDrawable(gdxGame.assetsAll.btn_def),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.btn_press),
        )
        val MAIN_MEDIUM get() = AButtonAnimTexture.Style(
            default  = TextureRegionDrawable(gdxGame.assetsAll.medium_def),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.medium_dis),
        )
    }

}