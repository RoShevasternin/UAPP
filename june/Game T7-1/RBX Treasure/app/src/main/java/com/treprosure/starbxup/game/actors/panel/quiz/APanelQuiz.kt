package com.treprosure.starbxup.game.actors.panel.quiz

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.treprosure.starbxup.game.actors.checkbox.base.ACheckBox
import com.treprosure.starbxup.game.actors.checkbox.base.ACheckBoxStyles
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.actor.disable
import com.treprosure.starbxup.game.utils.actor.setOnClickListener
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(32)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listTab      = List(5) { ACheckBox(screen, ACheckBoxStyles.QUIZ_TAB) }
    private val aBgImg       = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aQuestionLbl = Label("Q", FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.beige_E2CEAA))
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
        addListTab()
        addBgImg()
        addQuestionLbl()
        addBtns()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addListTab() {
        var nx = 0f
        listTab.forEach { tab ->
            addActor(tab)
            tab.setBounds(nx, 347f, 65f, 12f)
            nx += 4f + 65f

            tab.disable()
        }
    }

    private fun addBgImg() {
        aBgImg.setSize(344f, 339f)
        add(aBgImg) { centerX(); bottomToBottom() }
    }

    private fun addQuestionLbl() {
        aQuestionLbl.setSize(312f, 190f)
        add(aQuestionLbl) { centerX(); topToTop(margin = 65f) }
        aQuestionLbl.wrap = true
        aQuestionLbl.setAlignment(Align.center)
    }

    private fun addBtns() {
        aFalseBtn.setSize(168f, 51f)
        add(aFalseBtn) { startToStart(); bottomToBottom() }
        aTrueBtn.setSize(168f, 51f)
        add(aTrueBtn) { endToEnd(); bottomToBottom() }

        aFalseBtn.setOnClickListener { controller.answer(false) }
        aTrueBtn.setOnClickListener  { controller.answer(true) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
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