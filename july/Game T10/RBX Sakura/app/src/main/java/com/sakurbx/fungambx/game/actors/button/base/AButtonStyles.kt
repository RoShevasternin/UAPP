package com.sakurbx.fungambx.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.sakurbx.fungambx.game.utils.TextureEmpty
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------
    object Texture {
        val NONE get() = AButtonTexture.Style(default = TextureRegionDrawable(TextureEmpty.region))

//        val COUNT_NOW get() = AButtonTexture.Style(
//            default = TextureRegionDrawable(gdxGame.assetsAll.golden_def),
//            pressed = TextureRegionDrawable(gdxGame.assetsAll.gray_btn),
//            disabled = TextureRegionDrawable(gdxGame.assetsAll.gray_btn),
//        )
    }

    // ------------------------------------------------------------------------
    // AButtonAnim.Style
    // ------------------------------------------------------------------------
    object Anim {
        val NONE get() = AButtonAnim.Style(TextureRegionDrawable(TextureEmpty.region))

        val RETRY get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsLoader.retry_def))

        val BACK     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.back_def))
        val PINK     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.pink_def))
        val CLAIM    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.claim_def))
        val FREE     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.get_prize_def))
        val SETTINGS get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.settings_def))
    }

    // ------------------------------------------------------------------------
    // AButtonAnimTexture.Style
    // ------------------------------------------------------------------------
    object AnimTexture {
        val NONE get() = AButtonAnimTexture.Style(TextureRegionDrawable(TextureEmpty.region))

//        val YELLOW get() = AButtonAnimTexture.Style(
//            default  = TextureRegionDrawable(gdxGame.assetsAll.yellow_def),
//            disabled = TextureRegionDrawable(gdxGame.assetsAll.yellow_check),
//        )
    }
}