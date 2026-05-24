package com.rbuxrds.counter.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxrds.counter.game.utils.TextureEmpty
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------

    val NONE get() = AButtonTexture.Style(
        default  = TextureRegionDrawable(TextureEmpty.region),
        pressed  = TextureRegionDrawable(TextureEmpty.region),
        disabled = TextureRegionDrawable(TextureEmpty.region),
    )
    val NEXT get() = AButtonTexture.Style(
        default  = TextureRegionDrawable(gdxGame.assetsAll.next_def),
        pressed  = TextureRegionDrawable(gdxGame.assetsAll.next_press),
        disabled = TextureRegionDrawable(gdxGame.assetsAll.next_press),
    )
    val BACK get() = AButtonTexture.Style(
        default  = TextureRegionDrawable(gdxGame.assetsAll.back_def),
        pressed  = TextureRegionDrawable(gdxGame.assetsAll.back_press),
        disabled = TextureRegionDrawable(gdxGame.assetsAll.back_press),
    )
    val OK get() = AButtonTexture.Style(
        default  = TextureRegionDrawable(gdxGame.assetsAll.ok_def),
        pressed  = TextureRegionDrawable(gdxGame.assetsAll.ok_press),
        disabled = TextureRegionDrawable(gdxGame.assetsAll.ok_press),
    )
    val TRUE get() = AButtonTexture.Style(
        default  = TextureRegionDrawable(gdxGame.assetsAll.true_def),
        pressed  = TextureRegionDrawable(gdxGame.assetsAll.true_press),
        disabled = TextureRegionDrawable(gdxGame.assetsAll.true_press),
    )
    val FALSE get() = AButtonTexture.Style(
        default  = TextureRegionDrawable(gdxGame.assetsAll.false_def),
        pressed  = TextureRegionDrawable(gdxGame.assetsAll.false_press),
        disabled = TextureRegionDrawable(gdxGame.assetsAll.false_press),
    )

    // ------------------------------------------------------------------------
    // AButtonAnim.Style
    // ------------------------------------------------------------------------

    // All ------------------------------------------------------------------------
    val DAILY_CONVERTER_ITEM           get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_converter_item))
    val DAILY_FREE_RBX_CALCULATOR_ITEM get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_free_rbx_calculator_item))

    // All | main ------------------------------------------------------------------------
    val ACCESSORIES     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.accessories_def))
    val ALL_CHARATCERS  get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.all_charatcers_def))
    val ALL_CLOTHING    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.all_clothing_def))
    val ANIMATIONS      get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.animations_def))
    val DAILY_CONVERTER get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_converter_def))
    val DAILY_NEW_RBX   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_new_rbx_def))
    val DOLLAR_TO_RBX   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.dollar_to_rbx_def))
    val HEAD_BODY       get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.head_body_def))
    val LOGIC_QUIZ_TIME get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.logic_quiz_time_def))
    val MEME_FOR_FUN    get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.meme_for_fun_def))
    val RBX_TO_DOLLAR   get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.rbx_to_dollar_def))
    val REDEEM_COIN     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.redeem_coin_def))
    val SCRATCH_WIN     get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.scratch_win_def))
    val SETTINGS        get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.settings_def))
    val SPIN_WHEEL      get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.spin_wheel_def))

}