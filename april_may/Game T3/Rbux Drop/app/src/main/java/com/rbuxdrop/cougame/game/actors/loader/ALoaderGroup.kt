package com.rbuxdrop.cougame.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.utils.actor.animHideAndDisable
import com.rbuxdrop.cougame.game.utils.actor.animShowAndEnable
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedGroup
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aLogoImg   = Image(gdxGame.assetsLoader.logo)
    private val aTitleImg  = Image(gdxGame.assetsLoader.title)
    private val aLoaderImg = Image(gdxGame.assetsLoader.loader)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addTitleImg()
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLogoImg() {
        addActor(aLogoImg)
        aLogoImg.setBounds(4f, 272f, 384f, 384f)

        animLogo()
    }

    private fun addTitleImg() {
        addActor(aTitleImg)
        aTitleImg.setBounds(116f, 288f, 143f, 88f)

        animTitle()
    }

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(170f, 170f, 36f, 36f)

        animLoader()
    }

    private fun addPanelNoWifi() {
        aPanelNoWifi.animHideAndDisable()
        addActor(aPanelNoWifi)
        aPanelNoWifi.setBounds(30f, 249f, 316f, 316f)

        aPanelNoWifi.onRetry = { onRetry() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.25f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animLogo() {

        aLogoImg.apply {

            setOrigin(Align.center)

            color.a = 0f
            setScale(0.7f)
            rotation = -8f

            addAction(
                Actions.sequence(

                    // гарна поява
                    Actions.parallel(

                        Actions.fadeIn(
                            0.6f,
                            Interpolation.fade
                        ),

                        Actions.scaleTo(
                            1f,
                            1f,
                            0.7f,
                            Interpolation.swingOut
                        ),

                        Actions.rotateTo(
                            0f,
                            0.7f,
                            Interpolation.smooth
                        )
                    ),

                    // breathing
                    Actions.forever(

                        Actions.sequence(

                            Actions.scaleTo(
                                1.05f,
                                1.05f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                0.95f,
                                0.95f,
                                1.6f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                1f,
                                1f,
                                1.6f,
                                Interpolation.sine
                            )
                        )
                    )
                )
            )
        }
    }

    private fun animTitle() {

        aTitleImg.apply {

            setOrigin(Align.center)

            val startY = y

            color.a = 0f
            setScale(0.92f)

            addAction(
                Actions.sequence(

                    // поява
                    Actions.parallel(

                        Actions.fadeIn(
                            0.6f,
                            Interpolation.fade
                        ),

                        Actions.scaleTo(
                            1f,
                            1f,
                            0.6f,
                            Interpolation.smooth
                        )
                    ),

                    // нескінченна левітація
                    Actions.forever(

                        Actions.parallel(

                            // float
                            Actions.sequence(

                                Actions.moveTo(
                                    x,
                                    startY + 6f,
                                    1.8f,
                                    Interpolation.sine
                                ),

                                Actions.moveTo(
                                    x,
                                    startY - 4f,
                                    1.8f,
                                    Interpolation.sine
                                ),

                                Actions.moveTo(
                                    x,
                                    startY,
                                    1.8f,
                                    Interpolation.sine
                                )
                            ),

                            // breathing
                            Actions.sequence(

                                Actions.scaleTo(
                                    1.03f,
                                    1.03f,
                                    1.8f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    0.97f,
                                    0.97f,
                                    1.8f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    1f,
                                    1f,
                                    1.8f,
                                    Interpolation.sine
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    private fun animLoader() {
        aLoaderImg.apply {
            setOrigin(Align.center)

            addAction(
                Actions.forever(
                    Actions.rotateBy(-360f, 1.0f) // швидкість крутим як треба
                )
            )
        }
    }

}