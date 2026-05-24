package com.rsbuxs.rcounbux.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.utils.actor.animHideAndDisable
import com.rsbuxs.rcounbux.game.utils.actor.animShowAndEnable
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

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
        aLogoImg.setBounds(124f, 403f, 128f, 128f)

        animLogo()
    }

    private fun addTitleImg() {
        addActor(aTitleImg)
        aTitleImg.setBounds(100f, 281f, 176f, 98f)

        animTitle()
    }

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(160f, 126f, 56f, 56f)

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

            val finalX = x
            val startOffset = 80f

            color.a = 0f
            setScale(0.9f)
            setPosition(finalX - startOffset, y)

            addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(finalX, y, 0.5f, Interpolation.smooth),
                        Actions.fadeIn(0.4f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
                    ),

                    // breathing після появи
                    Actions.forever(
                        Actions.sequence(
                            Actions.scaleTo(1.05f, 1.05f, 1.2f, Interpolation.sine),
                            Actions.scaleTo(0.95f, 0.95f, 1.2f, Interpolation.sine)
                        )
                    )
                )
            )
        }
    }

    private fun animTitle() {
        aTitleImg.apply {
            setOrigin(Align.center)

            val finalX = x
            val startOffset = 80f

            color.a = 0f
            setScale(0.95f)
            setPosition(finalX + startOffset, y)

            addAction(
                Actions.sequence(
                    Actions.delay(0.2f),

                    Actions.parallel(
                        Actions.moveTo(finalX, y, 0.6f, Interpolation.smooth),
                        Actions.fadeIn(0.5f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
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
                    Actions.rotateBy(-360f, 1.2f) // швидкість крутим як треба
                )
            )
        }
    }

}