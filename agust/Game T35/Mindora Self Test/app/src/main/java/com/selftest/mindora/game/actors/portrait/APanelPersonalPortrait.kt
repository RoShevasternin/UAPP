package com.selftest.mindora.game.actors.portrait

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.AMainButton
import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.content.SynthesisTitle
import com.selftest.mindora.game.controller.PortraitController
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  APanelPersonalPortrait — зібраний портрет цілком.
//
//   YOUR PERSONAL PORTRAIT        ← header із synthesis.json
//   The Layered Original          ← name
//   There is more to you…         ← tagline, жовтим курсивом
//   [ арт ]
//   body
//   [ Your Archetype    ⌄ ]  ┐
//   [ Your Personality  ⌄ ]  ├ по рядку на КОЖЕН пройдений вимір
//   …                        ┘
//   ( Share result )
//   ( Restart the test )
//   Not a medical or psychological diagnosis
//
//  ВИСОТА ПЛАВАЮЧА ДВІЧІ. Спершу — бо body і tagline з JSON різної довжини
//  (як в ACardResultSingle). А потім ще й тому, що рядки розкриваються: тому
//  розкладка винесена в relayout() і кличеться повторно з onHeightChanged
//  кожного рядка, а не робиться один раз у bind.
//
//  РЯДКІВ РІВНО СТІЛЬКИ, СКІЛЬКИ ПРОЙДЕНО. Поріг синтезу може бути 3 з 5, і
//  тоді непройдених вимірів у портреті просто немає — показувати порожній
//  рядок «результату ще нема» тут нема сенсу.
// ═════════════════════════════════════════════════════════════════════════════
class APanelPersonalPortrait(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 344f

        private const val PAD     = 17f          // (344 − 310) / 2 — як в ACardResultSingle
        private const val CONTENT = W - PAD * 2  // 310

        private const val ART   = 240f
        private const val BTN_H = 55f
        private const val GAP   = 12f
        private const val ROW_GAP = 8f
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
    private val aBgImg = Image(gdxGame.assetsAll.panel_result)

    private val aKickerLbl  = AMsdfLabel("", styleKicker)
    private val aNameLbl    = AMsdfLabel("", styleName)
    private val aTaglineLbl = AMsdfLabel("", styleTagline)

    /**
     * Ілюстрація ОДНА на всі титули — мапінга «id титула → арт» немає і не
     * планується: за макетом всі шість карток портрета мають один і той самий
     * круглий арт, різниться тільки текст.
     *
     * Текстура квадратна (681×681), тому кладеться в квадрат ART×ART — без
     * підгону пропорцій. Раніше тут стояв PANEL_YOUR_PRE_PORTRAIT (930×630,
     * прямокутний арт «до синтезу») і його розтягувало в квадрат.
     */
    private val aArtImg = Image(gdxGame.assetsAll.ICON_PORTRAIT_RESULT)

    private val aBodyLbl = AMsdfLabel("", styleBody)

    private val aShareBtn   = AButtonAnim(screen, AButtonStyles.Anim.SHARE_RESULT)
    private val aRestartBtn = AMainButton(screen, "Restart the test")
    private val aNoteLbl    = AMsdfLabel("Not a medical or psychological diagnosis", styleNote)

    private val aRows = mutableListOf<ACardPortraitDimension>()

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onShare  : Block = {}
    var onRestart: Block = {}

    /** Панель перерахувала висоту — екрану треба перерозкласти скрол. */
    var onHeightChanged: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addActor(aKickerLbl);  aKickerLbl.setAlignment(Align.center)
        addActor(aNameLbl);    aNameLbl.setAlignment(Align.center)
        addActor(aTaglineLbl); aTaglineLbl.setAlignment(Align.center); aTaglineLbl.setWrap(true)
        addActor(aArtImg)
        addActor(aBodyLbl);    aBodyLbl.setAlignment(Align.topLeft);   aBodyLbl.setWrap(true)

        addActor(aShareBtn)
        addActor(aRestartBtn)
        addActor(aNoteLbl);    aNoteLbl.setAlignment(Align.center)

        aShareBtn.setOnClickListener   { onShare() }
        aRestartBtn.setOnClickListener { onRestart() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun bind(title: SynthesisTitle, header: String, state: PortraitController.State) {
        aKickerLbl.setText(header)
        aNameLbl.setText(title.name)
        aTaglineLbl.setText(title.tagline)
        aBodyLbl.setText(title.body)

        buildRows(state)
        relayout()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun buildRows(state: PortraitController.State) {
        aRows.forEach { it.remove() }
        aRows.clear()

        state.cards.forEachIndexed { i, card ->
            if (!card.done) return@forEachIndexed

            val row = ACardPortraitDimension(screen)
            row.setSize(ACardPortraitDimension.W, ACardPortraitDimension.COLLAPSED)
            addActor(row)
            row.bind(i, card)

            // Розкриття одного рядка міняє висоту всієї панелі — і далі вгору
            // по ланцюжку до AScrollPane.
            row.onHeightChanged = { relayout() }

            // Акордеон — див. collapseOthers.
            row.onExpanded = { collapseOthers(row) }

            aRows += row
        }
    }

    /**
     * Відкритий вимір завжди один.
     *
     * П'ять розкритих рядків дають простирадло на кілька екранів, у якому вже
     * не видно ні арту, ні кнопок — а сенс портрета саме в цілісній картинці.
     *
     * collapse() на вже згорнутому рядку виходить одразу, тож окремої
     * перевірки стану тут не треба.
     */
    private fun collapseOthers(except: ACardPortraitDimension) {
        aRows.forEach { if (it !== except) it.collapse() }
    }

    // ------------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------------
    /**
     * Спершу набираємо повну висоту, потім розставляємо від верху: у scene2d
     * координати від низу, тож без відомої висоти нема від чого відкладати.
     *
     * Кличеться і з bind, і з кожного розкриття рядка.
     */
    private fun relayout() {
        aKickerLbl.setSize(CONTENT, 16f)
        aNameLbl.setSize(CONTENT, 32f)

        aTaglineLbl.setSize(CONTENT, 1f)
        aTaglineLbl.setSize(CONTENT, aTaglineLbl.prefHeight)

        aArtImg.setSize(ART, ART)

        aBodyLbl.setSize(CONTENT, 1f)
        aBodyLbl.setSize(CONTENT, aBodyLbl.prefHeight)

        aShareBtn.setSize(CONTENT, BTN_H)
        aRestartBtn.setSize(CONTENT, BTN_H)
        aNoteLbl.setSize(CONTENT, 14f)

        // Блок рядків РАЗОМ із власним відступом знизу. Рядків може не бути
        // взагалі (поріг синтезу нижчий за кількість пройдених тестів), і тоді
        // GAP після них теж не потрібен — інакше під body висіла б порожня
        // дірка на рівному місці.
        val rowsBlock =
            if (aRows.isEmpty()) 0f
            else aRows.sumOf { it.height.toDouble() }.toFloat() +
                    ROW_GAP * (aRows.size - 1) + GAP

        val total = PAD +
                aKickerLbl.height + 2f +
                aNameLbl.height + 2f +
                aTaglineLbl.height + GAP +
                ART + GAP +
                aBodyLbl.height + GAP +
                rowsBlock +
                BTN_H + 8f +          // Share result
                BTN_H + 12f +         // Restart the test
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
        place(aArtImg, GAP)
        place(aBodyLbl, GAP)
        aRows.forEachIndexed { i, row -> place(row, if (i == aRows.lastIndex) GAP else ROW_GAP) }
        place(aShareBtn, 8f)
        place(aRestartBtn, 12f)
        place(aNoteLbl, 0f)

        onHeightChanged()
    }
}
