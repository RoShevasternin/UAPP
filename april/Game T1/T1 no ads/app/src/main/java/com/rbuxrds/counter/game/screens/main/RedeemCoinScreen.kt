package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.ADim
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelRedeem
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.actors.redeem.ACoffer
import com.rbuxrds.counter.game.actors.redeem.ADialogOops
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.animDelay
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animHideAndDisable
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.actor.animShowAndEnable
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.screenState.ScreenState
import com.rbuxrds.counter.game.utils.screenState.ScreenStateMachine

class RedeemCoinScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    = APanelTop(this)
    private val aCoffer      = ACoffer(this)
    private val aPanelRedeem = APanelRedeem(this)

    // Актори для Діалогу
    private val aDim         = ADim(this)
    private val aDialogOops  = ADialogOops(this)

    // State
    private val listStateGroup = listOf(aDim, aDialogOops)
    private val stateMachine   = ScreenStateMachine()

    private val stateDialogOops = object : ScreenState {
        override fun onEnter() {
            aDim.animShowAndEnable(0.25f)
            aDialogOops.animShowAndEnable(0.25f)
        }
        override fun onExit() {
            aDim.animHideAndDisable(0.25f)
            aDialogOops.animHideAndDisable(0.25f)
        }
    }

    // Переходи — викликаєш з будь-якого місця
    fun goToDialogOops() { stateMachine.setState(stateDialogOops) }
    fun goToDialogOopsClose() { stateMachine.setState(null) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addCoffer()
        addPanelRedeem()

        addDim()
        addDialogOops()

        // Всі групи приховані
        listStateGroup.forEach { it.animHideAndDisable() }

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
        aPanelTop.setTitle("Redeem Coin")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addCoffer() {
        aCoffer.setSize(260f, 260f)
        addActorWithConstraints(aCoffer) {
            startToStartOf   = this@addCoffer
            endToEndOf       = this@addCoffer
            topToBottomOf    = aPanelTop

            marginTop = 16f
        }
    }

    private fun Group.addPanelRedeem() {
        aPanelRedeem.setSize(344f, 174f)
        addActorWithConstraints(aPanelRedeem) {
            startToStartOf   = this@addPanelRedeem
            endToEndOf       = this@addPanelRedeem
            topToBottomOf    = aCoffer

            marginTop = 24f
        }

        aPanelRedeem.onClick = { goToDialogOops() }
    }

    private fun Group.addDim() {
        addAndFillActor(aDim)
        aDim.setOnClickListener { }  // блокуємо кліки крізь dim
    }

    private fun Group.addDialogOops() {
        aDialogOops.setSize(316f, 360f)
        addActorAligned(aDialogOops, AlignH.CENTER, AlignV.CENTER)

        aDialogOops.onOk = { goToDialogOopsClose() }
    }

}