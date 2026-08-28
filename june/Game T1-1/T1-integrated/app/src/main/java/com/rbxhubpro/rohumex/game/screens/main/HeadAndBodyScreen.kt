package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.game.actors.headAndBodyStep.AStepLock
import com.rbxhubpro.rohumex.game.actors.headAndBodyStep.AStepSelect
import com.rbxhubpro.rohumex.game.actors.headAndBodyStep.AStepShape
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.actor.disable
import com.rbxhubpro.rohumex.game.utils.actor.setSize
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.game.utils.screenState.ScreenState
import com.rbxhubpro.rohumex.game.utils.screenState.ScreenStateMachine
import com.rbxhubpro.rohumex.game.utils.wizardHelper.WizardStep

class HeadAndBodyScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.CATALOG
    override val analyticsBlock = "head_and_body_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this, false)

    private val aStepSelect = AStepSelect(this)
    private val aStepLock   = AStepLock(this)
    private val aStepShape  = AStepShape(this)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val listStateGroup = listOf<WizardStep>(aStepSelect, aStepLock, aStepShape)
    private val stateMachine   = ScreenStateMachine()

    private val stateSelect   = object : ScreenState {
        override fun onEnter() { aStepSelect.onEnter() }
        override fun onExit() { aStepSelect.onExit() }
    }
    private val stateLock    = object : ScreenState {
        override fun onEnter() { aStepLock.onEnter() }
        override fun onExit() { aStepLock.onExit() }
    }
    private val stateShape    = object : ScreenState {
        override fun onEnter() { aStepShape.onEnter() }
        override fun onExit() { aStepShape.onExit() }
    }

    // Переходи — викликаєш з будь-якого місця
    fun goToSelect() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)
        stateMachine.setState(stateSelect)
    }
    fun goToLock()   {
        gdxGame.activity.hideNative()
        stateMachine.setState(stateLock)
    }
    fun goToShape()  {
        //gdxGame.activity.hideNative()
        stateMachine.setState(stateShape)
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

        aStepSelect.onLock  = { goToLock() }
        aStepSelect.onShape = { goToShape() }
    }

}