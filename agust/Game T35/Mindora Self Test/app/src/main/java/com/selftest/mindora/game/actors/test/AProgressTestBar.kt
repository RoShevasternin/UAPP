package com.selftest.mindora.game.actors.test

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.graphics.Color
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedGroup
import com.selftest.mindora.game.utils.advanced.AdvancedScreen

// ─────────────────────────────────────────────────────────────────────────────
// Прогрес проходження теста: трек 344×5 + фіолетова заливка.
//
// Дизайн (Figma, Progressbar_Test): контейнер-заливка росте зліва направо,
// 29/344 на першому питанні → 344/344 на останньому. Радіус = половина
// висоти — повністю круглі торці.
//
// Чому два ARoundRect, а не маска: заливка сама заокруглена, тож на малих
// частках вона виглядає як «пігулка», що росте — рівно як у макеті.
// ─────────────────────────────────────────────────────────────────────────────
class AProgressTestBar(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aTrack = ARoundRect(screen)
    private val aFill  = ARoundRect(screen)

    // Мінімальна ширина заливки: коротше за діаметр торців шейдер малює
    // артефакт, та й «нульовий» прогрес у макеті все одно видимий (29px).
    private val minFill get() = height

    override fun addActorsOnGroup() {
        aTrack.apply {
            radius      = this@AProgressTestBar.height / 2f
            color       = Color.WHITE
            fillAlpha   = 0.10f
            strokeWidth = 0f
        }
        addAndFillActor(aTrack)

        aFill.apply {
            radius      = this@AProgressTestBar.height / 2f
            color       = GameColor.purple_9979FF
            fillAlpha   = 1f
            strokeWidth = 0f
            setSize(minFill, this@AProgressTestBar.height)
        }
        addActor(aFill)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** fraction 0..1. Анімовано, щоб перехід між питаннями відчувався. */
    fun setProgress(fraction: Float, animated: Boolean = true) {
        val target = (width * fraction.coerceIn(0f, 1f)).coerceAtLeast(minFill)
        aFill.clearActions()
        if (animated) aFill.addAction(Actions.sizeTo(target, height, 0.25f))
        else aFill.setSize(target, height)
    }
}