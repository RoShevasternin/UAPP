package com.selftest.mindora.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.selftest.mindora.game.utils.TextureEmpty
import com.selftest.mindora.game.utils.gdxGame

object ACheckBoxStyles {
    val CHECK get() = ACheckBox.Style(
        default = TextureRegionDrawable(TextureEmpty),
        checked = TextureRegionDrawable(gdxGame.assetsAll.check),
    )
}