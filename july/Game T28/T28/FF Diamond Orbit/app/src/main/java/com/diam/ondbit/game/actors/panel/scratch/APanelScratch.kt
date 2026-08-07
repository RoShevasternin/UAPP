package com.diam.ondbit.game.actors.panel.scratch

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.diam.ondbit.game.actors.layout.autoLayout.AAutoLayout
import com.diam.ondbit.game.utils.advanced.AdvancedGroup
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

class APanelScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val randomResult = Result.entries.random().sum

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aScratchCard  = List(6) { AScratchCard(
        screen,
        TextureRegionDrawable(gdxGame.assetsAll.scratch_here),
        scratchRadius = 0.075f
    ) }

    private val randomBool    = List(6) { Random.nextBoolean() }
    private val aResultImg    = List(6) { Image(if (randomBool[it]) gdxGame.assetsAll.scratch_25 else gdxGame.assetsAll.scratch_zero) }

    private val aTable1 = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 16f
    )
    private val aTable2 = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 16f
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (sum: Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aTable1)
        addAndFillActor(aTable2)

        addResult()
        addScratchCard()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addResult() {
        aResultImg.forEachIndexed { index, card ->
            card.setSize(168f, 168f)
            aTable1.add(card)
        }
    }

    private fun addScratchCard() {
        aScratchCard.forEachIndexed { index, card ->
            card.setSize(168f, 168f)
            aTable2.add(card)

            val oneTime = AtomicBoolean(true)
            card.onScratched = { percent ->
                if (percent > 85) {
                    if (oneTime.getAndSet(false)) {
                        val result = if (randomBool[index]) Result._50 else Result._0
                        onResult(result.sum)
                    }
                }
            }
        }

    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    enum class Result(val sum: Long) {
        _0  (0),
        _50  (25),
    }

}