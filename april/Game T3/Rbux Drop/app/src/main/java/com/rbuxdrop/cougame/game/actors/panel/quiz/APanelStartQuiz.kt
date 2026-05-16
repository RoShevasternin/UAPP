package com.rbuxdrop.cougame.game.actors.panel.quiz

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.checkbox.base.ACheckBox
import com.rbuxdrop.cougame.game.actors.checkbox.base.ACheckBoxGroup
import com.rbuxdrop.cougame.game.actors.checkbox.base.ACheckBoxStyles
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.actor.disable
import com.rbuxdrop.cougame.game.utils.actor.setOnClickListener
import com.rbuxdrop.cougame.game.utils.actor.setSize
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.util.log

class APanelStartQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Question")
        .setSize(14)
    private val parameter16 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)
    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelQuestionImg = Image(gdxGame.assetsAll.panel_question)
    private val aPanelQuestionLbl = ALabel(screen, "Question 0", GameColor.purple_3D, parameter14, screen.fontGenerator_Medium)
    private val aQuestionLbl      = ALabel(screen, "", Color.WHITE, parameter24, screen.fontGenerator_Bold)
    private val listAnswerBox     = List(4) { ACheckBox(screen, ACheckBoxStyles.GRADIENT) }
    private val listAnswerLbl     = List(4) { ALabel(screen, "", Color.WHITE, parameter16, screen.fontGenerator_Light) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    data class QuizQuestion(
        val question      : String,
        val answers       : List<String>,
        val correctIndex  : Int,
    )

    private val QUIZ_QUESTIONS = listOf(

        QuizQuestion(
            question = "What shape does the RBX coin usually have?",
            answers = listOf(
                "Square",
                "Circle",
                "Triangle",
                "Hexagon"
            ),
            correctIndex = 0
        ),

        QuizQuestion(
            question = "What can RBX usually be used for?",
            answers = listOf(
                "Cooking food",
                "Avatar items",
                "Watching movies",
                "Phone calls"
            ),
            correctIndex = 1
        ),

        QuizQuestion(
            question = "What color is most common for RBX rewards?",
            answers = listOf(
                "Purple",
                "Green",
                "Orange",
                "Blue"
            ),
            correctIndex = 1
        ),

        QuizQuestion(
            question = "What do players often buy with RBX?",
            answers = listOf(
                "Cars",
                "Pets",
                "Accessories",
                "Books"
            ),
            correctIndex = 2
        ),

        QuizQuestion(
            question = "What is usually needed to play online?",
            answers = listOf(
                "Internet",
                "Printer",
                "Scanner",
                "Camera"
            ),
            correctIndex = 0
        ),

        QuizQuestion(
            question = "Which reward is usually the rarest?",
            answers = listOf(
                "Starter item",
                "Daily bonus",
                "Legendary crate",
                "Basic ticket"
            ),
            correctIndex = 2
        ),

        QuizQuestion(
            question = "What can players customize?",
            answers = listOf(
                "Weather",
                "Avatar",
                "Internet",
                "Battery"
            ),
            correctIndex = 1
        ),

        QuizQuestion(
            question = "What is commonly collected in games?",
            answers = listOf(
                "Rewards",
                "Homework",
                "Passwords",
                "Invoices"
            ),
            correctIndex = 0
        ),

        QuizQuestion(
            question = "What usually unlocks premium rewards?",
            answers = listOf(
                "Level progress",
                "Airplane mode",
                "Phone restart",
                "Brightness"
            ),
            correctIndex = 0
        ),

        QuizQuestion(
            question = "What do daily rewards encourage?",
            answers = listOf(
                "Deleting games",
                "Daily login",
                "Lower FPS",
                "Offline mode"
            ),
            correctIndex = 1
        ),
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
        addPanelQuestionImg()
        addQuestionLbl()
        addListAnswer()

        showQuestion(0)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelQuestionImg() {
        aPanelQuestionImg.setSize(90f, 36f)
        add(aPanelQuestionImg) { startToStart(margin = 16f); topToTop(margin = 12f) }

        aPanelQuestionLbl.setSize(90f, 36f)
        add(aPanelQuestionLbl) {
            startToStart(aPanelQuestionImg); endToEnd(aPanelQuestionImg)
            topToTop(aPanelQuestionImg); bottomToBottom(aPanelQuestionImg)
        }
        aPanelQuestionLbl.setAlignment(Align.center)
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(344f, 64f)
        add(aQuestionLbl) { centerX(); topToBottom(aPanelQuestionImg, 16f) }
        aQuestionLbl.label.wrap = true
    }

    private fun addListAnswer() {
        var ny  = 228f
        val cbg = ACheckBoxGroup()
        listAnswerBox.forEachIndexed { index, box ->
            addActor(box)
            box.setBounds(16f, ny, 344f, 64f)

            val lbl = listAnswerLbl[index]
            addActor(lbl)
            lbl.setBounds(28f, ny, 332f, 64f)
            lbl.disable()

            ny -= 8f + 64f

            box.checkBoxGroup = cbg
            box.setOnCheckListener { if (it) {
                onAnswer(index)
            } }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun showQuestion(index: Int) {

        if (index >= totalQuestions) {
            // finish quiz
            listAnswerBox.forEach { it.disable() }
            return
        }

        currentIndex = index

        // reset selection
        listAnswerBox.forEach { it.uncheck(false) }

        val question = QUIZ_QUESTIONS[index]

        aPanelQuestionLbl.setText("Question ${index + 1}")
        aQuestionLbl.setText(question.question)

        question.answers.forEachIndexed { answerIndex, text ->
            listAnswerLbl[answerIndex].setText(text)
        }
    }

    private fun onAnswer(answerIndex: Int) {
        val question = QUIZ_QUESTIONS[currentIndex]

        if (answerIndex == question.correctIndex) {
            gdxGame.modelPlayer.addRbx(5)
        }

        showQuestion(currentIndex + 1)
    }

}