package com.rbuxdrop.cougame.game.actors.panel.wheel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.businesModule.economy.Econ
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedGroup
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class AWheel(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aWheelImg = Image(gdxGame.assetsAll.WHEEL)

//    var blockResult: (Result) -> Unit = {}

    var isSpinning = false
        private set

    // Сектори за годинниковою, починаючи з верхнього (-15°..15°).
    // Значення — ІНДЕКС у списку сум (WHEEL_DEF), а не сума: суми приходять з
    // Econ (ключ "wheel"), і і підпис, і нарахування беруться з одного payout().
    private val listItem = listOf(
        Item(3,  Segment(-15f,          15f)),          // 20
        Item(4,  Segment(15 * 1f,       15 + 30f * 1f)),  // 25
        Item(5,  Segment(15 + 30 * 1f,  15 + 30f * 2f)),  // 30
        Item(6,  Segment(15 + 30 * 2f,  15 + 30f * 3f)),  // 35
        Item(7,  Segment(15 + 30 * 3f,  15 + 30f * 4f)),  // 40
        Item(8,  Segment(15 + 30 * 4f,  15 + 30f * 5f)),  // 45
        Item(9,  Segment(15 + 30 * 5f,  15 + 30f * 6f)),  // 50
        Item(10, Segment(15 + 30 * 6f,  15 + 30f * 7f)),  // 100
        Item(11, Segment(15 + 30 * 7f,  15 + 30f * 8f)),  // 150
        Item(0,  Segment(15 + 30 * 8f,  15 + 30f * 9f)),  // 5
        Item(1,  Segment(15 + 30 * 9f,  15 + 30f * 10f)), // 10
        Item(2,  Segment(15 + 30 * 10f, 15 + 30f * 11f)), // 15

        Item(3,  Segment(15 + 30f * 11f, 360f)), // Дублюємо 1 для 345..360
    )

    override fun addActorsOnGroup() {
        addAndFillActor(aWheelImg)
        aWheelImg.setOrigin(Align.center)

        val aTargetImg = Image(gdxGame.assetsAll.wheel_target)
        addActor(aTargetImg)
        aTargetImg.setBounds(143f, 301f, 106f, 116f)
    }

    // Logic -------------------------------------------------------------------------

    fun spin(blockResult: (win: Int) -> Unit) {
        if (isSpinning) return

        isSpinning = true

        // Генеруємо випадковий кут обертання: від 720° до 1500°
        val randomRotation = (720..1500).random().toFloat()

        aWheelImg.addAction(
            Actions.sequence(
                Actions.rotateBy(randomRotation, (2..4).random().toFloat(), Interpolation.pow5),
                Actions.run {
                    val degree = (aWheelImg.rotation.roundToInt().absoluteValue) % 360f

                    val index = calculateWinningSegment(degree)
                    isSpinning = false
                    blockResult(payout(index))
                }
            )
        )
    }

    private fun calculateWinningSegment(degree: Float): Int {
        return listItem.firstOrNull { degree in (it.segment.startAngle..it.segment.endAngle) }?.index ?: listItem.first().index
    }

    // Єдине джерело суми сектора
    private fun payout(index: Int): Int = Econ.rewardList(REWARDS_KEY, WHEEL_DEF)[index]

    data class Item(val index: Int, val segment: Segment)

    data class Segment(val startAngle: Float, val endAngle: Float)

    companion object {
        private const val REWARDS_KEY = "wheel"
        // Суми, як намальовано на колесі (WHEEL в атласі)
        private val WHEEL_DEF = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100, 150)
    }

}