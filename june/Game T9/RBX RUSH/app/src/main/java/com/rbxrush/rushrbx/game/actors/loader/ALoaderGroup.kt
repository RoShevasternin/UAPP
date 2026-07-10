package com.rbxrush.rushrbx.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.AlignH
import com.rbxrush.rushrbx.game.actors.layout.AlignV
import com.rbxrush.rushrbx.game.utils.actor.addActorAligned
import com.rbxrush.rushrbx.game.utils.actor.animHideAndDisable
import com.rbxrush.rushrbx.game.utils.actor.animShowAndEnable
import com.rbxrush.rushrbx.game.utils.actor.setBounds
import com.rbxrush.rushrbx.game.utils.actor.setSize
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedGroup
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoBackImg = Image(gdxGame.assetsLoader.LOGO_BACK)
    private val aLogoImg     = Image(gdxGame.assetsLoader.logo)
    private val aLoaderImg   = Image(gdxGame.assetsLoader.loader)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoBackImg()
        addLogoImg()
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLogoBackImg() {
        aLogoBackImg.setBounds(-14f, 251f, 392f, 311f)
        aLogoBackImg.setOrigin(Align.center)
        addActor(aLogoBackImg)
        animLogoBack()
    }

    private fun addLogoImg() {
        aLogoImg.setSize(344f, 121f)
        addActorAligned(aLogoImg, AlignH.CENTER, AlignV.BOTTOM)
        aLogoImg.y += 337f

        animLogo()
    }

    private fun addLoaderImg() {
        aLoaderImg.setSize(54f, 54f)
        addActorAligned(aLoaderImg, AlignH.CENTER, AlignV.BOTTOM)
        aLoaderImg.y += 121f

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

    private fun animLogoBack() {

        aLogoBackImg.apply {

            setOrigin(Align.center)

            addAction(
                Actions.forever(

                    Actions.sequence(

                        // вліво + збільшення
                        Actions.parallel(

                            Actions.scaleTo(
                                1.25f,
                                1.25f,
                                2f,
                                Interpolation.sine
                            ),

                            Actions.rotateTo(
                                -8f,
                                2f,
                                Interpolation.sine
                            )
                        ),

                        // вправо + зменшення
                        Actions.parallel(

                            Actions.scaleTo(
                                0.95f,
                                0.95f,
                                2f,
                                Interpolation.sine
                            ),

                            Actions.rotateTo(
                                8f,
                                2f,
                                Interpolation.sine
                            )
                        ),

                        // назад в центр
                        Actions.parallel(

                            Actions.scaleTo(
                                1.1f,
                                1.1f,
                                1.5f,
                                Interpolation.sine
                            ),

                            Actions.rotateTo(
                                0f,
                                1.5f,
                                Interpolation.sine
                            )
                        )
                    )
                )
            )
        }
    }

    private fun animLogo() {

        aLogoImg.apply {

            setOrigin(Align.center)

            color.a = 0f
            rotation = -10f

            setScale(0f)

            addAction(

                Actions.sequence(

                    Actions.parallel(

                        Actions.fadeIn(
                            0.25f,
                            Interpolation.fade
                        ),

                        Actions.sequence(

                            // POP
                            Actions.scaleTo(
                                1.15f,
                                1.15f,
                                0.35f,
                                Interpolation.swingOut
                            ),

                            // bounce
                            Actions.scaleTo(
                                0.92f,
                                0.92f,
                                0.10f,
                                Interpolation.smooth
                            ),

                            // final
                            Actions.scaleTo(
                                1f,
                                1f,
                                0.12f,
                                Interpolation.smooth
                            )
                        ),

                        Actions.sequence(

                            Actions.rotateTo(
                                4f,
                                0.20f,
                                Interpolation.swingOut
                            ),

                            Actions.rotateTo(
                                0f,
                                0.25f,
                                Interpolation.smooth
                            )
                        )
                    ),

                    // breathing
                    Actions.forever(

                        Actions.sequence(

                            Actions.scaleTo(
                                1.03f,
                                1.03f,
                                1.5f,
                                Interpolation.sine
                            ),

                            Actions.scaleTo(
                                0.98f,
                                0.98f,
                                1.5f,
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
            addAction(Actions.forever(Actions.rotateBy(-360f, 0.85f, Interpolation.sine)))
        }
    }

}