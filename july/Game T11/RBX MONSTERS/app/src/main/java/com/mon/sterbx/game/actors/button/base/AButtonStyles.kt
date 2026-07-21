package com.mon.sterbx.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.mon.sterbx.game.utils.TextureEmpty
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.region

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
        val ORANGE   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.orange_def))
        val TRUE     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.true_def))
        val FALSE    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.false_def))
        val CLAIM    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.claim_def))
        val CLOSE    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.close_def))
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