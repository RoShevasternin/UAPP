package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.ADim
import com.rbuxrds.counter.game.actors.daily.ADailyFreeRbxCalculatorInput
import com.rbuxrds.counter.game.actors.daily.ADailyFreeRbxCalculatorSelect
import com.rbuxrds.counter.game.actors.daily.ADialogLose
import com.rbuxrds.counter.game.actors.daily.ADialogWin
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelTop
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

class DailyFreeRbxCalculatorScreen: AdvancedScreen() {

    companion object {
        private var hasWin = false
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop = APanelTop(this)

    private val aSelect = ADailyFreeRbxCalculatorSelect(this)
    private val aInput  = ADailyFreeRbxCalculatorInput(this)

    // Актори для результату
    private val aDim         = ADim(this)           // затемнення
    private val aResultWin   = ADialogWin(this)     // Well done! з числом
    private val aResultLose  = ADialogLose(this)    // That's all for today!

    private val listGroup = listOf(aSelect, aInput, aDim, aResultWin, aResultLose)

    // State
    private val stateSelect = object : ScreenState {
        override fun onEnter() { aSelect.animShowAndEnable(0.25f)  }
        override fun onExit()  { aSelect.animHideAndDisable(0.25f) }
    }

    private val stateInput = object : ScreenState {
        override fun onEnter() { aInput.animShowAndEnable(0.25f)  }
        override fun onExit()  { aInput.animHideAndDisable(0.25f) }
    }

    private val stateResultWin = object : ScreenState {
        override fun onEnter() {
            aDim.animShowAndEnable(0.25f)
            aResultWin.animShowAndEnable(0.25f)
        }
        override fun onExit() {
            aDim.animHideAndDisable(0.25f)
            aResultWin.animHideAndDisable(0.25f)
        }
    }

    private val stateResultLose = object : ScreenState {
        override fun onEnter() {
            aDim.animShowAndEnable(0.25f)
            aResultLose.animShowAndEnable(0.25f)
        }
        override fun onExit() {
            aDim.animHideAndDisable(0.25f)
            aResultLose.animHideAndDisable(0.25f)
        }
    }

    private val stateMachine = ScreenStateMachine()

    // Переходи — викликаєш з будь-якого місця
    fun goToSelect() { stateMachine.setState(stateSelect) }
    fun goToInput() { stateMachine.setState(stateInput) }

    fun goToResultWin(value: Int) {
        ADialogWin.VALUE.value = value
        stateMachine.setState(stateResultWin)
    }
    fun goToResultLose() { stateMachine.setState(stateResultLose) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        hasWin = false

        addPanelTop()
        addSelect()
        addInput()

        // Dim і результати — поверх всього
        addDim()
        addResultWin()
        addResultLose()

        // Всі групи приховані
        listGroup.forEach { it.animHideAndDisable() }

        // Початковий стан
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
        aPanelTop.setTitle("Daily Free Rbx Calculator")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addSelect() {
        aSelect.setSize(344f, 272f)
        addActorWithConstraints(aSelect) {
            startToStartOf = this@addSelect
            endToEndOf     = this@addSelect
            topToBottomOf  = aPanelTop

            marginTop = 16f
        }

        aSelect.onSelect = { title ->
            aPanelTop.setTitle(title)
            goToInput()
        }
    }

    private fun Group.addInput() {
        aInput.setSize(344f, 641f)
        addActorWithConstraints(aInput) {
            startToStartOf   = this@addInput
            endToEndOf       = this@addInput
            bottomToBottomOf = this@addInput

            marginBottom = 20f
        }

        aInput.onCountNowClick = {
            val result = aInput.resultInput
            if (!hasWin && result > 0) {
                goToResultWin(result)
                hasWin = true
            } else {
                goToResultLose()
            }
        }

    }

    private fun Group.addDim() {
        addAndFillActor(aDim)
        aDim.setOnClickListener { }  // блокуємо кліки крізь dim
    }

    private fun Group.addResultWin() {
        aResultWin.setSize(316f, 338f)
        addActorAligned(aResultWin, AlignH.CENTER, AlignV.CENTER)

        aResultWin.onOk = { goToInput() }
    }

    private fun Group.addResultLose() {
        aResultLose.setSize(316f, 338f)
        addActorAligned(aResultLose, AlignH.CENTER, AlignV.CENTER)

        aResultLose.onOk = { goToInput() }
    }

}