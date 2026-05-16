package com.rbuxdrop.cougame.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxdrop.cougame.game.utils.gdxGame

object ACheckBoxStyles {
    val GRADIENT get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.box_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.box_check),
    )
}