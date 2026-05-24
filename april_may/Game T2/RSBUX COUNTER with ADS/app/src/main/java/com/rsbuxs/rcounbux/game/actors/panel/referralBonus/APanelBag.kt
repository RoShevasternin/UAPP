package com.rsbuxs.rcounbux.game.actors.panel.referralBonus

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class APanelBag(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aGlowImg = Image(gdxGame.assetsAll.glow)
    private val aBagImg  = Image(gdxGame.assetsAll.bag)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aGlowImg) { fillParent() }

        aBagImg.setSize(120f, 130f)
        add(aBagImg) { center() }

        animGlow()
        animBag()
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun animGlow() {
        aGlowImg.apply {

            setOrigin(Align.center)

            addAction(
                Actions.forever(
                    Actions.parallel(

                        // плавне обертання
                        Actions.rotateBy(
                            360f,
                            12f
                        ),

                        // breathing scale
                        Actions.sequence(
                            Actions.scaleTo(
                                1.05f,
                                1.05f,
                                2f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                0.95f,
                                0.95f,
                                2f,
                                Interpolation.sine
                            )
                        ),
                        Actions.sequence(
                            Actions.alpha(0.7f, 2f),
                            Actions.alpha(1f, 2f)
                        )

                    )
                )
            )
        }
    }

    private fun animBag() {

        aBagImg.apply {

            setOrigin(Align.center)

            val startX = x
            val startY = y

            addAction(
                Actions.forever(
                    Actions.parallel(

                        // левітація
                        Actions.sequence(

                            Actions.moveTo(
                                startX,
                                startY + 8f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.moveTo(
                                startX,
                                startY - 4f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.moveTo(
                                startX,
                                startY,
                                1.6f,
                                Interpolation.sine
                            )
                        ),

                        // breathing scale
                        Actions.sequence(

                            Actions.scaleTo(
                                1.04f,
                                1.04f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                0.96f,
                                0.96f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                1f,
                                1f,
                                1.6f,
                                Interpolation.sine
                            )
                        ),

                        // міні rotation
                        Actions.sequence(

                            Actions.rotateTo(
                                3f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.rotateTo(
                                -3f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.rotateTo(
                                0f,
                                1.6f,
                                Interpolation.sine
                            )
                        )
                    )
                )
            )
        }
    }

}