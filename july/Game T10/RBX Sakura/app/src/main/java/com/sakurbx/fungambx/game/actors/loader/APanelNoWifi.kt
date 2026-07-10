package com.sakurbx.fungambx.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel
import com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.button.base.AButtonAnim
import com.sakurbx.fungambx.game.actors.button.base.AButtonStyles
import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelNoWifi(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsLoader.panel_no_wifi)
    private val aWifiImg  = Image(gdxGame.assetsLoader.wifi)
    private val aRetryBtn = AButtonAnim(screen, AButtonStyles.Anim.RETRY)

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
        aWifiImg.setBounds(89f, 153f, 139f, 139f)

        animWifi()
    }

    private fun addRetryBtn() {
        addActor(aRetryBtn)
        aRetryBtn.setBounds(111f, 41f, 95f, 95f)

        aRetryBtn.setOnClickListener(null) { onRetry() }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animWifi() {
        aWifiImg.apply {
            setOrigin(Align.center)

            color.a  = 0f
            rotation = 0f
            setScale(0.9f)

            // поява
            addAction(sequence(
                parallel(
                    fadeIn(0.4f, Interpolation.fade),
                    scaleTo(1f, 1f, 0.4f, Interpolation.swingOut)
                ),

                // пульсація "no signal" — дихання прозорістю + масштабом
                forever(sequence(
                    parallel(
                        alpha(0.45f, 0.9f, Interpolation.sine),
                        scaleTo(0.93f, 0.93f, 0.9f, Interpolation.sine)
                    ),
                    parallel(
                        alpha(1f, 0.9f, Interpolation.sine),
                        scaleTo(1f, 1f, 0.9f, Interpolation.sine)
                    )
                ))
            ))

            // окремий цикл — легке похитування (шукає сигнал), несинхронно з пульсацією
            addAction(sequence(
                delay(0.4f),   // після появи
                forever(sequence(
                    rotateTo(-4f, 0.7f, Interpolation.sine),
                    rotateTo(4f, 0.7f, Interpolation.sine),
                    rotateTo(0f, 0.5f, Interpolation.sine),
                    delay(0.6f),   // пауза між "спробами"
                ))
            ))
        }
    }

}