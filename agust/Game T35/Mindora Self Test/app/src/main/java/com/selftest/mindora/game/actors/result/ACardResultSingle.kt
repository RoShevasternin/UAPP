package com.selftest.mindora.game.actors.result

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.content.ResultAssets
import com.selftest.mindora.game.content.TestResultText
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardResultSingle — результат для ЧОТИРЬОХ тестів: archetype, types16,
//  attachment, love_language. Одна картка на всі, бо розкладка в макеті у
//  них однакова до пікселя — відрізняються лише картинка і тексти.
//
//   Your Archetype              ← kicker
//   The Explorer                ← name
//   Born for the horizon        ← tagline, жовтим
//   [ картинка 310×200 ]
//   body
//   [ At your best: ]
//   [ You grow when: ]
//   ( Share result )            310×55  ┐
//   ( Add to the portrait )     310×55  ├ блок 310×176 з макета
//     Open next test            310×50  ┘
//   Not a medical or psychological diagnosis
//
//  ВИСОТА ПЛАВАЮЧА: body в JSON від двох до п'яти рядків, блоки теж різні.
//  Тому розкладка йде лічильником `y`, а в кінці setSize повідомляє батьку
//  реальну висоту — AScrollPane її підхопить.
//
//  ФОН — 9-patch panel_result: тягнеться під будь-яку висоту без спотворення
//  кутів. Тому фон тут Image, а не ARoundRect.
// ═════════════════════════════════════════════════════════════════════════════
class ACardResultSingle(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 344f
        private const val PAD     = 17f          // (344 − 310) / 2 — контент 310 як кнопки
        private const val CONTENT = W - PAD * 2  // 310
        private const val IMG_H   = 200f
        private const val BTN_H   = 55f
        private const val LINK_H  = 50f
        private const val GAP     = 12f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleKicker  = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_70)
    private val styleName    = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 26f, Color.WHITE)
    private val styleTagline = MsdfStyle(msdf, msdf.fontMontserrat_Italic, 14f, GameColor.yellow_FFD98A)
    private val styleBody    = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 13f, GameColor.white_80)
    private val styleNote    = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 10f, GameColor.white_70)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg      = Image(gdxGame.assetsAll.panel_result)
    private val aKickerLbl  = AMsdfLabel("", styleKicker)
    private val aNameLbl    = AMsdfLabel("", styleName)
    private val aTaglineLbl = AMsdfLabel("", styleTagline)
    private val aImg        = Image()
    private val aBodyLbl    = AMsdfLabel("", styleBody)

    // Заголовок та іконка тепер усередині 9-patch, тож блок отримує текстуру,
    // а не рядок: panel_best / panel_grow відрізняються тільки нею.
    private val aBestBlock  = APanelResultBlock(screen, gdxGame.assetsAll.panel_best)
    private val aGrowBlock  = APanelResultBlock(screen, gdxGame.assetsAll.panel_grow)

    private val aShareBtn    = AButtonAnim(screen, AButtonStyles.Anim.SHARE_RESULT)
    private val aPortraitBtn = AButtonAnim(screen, AButtonStyles.Anim.ADD_TO_PORTRAIT)
    private val aNextBtn     = AButtonAnim(screen, AButtonStyles.Anim.OPEN_NEXT_TEST)
    private val aNoteLbl     = AMsdfLabel("Not a medical or psychological diagnosis", styleNote)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onShare      : Block = {}
    var onAddPortrait: Block = {}
    var onNextTest   : Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addActor(aKickerLbl);  aKickerLbl.setAlignment(Align.center)
        addActor(aNameLbl);    aNameLbl.setAlignment(Align.center)
        addActor(aTaglineLbl); aTaglineLbl.setAlignment(Align.center)
        addActor(aImg)
        addActor(aBodyLbl);    aBodyLbl.setAlignment(Align.topLeft); aBodyLbl.setWrap(true)

        addActor(aBestBlock)
        addActor(aGrowBlock)

        addActor(aShareBtn)
        addActor(aPortraitBtn)
        addActor(aNextBtn)
        addActor(aNoteLbl); aNoteLbl.setAlignment(Align.center)

        aShareBtn.setOnClickListener    { onShare() }
        aPortraitBtn.setOnClickListener { onAddPortrait() }
        aNextBtn.setOnClickListener     { onNextTest() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @param kicker    «Your Archetype» / «Your love language» — з TestCatalog.
     * @param testIndex позиція теста в TestRepository.ALL — вибирає картинку.
     */
    fun bind(kicker: String, testIndex: Int, result: TestResultText) {
        aKickerLbl.setText(kicker)
        aNameLbl.setText(result.name)
        aTaglineLbl.setText(result.tagline)
        aImg.drawable = ResultAssets.single(testIndex)
        aBodyLbl.setText(result.body)

        layoutContent(result)
    }

    /**
     * Спершу набираємо повну висоту, потім розставляємо від верху: у scene2d
     * координати від низу, тож без відомої висоти нема від чого відкладати.
     */
    private fun layoutContent(result: TestResultText) {
        aKickerLbl.setSize(CONTENT, 16f)
        aNameLbl.setSize(CONTENT, 32f)
        aTaglineLbl.setSize(CONTENT, 20f)
        aImg.setSize(CONTENT, IMG_H)

        aBodyLbl.setSize(CONTENT, 1f)
        aBodyLbl.setSize(CONTENT, aBodyLbl.prefHeight)

        aBestBlock.setSize(CONTENT, 1f)
        aGrowBlock.setSize(CONTENT, 1f)
        val bestH = aBestBlock.setBody(result.atYourBest)
        val growH = aGrowBlock.setBody(result.youGrowWhen)

        aShareBtn.setSize(CONTENT, BTN_H)
        aPortraitBtn.setSize(CONTENT, BTN_H)
        aNextBtn.setSize(CONTENT, LINK_H)
        aNoteLbl.setSize(CONTENT, 14f)

        val total = PAD +
                aKickerLbl.height + 2f +
                aNameLbl.height + 2f +
                aTaglineLbl.height + GAP +
                IMG_H + GAP +
                aBodyLbl.height + GAP +
                bestH + 8f +
                growH + 20f +
                BTN_H + 8f +          // Share
                BTN_H + 8f +          // Add to portrait
                LINK_H + 12f +        // Open next test — разом 310×176 як у макеті
                aNoteLbl.height + PAD

        setSize(W, total)

        var y = total - PAD
        fun place(a: Actor, gapAfter: Float) {
            y -= a.height
            a.setPosition((W - a.width) / 2f, y)
            y -= gapAfter
        }

        place(aKickerLbl, 2f)
        place(aNameLbl, 2f)
        place(aTaglineLbl, GAP)
        place(aImg, GAP)
        place(aBodyLbl, GAP)
        place(aBestBlock, 8f)
        place(aGrowBlock, 20f)
        place(aShareBtn, 8f)
        place(aPortraitBtn, 8f)
        place(aNextBtn, 12f)
        place(aNoteLbl, 0f)
    }
}