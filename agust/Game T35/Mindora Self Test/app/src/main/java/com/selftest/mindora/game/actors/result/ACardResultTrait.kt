package com.selftest.mindora.game.actors.result

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.content.ResultAssets
import com.selftest.mindora.game.content.TestResultText
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardResultTrait — одна риса Big Five, картка з розкриттям.
//
//   ЗГОРНУТА (104):             РОЗГОРНУТА:
//    [img] (High)   ⌄            [img] (High)   ⌃
//          Openness                    Openness
//          A mind that…                A mind that…
//                                  ─────────────
//                                  body
//                                  [ At your best: ]
//                                  [ You grow when: ]
//
//  ─── ДВА ПОВЕРХИ РОЗКЛАДКИ, і це принципово ───────────────────────────────
//
//  ХЕДЕР (картинка, бейдж, назва, підзаголовок, шеврон) сидить на
//  КОНСТРЕЙНТАХ з прив'язкою до ВЕРХУ. Він не залежить від height взагалі,
//  тому під час анімації не рухається ні на піксель.
//
//  Раніше хедер позиціонувався вручну від `top = height`. Формально це те
//  саме, але фактично — ні: ручні координати діють лише доти, доки хтось
//  їх перерахує, а анімація міняє height 60 разів на секунду. Звідси і
//  «шеврон улітає», і тремтіння всього вмісту.
//
//  ТІЛО (роздільник, body, два блоки) — вручну, бо його зсуви залежать від
//  ВИМІРЯНИХ висот тексту, які відомі тільки після setText. Але воно нижче
//  хедера і все одно обрізається кліпом, тож смикання там не видно.
//
//  ─── ЧОМУ ТЕКСТ «ПЛИВ» ДО ПЕРШОГО ТАПУ ────────────────────────────────────
//
//  bind() викликався ПІСЛЯ того, як картка вже мала висоту COLLAPSED. Тобто
//  setSize(W, COLLAPSED) не міняв нічого, sizeChanged не спрацьовував, і
//  relayout не викликався — тіло лишалось з координатами від нульових
//  розмірів. Перший тап міняв висоту, relayout нарешті спрацьовував, і все
//  ставало на місце. Тепер bind() кличе relayout НАПРЯМУ.
// ═════════════════════════════════════════════════════════════════════════════
class ACardResultTrait(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 344f

        private const val PAD     = 12f
        private const val IMG     = 80f
        private const val CHEVRON = 32f
        private const val BADGE_W = 60f
        private const val BADGE_H = 24f
        private const val NAME_H  = 26f
        private const val TAG_H   = 18f

        private const val COLLAPSED = PAD + IMG + PAD

        /** Текстова колонка: після картинки і до шеврона. */
        private const val TEXT_X = PAD + IMG + 12f
        private const val TEXT_W = W - TEXT_X - CHEVRON - PAD - 8f

        // Тривалість і крива розкриття. smooth замість pow3Out: різкий старт
        // читався як стрибок, а не як рух.
        private const val TIME_TOGGLE = 0.30f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleBadge   = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 11f, Color.WHITE)
    private val styleName    = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 20f, Color.WHITE)
    private val styleTagline = MsdfStyle(msdf, msdf.fontMontserrat_Italic, 13f, GameColor.yellow_FFD98A)
    private val styleBody    = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 13f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg      = Image(gdxGame.assetsAll.panel_result)
    private val aImg        = Image()
    private val aBadgeBg    = ARoundRect(screen)
    private val aBadgeLbl   = AMsdfLabel("", styleBadge)
    private val aNameLbl    = AMsdfLabel("", styleName)
    private val aTaglineLbl = AMsdfLabel("", styleTagline)
    private val aChevronImg = Image(gdxGame.assetsAll.shevron)

    private val aDivider    = ARoundRect(screen)
    private val aBodyLbl    = AMsdfLabel("", styleBody)
    private val aBestBlock  = APanelResultBlock(screen, gdxGame.assetsAll.panel_best)
    private val aGrowBlock  = APanelResultBlock(screen, gdxGame.assetsAll.panel_grow)

    /** Актори, які існують лише в розгорнутому стані. */
    private val expandedActors: List<Actor> by lazy {
        listOf(aDivider, aBodyLbl, aBestBlock, aGrowBlock)
    }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var expandedHeight = COLLAPSED

    var isExpanded = false
        private set

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /** Висота змінилась — батьку треба перерозкласти список. */
    var onHeightChanged: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addHeader()
        addBody()

        setOnClickListener(stopEvent = false) { toggle() }

        applyExpanded(animated = false)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    /**
     * Хедер — тільки констрейнти від верху. Жодного setPosition: усе, що
     * прив'язане до `top`, під час анімації висоти поїхало б.
     */
    private fun addHeader() {
        aImg.setSize(IMG, IMG)
        add(aImg) { startToStart(margin = PAD); topToTop(margin = PAD) }

        aBadgeBg.radius      = BADGE_H / 2f
        aBadgeBg.color       = GameColor.purple_9979FF.cpy()
        aBadgeBg.fillAlpha   = 1f
        aBadgeBg.strokeWidth = 0f
        aBadgeBg.setSize(BADGE_W, BADGE_H)
        add(aBadgeBg) { startToStart(margin = TEXT_X); topToTop(margin = PAD) }

        aBadgeLbl.setSize(BADGE_W, BADGE_H)
        add(aBadgeLbl) { startToStart(margin = TEXT_X); topToTop(margin = PAD) }
        aBadgeLbl.setAlignment(Align.center)

        aNameLbl.setSize(TEXT_W, NAME_H)
        add(aNameLbl) { startToStart(margin = TEXT_X); topToTop(margin = PAD + BADGE_H + 4f) }
        aNameLbl.setAlignment(Align.left)

        aTaglineLbl.setSize(TEXT_W, TAG_H)
        add(aTaglineLbl) { startToStart(margin = TEXT_X); topToTop(margin = PAD + BADGE_H + 4f + NAME_H) }
        aTaglineLbl.setAlignment(Align.left)

        // По центру зони хедера: у згорнутої картки хедер — це вся картка,
        // тож шеврон опиняється рівно посередині, як у макеті.
        aChevronImg.setSize(CHEVRON, CHEVRON)
        add(aChevronImg) { endToEnd(margin = PAD); topToTop(margin = (COLLAPSED - CHEVRON) / 2f) }
        // Origin по центру — інакше scaleY = -1 відзеркалить відносно нижнього
        // краю, і стрілка поїде вниз за межі свого місця.
        aChevronImg.setOrigin(Align.center)
    }

    private fun addBody() {
        aDivider.radius      = 0.5f
        aDivider.color       = Color.WHITE.cpy()
        aDivider.fillAlpha   = 0.15f
        aDivider.strokeWidth = 0f
        addActor(aDivider)

        addActor(aBodyLbl)
        aBodyLbl.setAlignment(Align.topLeft)
        aBodyLbl.setWrap(true)

        addActor(aBestBlock)
        addActor(aGrowBlock)
    }

    // ------------------------------------------------------------------------
    // Draw
    // ------------------------------------------------------------------------
    /**
     * Кліп по власних межах. Без нього тіло, що не влазить у поточну висоту,
     * малювалось би поверх сусідньої картки всю анімацію — саме кліп робить
     * розкриття схожим на висувну шухляду.
     *
     * flush з обох боків обов'язковий: scissor — це стан GL, а батч малює
     * відкладено, тож без flush обріжеться не той кадр.
     */
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch ?: return
        batch.flush()
        if (clipBegin()) {
            super.draw(batch, parentAlpha)
            batch.flush()
            clipEnd()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @param traitIndex позиція в outcome.resultIds (= порядок axes у JSON) —
     *        вибирає картинку з listResultBig5.
     */
    fun bind(traitIndex: Int, result: TestResultText) {
        // name у JSON виглядає як «Openness — High»: ліва частина в заголовок,
        // права — у бейдж. Окремих полів у контенті немає, бо для решти тестів
        // другої частини не існує.
        val parts = result.name.split("—", "-").map { it.trim() }
        aNameLbl.setText(parts.getOrElse(0) { result.name })
        aBadgeLbl.setText(parts.getOrElse(1) { "" })

        aTaglineLbl.setText(result.tagline)
        aBodyLbl.setText(result.body)
        aImg.drawable = ResultAssets.trait(traitIndex)

        measureBody(result)

        // ⚠️ НАПРЯМУ, а не через sizeChanged: якщо картка вже має висоту
        // COLLAPSED, наступний setSize нічого не змінить, sizeChanged не
        // спрацює — і тіло лишиться з координатами від нульових розмірів.
        // Саме через це текст «плив» до першого тапу.
        layoutBody()
        applyExpanded(animated = false)
    }

    fun collapse() {
        if (!isExpanded) return
        isExpanded = false
        applyExpanded(animated = true)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun toggle() {
        isExpanded = !isExpanded
        applyExpanded(animated = true)
    }

    private fun applyExpanded(animated: Boolean) {
        // Стрілка перевертається одразу — реагує на НАМІР, а не на завершення
        // анімації, інакше тап відчувається як «не спрацював».
        aChevronImg.scaleY = if (isExpanded) -1f else 1f

        val target = if (isExpanded) expandedHeight else COLLAPSED

        clearActions()

        if (!animated) {
            expandedActors.forEach { it.isVisible = isExpanded }
            setSize(W, target)
            return
        }

        if (isExpanded) {
            // Показуємо ДО анімації: тіло виїжджає з-під хедера завдяки кліпу,
            // а не з'являється ривком у кінці.
            expandedActors.forEach { it.isVisible = true }
            addAction(Actions.sizeTo(W, target, TIME_TOGGLE, Interpolation.smooth))
        } else {
            // Ховаємо ПІСЛЯ: поки картка стискається, тіло ще видно, і кліп
            // з'їдає його поступово — це і читається як згортання.
            addAction(Actions.sequence(
                Actions.sizeTo(W, target, TIME_TOGGLE, Interpolation.smooth),
                Actions.run { expandedActors.forEach { it.isVisible = false } },
            ))
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        // Тіло прив'язане до ВЕРХУ, а координати в scene2d — від низу, тож
        // при кожній зміні висоти його y треба перерахувати. Інакше воно
        // їхало б донизу разом з нижнім краєм замість того, щоб стояти
        // нерухомо і поступово відкриватись.
        layoutBody()
        onHeightChanged()
    }

    // ------------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------------
    /** Виміряти тіло і порахувати розгорнуту висоту. Зветься раз, з bind. */
    private fun measureBody(result: TestResultText) {
        val contentW = W - PAD * 2

        aDivider.setSize(contentW, 1f)

        // prefHeight рахується ТІЛЬКИ при заданій ширині: wrap рахує переноси.
        aBodyLbl.setSize(contentW, 1f)
        aBodyLbl.setSize(contentW, aBodyLbl.prefHeight)

        aBestBlock.setSize(contentW, 1f)
        aGrowBlock.setSize(contentW, 1f)
        val bestH = aBestBlock.setBody(result.atYourBest)
        val growH = aGrowBlock.setBody(result.youGrowWhen)

        expandedHeight = COLLAPSED + 12f + 1f + 12f +
                aBodyLbl.height + 12f + bestH + 8f + growH + PAD
    }

    /**
     * Позиції тіла — від ВЕРХУ картки, тобто від поточної height. Так тіло
     * стоїть нерухомо, а росте лише межа обрізання: рух читається як
     * розкриття, а не як контент, що кудись повзе.
     */
    private fun layoutBody() {
        var y = height - COLLAPSED - 12f

        y -= 1f;                aDivider.setPosition(PAD, y);   y -= 12f
        y -= aBodyLbl.height;   aBodyLbl.setPosition(PAD, y);   y -= 12f
        y -= aBestBlock.height; aBestBlock.setPosition(PAD, y); y -= 8f
        y -= aGrowBlock.height; aGrowBlock.setPosition(PAD, y)
    }
}