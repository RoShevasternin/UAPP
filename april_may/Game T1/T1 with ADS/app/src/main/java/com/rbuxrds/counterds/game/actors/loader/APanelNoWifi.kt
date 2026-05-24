package com.rbuxrds.counterds.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.game.actors.button.base.AButtonAnim
import com.rbuxrds.counterds.game.actors.button.base.AButtonStyles
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame

class APanelNoWifi(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsLoader.panel_no_wifi)
    private val aWifiImg  = Image(gdxGame.assetsLoader.wifi)
    private val aRetryBtn = AButtonAnim(screen, AButtonStyles.RETRY)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addWifiImg()
        addRetryBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addWifiImg() {
        addActor(aWifiImg)
        aWifiImg.setBounds(113f, 194f, 90f, 90f)

        animWifi()
    }

    private fun addRetryBtn() {
        addActor(aRetryBtn)
        aRetryBtn.setBounds(90f, 44f, 136f, 46f)

        aRetryBtn.setOnClickListener(null) { onRetry() }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animWifi() {
        aWifiImg.apply {
            setOrigin(Align.center)

            color.a = 0f
            setScale(0.9f)

            addAction(sequence(
                // поява
                parallel(
                    fadeIn(0.5f, Interpolation.fade),
                    scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
                ),

                // нескінченний "no signal" ефект
                forever(
                    sequence(
                        parallel(
                            alpha(0.5f, 0.8f, Interpolation.sine),
                            scaleTo(0.95f, 0.95f, 0.8f, Interpolation.sine)
                        ),
                        parallel(
                            alpha(1f, 0.8f, Interpolation.sine),
                            scaleTo(1f, 1f, 0.8f, Interpolation.sine)
                        )
                    )
                )
            ))
        }
    }

}