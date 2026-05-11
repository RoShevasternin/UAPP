package com.rbuxrds.counterds.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counterds.game.actors.button.ABlueButton
import com.rbuxrds.counterds.game.actors.layout.AlignH
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.actors.panel.APanelTop
import com.rbuxrds.counterds.game.actors.select_1_Step.AStepAnimationPack
import com.rbuxrds.counterds.game.actors.select_1_Step.AStepAnime
import com.rbuxrds.counterds.game.actors.select_1_Step.AStepCloth
import com.rbuxrds.counterds.game.actors.select_1_Step.AStepSkin
import com.rbuxrds.counterds.game.actors.select_1_Step.SelectableBox
import com.rbuxrds.counterds.game.utils.Block
import com.rbuxrds.counterds.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counterds.game.utils.actor.addActorAligned
import com.rbuxrds.counterds.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counterds.game.utils.actor.animDelay
import com.rbuxrds.counterds.game.utils.actor.animHide
import com.rbuxrds.counterds.game.utils.actor.animShow
import com.rbuxrds.counterds.game.utils.actor.disable
import com.rbuxrds.counterds.game.utils.actor.enable
import com.rbuxrds.counterds.game.utils.actor.setSize
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardController
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep
import com.rbuxrds.counterds.util.log

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

        aPanelTop.onBack = {
            wizardController.back()

            gdxGame.activity.onBackNavigation()
        }
    }

    private fun Group.addNextBtn() {
        aNextBtn.setSize(344f, 56f)
        addActorAligned(aNextBtn, AlignH.CENTER, AlignV.BOTTOM)
        aNextBtn.y += safeBannerUI

        aNextBtn.disableBtn()

        aNextBtn.onClick = {
            aNextBtn.disableBtn()
            wizardController.next()

            gdxGame.activity.onFrontNavigation()
        }
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

            step.onEnterBlock = {
                if (index <= 1) {
                    val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, aNextBtn.y + aNextBtn.height + 15f))
                    gdxGame.activity.showNativeAt(coords.y)
                } else gdxGame.activity.hideNative()

                aPanelTop.setTitle(step.title)
            }

            (step.group as? SelectableBox)?.onSelectBlock = { isCheck -> if (isCheck) aNextBtn.enableBtn() }

        }
    }

}