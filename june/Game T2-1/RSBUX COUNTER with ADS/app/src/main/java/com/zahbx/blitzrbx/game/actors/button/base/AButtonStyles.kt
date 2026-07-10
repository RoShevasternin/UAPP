package com.zahbx.blitzrbx.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.zahbx.blitzrbx.game.utils.TextureEmpty
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------
    object Texture {
        val NONE get() = AButtonTexture.Style(default = TextureRegionDrawable(TextureEmpty.region))

        val GREEN_GRAY get() = AButtonTexture.Style(
            default = TextureRegionDrawable(gdxGame.assetsAll.green_btn),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.gray_btn),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.gray_btn),
        )
    }

    // ------------------------------------------------------------------------
    // AButtonAnim.Style
    // ------------------------------------------------------------------------
    object Anim {
        val NONE get() = AButtonAnim.Style(TextureRegionDrawable(TextureEmpty.region))

        val RETRY get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsLoader.retry_def))

        val BACK  get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.back_btn))
        val GREEN get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.green_btn))
    }
}