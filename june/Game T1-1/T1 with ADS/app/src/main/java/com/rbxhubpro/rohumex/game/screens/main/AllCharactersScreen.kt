package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbxhubpro.rohumex.game.actors.AScrollPane
import com.rbxhubpro.rohumex.game.actors.allCharacters.ACharacter
import com.rbxhubpro.rohumex.game.actors.allCharacters.ACharacters
import com.rbxhubpro.rohumex.game.actors.button.ABlueButton
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animHideAndDisable
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.actor.animShowAndEnable
import com.rbxhubpro.rohumex.game.utils.actor.setSize
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.game.utils.screenState.ScreenState
import com.rbxhubpro.rohumex.game.utils.screenState.ScreenStateMachine
import com.rbxhubpro.rohumex.util.log

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
        aDoneBtn.y += adBannerUI //20f

        aDoneBtn.onClick = { goToCharacters() }
    }

}