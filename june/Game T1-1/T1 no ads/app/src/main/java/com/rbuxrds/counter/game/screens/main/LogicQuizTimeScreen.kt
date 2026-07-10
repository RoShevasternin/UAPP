package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.button.base.AButtonStyles
import com.rbuxrds.counter.game.actors.button.base.AButtonTexture
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelLevel
import com.rbuxrds.counter.game.actors.panel.APanelQuestion
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counter.game.utils.actor.animDelay
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter
import com.rbuxrds.counter.game.utils.gdxGame

class LogicQuizTimeScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(36)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop      = APanelTop(this)
    private val aPanelLevel    = APanelLevel(this)
    private val aTextLbl       = ALabel(this, "", Color.WHITE, parameter, fontGenerator_InterTight_Bold)
    private val aPanelQuestion = APanelQuestion(this)
    private val aTrueBtn       = AButtonTexture(this, AButtonStyles.TRUE)
    private val aFalseBtn      = AButtonTexture(this, AButtonStyles.FALSE)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    data class QuizQuestion(
        val text    : String,
        val answer  : Boolean,  // true = правильна відповідь True, false = правильна відповідь False
    )

    val QUIZ_QUESTIONS = listOf(
        QuizQuestion("Theres no way to get Rbx?",                    false),  // False — є способи
        QuizQuestion("You can earn Rbx by selling game passes?",      true),
        QuizQuestion("Robux can be converted to real money?",         true),
        QuizQuestion("Free Robux generators are safe to use?",        false),
        QuizQuestion("You need Premium to create a Roblox game?",     false),
    )

    // ------------------------------------------------------------------------
    // Quiz state
    // ------------------------------------------------------------------------
    private var currentIndex = 0
    private val totalQuestions = QUIZ_QUESTIONS.size

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addPanelLevel()
        addTextLbl()
        addPanelQuestion()
        addBtns()

        // Показуємо перше питання
        showQuestion(0)

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
        aPanelTop.setTitle("Logic Quiz Time")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addBtns() {
        aTrueBtn.setSize(168f, 56f)
        addActorWithConstraints(aTrueBtn) {
            endToEndOf       = this@addBtns
            bottomToBottomOf = this@addBtns

            marginEnd    = 16f
            marginBottom = 33f
        }

        aFalseBtn.setSize(168f, 56f)
        addActorWithConstraints(aFalseBtn) {
            startToStartOf   = this@addBtns
            bottomToBottomOf = this@addBtns

            marginStart  = 16f
            marginBottom = 33f
        }

        aTrueBtn.setOnClickListener  { onAnswer(true)  }
        aFalseBtn.setOnClickListener { onAnswer(false) }

    }

    private fun Group.addPanelLevel() {
        aPanelLevel.setSize(73f, 36f)
        addActorWithConstraints(aPanelLevel) {
            startToStartOf   = this@addPanelLevel
            endToEndOf       = this@addPanelLevel
            topToBottomOf    = aPanelTop

            marginTop = 185f
        }
    }

    private fun Group.addTextLbl() {
        aTextLbl.setSize(344f, 88f)
        addActorWithConstraints(aTextLbl) {
            startToStartOf   = this@addTextLbl
            endToEndOf       = this@addTextLbl
            topToBottomOf    = aPanelLevel

            marginTop = 36f
        }

        aTextLbl.getLabelOrNull()?.let {
            it.setAlignment(Align.center)
            it.wrap = true
        }
    }

    private fun Group.addPanelQuestion() {
        aPanelQuestion.setSize(117f, 36f)
        addActorWithConstraints(aPanelQuestion) {
            startToStartOf = this@addPanelQuestion
            endToEndOf     = this@addPanelQuestion
            topToBottomOf = aTextLbl

            marginTop = 36f
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun showQuestion(index: Int) {
        if (index >= totalQuestions) return

        currentIndex = index
        val question = QUIZ_QUESTIONS[index]

        aTextLbl.setText(question.text)
        aPanelLevel.setLevel(index + 1)
        aPanelQuestion.setQuestionNum(index + 1)
    }

    private fun onAnswer(userAnswer: Boolean) {
        showQuestion(currentIndex + 1)
    }

}