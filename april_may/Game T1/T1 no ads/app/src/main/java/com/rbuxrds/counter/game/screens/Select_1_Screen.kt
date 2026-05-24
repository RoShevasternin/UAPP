package com.rbuxrds.counter.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.actors.button.ABlueButton
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.actors.select_1_Step.AStepAnimationPack
import com.rbuxrds.counter.game.actors.select_1_Step.AStepAnime
import com.rbuxrds.counter.game.actors.select_1_Step.AStepCloth
import com.rbuxrds.counter.game.actors.select_1_Step.AStepSkin
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
import com.rbuxrds.counter.game.utils.wizardHelper.WizardController
import com.rbuxrds.counter.game.utils.wizardHelper.WizardStep

class Select_1_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)
    private val aNextBtn   = ABlueButton(this, "Next")

    private val aStepCloth = AStepCloth(this)
    private val aStepAnime = AStepAnime(this)
    private val aStepSkin  = AStepSkin(this)
    private val aStepPack  = AStepAnimationPack(this)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // Steps
    private val steps = listOf<WizardStep>(aStepCloth, aStepAnime, aStepSkin, aStepPack)

    private val wizardController = WizardController(
        steps    = steps,
        onFinish = { animHideScreen { gdxGame.navigationManager.navigate(MainScreen::class.java.name, this::class.java.name) } }
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addSteps()
        addNextBtn()

        // Показуємо перший крок
        wizardController.currentStep.onEnter()

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

        aPanelTop.onBack = { wizardController.back() }
    }

    private fun Group.addNextBtn() {
        aNextBtn.setSize(344f, 56f)
        addActorAligned(aNextBtn, AlignH.CENTER, AlignV.BOTTOM)
        aNextBtn.y += 20f

        aNextBtn.onClick = { wizardController.next() }
    }

    private fun Group.addSteps() {
        val listSize = listOf(
            Vector2(344f, 326f),
            Vector2(344f, 326f),
            Vector2(376f, 609f),
            Vector2(368f, 520f),
        )
        steps.forEachIndexed { index, step ->
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
    }

}