package com.rbuxrds.counter.game.actors.redeem

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel
import com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class ACoffer(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aGlowImg   = Image(gdxGame.assetsAll.REDEEM_GLOW)
    private val aCofferImg = Image(gdxGame.assetsAll.REDEEM_COFFER)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aGlowImg)
        aGlowImg.setOrigin(Align.center)

        aCofferImg.setSize(196f, 196f)
        addActorAligned(aCofferImg, AlignH.CENTER, AlignV.CENTER)
        aCofferImg.setOrigin(Align.center)

        startAnimations()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun startAnimations() {
        animGlow()
        animCoffer()
    }

    private fun animGlow() {
        // Повільне обертання
        aGlowImg.addAction(
            forever(
                rotateBy(360f, 12f, Interpolation.linear)
            )
        )
        // Пульсування scale
        aGlowImg.addAction(
            forever(
                sequence(
                    parallel(
                        scaleTo(1.08f, 1.08f, 2.5f, Interpolation.sine),
                        alpha(0.7f, 2.5f, Interpolation.sine)
                    ),
                    parallel(
                        scaleTo(0.92f, 0.92f, 2.5f, Interpolation.sine),
                        alpha(1.0f, 2.5f, Interpolation.sine)
                    )
                )
            )
        )
    }

    private fun animCoffer() {
        // Легке левітування вгору-вниз
        aCofferImg.addAction(
            forever(
                sequence(
                    moveBy(0f, 7f, 1.6f, Interpolation.sine),
                    moveBy(0f, -7f, 1.6f, Interpolation.sine)
                )
            )
        )
        // Легкий нахил при левітуванні
        aCofferImg.addAction(
            forever(
                sequence(
                    rotateTo(2f, 1.6f, Interpolation.sine),
                    rotateTo(-2f, 1.6f, Interpolation.sine)
                )
            )
        )
    }

}