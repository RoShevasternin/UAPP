package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.AWheel
import com.rbuxrds.counter.game.actors.button.ABlueButton
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelRBX
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counter.game.utils.actor.animDelay
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.util.log

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
        aSpinNowBtn.y += 20f

        aSpinNowBtn.onClick = {
            aWheel.spin { result ->
                log("result = $result")
                aPanelRBX.setResult(result.sum)
            }
        }

    }

}