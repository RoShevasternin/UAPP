package com.rbuxdrop.cougame.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxdrop.cougame.game.utils.TextureEmpty
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------
    object Texture {
        val NONE get() = AButtonTexture.Style(default = TextureRegionDrawable(TextureEmpty.region))

        val COUNT_NOW get() = AButtonTexture.Style(
            default = TextureRegionDrawable(gdxGame.assetsAll.count_now_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.count_now_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.count_now_press),
        )
    }

    // ------------------------------------------------------------------------
    // AButtonAnim.Style
    // ------------------------------------------------------------------------
    object Anim {
        val NONE get() = AButtonAnim.Style(TextureRegionDrawable(TextureEmpty.region))

        val RETRY get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsLoader.retry_def))

        val BACK     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.back_def))
        val PURPLE   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.purple_btn))
        val SETTINGS get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.settings_btn))
    }
}