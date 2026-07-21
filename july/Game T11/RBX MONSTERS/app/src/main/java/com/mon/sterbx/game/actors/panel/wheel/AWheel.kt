package com.mon.sterbx.game.actors.panel.wheel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.utils.actor.disable
import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class AWheel(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aWheelImg  = Image(gdxGame.assetsAll.WHEEL)
    private val aTargetImg = Image(gdxGame.assetsAll.TARGET)

//    var blockResult: (Result) -> Unit = {}

    var isSpinning = false
        private set

    private val listItem = listOf(
        Item(Result._100 , Segment(0f,            30f)),
        Item(Result._35  , Segment(30 * 1f,       30 + 30f * 1f)),
        Item(Result._150 , Segment(30 + 30 * 1f,  30 + 30f * 2f)),
        Item(Result._50  , Segment(30 + 30 * 2f,  30 + 30f * 3f)),
        Item(Result._300 , Segment(30 + 30 * 3f,  30 + 30f * 4f)),
        Item(Result._75  , Segment(30 + 30 * 4f,  30 + 30f * 5f)),
        Item(Result._500 , Segment(30 + 30 * 5f,  30 + 30f * 6f)),
        Item(Result._55  , Segment(30 + 30 * 6f,  30 + 30f * 7f)),
        Item(Result._500 , Segment(30 + 30 * 7f,  30 + 30f * 8f)),
        Item(Result._125 , Segment(30 + 30 * 8f,  30 + 30f * 9f)),
        Item(Result._25  , Segment(30 + 30 * 9f,  30 + 30f * 10f)),
        Item(Result._50  , Segment(30 + 30 * 10f, 30 + 30f * 11f)),

        //Item(Result._50,   Segment(20 + 40f * 11f, 360f)), // Дублюємо 1 для 345..360
    )

    override fun addActorsOnGroup() {
        disable()

        addAndFillActor(aWheelImg)
        aWheelImg.setOrigin(Align.center)

        addActor(aTargetImg)
        aTargetImg.setBounds(83f, 307f, 178f, 178f)

        val startX = aTargetImg.x
        val startY = aTargetImg.y

        aTargetImg.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.moveBy(0f, -14f, 1.2f, Interpolation.sine),   // вниз
                    Actions.moveTo(startX, startY, 1.2f, Interpolation.sine),   // назад вгору
                )
            )
        )
    }

    // Logic -------------------------------------------------------------------------

    fun spin(blockResult: (winItem: Result) -> Unit) {
        if (isSpinning) return

        isSpinning = true

        // Генеруємо випадковий кут обертання: від 1020° до 1750°
        val randomRotation = (1005..2203).random().toFloat()

        aWheelImg.addAction(
            Actions.sequence(
                Actions.rotateBy(randomRotation, (2..3).random().toFloat(), Interpolation.fastSlow),
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

    enum class Result(val sum: Long) {
        _100(100),
        _35 (35),
        _50 (50),
        _150(150),
        _300(300),
        _75(75),
        _55(55),
        _500(500),
        _125(125),
        _25(25),
    }

}