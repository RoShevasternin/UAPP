package com.coinsclub.funrbx.game.actors.panel.quiz

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.actor.disable
import com.coinsclub.funrbx.game.utils.actor.setOnClickListener
import com.coinsclub.funrbx.game.utils.actor.setSize
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(34)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelProgress = APanelProgressQuiz(screen)
    private val aBgImg         = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aQuestionLbl   = Label("Q", lsDef)
    private val aFalseBtn      = Actor()
    private val aTrueBtn       = Actor()

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy { QuizController(totalQuestions = 5) }//aPanelProgress.size) }

    // ------------------------------------------------------------------------
    // Callbacks (виставляє екран)
    // ------------------------------------------------------------------------
    var onCorrect  : (Long) -> Unit      = {}
    var onFinished : (Int, Long) -> Unit = { _, _ -> }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelProgress()
        addBgImg()
        addQuestionLbl()
        addBtns()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelProgress() {
        aPanelProgress.setSize(344f, 65f)
        add(aPanelProgress) { centerX(); topToTop() }
    }

    private fun addBgImg() {
        aBgImg.setSize(347f, 405f)
        add(aBgImg) { centerX(); bottomToBottom() }
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(320f, 320f)
        add(aQuestionLbl) { centerX(); bottomToBottom(margin = 75f) }
        aQuestionLbl.wrap = true
        aQuestionLbl.setAlignment(Align.center)
    }

    private fun addBtns() {
        aFalseBtn.setSize(169f, 56f)
        add(aFalseBtn) { startToStart(); bottomToBottom() }
        aTrueBtn.setSize(169f, 56f)
        add(aTrueBtn) { endToEnd(); bottomToBottom() }

        aFalseBtn.setOnClickListener { controller.answer(false) }
        aTrueBtn.setOnClickListener  { controller.answer(true) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
        controller.onQuestion = { _, text ->
            aQuestionLbl.setText(text)
        }
        controller.onAnswered = { index, correct ->
            aPanelProgress.setResult(index, correct)
        }
        controller.onCorrect  = { onCorrect(it) }
        controller.onFinished = { correct, reward -> onFinished(correct, reward) }
        controller.initialize()
    }

}