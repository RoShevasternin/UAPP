package com.treprosure.starbxup.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.treprosure.starbxup.game.actors.layout.AlignH
import com.treprosure.starbxup.game.actors.layout.AlignV
import com.treprosure.starbxup.game.utils.actor.addActorAligned
import com.treprosure.starbxup.game.utils.actor.animHideAndDisable
import com.treprosure.starbxup.game.utils.actor.animShowAndEnable
import com.treprosure.starbxup.game.utils.actor.setSize
import com.treprosure.starbxup.game.utils.advanced.AdvancedGroup
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.gdxGame

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
        aLogoImg.setSize(117f, 120f)
        addActorAligned(aLogoImg, AlignH.CENTER, AlignV.CENTER)

        animLogo()
    }

    private fun addLoaderImg() {
        aLoaderImg.setSize(54f, 54f)
        addActorAligned(aLoaderImg, AlignH.CENTER, AlignV.BOTTOM)
        aLoaderImg.y += 153f

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
            setScale(0.8f)
            y += 80f

            addAction(

                Actions.parallel(

                    Actions.fadeIn(
                        0.35f,
                        Interpolation.fade
                    ),

                    Actions.moveTo(
                        x,
                        finalY,
                        0.6f,
                        Interpolation.swingOut
                    ),

                    Actions.sequence(

                        Actions.scaleTo(
                            1.08f,
                            1.08f,
                            0.3f,
                            Interpolation.swingOut
                        ),

                        Actions.scaleTo(
                            1f,
                            1f,
                            0.15f,
                            Interpolation.smooth
                        )
                    )
                )
            )
        }
    }

    private fun animLoader() {
        aLoaderImg.apply {

            setOrigin(Align.center)
            setScale(0.8f)

            addAction(
                Actions.forever(
                    Actions.parallel(

                        Actions.rotateBy(
                            -360f,
                            1f
                        ),

                        Actions.sequence(
                            Actions.scaleTo(
                                1.1f,
                                1.1f,
                                0.5f,
                                Interpolation.sine
                            ),
                            Actions.scaleTo(
                                0.9f,
                                0.9f,
                                0.5f,
                                Interpolation.sine
                            )
                        )
                    )
                )
            )
        }
    }

}