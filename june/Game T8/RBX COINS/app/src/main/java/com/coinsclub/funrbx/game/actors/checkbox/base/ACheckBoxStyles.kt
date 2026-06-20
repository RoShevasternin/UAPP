package com.coinsclub.funrbx.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.coinsclub.funrbx.game.utils.TextureEmpty
import com.coinsclub.funrbx.game.utils.gdxGame

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
        default = NinePatchDrawable(gdxGame.assetsAll.tab_def),
        checked = NinePatchDrawable(gdxGame.assetsAll.tab_check),
    )
}