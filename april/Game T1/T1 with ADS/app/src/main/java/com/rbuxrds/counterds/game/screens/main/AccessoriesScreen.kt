package com.rbuxrds.counterds.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counterds.game.actors.accessoriesStep.AStepFace
import com.rbuxrds.counterds.game.actors.accessoriesStep.AStepHead
import com.rbuxrds.counterds.game.actors.accessoriesStep.AStepNeck
import com.rbuxrds.counterds.game.actors.accessoriesStep.AStepSelect
import com.rbuxrds.counterds.game.actors.layout.AlignH
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.actors.panel.APanelTop
import com.rbuxrds.counterds.game.utils.Block
import com.rbuxrds.counterds.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counterds.game.utils.actor.addActorAligned
import com.rbuxrds.counterds.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counterds.game.utils.actor.animDelay
import com.rbuxrds.counterds.game.utils.actor.animHide
import com.rbuxrds.counterds.game.utils.actor.animShow
import com.rbuxrds.counterds.game.utils.actor.disable
import com.rbuxrds.counterds.game.utils.actor.setSize
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.screenState.ScreenState
import com.rbuxrds.counterds.game.utils.screenState.ScreenStateMachine
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep

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
    fun goToSelect() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        gdxGame.activity.showNativeAt(coords.y)
        stateMachine.setState(stateSelect)
    }
    fun goToFace() {
        gdxGame.activity.hideNative()
        stateMachine.setState(stateFace)
    }
    fun goToHead() {
        gdxGame.activity.hideNative()
        stateMachine.setState(stateHead)
    }
    fun goToNeck() {
        gdxGame.activity.hideNative()
        stateMachine.setState(stateNeck)
    }

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