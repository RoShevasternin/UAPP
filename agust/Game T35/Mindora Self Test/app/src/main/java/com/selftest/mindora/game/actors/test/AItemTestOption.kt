package com.selftest.mindora.game.actors.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
// Варіант відповіді (Figma: «Card / Radio option», 344×64).
//
//   [ (○)  Текст варіанта до двох рядків              ]
//
// Стани:
//   звичайний — біла картка 6% + рамка 12%, кільце радіо без крапки;
//   вибраний  — фіолетова рамка + фіолетова підкладка 12%, крапка в кільці.
//
// Клік віддається наверх через onPick — рішення «що далі» (підсвітити,
// заблокувати інші, перейти до наступного питання) приймає екран, бо тільки
// він знає стан усього списку.
// ─────────────────────────────────────────────────────────────────────────────
class AItemTestOption(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleText = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 14f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBg        = ARoundRect(screen)
    private val aRadioRing = ARoundRect(screen)
    private val aRadioDot  = ARoundRect(screen)
    private val aTextLbl   = AMsdfLabel("", styleText)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onPick: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBg()
        addRadio()
        addTextLbl()

        setSelected(selected = false, animated = false)
        setOnClickListener { onPick() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBg() {
        aBg.radius      = 16f
        aBg.strokeWidth = 1.5f
        add(aBg) { fillParent() }
    }

    private fun addRadio() {
        aRadioRing.apply {
            radius      = 10f
            fillAlpha   = 0f
            strokeWidth = 1.5f
            strokeAlpha = 1f
            color       = GameColor.white_70
        }
        aRadioRing.setSize(20f, 20f)
        add(aRadioRing) { startToStart(margin = 18f); centerY() }

        aRadioDot.apply {
            radius      = 5f
            fillAlpha   = 1f
            strokeWidth = 0f
            color       = GameColor.purple_9979FF
        }
        aRadioDot.setSize(10f, 10f)
        add(aRadioDot) { startToStart(margin = 23f); centerY() }
    }

    private fun addTextLbl() {
        aTextLbl.setSize(272f, 40f)
        add(aTextLbl) { startToStart(margin = 54f); centerY() }
        aTextLbl.setAlignment(Align.left)
        aTextLbl.setWrap(true)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(text: String) {
        aTextLbl.setText(text)
    }

    /**
     * animated=false — при біндінгу нового питання: стани мають стати на
     * місце миттєво, до появи картки, інакше видно «перемикання».
     */
    fun setSelected(selected: Boolean, animated: Boolean = true) {
        val t = if (animated) 0.12f else 0f

        if (selected) {
            aBg.color       = GameColor.purple_9979FF.cpy()
            aBg.fillAlpha   = 0.12f
            aBg.strokeAlpha = 0.9f
            aRadioRing.color = GameColor.purple_9979FF.cpy()
            aRadioDot.animShow(t)
        } else {
            aBg.color       = Color.WHITE.cpy()
            aBg.fillAlpha   = 0.06f
            aBg.strokeAlpha = 0.12f
            aRadioRing.color = GameColor.white_70.cpy()
            aRadioDot.animHide(t)
        }
    }
}