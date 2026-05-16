package com.rbuxdrop.cougame.game.actors.panel.flip

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.scratch.AScratch
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animHideAndDisable
import com.rbuxdrop.cougame.game.utils.actor.animShowAndEnable
import com.rbuxdrop.cougame.game.utils.actor.disable
import com.rbuxdrop.cougame.game.utils.actor.setOnClickListener
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class APanelFlip(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelFlipResult = APanelFlipResult(screen)
    private val aFlipCardImg     = Image(gdxGame.assetsAll.FLIP_CARD)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var randomResult = Result.entries.random()
        set(value) {
            aPanelFlipResult.setReward(value.sum.toLong())
            field = value
        }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFlip: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        children.forEach { it.disable() }
        randomResult = Result.entries.random()

        aPanelFlipResult.scaleX    = 0f
        aPanelFlipResult.isVisible = false

        aPanelFlipResult.setBounds(-64f, -64f, 412f, 526f)
        addActor(aPanelFlipResult)
        add(aFlipCardImg) { fillParent() }

        setOnClickListener {
            disable()
            flipCard()
            onFlip(randomResult.sum.toLong())
        }
    }

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

    private fun flipCard() {

        aFlipCardImg.apply {

            setOrigin(Align.center)
        }

        aPanelFlipResult.apply {

            setOrigin(Align.center)

            scaleX = 0f
            color.a = 1f
        }

        aFlipCardImg.addAction(

            Actions.sequence(

                // flip hide
                Actions.scaleTo(
                    0f,
                    1f,
                    0.2f,
                    Interpolation.fastSlow
                ),

                Actions.run {

                    aFlipCardImg.disable()
                    aFlipCardImg.isVisible = false

                    aPanelFlipResult.isVisible = true
                },

                // flip show result
                Actions.run {

                    aPanelFlipResult.addAction(

                        Actions.parallel(

                            Actions.scaleTo(
                                1f,
                                1f,
                                0.25f,
                                Interpolation.swingOut
                            ),

                            Actions.sequence(

                                Actions.scaleTo(
                                    1.05f,
                                    1.05f,
                                    0.12f
                                ),

                                Actions.scaleTo(
                                    1f,
                                    1f,
                                    0.12f
                                )
                            )
                        )
                    )
                }
            )
        )
    }

}