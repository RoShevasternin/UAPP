package com.sakurbx.fungambx.game.actors.panel.wheel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.utils.actor.disable
import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class AWheel(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aWheelImg  = Image(gdxGame.assetsAll.WHEEL)
    private val aTargetImg = Image(gdxGame.assetsAll.TARGET)

//    var blockResult: (Result) -> Unit = {}

    var isSpinning = false
        private set

    private val listItem = listOf(
        Item(Result._50,  Segment(-20f,          20f)),
        Item(Result._25,  Segment(20 * 1f,       20 + 40f * 1f)),
        Item(Result._500, Segment(20 + 30 * 1f,  20 + 40f * 2f)),
        Item(Result._125, Segment(20 + 30 * 2f,  20 + 40f * 3f)),
        Item(Result._50,  Segment(20 + 30 * 3f,  20 + 40f * 4f)),
        Item(Result._25,  Segment(20 + 30 * 4f,  20 + 40f * 5f)),
        Item(Result._250, Segment(20 + 30 * 5f,  20 + 40f * 6f)),
        Item(Result._10,  Segment(20 + 30 * 6f,  20 + 40f * 7f)),
        Item(Result._100, Segment(20 + 30 * 7f,  20 + 40f * 8f)),
        //Item(Result._5,    Segment(15 + 30 * 8f,  15 + 30f * 9f)),
        //Item(Result._10,   Segment(15 + 30 * 9f,  15 + 30f * 10f)),
        //Item(Result._15,   Segment(15 + 30 * 10f, 15 + 30f * 11f)),

        Item(Result._50,   Segment(20 + 40f * 11f, 360f)), // Дублюємо 1 для 345..360
    )

    override fun addActorsOnGroup() {
        disable()

        addActor(aWheelImg)
        aWheelImg.setBounds(8f, 3f, 328f, 328f)
        aWheelImg.setOrigin(Align.center)

        addActor(aTargetImg)
        aTargetImg.setBounds(144f, 283f, 60f, 56f)
    }

    // Logic -------------------------------------------------------------------------

    fun spin(blockResult: (winItem: Result) -> Unit) {
        if (isSpinning) return

        isSpinning = true

        // Генеруємо випадковий кут обертання: від 1020° до 1750°
        val randomRotation = (1230..1770).random().toFloat()

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
        _500(500),
        _125(125),
        _250(250),
    }

}