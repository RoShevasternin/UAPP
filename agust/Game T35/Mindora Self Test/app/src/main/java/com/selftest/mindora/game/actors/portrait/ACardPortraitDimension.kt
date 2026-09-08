package com.selftest.mindora.game.actors.portrait

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.controller.PortraitController
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardPortraitDimension — один вимір у зібраному портреті, з розкриттям.
//
//   ЗГОРНУТИЙ (76):              РОЗГОРНУТИЙ:
//    [⬡] Your Archetype    ⌄      [⬡] Your Archetype    ⌃
//        The Explorer                 The Explorer
//        Born for the horizon         Born for the horizon
//                                 ──────────────────────
//                                 body
//
//  РОЗКЛАДКА ЗРОБЛЕНА ТАК САМО, ЯК В ACardResultTrait, і не випадково:
//  хедер сидить на КОНСТРЕЙНТАХ від верху, тіло — вручну від поточної
//  height. Під час анімації height міняється 60 разів на секунду, і все,
//  що прив'язане до низу, поїхало б разом з нижнім краєм.
//
//  ФОН — 9-patch panel_result, а не ARoundRect: він тягнеться під будь-яку
//  висоту без спотворення кутів, тобто переживає анімацію розкриття
//  безкоштовно.
// ═════════════════════════════════════════════════════════════════════════════
class ACardPortraitDimension(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 310f

        private const val PAD     = 12f
        private const val ICON    = 44f
        private const val CHEVRON = 26f

        /** Висота згорнутого рядка: іконка з полями і три рядки тексту. */
        const val COLLAPSED = 76f

        /** Текстова колонка: після іконки і до шеврона. */
        private const val TEXT_X = PAD + ICON + 10f
        private const val TEXT_W = W - TEXT_X - CHEVRON - PAD - 6f

        private const val KICKER_H = 14f
        private const val NAME_H   = 22f
        private const val TAG_H    = 16f

        private const val TIME_TOGGLE = 0.30f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleKicker  = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 11f, GameColor.white_70)
    private val styleName    = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 16f, Color.WHITE)
    private val styleTagline = MsdfStyle(msdf, msdf.fontMontserrat_Italic, 12f, GameColor.yellow_FFD98A)
    private val styleBody    = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg      = Image(gdxGame.assetsAll.panel_result)
    private val aIconImg    = Image()
    private val aKickerLbl  = AMsdfLabel("", styleKicker)
    private val aNameLbl    = AMsdfLabel("", styleName)
    private val aTaglineLbl = AMsdfLabel("", styleTagline)
    private val aChevronImg = Image(gdxGame.assetsAll.shevron)

    private val aDivider = ARoundRect(screen)
    private val aBodyLbl = AMsdfLabel("", styleBody)

    /** Актори, які існують лише в розгорнутому стані. */
    private val expandedActors: List<Actor> by lazy { listOf(aDivider, aBodyLbl) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var expandedHeight = COLLAPSED

    /** Порожнє тіло — розкривати нема чого, шеврон ховаємо. */
    private var hasBody = false

    var isExpanded = false
        private set

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /** Висота змінилась — батьку треба перерозкласти список. */
    var onHeightChanged: Block = {}

    /**
     * Рядок щойно РОЗКРИЛИ.
     *
     * Акордеон тримає панель, а не картка: згортати сусідів може лише той,
     * хто про них знає, а рядок про свій список нічого не знає і не повинен.
     */
    var onExpanded: Block = {}

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
    /** Тільки констрейнти від верху — див. шапку файлу. */
    private fun addHeader() {
        aIconImg.setSize(ICON, ICON)
        add(aIconImg) { startToStart(margin = PAD); topToTop(margin = (COLLAPSED - ICON) / 2f) }

        aKickerLbl.setSize(TEXT_W, KICKER_H)
        add(aKickerLbl) { startToStart(margin = TEXT_X); topToTop(margin = PAD) }
        aKickerLbl.setAlignment(Align.left)

        aNameLbl.setSize(TEXT_W, NAME_H)
        add(aNameLbl) { startToStart(margin = TEXT_X); topToTop(margin = PAD + KICKER_H) }
        aNameLbl.setAlignment(Align.left)
        aNameLbl.setEllipsis(true)

        aTaglineLbl.setSize(TEXT_W, TAG_H)
        add(aTaglineLbl) { startToStart(margin = TEXT_X); topToTop(margin = PAD + KICKER_H + NAME_H) }
        aTaglineLbl.setAlignment(Align.left)
        aTaglineLbl.setEllipsis(true)

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
    }

    // ------------------------------------------------------------------------
    // Draw
    // ------------------------------------------------------------------------
    /**
     * Кліп по власних межах: без нього тіло, що не влазить у поточну висоту,
     * малювалось би поверх сусіднього рядка всю анімацію.
     *
     * flush з обох боків обов'язковий — scissor це стан GL, а батч малює
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
     * @param index позиція теста в TestRepository.ALL — вона ж вибирає іконку
     *        ic_ena_N, тому окремої мапи не треба.
     */
    fun bind(index: Int, card: PortraitController.DimensionCard) {
        aIconImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listIcEna[index])

        aKickerLbl.setText(card.kicker)
        aNameLbl.setText(card.resultName.orEmpty())
        aTaglineLbl.setText(card.resultTagline.orEmpty())
        aBodyLbl.setText(card.resultBody.orEmpty())

        hasBody = !card.resultBody.isNullOrBlank()
        aChevronImg.isVisible = hasBody

        measureBody()

        // ⚠️ НАПРЯМУ, а не через sizeChanged: якщо картка вже має висоту
        // COLLAPSED, наступний setSize нічого не змінить, sizeChanged не
        // спрацює — і тіло лишиться з координатами від нульових розмірів.
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
        if (!hasBody) return
        isExpanded = !isExpanded
        applyExpanded(animated = true)

        // Тільки на розкриття: після згортання згортати сусідів нічого.
        if (isExpanded) onExpanded()
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
            addAction(Actions.sequence(
                Actions.sizeTo(W, target, TIME_TOGGLE, Interpolation.smooth),
                Actions.run { expandedActors.forEach { it.isVisible = false } },
            ))
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        // Тіло прив'язане до ВЕРХУ, а координати в scene2d — від низу, тож при
        // кожній зміні висоти його y треба перерахувати.
        layoutBody()
        onHeightChanged()
    }

    // ------------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------------
    private fun measureBody() {
        val contentW = W - PAD * 2

        aDivider.setSize(contentW, 1f)

        // prefHeight рахується ТІЛЬКИ при заданій ширині: wrap рахує переноси.
        aBodyLbl.setSize(contentW, 1f)
        aBodyLbl.setSize(contentW, aBodyLbl.prefHeight)

        expandedHeight =
            if (hasBody) COLLAPSED + 10f + 1f + 10f + aBodyLbl.height + PAD
            else COLLAPSED
    }

    private fun layoutBody() {
        var y = height - COLLAPSED - 10f

        y -= 1f;              aDivider.setPosition(PAD, y); y -= 10f
        y -= aBodyLbl.height; aBodyLbl.setPosition(PAD, y)
    }
}
