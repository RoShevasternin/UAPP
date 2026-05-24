package com.bossrbx.rbxcalculator.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.bossrbx.rbxcalculator.game.utils.TextureEmpty
import com.bossrbx.rbxcalculator.game.utils.gdxGame

object ACheckBoxStyles {
    val DEF get() = ACheckBox.Style(
        default = TextureRegionDrawable(TextureEmpty),
        checked = TextureRegionDrawable(gdxGame.assetsAll.box_check),
    )
}