package com.sakurbx.fungambx.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.utils.actor.setBounds
import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class ALeaf(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listLeaf = List(12) { Image(gdxGame.assetsLoader.listLeaf[it]) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val listBounds = listOf(
        Rectangle(169f, 90f, 48f, 41f),
        Rectangle(91f, 203f, 35f, 39f),
        Rectangle(61f, 0f, 28f, 27f),
        Rectangle(0f, 28f, 31f, 23f),
        Rectangle(73f, 28f, 12f, 14f),
        Rectangle(132f, 8f, 21f, 17f),
        Rectangle(237f, 40f, 20f, 17f),
        Rectangle(118f, 207f, 21f, 20f),
        Rectangle(242f, 31f, 10f, 8f),
        Rectangle(124f, 193f, 7f, 9f),
        Rectangle(208f, 28f, 23f, 17f),
        Rectangle(258f, 143f, 21f, 12f),
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        listLeaf.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(listBounds[index])
            img.setOrigin(Align.center)
            animateLeaf(img, index)
        }
    }

    // ------------------------------------------------------------------------
    // Animation — левітація навколо лого
    // ------------------------------------------------------------------------
    private fun animateLeaf(img: Image, index: Int) {
        // стартовий нахил — кожна пелюстка під своїм кутом
        img.rotation = MathUtils.random(-40f, 40f)

        val driftX   = MathUtils.random(6f, 16f)  * (if (index % 2 == 0) 1f else -1f)
        val driftY   = MathUtils.random(10f, 22f)
        val timeUp   = MathUtils.random(2.2f, 3.6f)
        val timeSide = MathUtils.random(1.8f, 3.0f)
        val startDelay = MathUtils.random(0f, 1.5f)

        // амплітуда й напрям обертання — на вітру помітніше
        val swayAngle = MathUtils.random(25f, 50f) * (if (index % 3 == 0) 1f else -1f)
        val timeSway  = MathUtils.random(1.4f, 2.4f)

        // вертикальний дрейф
        img.addAction(Actions.sequence(
            Actions.delay(startDelay),
            Actions.forever(Actions.sequence(
                Actions.moveBy(0f, driftY, timeUp, Interpolation.sine),
                Actions.moveBy(0f, -driftY, timeUp, Interpolation.sine),
            ))
        ))

        // горизонтальне похитування
        img.addAction(Actions.sequence(
            Actions.delay(startDelay),
            Actions.forever(Actions.sequence(
                Actions.moveBy(driftX, 0f, timeSide, Interpolation.sine),
                Actions.moveBy(-driftX, 0f, timeSide, Interpolation.sine),
            ))
        ))

        // ── обертання "на вітру" — поривчасте, нерівномірне ──
        img.addAction(Actions.sequence(
            Actions.delay(startDelay),
            Actions.forever(Actions.sequence(
                // різкий порив в один бік
                Actions.rotateBy(swayAngle, timeSway * 0.4f, Interpolation.pow2Out),
                // повільне повернення назад
                Actions.rotateBy(-swayAngle * 1.3f, timeSway, Interpolation.sine),
                // легкий довиток
                Actions.rotateBy(swayAngle * 0.3f, timeSway * 0.6f, Interpolation.sine),
            ))
        ))
    }

}