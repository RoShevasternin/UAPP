package com.treprosure.starbxup.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.treprosure.starbxup.game.utils.TextureEmpty
import com.treprosure.starbxup.game.utils.gdxGame

object ACheckBoxStyles {
    val BOX_DEF get() = ACheckBox.Style(
        default = TextureRegionDrawable(TextureEmpty),
        checked = TextureRegionDrawable(gdxGame.assetsAll.yellow_check),
    )
    val QUIZ_TAB get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.quiz_tab_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.quiz_tab_check),
    )
    val FILTER_TAB get() = ACheckBox.Style(
        default = NinePatchDrawable(gdxGame.assetsAll.tab_def),
        checked = NinePatchDrawable(gdxGame.assetsAll.tab_check),
    )
}