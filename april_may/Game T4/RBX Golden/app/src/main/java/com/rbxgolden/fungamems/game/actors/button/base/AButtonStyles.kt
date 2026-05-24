package com.rbxgolden.fungamems.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxgolden.fungamems.game.utils.TextureEmpty
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------
    object Texture {
        val NONE get() = AButtonTexture.Style(default = TextureRegionDrawable(TextureEmpty.region))

        val COUNT_NOW get() = AButtonTexture.Style(
            default = TextureRegionDrawable(gdxGame.assetsAll.golden_def),
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

        val BACK     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.back_def))
        val GOLDEN   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.golden_def))
        val SETTINGS get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.settings))
    }

    // ------------------------------------------------------------------------
    // AButtonAnimTexture.Style
    // ------------------------------------------------------------------------
    object AnimTexture {
        val NONE get() = AButtonAnimTexture.Style(TextureRegionDrawable(TextureEmpty.region))

        val GOLDEN get() = AButtonAnimTexture.Style(
            default  = TextureRegionDrawable(gdxGame.assetsAll.golden_def),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.golden_dis),
        )
    }
}