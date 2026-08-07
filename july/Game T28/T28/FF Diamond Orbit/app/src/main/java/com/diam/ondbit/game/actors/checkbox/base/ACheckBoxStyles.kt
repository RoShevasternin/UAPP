package com.diam.ondbit.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.diam.ondbit.game.utils.gdxGame

object ACheckBoxStyles {
    val ITEM get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.item_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.item_check),
    )
    val FILTER_TAB get() = ACheckBox.Style(
        default = NinePatchDrawable(gdxGame.assetsAll.tab_def),
        checked = NinePatchDrawable(gdxGame.assetsAll.tab_check),
    )
}