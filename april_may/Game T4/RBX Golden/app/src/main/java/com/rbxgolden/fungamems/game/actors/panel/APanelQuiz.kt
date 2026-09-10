package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.actor.disable
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.businesModule.backend.Bt
import com.rbxgolden.fungamems.businesModule.backend.Events
import com.rbxgolden.fungamems.businesModule.economy.Econ
import com.rbxgolden.fungamems.businesModule.economy.Wallet
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class APanelQuiz(
    override val screen: AdvancedScreen
) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter16 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "/")
        .setSize(16)

    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelQuestionImg = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aCounterLbl       = Label("0/5", FontFactory.create(screen, parameter16, screen.fontGenerator_Bold, Color.WHITE))
    private val aQuestionLbl      = Label("Question", FontFactory.create(screen, parameter24, screen.fontGenerator_Bold, Color.WHITE))
    private val aTrueBtn          = Actor()
    private val aFalseBtn         = Actor()

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------
    data class QuizQuestion(
        val question : String,
        val answer   : Boolean,
    )

    private val QUIZ_QUESTIONS = listOf(

        QuizQuestion("Theres no way to get Rbx?", false),
        QuizQuestion("Can you customize avatars?", true),
        QuizQuestion("Is offline mode multiplayer?", false),
        QuizQuestion("Can players earn daily rewards?", true),
        QuizQuestion("Are all Rbx generators safe?", false),

        QuizQuestion("Do games need internet online?", true),
        QuizQuestion("Can you unlock bonus items?", true),
        QuizQuestion("Does every game give free Rbx?", false),
        QuizQuestion("Can rewards be time limited?", true),
        QuizQuestion("Do all items cost Rbx?", false),

        QuizQuestion("Can players play with friends?", true),
        QuizQuestion("Is every reward unlimited?", false),
        QuizQuestion("Can events give bonus rewards?", true),
        QuizQuestion("Do all games support voice chat?", false),
        QuizQuestion("Can players collect coins?", true),

        QuizQuestion("Are cheats always allowed?", false),
        QuizQuestion("Can players level up?", true),
        QuizQuestion("Is internet optional online?", false),
        QuizQuestion("Can games have mini rewards?", true),
        QuizQuestion("Do rewards reset daily?", true),

        QuizQuestion("Can players unlock skins?", true),
        QuizQuestion("Is every item free?", false),
        QuizQuestion("Can bonuses expire?", true),
        QuizQuestion("Do all rewards cost money?", false),
        QuizQuestion("Can players earn game points?", true),
    )

    // ------------------------------------------------------------------------
    // Quiz
    // ------------------------------------------------------------------------
    private val selectedQuestions =
        QUIZ_QUESTIONS.shuffled().take(5)

    private var currentIndex = 0

    private val totalQuestions
        get() = selectedQuestions.size

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinish = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelQuestionImg()
        addCounterLbl()
        addQuestionLbl()
        addButtons()

        showQuestion(0)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelQuestionImg() {
        add(aPanelQuestionImg) { fillParent() }
    }

    private fun addCounterLbl() {
        addActor(aCounterLbl)
        aCounterLbl.setBounds(191f, 279f, 25f, 22f)
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(312f, 64f)
        add(aQuestionLbl) { centerX(); topToTop(margin = 180f) }

        aQuestionLbl.wrap = true
        aQuestionLbl.setAlignment(Align.center)
    }

    private fun addButtons() {
        addActor(aTrueBtn)
        aTrueBtn.setBounds(192f, 74f, 168f, 56f)

        addActor(aFalseBtn)
        aFalseBtn.setBounds(16f, 74f, 168f, 56f)

        aTrueBtn.setOnClickListener {
            onAnswer(true)
        }

        aFalseBtn.setOnClickListener {
            onAnswer(false)
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun showQuestion(index: Int) {
        if (index >= totalQuestions) {
            finishQuiz()
            return
        }

        currentIndex = index
        val question = selectedQuestions[index]
        aCounterLbl.setText("${index + 1}/$totalQuestions")
        aQuestionLbl.setText(question.question)
    }

    // лічильник правильних — іде в feature_complete наприкінці проходження
    private var correctCount = 0

    private fun onAnswer(userAnswer: Boolean) {
        val question = selectedQuestions[currentIndex]

        // Нагорода з конфігу: ключ = analyticsBlock екрана ("quiz_screen") —
        // рівно цей рядок вписуємо в картку апки на сервері.
        // Дефолт 10 = сьогоднішня поведінка. coins_earned шле сам Wallet.
        if (userAnswer == question.answer) {
            correctCount++
            Wallet.add(Econ.reward(BLOCK, 10), bt = Bt.QUIZ, block = BLOCK)
        } else {
            // Штраф з конфігу (economy.penalties.quiz_screen). Дефолт 0 —
            // сьогодні неправильна відповідь нічого не коштує, гілка холоста,
            // але ручка є. spend не пускає баланс у мінус.
            Wallet.spend(Econ.penalty(BLOCK, PENALTY_DEF), bt = Bt.QUIZ, block = BLOCK)
        }

        showQuestion(currentIndex + 1)
    }

    private fun finishQuiz() {
        aTrueBtn.disable()
        aFalseBtn.disable()

        // Ігровий цикл завершено, amount = число правильних відповідей.
        // Це ЄДИНИЙ сигнал «квіз реально пройшли до кінця».
        Events.featureComplete(bt = Bt.QUIZ, block = BLOCK, amount = correctCount)

        onFinish()
    }

    companion object {
        // Той самий рядок, що analyticsBlock у QuizScreen — ключ економіки й подій
        private const val BLOCK = "quiz_screen"
        private const val PENALTY_DEF = 0
    }
}