package com.coinsclub.funrbx.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.layout.AlignH
import com.coinsclub.funrbx.game.actors.layout.AlignV
import com.coinsclub.funrbx.game.utils.actor.addActorAligned
import com.coinsclub.funrbx.game.utils.actor.animHideAndDisable
import com.coinsclub.funrbx.game.utils.actor.animShowAndEnable
import com.coinsclub.funrbx.game.utils.actor.setSize
import com.coinsclub.funrbx.game.utils.advanced.AdvancedGroup
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
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
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLogoImg() {
        aLogoImg.setSize(179f, 112f)
        addActorAligned(aLogoImg, AlignH.CENTER, AlignV.CENTER)

        animLogo()
    }

    private fun addLoaderImg() {
        aLoaderImg.setSize(54f, 54f)
        addActorAligned(aLoaderImg, AlignH.CENTER, AlignV.BOTTOM)
        aLoaderImg.y += 188f

        animLoader()
    }

    private fun addPanelNoWifi() {
        aPanelNoWifi.animHideAndDisable()

        aPanelNoWifi.setSize(316f, 316f)
        addActorAligned(aPanelNoWifi, AlignH.CENTER, AlignV.CENTER)

        aPanelNoWifi.onRetry = { onRetry() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.2f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animLogo() {

        aLogoImg.apply {

            setOrigin(Align.center)

            val finalY = y

            color.a = 0f
            rotation = -12f
            setScale(0.6f)

            y += 60f

            addAction(
                Actions.sequence(

                    // appearance
                    Actions.parallel(

                        Actions.fadeIn(
                            0.45f,
                            Interpolation.fade
                        ),

                        Actions.moveTo(
                            x,
                            finalY,
                            0.55f,
                            Interpolation.swingOut
                        ),

                        Actions.sequence(

                            Actions.scaleTo(
                                1.12f,
                                1.12f,
                                0.35f,
                                Interpolation.swingOut
                            ),

                            Actions.scaleTo(
                                1f,
                                1f,
                                0.18f,
                                Interpolation.smooth
                            )
                        ),

                        Actions.sequence(

                            Actions.rotateTo(
                                3f,
                                0.3f,
                                Interpolation.smooth
                            ),

                            Actions.rotateTo(
                                0f,
                                0.25f,
                                Interpolation.smooth
                            )
                        )
                    ),

                    // breathing forever
                    Actions.forever(

                        Actions.sequence(

                            Actions.scaleTo(
                                1.03f,
                                1.03f,
                                1.4f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                0.97f,
                                0.97f,
                                1.4f,
                                Interpolation.sine
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
            addAction(Actions.forever(Actions.rotateBy(-360f, 1.2f, Interpolation.linear)))
        }
    }

}