package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.AScrollPane
import com.rbuxrds.counter.game.actors.allCharacters.ACharacter
import com.rbuxrds.counter.game.actors.allCharacters.ACharacters
import com.rbuxrds.counter.game.actors.button.ABlueButton
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counter.game.utils.actor.animDelay
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animHideAndDisable
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.actor.animShowAndEnable
import com.rbuxrds.counter.game.utils.actor.setSize
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.screenState.ScreenState
import com.rbuxrds.counter.game.utils.screenState.ScreenStateMachine
import com.rbuxrds.counter.util.log

class AllCharactersScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop   = APanelTop(this)
    private val aCharacters = ACharacters(this)
    private val aScrollPane = AScrollPane(aCharacters)
    private val aCharacter  = ACharacter(this)
    private val aDoneBtn    = ABlueButton(this, "Done")

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val listStateGroup = listOf<Actor>(aScrollPane, aCharacter, aDoneBtn)
    private val stateMachine   = ScreenStateMachine()

    private val stateCharacters = object : ScreenState {
        override fun onEnter() { aScrollPane.animShowAndEnable(0.25f) }
        override fun onExit() { aScrollPane.animHideAndDisable(0.25f) }
    }
    private val stateCharacter = object : ScreenState {
        override fun onEnter() {
            aCharacter.animShowAndEnable(0.25f)
            aDoneBtn.animShowAndEnable(0.25f)
        }
        override fun onExit() {
            aCharacter.animHideAndDisable(0.25f)
            aDoneBtn.animHideAndDisable(0.25f)
        }
    }

    // Переходи — викликаєш з будь-якого місця
    fun goToCharacters() { stateMachine.setState(stateCharacters) }
    fun goToCharacter() { stateMachine.setState(stateCharacter) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addScrollPane()
        addCharacter()
        addDoneBtn()

        // Всі групи приховані
        listStateGroup.forEach { it.animHideAndDisable() }

        goToCharacters()

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
        aPanelTop.setTitle("Select Cloth")

        aPanelTop.onBack = {
            // Перевіряємо: якщо ми в детальному перегляді персонажа
            if (stateMachine.getCurrentState() == stateCharacter) {
                goToCharacters() // Повертаємося до списку
            } else {
                // Якщо ми вже в списку (stateCharacters) — закриваємо екран
                animHideScreen { gdxGame.navigationManager.back() }
            }
        }
    }

    private fun Group.addScrollPane() {
        aScrollPane.setSize(376f, aPanelTop.y)
        addActorAligned(aScrollPane, AlignH.CENTER, AlignV.BOTTOM)

        aCharacters.setSize(376f, 1233f)

        aCharacters.onSelectCharacter = {
            aCharacter.setDataCharacter(it)
            goToCharacter()
        }
    }

    private fun Group.addCharacter() {
        aCharacter.setSize(344f, 553f)
        addActorWithConstraints(aCharacter) {
            startToStartOf = this@addCharacter
            endToEndOf     = this@addCharacter
            topToBottomOf  = aPanelTop

            marginTop = 4f
        }

    }

    private fun Group.addDoneBtn() {
        aDoneBtn.setSize(344f, 56f)
        addActorAligned(aDoneBtn, AlignH.CENTER, AlignV.BOTTOM)
        aDoneBtn.y += 20f

        aDoneBtn.onClick = { goToCharacters() }
    }

}