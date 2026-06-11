package com.skindustry.skinly.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.utils.TextureEmpty
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.region

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
        val ERASER   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.eraser_def))
        val SHARE    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.share_def))
        val SETTINGS get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.settings_def))
        val CLOSE    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.close_def))
    }

    // ------------------------------------------------------------------------
    // AButtonAnimTexture.Style
    // ------------------------------------------------------------------------
    object AnimTexture {
        val NONE get() = AButtonAnimTexture.Style(TextureRegionDrawable(TextureEmpty.region))

        val ORANGE get() = AButtonAnimTexture.Style(
            default  = TextureRegionDrawable(gdxGame.assetsAll.orange_def),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.orange_dis),
        )
    }
}