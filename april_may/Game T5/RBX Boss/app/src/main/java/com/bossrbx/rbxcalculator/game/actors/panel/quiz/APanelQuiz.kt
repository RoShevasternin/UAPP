package com.bossrbx.rbxcalculator.game.actors.panel.quiz

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.addActors
import com.bossrbx.rbxcalculator.game.utils.actor.disable
import com.bossrbx.rbxcalculator.game.utils.actor.setOnClickListener
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter12 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Question /")
        .setSize(12)
    private val parameter16 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)
    private val parameter36 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(36)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelQuestionImg  = Image(screen.drawerUtil.getTexture(GameColor.blue_335FFF))
    private val aPanelQuestionLbl  = Label("Question 0/10", FontFactory.create(screen, parameter12, screen.fontGenerator_Light, Color.WHITE))

    private val aQuestionLbl    = Label("QUESTION", FontFactory.create(screen, parameter36, screen.fontGenerator_FIRENIGHT, Color.WHITE))
    private val aPanelAnswerImg = Image(gdxGame.assetsAll.PANEL_ANSWER)
    private val listAnswerLbl   = List(4) { Label("ANSWER", FontFactory.create(screen, parameter16, screen.fontGenerator_Light, GameColor.gray_808080)) }
    private val listAnswerBtn   = List(4) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onAnswer: (isTrue: Boolean) -> Unit = {}

    // ------------------------------------------------------------------------
    // Quiz Data
    // ------------------------------------------------------------------------
    data class QuizQuestion(
        val question      : String,
        val answers       : List<String>,
        val correctAnswer : String,
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
            correctAnswer = "Square"
        ),

        QuizQuestion(
            question = "What do players usually collect?",
            answers = listOf(
                "Rewards",
                "Homework",
                "Passwords",
                "Invoices"
            ),
            correctAnswer = "Rewards"
        ),

        QuizQuestion(
            question = "What is needed for online play?",
            answers = listOf(
                "Internet",
                "Scanner",
                "Camera",
                "Printer"
            ),
            correctAnswer = "Internet"
        ),

        QuizQuestion(
            question = "What can players customize?",
            answers = listOf(
                "Avatar",
                "Battery",
                "Weather",
                "Monitor"
            ),
            correctAnswer = "Avatar"
        ),

        QuizQuestion(
            question = "What do daily rewards encourage?",
            answers = listOf(
                "Daily login",
                "Deleting games",
                "Offline mode",
                "Low FPS"
            ),
            correctAnswer = "Daily login"
        ),

        QuizQuestion(
            question = "What is commonly bought with RBX?",
            answers = listOf(
                "Accessories",
                "Cars",
                "Books",
                "Printers"
            ),
            correctAnswer = "Accessories"
        ),

        QuizQuestion(
            question = "What color is common for rewards?",
            answers = listOf(
                "Green",
                "Brown",
                "Gray",
                "Pink"
            ),
            correctAnswer = "Green"
        ),

        QuizQuestion(
            question = "What is usually the rarest item?",
            answers = listOf(
                "Legendary crate",
                "Starter item",
                "Basic ticket",
                "Small coin"
            ),
            correctAnswer = "Legendary crate"
        ),

        QuizQuestion(
            question = "What can players unlock?",
            answers = listOf(
                "Skins",
                "Taxes",
                "Emails",
                "Drivers"
            ),
            correctAnswer = "Skins"
        ),

        QuizQuestion(
            question = "What can improve your level?",
            answers = listOf(
                "Practice",
                "Brightness",
                "Restart",
                "Battery"
            ),
            correctAnswer = "Practice"
        ),

        QuizQuestion(
            question = "What is often found in game shops?",
            answers = listOf(
                "Pets",
                "Invoices",
                "Folders",
                "Printers"
            ),
            correctAnswer = "Pets"
        ),

        QuizQuestion(
            question = "What can players earn daily?",
            answers = listOf(
                "Rewards",
                "Homework",
                "Drivers",
                "Batteries"
            ),
            correctAnswer = "Rewards"
        ),

        QuizQuestion(
            question = "What usually costs RBX?",
            answers = listOf(
                "Accessories",
                "Internet",
                "Weather",
                "Cameras"
            ),
            correctAnswer = "Accessories"
        ),

        QuizQuestion(
            question = "What do multiplayer games need?",
            answers = listOf(
                "Internet",
                "Scanner",
                "Paper",
                "Projector"
            ),
            correctAnswer = "Internet"
        ),

        QuizQuestion(
            question = "What can players receive from quests?",
            answers = listOf(
                "Rewards",
                "Invoices",
                "Passwords",
                "Taxes"
            ),
            correctAnswer = "Rewards"
        ),
    )

    private val selectedQuestions = QUIZ_QUESTIONS.shuffled().take(10)

    private var currentIndex = 0

    private val totalQuestions get() = selectedQuestions.size

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelQuestion()
        addQuestionLbl()
        addPanelAnswerImg()

        showQuestion(0)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelQuestion() {
        aPanelQuestionImg.setSize(94f, 28f)
        add(aPanelQuestionImg) { centerX(); topToTop(margin = 12f) }

        aPanelQuestionLbl.setSize(94f, 28f)
        add(aPanelQuestionLbl) { center(aPanelQuestionImg); }

        aPanelQuestionLbl.setAlignment(Align.center)
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(344f, 88f)
        add(aQuestionLbl) { centerX(); topToBottom(aPanelQuestionImg, 16f) }

        aQuestionLbl.setAlignment(Align.center)
        aQuestionLbl.wrap = true
    }

    private fun addPanelAnswerImg() {
        aPanelAnswerImg.setSize(344f, 280f)
        add(aPanelAnswerImg) { centerX(); topToBottom(aQuestionLbl, 16f) }

        var nx = 16f
        var ny = 228f

        listAnswerBtn.forEachIndexed { index, btn ->
            val lbl = listAnswerLbl[index]
            lbl.setAlignment(Align.center)

            addActors(lbl, btn)

            btn.setBounds(nx, ny, 344f, 64f)
            lbl.setBounds(nx, ny, 344f, 64f)

            ny -= 8f + 64f

            btn.setOnClickListener {
                val question = selectedQuestions[currentIndex]
                val answer = listAnswerLbl[index].text.toString()

                val isTrue = answer == question.correctAnswer
                onAnswer(isTrue)

                showQuestion(currentIndex + 1)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun showQuestion(index: Int) {
        if (index >= totalQuestions) {
            listAnswerBtn.forEach { it.disable() }
            return
        }

        currentIndex = index
        val question = selectedQuestions[index]

        aPanelQuestionLbl.setText("Question ${index + 1}/$totalQuestions")
        aQuestionLbl.setText(question.question)

        // random answers
        val shuffledAnswers = question.answers.shuffled()

        shuffledAnswers.forEachIndexed { i, answer ->
            listAnswerLbl[i].setText(answer)
        }
    }

}