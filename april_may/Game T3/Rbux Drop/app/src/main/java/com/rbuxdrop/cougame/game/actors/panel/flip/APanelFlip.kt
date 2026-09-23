package com.rbuxdrop.cougame.game.actors.panel.flip

import com.rbuxdrop.cougame.businesModule.economy.Econ
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
    // Сума з Econ (ключ "flip_card"); підпис на звороті картки і нарахування —
    // з одного значення.
    private var payout = randomPayout()
        set(value) {
            aPanelFlipResult.setReward(value.toLong())
            field = value
        }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFlip: (win: Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        children.forEach { it.disable() }
        payout = randomPayout()

        aPanelFlipResult.scaleX    = 0f
        aPanelFlipResult.isVisible = false

        aPanelFlipResult.setBounds(-64f, -64f, 412f, 526f)
        addActor(aPanelFlipResult)
        add(aFlipCardImg) { fillParent() }

        setOnClickListener {
            disable()
            flipCard()
            onFlip(payout)
        }
    }

    private fun randomPayout(): Int = Econ.rewardList(REWARDS_KEY, FLIP_DEF).random()

    companion object {
        private const val REWARDS_KEY = "flip_card"
        private val FLIP_DEF = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100, 150)
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