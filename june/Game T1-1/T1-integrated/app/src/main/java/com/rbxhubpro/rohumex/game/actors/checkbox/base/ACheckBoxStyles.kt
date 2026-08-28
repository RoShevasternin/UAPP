package com.rbxhubpro.rohumex.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxhubpro.rohumex.game.utils.TextureEmpty
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.game.utils.region

object ACheckBoxStyles {
    val HEX  get() = ACheckBox.Style(
        default = TextureRegionDrawable(TextureEmpty.region),
        checked = TextureRegionDrawable(gdxGame.assetsAll.hex_check),
    )
    val LONG get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.long_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.long_check),
    )
}