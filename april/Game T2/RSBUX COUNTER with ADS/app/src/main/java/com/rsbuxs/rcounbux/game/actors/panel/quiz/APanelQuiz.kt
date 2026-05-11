package com.rsbuxs.rcounbux.game.actors.panel.quiz

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import com.rsbuxs.rcounbux.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
    private val parameter20 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(20)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg       = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aTrueBtn        = Actor()
    private val aFalseBtn       = Actor()
    private val aQuestionLbl    = ALabel(screen, "", Color.WHITE, parameter20, screen.fontGenerator_Medium)
    private val aTotalLbl       = ALabel(screen, "Total Question: 0/10", Color.WHITE, parameter14, screen.fontGenerator_Medium)


    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    data class QuizQuestion(
        val question: String,
        val answer  : Boolean,  // true = правильна відповідь True, false = правильна відповідь False
    )

    val QUIZ_QUESTIONS = listOf(
        QuizQuestion($$"R$BUX can only be earned by playing games?",         false),
        QuizQuestion($$"You can customize your character with R$BUX items?", true),
        QuizQuestion($$"Free R$BUX generators are always safe?",             false),
        QuizQuestion($$"You can create your own mini-games?",                true),
        QuizQuestion($$"R$BUX can be used to unlock special content?",       true),
        QuizQuestion($$"Every player starts with unlimited R$BUX?",          false),
        QuizQuestion($$"You can play with friends online?",                  true),
        QuizQuestion($$"R$BUX is used only for avatar outfits?",             false),
        QuizQuestion($$"Some rewards can be earned through events?",         true),
        QuizQuestion($$"Offline mode supports online multiplayer?",          false),
    )

    // ------------------------------------------------------------------------
    // Quiz state
    // ------------------------------------------------------------------------
    private var currentIndex = 0
    private val totalQuestions = QUIZ_QUESTIONS.size

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelImg()
        addBtns()
        addQuestionLbl()
        addTotalLbl()

        showQuestion(0)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelImg() {
        add(aPanelImg) { fillParent() }
    }

    private fun addBtns() {
        aTrueBtn.setSize(168f, 60f)
        add(aTrueBtn) {
            startToStart(margin = 16f)
            bottomToBottom(margin = 48f)
        }

        aFalseBtn.setSize(168f, 60f)
        add(aFalseBtn) {
            endToEnd(margin = 16f)
            bottomToBottom(margin = 48f)
        }

        aTrueBtn.setOnClickListener  { onAnswer(true)  }
        aFalseBtn.setOnClickListener { onAnswer(false) }

    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(312f, 103f)
        add(aQuestionLbl) {
            centerX()
            topToTop(margin = 72f)
        }

        aQuestionLbl.getLabelOrNull()?.let {
            it.setAlignment(Align.center)
            it.wrap = true
        }
    }

    private fun addTotalLbl() {
        aTotalLbl.setSize(133f, 32f)
        add(aTotalLbl) {
            centerX()
            topToTop(margin = 16f)
        }

        aTotalLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun showQuestion(index: Int) {
        if (index >= totalQuestions) return

        currentIndex = index
        val question = QUIZ_QUESTIONS[index]

        aQuestionLbl.setText(question.question)
        aTotalLbl.setText("Total Question: ${index.inc()}/10")
    }

    private fun onAnswer(userAnswer: Boolean) {
        if (currentIndex >= totalQuestions) return

        if (userAnswer == QUIZ_QUESTIONS[currentIndex].answer) {
            gdxGame.modelPlayer.addRbx(5)
        }

        showQuestion(currentIndex + 1)
    }

}