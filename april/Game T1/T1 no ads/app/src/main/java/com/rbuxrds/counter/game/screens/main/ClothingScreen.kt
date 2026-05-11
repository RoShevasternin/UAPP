package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.clothingStep.AStepPants
import com.rbuxrds.counter.game.actors.clothingStep.AStepShoes
import com.rbuxrds.counter.game.actors.clothingStep.AStepSelect
import com.rbuxrds.counter.game.actors.clothingStep.AStepShirts
import com.rbuxrds.counter.game.actors.clothingStep.AStepT_Shiets
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

class ClothingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)

    private val aStepSelect   = AStepSelect(this)
    private val aStepShoes    = AStepShoes(this)
    private val aStepPants    = AStepPants(this)
    private val aStepT_Shiets = AStepT_Shiets(this)
    private val aStepShirts   = AStepShirts(this)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val listStateGroup = listOf<WizardStep>(aStepSelect, aStepShoes, aStepPants, aStepT_Shiets, aStepShirts)
    private val stateMachine   = ScreenStateMachine()

    private val stateSelect   = object : ScreenState {
        override fun onEnter() { aStepSelect.onEnter() }
        override fun onExit() { aStepSelect.onExit() }
    }
    private val stateShoes    = object : ScreenState {
        override fun onEnter() { aStepShoes.onEnter() }
        override fun onExit() { aStepShoes.onExit() }
    }
    private val statePants    = object : ScreenState {
        override fun onEnter() { aStepPants.onEnter() }
        override fun onExit() { aStepPants.onExit() }
    }
    private val stateT_Shiets = object : ScreenState {
        override fun onEnter() { aStepT_Shiets.onEnter() }
        override fun onExit() { aStepT_Shiets.onExit() }
    }
    private val stateShirts   = object : ScreenState {
        override fun onEnter() { aStepShirts.onEnter() }
        override fun onExit() { aStepShirts.onExit() }
    }

    // Переходи — викликаєш з будь-якого місця
    fun goToSelect()   { stateMachine.setState(stateSelect) }
    fun goToShoes()    { stateMachine.setState(stateShoes) }
    fun goToPants()    { stateMachine.setState(statePants) }
    fun goToT_Shiets() { stateMachine.setState(stateT_Shiets) }
    fun goToShirts()   { stateMachine.setState(stateShirts) }

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
            Vector2(344f, 336f),
            Vector2(376f, aPanelTop.y),
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

        aStepSelect.onShoes    = { goToShoes() }
        aStepSelect.onPants    = { goToPants() }
        aStepSelect.onT_Shiets = { goToT_Shiets() }
        aStepSelect.onShirts   = { goToShirts() }
    }

}