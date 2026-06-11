package com.skindustry.skinly.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.utils.TextureEmpty
import com.skindustry.skinly.game.utils.gdxGame

object ACheckBoxStyles {
    val SELECTOR_ITEM get() = ACheckBox.Style(
        default = NinePatchDrawable(gdxGame.assetsAll.panel_box_def),
        checked = NinePatchDrawable(gdxGame.assetsAll.panel_box_check),
    )
    val HOME get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.home_check),
        checked = TextureRegionDrawable(gdxGame.assetsAll.home_def),
    )
    val SKIN_BOOK get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.skin_book_check),
        checked = TextureRegionDrawable(gdxGame.assetsAll.skin_book_def),
    )
    val FILTER_ITEM get() = ACheckBox.Style(
        default = NinePatchDrawable(gdxGame.assetsAll.filter_item_def),
        checked = NinePatchDrawable(gdxGame.assetsAll.filter_item_check),
    )
    val TEXTURE_ITEM get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.texture_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.texture_check),
    )
    val BOT_TEXTURE get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.bot_texture_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.bot_texture_check),
    )
    val BOT_STICKER get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.sticker_def),
        checked = TextureRegionDrawable(gdxGame.assetsAll.sticker_check),
    )
    val MUSIC get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.music_off),
        checked = TextureRegionDrawable(gdxGame.assetsAll.music_on),
    )
}