package com.rbuxrds.counter.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.setBounds
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class AWheel(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aWheelImg = Image(gdxGame.assetsAll.WHEEL)

//    var blockResult: (Result) -> Unit = {}

    var isSpinning = false
        private set

    private val listItem = listOf(
        Item(Result._20,   Segment(-15f,          15f)),
        Item(Result._25,   Segment(15 * 1f,       15 + 30f * 1f)),
        Item(Result._30,   Segment(15 + 30 * 1f,  15 + 30f * 2f)),
        Item(Result._35,   Segment(15 + 30 * 2f,  15 + 30f * 3f)),
        Item(Result._40,   Segment(15 + 30 * 3f,  15 + 30f * 4f)),
        Item(Result._45,   Segment(15 + 30 * 4f,  15 + 30f * 5f)),
        Item(Result._50,   Segment(15 + 30 * 5f,  15 + 30f * 6f)),
        Item(Result._100,  Segment(15 + 30 * 6f,  15 + 30f * 7f)),
        Item(Result._150,  Segment(15 + 30 * 7f,  15 + 30f * 8f)),
        Item(Result._5,    Segment(15 + 30 * 8f,  15 + 30f * 9f)),
        Item(Result._10,   Segment(15 + 30 * 9f,  15 + 30f * 10f)),
        Item(Result._15,   Segment(15 + 30 * 10f, 15 + 30f * 11f)),

        Item(Result._20,   Segment(15 + 30f * 11f, 360f)), // Дублюємо 1 для 345..360
    )

    override fun addActorsOnGroup() {
        addAndFillActor(aWheelImg)
        aWheelImg.setOrigin(Align.center)

        val aTargetImg = Image(gdxGame.assetsAll.wheel_target)
        addActor(aTargetImg)
        aTargetImg.setBounds(146f, 314f, 54f, 48f)
    }

    // Logic -------------------------------------------------------------------------

    fun spin(blockResult: (winItem: Result) -> Unit) {
        if (isSpinning) return

        isSpinning = true

        // Генеруємо випадковий кут обертання: від 720° до 1500°
        val randomRotation = (720..1500).random().toFloat()

        aWheelImg.addAction(
            Actions.sequence(
                Actions.rotateBy(randomRotation, (3..5).random().toFloat(), Interpolation.fastSlow),
                Actions.run {
                    val degree = (aWheelImg.rotation.roundToInt().absoluteValue) % 360f

                    calculateWinningSegment(degree).also { result ->
                        isSpinning = false
                        blockResult(result)
                    }
                }
            )
        )
    }

    private fun calculateWinningSegment(degree: Float): Result {
        return listItem.firstOrNull { degree in (it.segment.startAngle..it.segment.endAngle) }?.result ?: listItem.first().result
    }

    data class Item(val result: Result, val segment: Segment)

    data class Segment(val startAngle: Float, val endAngle: Float)

    enum class Result(val sum: Int) {
        _5  (5),
        _10 (10),
        _15 (15),
        _20 (20),
        _25 (25),
        _30 (30),
        _35 (35),
        _40 (40),
        _45 (45),
        _50 (50),
        _100(100),
        _150(150),
    }

}