package com.selftest.mindora.game.actors.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.checkbox.base.ACheckBox
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxGroup
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
// Шкала Likert 1..N (Figma: «Badge / Question number», 344×94).
//
//   (1) (2) (3) (4) (5)
//   It's not like me at all      That's totally me
//
// RADIO, як і варіанти вибору: кружечки взаємовиключні, тож усі сидять в
// одному ACheckBoxGroup. Без групи ACheckBoxBase працює в режимі toggle —
// можна було б лишити підсвіченими кілька значень одразу.
//
// ЧОМУ ЦИФРА — ОКРЕМИЙ ЛЕЙБЛ, А НЕ ЧАСТИНА ТЕКСТУРИ: текстур дві на всю
// шкалу (def/check), а цифр стільки, скільки поділок. Лейбл поверх
// чекбокса, touchable вимкнений — інакше він з'їдав би тапи, бо в scene2d
// будь-який актор ловить hit, а слухач висить на чекбоксі-сусіді.
// ─────────────────────────────────────────────────────────────────────────────
class AScaleSelector(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        private const val CIRCLE      = 56f
        private const val CAPTION_GAP = 10f
        private const val CAPTION_H   = 24f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleNumber  = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, Color.WHITE)
    private val styleCaption = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 10f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private class Cell(val box: ACheckBox, val lbl: AMsdfLabel)

    private val cells = mutableListOf<Cell>()
    private val group = ACheckBoxGroup()

    private val aLeftLbl  = AMsdfLabel("It's not like me at all", styleCaption)
    private val aRightLbl = AMsdfLabel("That's totally me",       styleCaption)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var scaleSize = 5
    private var selected  : Int? = null

    // Ширина, під яку рахувались відступи кружечків. Порівнюємо в sizeChanged:
    // margin обчислюється ОДИН раз при побудові, тож після зміни ширини
    // поділки лишились би розставленими під стару.
    private var builtWidth = 0f

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /** value 1..scaleSize */
    var onPick: (value: Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        buildCells()
        addCaptions()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        if (width > 0f && width != builtWidth) buildCells()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun buildCells() {
        cells.forEach { it.box.remove(); it.lbl.remove() }
        cells.clear()
        group.clear()

        builtWidth = width

        val gap = (width - scaleSize * CIRCLE) / (scaleSize - 1).coerceAtLeast(1)

        repeat(scaleSize) { i ->
            val value  = i + 1
            val margin = i * (CIRCLE + gap)

            val box = ACheckBox(screen, ACheckBoxStyles.TEST_SCALE)
            box.setSize(CIRCLE, CIRCLE)
            box.checkBoxGroup = group
            add(box) { startToStart(margin = margin); topToTop() }
            box.setOnCheckListener { if (it) pick(value) }

            val lbl = AMsdfLabel(value.toString(), styleNumber)
            lbl.setSize(CIRCLE, CIRCLE)
            lbl.setAlignment(Align.center)
            lbl.touchable = Touchable.disabled
            add(lbl) { startToStart(margin = margin); topToTop() }

            cells += Cell(box, lbl)
        }

        restoreSelection()
    }

    private fun addCaptions() {
        aLeftLbl.setSize(61f, CAPTION_H)
        add(aLeftLbl) { startToStart(); topToTop(margin = CIRCLE + CAPTION_GAP) }
        aLeftLbl.setAlignment(Align.top, Align.center)
        aLeftLbl.wrap = true

        aRightLbl.setSize(54f, CAPTION_H)
        add(aRightLbl) { endToEnd(); topToTop(margin = CIRCLE + CAPTION_GAP) }
        aRightLbl.setAlignment(Align.top, Align.center)
        aRightLbl.wrap = true
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun pick(value: Int) {
        selected = value
        applyLabelColors()
        onPick(value)
    }

    /**
     * Намалювати збережений вибір, НЕ смикаючи onPick.
     *
     * Тихо — принципово: group.select з invokeBlock=true екран сприйняв би як
     * новий тап користувача і гортнув би питання вперед само по собі.
     */
    private fun restoreSelection() {
        group.clear()
        selected
            ?.takeIf { it in 1..scaleSize }
            ?.let { group.select(cells[it - 1].box, invokeBlock = false) }
        applyLabelColors()
    }

    /**
     * Колір цифр. Текстури def/check міняє сам чекбокс, а от лейбл поверх —
     * наш: група знімає попередній вибір через uncheck(invokeBlock = false),
     * тобто БЕЗ колбека, і цифра лишилась би білою на невибраному кружечку.
     */
    private fun applyLabelColors() {
        cells.forEachIndexed { i, cell ->
            val isSel = (i + 1) == selected
            cell.lbl.setTextColor(if (isSel) Color.WHITE else GameColor.white_80)
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * Викликати при біндінгу питання. selectedValue — попередня відповідь,
     * якщо юзер повернувся кнопкою назад; null — питання ще без відповіді.
     */
    fun bind(scaleSize: Int, selectedValue: Int?) {
        selected = selectedValue

        if (scaleSize != this.scaleSize || cells.isEmpty()) {
            this.scaleSize = scaleSize
            buildCells()          // сам покличе restoreSelection()
        } else {
            restoreSelection()
        }
    }

    /** Повна висота під розмітку екрана. */
    val fullHeight: Float get() = CIRCLE + CAPTION_GAP + CAPTION_H
}