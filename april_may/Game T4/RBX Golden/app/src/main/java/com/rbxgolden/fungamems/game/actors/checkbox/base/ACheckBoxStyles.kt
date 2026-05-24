package com.rbxgolden.fungamems.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxgolden.fungamems.game.utils.TextureEmpty
import com.rbxgolden.fungamems.game.utils.gdxGame

object ACheckBoxStyles {
    val YELLOW get() = ACheckBox.Style(
        default = TextureRegionDrawable(TextureEmpty),
        checked = TextureRegionDrawable(gdxGame.assetsAll.yellow_box),
    )
    val YELLOW_LONG get() = ACheckBox.Style(
        default = TextureRegionDrawable(TextureEmpty),
        checked = TextureRegionDrawable(gdxGame.assetsAll.yellow_long_box),
    )
}