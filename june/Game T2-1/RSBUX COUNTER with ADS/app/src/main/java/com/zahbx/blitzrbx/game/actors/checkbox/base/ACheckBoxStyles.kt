package com.zahbx.blitzrbx.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.zahbx.blitzrbx.game.utils.gdxGame

object ACheckBoxStyles {
    val GRADIENT get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.gradient_box_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.gradient_box_check),
    )
}