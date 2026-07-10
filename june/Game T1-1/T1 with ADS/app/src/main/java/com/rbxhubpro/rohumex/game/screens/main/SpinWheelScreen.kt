package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbxhubpro.rohumex.game.actors.AWheel
import com.rbxhubpro.rohumex.game.actors.button.ABlueButton
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelRBX
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.util.log

class SpinWheelScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop   = APanelTop(this)
    private val aWheel      = AWheel(this)
    private val aPanelRBX   = APanelRBX(this)
    private val aSpinNowBtn = ABlueButton(this, "Spin Now")

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addWheel()
        addPanelRBX()
        addSpinNowBtn()

        animShowScreen()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun Group.addPanelTop() {
        aPanelTop.setSize(376f, 56f)
        addActorAligned(aPanelTop, AlignH.CENTER, AlignV.TOP)
        aPanelTop.setTitle("Spin Wheel")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addWheel() {
        aWheel.setSize(345f, 345f)
        addActorWithConstraints(aWheel) {
            startToStartOf = this@addWheel
            endToEndOf     = this@addWheel
            topToBottomOf  = aPanelTop

            marginTop = 80f
        }

    }

    private fun Group.addPanelRBX() {
        aPanelRBX.setSize(105f, 48f)
        addActorWithConstraints(aPanelRBX) {
            startToStartOf = this@addPanelRBX
            endToEndOf     = this@addPanelRBX
            topToBottomOf  = aWheel

            marginTop = 48f
        }

    }

    private fun Group.addSpinNowBtn() {
        aSpinNowBtn.setSize(344f, 56f)
        addActorAligned(aSpinNowBtn, AlignH.CENTER, AlignV.BOTTOM)
        aSpinNowBtn.y += adBannerUI //20f

        aSpinNowBtn.onClick = {
            aWheel.spin { result ->
                log("result = $result")
                aPanelRBX.setResult(result.sum)
            }
        }

    }

}