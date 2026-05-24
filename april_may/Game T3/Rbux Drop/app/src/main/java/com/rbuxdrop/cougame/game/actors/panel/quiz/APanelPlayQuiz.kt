package com.rbuxdrop.cougame.game.actors.panel.quiz

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.screens.MainScreen
import com.rbuxdrop.cougame.game.screens.main.quiz.QuizHowScreen
import com.rbuxdrop.cougame.game.screens.main.quiz.QuizStartScreen
import com.rbuxdrop.cougame.game.utils.actor.setOnClickListener
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class APanelPlayQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_PLAY_QUIZ)
    private val aStartBtn = Actor()
    private val aHowBtn   = Actor()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelImg()
        addBtns()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelImg() {
        add(aPanelImg) { fillParent() }
    }

    private fun addBtns() {
        aStartBtn.setSize(344f, 60f)
        add(aStartBtn) { centerX(); bottomToBottom(margin = 60f) }

        aHowBtn.setSize(344f, 24f)
        add(aHowBtn) { centerX(); bottomToBottom(margin = 12f) }

        aStartBtn.setOnClickListener  { screen.animHideScreen { gdxGame.navigationManager.navigate(QuizStartScreen::class.java.name, screen::class.java.name) } }
        aHowBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(QuizHowScreen::class.java.name, screen::class.java.name) } }

    }

}