package com.racing.funtols.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.racing.funtols.game.utils.gdxGame

object ACheckBoxStyles {
    val ITEM get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.ITEM_DEF),
        checked = TextureRegionDrawable(gdxGame.assetsAll.ITEM_CHECK),
    )
    val ITEM_LONG get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.ITEM_LONG_DEF),
        checked = TextureRegionDrawable(gdxGame.assetsAll.ITEM_LONG_CHECK),
    )
    val FILTER_TAB get() = ACheckBox.Style(
        default = NinePatchDrawable(gdxGame.assetsAll.tab_check),
        checked = NinePatchDrawable(gdxGame.assetsAll.tab_def),
    )
    val CONVERTER get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.converter_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.converter_check),
    )
}