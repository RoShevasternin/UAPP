package com.fimer.skintool.game.actors.loader

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
import com.fimer.skintool.game.actors.button.base.AButtonStyles
import com.fimer.skintool.game.actors.button.base.AButtonAnim
import com.fimer.skintool.game.utils.advanced.AdvancedGroup
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

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
        aWifiImg.setBounds(89f, 89f, 139f, 139f)

        animWifi()
    }

    private fun addRetryBtn() {
        addActor(aRetryBtn)
        aRetryBtn.setBounds(98f, 29f, 120f, 35f)

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
            setScale(0.85f)

            // поява
            addAction(sequence(
                parallel(
                    fadeIn(0.35f, Interpolation.fade),
                    scaleTo(1f, 1f, 0.35f, Interpolation.swingOut)
                ),

                // "пінг" — сигнал шукається: різкий сплеск + плавне осідання, з паузою
                forever(sequence(
                    // сплеск (ніби пульс сигналу пішов)
                    parallel(
                        scaleTo(1.12f, 1.12f, 0.25f, Interpolation.pow2Out),
                        alpha(1f, 0.25f, Interpolation.pow2Out)
                    ),
                    // осідання + притухання (сигнал згас)
                    parallel(
                        scaleTo(1f, 1f, 0.4f, Interpolation.sine),
                        alpha(0.5f, 0.4f, Interpolation.sine)
                    ),
                    delay(0.5f)   // пауза перед наступним пінгом
                ))
            ))
        }
    }

}