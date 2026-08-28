package com.selftest.mindora.game.actors.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
// Шкала Likert 1..N (Figma: «Badge / Question number», 344×94).
//
//   (1) (2) (3) (4) (5)
//   Strongly disagree            Strongly agree
//
// Один актор на всі scale-питання: кружечки будуються за scaleSize з
// контенту (Big Five = 5), вибір підсвічується, підписи країв — константні
// для всіх питань теста.
// ─────────────────────────────────────────────────────────────────────────────
class AScaleSelector(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        private const val CIRCLE  = 56f
        private const val CAPTION_GAP = 10f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleNumber  = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 18f, GameColor.white_80)
    private val styleCaption = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 11f, GameColor.white_70)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private class Cell(val bg: ARoundRect, val lbl: AMsdfLabel)

    private val cells = mutableListOf<Cell>()

    private val aLeftLbl  = AMsdfLabel("Strongly disagree", styleCaption)
    private val aRightLbl = AMsdfLabel("Strongly agree",    styleCaption)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var scaleSize = 5
    private var selected  : Int? = null

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

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun buildCells() {
        cells.forEach { it.bg.remove(); it.lbl.remove() }
        cells.clear()

        val gap = (width - scaleSize * CIRCLE) / (scaleSize - 1).coerceAtLeast(1)

        repeat(scaleSize) { i ->
            val value = i + 1

            val bg = ARoundRect(screen).apply {
                radius      = CIRCLE / 2f
                strokeWidth = 1.5f
            }
            bg.setSize(CIRCLE, CIRCLE)
            add(bg) { startToStart(margin = i * (CIRCLE + gap)); topToTop() }
            bg.setOnClickListener { pick(value) }

            val lbl = AMsdfLabel(value.toString(), styleNumber)
            lbl.setSize(CIRCLE, CIRCLE)
            lbl.setAlignment(Align.center)
            lbl.touchable = Touchable.disabled
            add(lbl) { startToStart(margin = i * (CIRCLE + gap)); topToTop() }

            cells += Cell(bg, lbl)
        }
        applySelection(animatedSource = false)
    }

    private fun addCaptions() {
        aLeftLbl.setSize(150f, 14f)
        add(aLeftLbl) { startToStart(); topToTop(margin = CIRCLE + CAPTION_GAP) }
        aLeftLbl.setAlignment(Align.left)

        aRightLbl.setSize(150f, 14f)
        add(aRightLbl) { endToEnd(); topToTop(margin = CIRCLE + CAPTION_GAP) }
        aRightLbl.setAlignment(Align.right)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun pick(value: Int) {
        selected = value
        applySelection(animatedSource = true)
        onPick(value)
    }

    private fun applySelection(animatedSource: Boolean) {
        cells.forEachIndexed { i, cell ->
            val isSel = (i + 1) == selected
            cell.bg.apply {
                if (isSel) {
                    color       = GameColor.purple_9979FF.cpy()
                    fillAlpha   = 0.90f
                    strokeAlpha = 1f
                } else {
                    color       = Color.WHITE.cpy()
                    fillAlpha   = 0.06f
                    strokeAlpha = 0.12f
                }
            }
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
        this.selected = selectedValue
        if (scaleSize != this.scaleSize || cells.isEmpty()) {
            this.scaleSize = scaleSize
            buildCells()
        } else {
            applySelection(animatedSource = false)
        }
    }

    /** Повна висота під розмітку екрана. */
    val fullHeight: Float get() = CIRCLE + CAPTION_GAP + 14f
}