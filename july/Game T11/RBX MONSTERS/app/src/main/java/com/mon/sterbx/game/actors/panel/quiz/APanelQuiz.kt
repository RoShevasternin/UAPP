package com.mon.sterbx.game.actors.panel.quiz

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.actor.setOnClickListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter

import com.mon.sterbx.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterQuestion = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(22)
        
    private val parameterCounter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)

    private val lsQuestion = FontFactory.create(screen, parameterQuestion, screen.fontGenerator_BricolageGrotesque_ExtraBold, Color.BLACK)
    private val lsCounter  = FontFactory.create(screen, parameterCounter, screen.fontGenerator_BeVietnamPro_BlackItalic, GameColor.gray_939393)
    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg       = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aQuestionLbl = Label("Q", lsQuestion)
    private val aCounterLbl  = Label("1/7", lsCounter)

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    val controller by lazy { QuizController(totalQuestions = 7) }

    // ------------------------------------------------------------------------
    // Callbacks (виставляє екран)
    // ------------------------------------------------------------------------
    var onCorrect  : (Long) -> Unit      = {}
    var onFinished : (Int, Long) -> Unit = { _, _ -> }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addCounterLbl()
        addQuestionLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addCounterLbl() {
        aCounterLbl.setSize(30f, 23f)
        add(aCounterLbl) { centerX(); topToTop(margin = 45f) }
        aCounterLbl.setAlignment(Align.center)
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(272f, 48f)
        add(aQuestionLbl) { centerX(); topToTop(margin = 77f) }
        aQuestionLbl.wrap = true
        aQuestionLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
        controller.onProgress = { current, total ->
            aCounterLbl.setText("$current/$total")
        }
        controller.onQuestion = { _, text ->
            aQuestionLbl.setText(text)
        }
        controller.onCorrect  = { onCorrect(it) }
        controller.onFinished = { correct, reward -> onFinished(correct, reward) }
        controller.initialize()
    }

}