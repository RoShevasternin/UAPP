package com.skindustry.skinly.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.layout.AlignH
import com.skindustry.skinly.game.actors.layout.AlignV
import com.skindustry.skinly.game.utils.actor.addActorAligned
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.actor.setSize
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aGlowImg   = Image(gdxGame.assetsLoader.glow)
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
        addGlowImg()
        addLogoImg()
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addGlowImg() {
        aGlowImg.setSize(384f, 384f)
        addActorAligned(aGlowImg, AlignH.CENTER, AlignV.CENTER)

        animGlow()
    }

    private fun addLogoImg() {
        aLogoImg.setSize(333f, 333f)
        addActorAligned(aLogoImg, AlignH.CENTER, AlignV.CENTER)

        animLogo()
    }

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(170f, 181f, 36f, 36f)

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

    private fun animGlow() {

        aGlowImg.apply {

            setOrigin(Align.center)

            color.a = 0f
            setScale(0.7f)
            rotation = 0f

            addAction(

                Actions.sequence(

                    // smooth appear
                    Actions.parallel(

                        Actions.fadeIn(
                            0.3f,
                            Interpolation.fade
                        ),

                        Actions.scaleTo(
                            1f,
                            1f,
                            0.4f,
                            Interpolation.smooth
                        )
                    ),

                    // juicy glow motion
                    Actions.forever(

                        Actions.parallel(

                            // faster rotate
                            Actions.rotateBy(
                                360f,
                                7f
                            ),

                            // huge breathing
                            Actions.sequence(

                                Actions.scaleTo(
                                    1.22f,
                                    1.22f,
                                    2.5f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    0.9f,
                                    0.9f,
                                    2.5f,
                                    Interpolation.sine
                                )
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
            setScale(0f)

            addAction(

                Actions.sequence(

                    // rubber appearance
                    Actions.parallel(

                        Actions.fadeIn(
                            0.25f,
                            Interpolation.fade
                        ),

                        Actions.sequence(

                            // boom
                            Actions.scaleTo(
                                1.14f,
                                1.14f,
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

                    // endless flying motion
                    Actions.forever(

                        Actions.sequence(

                            // up
                            Actions.parallel(

                                Actions.moveBy(
                                    0f,
                                    12f,
                                    2f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    1.03f,
                                    1.03f,
                                    2f,
                                    Interpolation.sine
                                ),

                                Actions.rotateTo(
                                    -2f,
                                    2f,
                                    Interpolation.sine
                                )
                            ),

                            // down
                            Actions.parallel(

                                Actions.moveBy(
                                    0f,
                                    -12f,
                                    2f,
                                    Interpolation.sine
                                ),

                                Actions.scaleTo(
                                    0.98f,
                                    0.98f,
                                    2f,
                                    Interpolation.sine
                                ),

                                Actions.rotateTo(
                                    2f,
                                    2f,
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
                    Actions.rotateBy(-360f, 0.7f) // швидкість крутим як треба
                )
            )
        }
    }

}