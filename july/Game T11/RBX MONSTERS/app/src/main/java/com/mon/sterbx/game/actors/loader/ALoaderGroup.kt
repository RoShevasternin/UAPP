package com.mon.sterbx.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.layout.AlignH
import com.mon.sterbx.game.actors.layout.AlignV
import com.mon.sterbx.game.utils.actor.addActorAligned
import com.mon.sterbx.game.utils.actor.animHideAndDisable
import com.mon.sterbx.game.utils.actor.animShowAndEnable
import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg  = Image(gdxGame.assetsLoader.logo)

    val aProgress = AProgressLoader(screen)

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
        addActor(aLogoImg)
        aLogoImg.setBounds(49f, 591f, 278f, 130f)

        animLogo()
    }

    private fun addLoaderImg() {
        addActor(aProgress)
        aProgress.setBounds(35f, 131f, 306f, 20f)
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

            color.a = 0f
            setScale(0.92f)

            addAction(
                Actions.sequence(

                    // проста плавна поява
                    Actions.parallel(
                        Actions.fadeIn(0.6f, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 0.6f, Interpolation.sine),
                    ),

                    // ледь помітне дихання
                    Actions.forever(
                        Actions.sequence(
                            Actions.scaleTo(1.02f, 1.02f, 2f, Interpolation.sine),
                            Actions.scaleTo(1f, 1f, 2f, Interpolation.sine),
                        )
                    )
                )
            )
        }
    }

}