package com.selftest.mindora.game.actors.test.card

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.base.AButtonAnimTexture
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.NumberFormatter
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardTestBtn — кнопка всередині картки теста. 80×34.
//
//  Чотири стани, повністю задані станом картки:
//
//    LOCKED     test_btn_dis    «100»   темний на сірому · НЕ натискається
//    AFFORDABLE test_btn_ena    «100»   темний на жовтому · купити
//    PURCHASED  test_btn_open   «Open»  білий на фіолетовому
//    DONE       test_btn_again  «Take Again»
//
//  ЧОМУ ЦІНА НЕ ПО ЦЕНТРУ: кристал люменів намальований СПРАВА всередині
//  самої текстури ena/dis. Свій малювати не треба, але й центрувати число по
//  всій ширині не можна — наїде на кристал. Тому ціна живе в лівій зоні
//  0..CRYSTAL_X, а текстові стани центруються по всій кнопці.
//
//  ЧОМУ ДВА ЛЕЙБЛИ, А НЕ ОДИН: AMsdfLabel фіксує шрифт і розмір у
//  конструкторі (val font) — на льоту не перемкнути. Ціна Bold-темна,
//  підпис Medium-білий, тож це два різні лейбли; видимий завжди рівно один.
// ═════════════════════════════════════════════════════════════════════════════
class ACardTestBtn(override val screen: AdvancedScreen) : AButtonAnimTexture(
    screen = screen,
    style  = Style(TextureRegionDrawable(gdxGame.assetsAll.test_btn_open)),
) {

    companion object {
        const val W = 80f
        const val H = 34f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val stylePrice = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 11f, GameColor.gray_3D3D3D)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPriceLbl = AMsdfLabel("", stylePrice)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        addActor(aPriceLbl)
        aPriceLbl.setBounds(15f, 9f, 19f, 13f)
        aPriceLbl.setAlignment(Align.right)
        aPriceLbl.touchable = Touchable.disabled
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun bind(state: ACardTest.State, price: Long) {
        val region = when (state) {
            ACardTest.State.LOCKED     -> gdxGame.assetsAll.test_btn_dis
            ACardTest.State.AFFORDABLE -> gdxGame.assetsAll.test_btn_ena
            ACardTest.State.PURCHASED  -> gdxGame.assetsAll.test_btn_open
            ACardTest.State.DONE       -> gdxGame.assetsAll.test_btn_again
        }
        // default і disabled однакові: «сірий» тут — це окрема текстура
        // test_btn_dis, а не затемнення поверх, тож підміна робиться тут.
        val drawable = TextureRegionDrawable(region)
        setStyle(Style(drawable, drawable))

        val isPrice = state == ACardTest.State.LOCKED || state == ACardTest.State.AFFORDABLE

        aPriceLbl.isVisible = isPrice

        if (isPrice) {
            aPriceLbl.setText(NumberFormatter.format(price))
        } else {
            //aTextLbl.setText(if (state == ACardTest.State.DONE) "Take Again" else "Open")
        }

        // LOCKED єдиний, що не тиснеться: купити нема за що.
        if (state == ACardTest.State.LOCKED) {
            aPriceLbl.setTextColor(GameColor.gray_3D3D3D)
            disable()
        } else {
            aPriceLbl.setTextColor(GameColor.purple_0F003E)
            enable()
        }
    }
}