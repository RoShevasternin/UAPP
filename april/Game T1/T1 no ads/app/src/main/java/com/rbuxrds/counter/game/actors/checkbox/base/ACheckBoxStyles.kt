package com.rbuxrds.counter.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxrds.counter.game.utils.TextureEmpty
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.region

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