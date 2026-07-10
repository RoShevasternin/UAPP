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
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterQuestion = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(32)
    private val parameterCounter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    private val lsQuestion = FontFactory.create(screen, parameterQuestion, screen.fontGenerator_Laila_Bold)
    private val lsCounter = FontFactory.create(screen, parameterCounter, screen.fontGenerator_Laila_Bold, GameColor.brown_B95A02)
    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listTab      = List(5) { ACheckBox(screen, ACheckBoxStyles.QUIZ_TAB) }
    private val aBgImg       = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aQuestionLbl = Label("Q", lsQuestion)
    private val aCounterLbl  = Label("1 out of 5", lsCounter)
    private val aFalseBtn    = Actor()
    private val aTrueBtn     = Actor()

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy { QuizController(totalQuestions = listTab.size) }

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
        addListTab()
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

    private fun addListTab() {
        var nx = 8f
        listTab.forEach { tab ->
            addActor(tab)
            tab.setBounds(nx, 215f, 63f, 8f)
            nx += 4f + 63f

            tab.disable()
        }
    }

    private fun addCounterLbl() {
        aCounterLbl.setSize(52f, 13f)
        add(aCounterLbl) { centerX(); topToTop(margin = 38f) }   // підбери margin
        aCounterLbl.setAlignment(Align.center)
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(312f, 115f)
        add(aQuestionLbl) { centerX(); topToTop(margin = 91f) }
        aQuestionLbl.wrap = true
        aQuestionLbl.setAlignment(Align.center)
    }

    private fun addBtns() {
        aFalseBtn.setSize(152f, 44f)
        add(aFalseBtn) { startToStart(margin = 16f); bottomToBottom(margin = 16f) }
        aTrueBtn.setSize(152f, 44f)
        add(aTrueBtn) { endToEnd(margin = 16f); bottomToBottom(margin = 16f) }

        aFalseBtn.setOnClickListener { controller.answer(false) }
        aTrueBtn.setOnClickListener  { controller.answer(true) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
        controller.onProgress = { current, total ->
            aCounterLbl.setText("$current out of $total")
        }
        controller.onQuestion = { index, text ->
            updateTabs(index)
            aQuestionLbl.setText(text)
        }
        controller.onCorrect  = { onCorrect(it) }
        controller.onFinished = { correct, reward -> onFinished(correct, reward) }
        controller.initialize()
    }

    private fun updateTabs(index: Int) {
        listTab.forEachIndexed { i, tab ->
            if (i <= index) tab.check() else tab.uncheck()
        }
    }

}