package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.accessoriesStep.AStepFace
import com.rbuxrds.counter.game.actors.accessoriesStep.AStepHead
import com.rbuxrds.counter.game.actors.accessoriesStep.AStepNeck
import com.rbuxrds.counter.game.actors.accessoriesStep.AStepSelect
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counter.game.utils.actor.animDelay
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.actor.disable
import com.rbuxrds.counter.game.utils.actor.setSize
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.screenState.ScreenState
import com.rbuxrds.counter.game.utils.screenState.ScreenStateMachine
import com.rbuxrds.counter.game.utils.wizardHelper.WizardStep

class AccessoriesScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)

    private val aStepSelect  = AStepSelect(this)
    private val aStepFace    = AStepFace(this)
    private val aStepHead    = AStepHead(this)
    private val aStepNeck    = AStepNeck(this)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val listStateGroup = listOf<WizardStep>(aStepSelect, aStepFace, aStepHead, aStepNeck)
    private val stateMachine   = ScreenStateMachine()

    private val stateSelect = object : ScreenState {
        override fun onEnter() { aStepSelect.onEnter() }
        override fun onExit() { aStepSelect.onExit() }
    }
    private val stateFace = object : ScreenState {
        override fun onEnter() { aStepFace.onEnter() }
        override fun onExit() { aStepFace.onExit() }
    }
    private val stateHead = object : ScreenState {
        override fun onEnter() { aStepHead.onEnter() }
        override fun onExit() { aStepHead.onExit() }
    }
    private val stateNeck = object : ScreenState {
        override fun onEnter() { aStepNeck.onEnter() }
        override fun onExit() { aStepNeck.onExit() }
    }

    // Переходи — викликаєш з будь-якого місця
    fun goToSelect() { stateMachine.setState(stateSelect) }
    fun goToFace() { stateMachine.setState(stateFace) }
    fun goToHead() { stateMachine.setState(stateHead) }
    fun goToNeck() { stateMachine.setState(stateNeck) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addSteps()

        // Показуємо перший крок
        goToSelect()

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

        aPanelTop.onBack = {
            when(stateMachine.getCurrentState()) {
                stateSelect -> animHideScreen { gdxGame.navigationManager.back() }
                else -> goToSelect()
            }
        }
    }

    private fun Group.addSteps() {
        val listSize = listOf(
            Vector2(344f, 248f),
            Vector2(376f, aPanelTop.y),
            Vector2(376f, aPanelTop.y),
            Vector2(376f, aPanelTop.y),
        )
        listStateGroup.forEachIndexed { index, step ->
            step.group.color.a = 0f  // всі сховані
            step.group.disable()

            step.group.setSize(listSize[index])
            addActorWithConstraints(step.group) {
                startToStartOf = this@addSteps
                endToEndOf     = this@addSteps
                topToBottomOf  = aPanelTop
            }

            step.onEnterBlock = { aPanelTop.setTitle(step.title) }
        }

        aStepSelect.onFace = { goToFace() }
        aStepSelect.onHead = { goToHead() }
        aStepSelect.onNeck = { goToNeck() }
    }

}