package com.rbuxrds.counterds.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counterds.game.actors.animationsStep.AStepBundles
import com.rbuxrds.counterds.game.actors.animationsStep.AStepEmotes
import com.rbuxrds.counterds.game.actors.animationsStep.AStepSelect
import com.rbuxrds.counterds.game.actors.layout.AlignH
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.actors.panel.APanelTop
import com.rbuxrds.counterds.game.screens.MainScreen
import com.rbuxrds.counterds.game.utils.Block
import com.rbuxrds.counterds.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counterds.game.utils.actor.addActorAligned
import com.rbuxrds.counterds.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counterds.game.utils.actor.animDelay
import com.rbuxrds.counterds.game.utils.actor.animHide
import com.rbuxrds.counterds.game.utils.actor.animHideAndDisable
import com.rbuxrds.counterds.game.utils.actor.animShow
import com.rbuxrds.counterds.game.utils.actor.animShowAndEnable
import com.rbuxrds.counterds.game.utils.actor.disable
import com.rbuxrds.counterds.game.utils.actor.setSize
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.screenState.ScreenState
import com.rbuxrds.counterds.game.utils.screenState.ScreenStateMachine
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardController
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep

class AnimationsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)

    private val aStepSelect  = AStepSelect(this)
    private val aStepEmotes  = AStepEmotes(this)
    private val aStepBundles = AStepBundles(this)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val listStateGroup = listOf<WizardStep>(aStepSelect, aStepEmotes, aStepBundles)
    private val stateMachine   = ScreenStateMachine()

    private val stateSelect = object : ScreenState {
        override fun onEnter() { aStepSelect.onEnter() }
        override fun onExit() { aStepSelect.onExit() }
    }
    private val stateEmotes = object : ScreenState {
        override fun onEnter() { aStepEmotes.onEnter() }
        override fun onExit() { aStepEmotes.onExit() }
    }
    private val stateBundles = object : ScreenState {
        override fun onEnter() { aStepBundles.onEnter() }
        override fun onExit() { aStepBundles.onExit() }
    }

    // Переходи — викликаєш з будь-якого місця
    fun goToSelect() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        gdxGame.activity.showNativeAt(coords.y)
        stateMachine.setState(stateSelect)
    }
    fun goToEmotes() {
        gdxGame.activity.hideNative()
        stateMachine.setState(stateEmotes)
    }
    fun goToBundles() {
        gdxGame.activity.hideNative()
        stateMachine.setState(stateBundles)
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
            Vector2(344f, 160f),
            Vector2(344f, aPanelTop.y - 16f),
            Vector2(344f, aPanelTop.y - 16f),
        )
        listStateGroup.forEachIndexed { index, step ->
            step.group.color.a = 0f  // всі сховані
            step.group.disable()

            step.group.setSize(listSize[index])
            addActorWithConstraints(step.group) {
                startToStartOf = this@addSteps
                endToEndOf     = this@addSteps
                topToBottomOf  = aPanelTop

                marginTop = 16f
            }

            step.onEnterBlock = { aPanelTop.setTitle(step.title) }
        }

        aStepSelect.onEmotes  = { goToEmotes() }
        aStepSelect.onBundles = { goToBundles() }
    }

}