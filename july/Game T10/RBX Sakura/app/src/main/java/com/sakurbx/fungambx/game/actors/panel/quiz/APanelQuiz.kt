package com.sakurbx.fungambx.game.actors.panel.quiz

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBox
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxStyles
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.actor.disable
import com.sakurbx.fungambx.game.utils.actor.setOnClickListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.font.setDoubleShadow
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterQuestion = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(28)
        .setDoubleShadow()
    private val parameterCounter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)

    private val lsQuestion = FontFactory.create(screen, parameterQuestion, screen.fontGenerator_Laila_Bold, GameColor.beige_FFFAD3)
    private val lsCounter  = FontFactory.create(screen, parameterCounter, screen.fontGenerator_Laila_Bold, GameColor.purple_9A006C)
    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg       = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aQuestionLbl = Label("Q", lsQuestion)
    private val aCounterLbl  = Label("1/7", lsCounter)
    private val aFalseBtn    = Actor()
    private val aTrueBtn     = Actor()

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy { QuizController(totalQuestions = 7) }

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
        addBtns()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addCounterLbl() {
        aCounterLbl.setSize(30f, 17f)
        add(aCounterLbl) { centerX(); bottomToBottom(margin = 111f) }
        aCounterLbl.setAlignment(Align.center)
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(300f, 260f)
        add(aQuestionLbl) { centerX(); topToTop(margin = 60f) }
        aQuestionLbl.wrap = true
        aQuestionLbl.setAlignment(Align.center)
    }

    private fun addBtns() {
        aFalseBtn.setSize(168f, 57f)
        add(aFalseBtn) { startToStart(); bottomToBottom() }
        aTrueBtn.setSize(168f, 57f)
        add(aTrueBtn) { endToEnd(); bottomToBottom() }

        aFalseBtn.setOnClickListener { controller.answer(false) }
        aTrueBtn.setOnClickListener  { controller.answer(true) }
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