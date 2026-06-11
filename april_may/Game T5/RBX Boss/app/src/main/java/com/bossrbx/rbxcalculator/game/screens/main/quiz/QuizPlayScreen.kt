package com.bossrbx.rbxcalculator.game.screens.main.quiz

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelRS
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.util.log

class QuizPlayScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop   by lazy { APanelTop(this) }
    private val aQuizImg    by lazy { Image(gdxGame.assetsAll.PANEL_PLAY_QUIZ) }
    private val aPanelRBX   by lazy { APanelRS(this) }
    private val aStartBtn   by lazy { ABlueButton(this, "Start Quiz") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        gdxGame.activity.hideBanner()

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.showBanner()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addQuizImg()
        addPanelRBX()
        addStartBtn()
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

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 64f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Play Quiz")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addQuizImg() {
        aQuizImg.setSize(344f, 558f)
        add(aQuizImg) { centerX(); topToBottom(aPanelTop, 12f) }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(61f, 32f)
        add(aPanelRBX) { centerX(aQuizImg); bottomToBottom(aQuizImg, 12f) }
    }

    private fun AConstraintLayout.addStartBtn() {
        aStartBtn.setSize(344f, 64f)
        add(aStartBtn) { centerX(); topToBottom(aQuizImg, 24f) }

        aStartBtn.setOnClickListener {
            animHideScreen { gdxGame.navigationManager.navigate(QuizGameScreen::class.java.name, QuizPlayScreen::class.java.name) }
        }

    }

}