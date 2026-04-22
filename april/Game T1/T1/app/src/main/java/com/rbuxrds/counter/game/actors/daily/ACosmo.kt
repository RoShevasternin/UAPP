package com.rbuxrds.counter.game.actors.daily

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.setOrigin
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class ACosmo(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aHex    = Image(gdxGame.assetsAll.cosmo_hex)
    private val aPerson = Image(gdxGame.assetsAll.cosmo_person)
    private val a1      = Image(gdxGame.assetsAll.cosmo_1)
    private val a2      = Image(gdxGame.assetsAll.cosmo_2)
    private val a3      = Image(gdxGame.assetsAll.cosmo_3)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aHex)
        addActors(a3, a2, aPerson, a1)

        aPerson.setBounds(24f, 10f, 164f, 204f)
        a1.setBounds(21f, 138f, 36f, 38f)
        a2.setBounds(142f, 153f, 16f, 17f)
        a3.setBounds(145f, 101f, 10f, 11f)

        // Origin по центру для scale і rotate
        aHex.setOrigin(Align.center)
        aPerson.setOrigin(Align.center)
        a1.setOrigin(Align.center)
        a2.setOrigin(Align.center)
        a3.setOrigin(Align.center)

        startAnimations()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun startAnimations() {
        animHex()
        animPerson()
        animIcon1()
        animIcon2()
        animIcon3()
    }

    private fun animHex() {
        aHex.addAction(
            forever(
                sequence(
                    parallel(
                        scaleTo(1.04f, 1.04f, 2.5f, Interpolation.sine),
                        alpha(0.85f, 2.5f, Interpolation.sine)
                    ),
                    parallel(
                        scaleTo(1.0f, 1.0f, 2.5f, Interpolation.sine),
                        alpha(1.0f, 2.5f, Interpolation.sine)
                    )
                )
            )
        )
    }

    private fun animPerson() {
        aPerson.addAction(
            forever(
                sequence(
                    moveBy(0f, 6f, 1.8f, Interpolation.sine),
                    moveBy(0f, -6f, 1.8f, Interpolation.sine)
                )
            )
        )
    }

    private fun animIcon1() {
        a1.addAction(
            forever(rotateBy(-360f, 6f, Interpolation.linear))
        )
        a1.addAction(
            forever(
                sequence(
                    delay(0.3f),  // ← зміщення від person
                    moveBy(0f, 6f, 1.8f, Interpolation.sine),
                    moveBy(0f, -6f, 1.8f, Interpolation.sine)
                )
            )
        )
    }

    private fun animIcon2() {
        a2.addAction(
            forever(rotateBy(360f, 8f, Interpolation.linear))
        )
        a2.addAction(
            forever(
                sequence(
                    delay(0.9f),  // ← інше зміщення
                    moveBy(0f, -4f, 1.8f, Interpolation.sine),
                    moveBy(0f, 4f, 1.8f, Interpolation.sine)
                )
            )
        )
    }

    private fun animIcon3() {
        a3.addAction(
            forever(rotateBy(-360f, 10f, Interpolation.linear))
        )
        a3.addAction(
            forever(
                sequence(
                    delay(1.4f),  // ← ще інше зміщення
                    moveBy(0f, 3f, 1.8f, Interpolation.sine),
                    moveBy(0f, -3f, 1.8f, Interpolation.sine)
                )
            )
        )
    }

}