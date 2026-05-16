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

class APanelHowQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_HOW_QUIZ)
    private val aStartBtn = Actor()

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
        add(aStartBtn) { centerX(); bottomToBottom(margin = 24f) }

        aStartBtn.setOnClickListener  { screen.animHideScreen { gdxGame.navigationManager.navigate(QuizStartScreen::class.java.name, screen::class.java.name) } }
    }

}