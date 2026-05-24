package com.bossrbx.rbxcalculator.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    //private val aTitleImg  = Image(gdxGame.assetsLoader.title)
    private val aLogoImg   = Image(gdxGame.assetsLoader.logo)
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
        //addTitleImg()
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLogoImg() {
        addActor(aLogoImg)
        aLogoImg.setBounds(56f, 290f, 264f, 236f)

        animLogo()
    }

    private fun addTitleImg() {
//        addActor(aTitleImg)
//        aTitleImg.setBounds(133f, 723f, 110f, 20f)

        //animTitle()
    }

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(170f, 109f, 36f, 36f)

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
        aPanelNoWifi.animShowAndEnable(0.23f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animLogo() {

        aLogoImg.apply {

            setOrigin(Align.center)

            color.a = 0f
            setScale(0f)
            rotation = 0f

            addAction(

                Actions.sequence(

                    // smooth rubber appearance
                    Actions.parallel(

                        Actions.fadeIn(
                            0.2f,
                            Interpolation.fade
                        ),

                        Actions.sequence(

                            // grow
                            Actions.scaleTo(
                                1.12f,
                                1.12f,
                                0.28f,
                                Interpolation.swingOut
                            ),

                            // settle
                            Actions.scaleTo(
                                1f,
                                1f,
                                0.14f,
                                Interpolation.smooth
                            )
                        )
                    ),

                    // endless motion
                    Actions.forever(

                        Actions.sequence(

                            // left tilt
                            Actions.parallel(

                                Actions.rotateTo(
                                    -3f,
                                    2f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    1.03f,
                                    0.98f,
                                    2f,
                                    Interpolation.sine
                                )
                            ),

                            // right tilt
                            Actions.parallel(

                                Actions.rotateTo(
                                    3f,
                                    2f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    0.98f,
                                    1.03f,
                                    2f,
                                    Interpolation.sine
                                )
                            ),

                            // center
                            Actions.parallel(

                                Actions.rotateTo(
                                    0f,
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
            )
        }
    }

    private fun animLoader() {
        aLoaderImg.apply {
            setOrigin(Align.center)

            addAction(
                Actions.forever(
                    Actions.rotateBy(-360f, 0.75f) // швидкість крутим як треба
                )
            )
        }
    }

}