package com.rbxgolden.fungamems.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.utils.actor.animHideAndDisable
import com.rbxgolden.fungamems.game.utils.actor.animShowAndEnable
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

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
        aLogoImg.setBounds(39f, 336f, 300f, 139f)

        animLogo()
    }

    private fun addTitleImg() {
//        addActor(aTitleImg)
//        aTitleImg.setBounds(133f, 723f, 110f, 20f)

        //animTitle()
    }

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(152f, 178f, 72f, 72f)

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
            setScale(0.85f)
            rotation = 0f

            addAction(
                Actions.sequence(

                    // smooth appearance
                    Actions.parallel(

                        Actions.fadeIn(
                            0.8f,
                            Interpolation.fade
                        ),

                        Actions.scaleTo(
                            1f,
                            1f,
                            0.9f,
                            Interpolation.swingOut
                        )
                    ),

                    // beautiful levitation
                    Actions.forever(

                        Actions.sequence(

                            // floating up
                            Actions.parallel(

                                Actions.moveBy(
                                    0f,
                                    18f,
                                    2.2f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    1.03f,
                                    1.03f,
                                    2.2f,
                                    Interpolation.sine
                                ),

                                Actions.rotateTo(
                                    1.5f,
                                    2.2f,
                                    Interpolation.sine
                                )
                            ),

                            // floating down
                            Actions.parallel(

                                Actions.moveBy(
                                    0f,
                                    -18f,
                                    2.2f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    0.97f,
                                    0.97f,
                                    2.2f,
                                    Interpolation.sine
                                ),

                                Actions.rotateTo(
                                    -1.5f,
                                    2.2f,
                                    Interpolation.sine
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    /*private fun animTitle() {

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
    }*/

    private fun animLoader() {
        aLoaderImg.apply {
            setOrigin(Align.center)

            addAction(
                Actions.forever(
                    Actions.rotateBy(-360f, 0.95f) // швидкість крутим як треба
                )
            )
        }
    }

}