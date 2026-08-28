package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.businesModule.backend.Events
import com.rbxhubpro.rohumex.businesModule.economy.Wallet
import com.rbxhubpro.rohumex.businesModule.economy.Econ
import com.rbxhubpro.rohumex.game.actors.button.base.AButtonStyles
import com.rbxhubpro.rohumex.game.actors.button.base.AButtonTexture
import com.rbxhubpro.rohumex.game.actors.label.ALabel
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelLevel
import com.rbxhubpro.rohumex.game.actors.panel.APanelQuestion
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.font.FontParameter
import com.rbxhubpro.rohumex.game.utils.gdxGame

class LogicQuizTimeScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.QUIZ
    override val analyticsBlock = "logic_quiz_time_screen"

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

    // счёт верных ответов — уходит в feature_complete в конце прохождения
    private var correctCount = 0

    // БАГ БЕЗ ЭТОГО ФЛАГА: showQuestion(5) на последнем вопросе просто делал
    // return, экран оставался с 5-м вопросом, а кнопки жили. Каждый следующий
    // тап снова попадал в onAnswer с тем же currentIndex — бесконечная ферма
    // ±10 монет и дубли feature_complete на каждый клик.
    private var finished = false
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
            marginBottom = adBannerUI //33f
        }

        aFalseBtn.setSize(168f, 56f)
        addActorWithConstraints(aFalseBtn) {
            startToStartOf   = this@addBtns
            bottomToBottomOf = this@addBtns

            marginStart  = 16f
            marginBottom = adBannerUI //33f
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
        if (index >= totalQuestions) {
            if (finished) return          // страховка от повторного входа
            finished = true

            // Гасим кнопки: без этого экран остаётся «живым» после последнего
            // вопроса и монеты фармятся кликами по мёртвому вопросу.
            aTrueBtn.touchable  = Touchable.disabled
            aFalseBtn.touchable = Touchable.disabled

            // правка 4: цикл завершён, amount = число верных ответов.
            // Это ЕДИНСТВЕННЫЙ сигнал «квиз реально прошли до конца» —
            // без него в отчёте видно только что экран открывали.
            // Флаг finished гарантирует РОВНО одно событие на прохождение.
            Events.featureComplete(bt = analyticsBt, block = analyticsBlock, amount = correctCount)

            gdxGame.activity.showToast("Quiz complete: $correctCount / $totalQuestions")
            return
        }

        currentIndex = index
        val question = QUIZ_QUESTIONS[index]

        aTextLbl.setText(question.text)
        aPanelLevel.setLevel(index + 1)
        aPanelQuestion.setQuestionNum(index + 1)
    }

    private fun onAnswer(userAnswer: Boolean) {
        // правка 5: квиз подключён к экономике — верный ответ = награда,
        // неверный = штраф. Числа из конфига (economy.rewards/penalties),
        // ключ = analyticsBlock, то есть "logic_quiz_time_screen" — ровно эту
        // строку мы вписываем в карточку приложения на сервере; разойдётся с
        // кодом — крутилка экономики будет крутить воздух.
        // Дефолт 10/10 — канон парка (rbuxcounter: 10/-10).
        // coins_earned/coins_spent шлёт сам Wallet; штраф через spend — при
        // пустом балансе списания (и события) нет, в минус не уходим.
        if (finished) return   // квиз пройден — клики больше не платят

        QUIZ_QUESTIONS.getOrNull(currentIndex)?.let { q ->
            if (userAnswer == q.answer) {
                correctCount++
                Wallet.add(Econ.reward(analyticsBlock, 10), bt = analyticsBt, block = analyticsBlock)
            } else {
                Wallet.spend(Econ.penalty(analyticsBlock, 10), bt = analyticsBt, block = analyticsBlock)
            }
        }
        showQuestion(currentIndex + 1)
    }

}