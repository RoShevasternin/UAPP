package com.diam.ondbit.game.actors.panel.quiz

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.button.base.AButtonAnim
import com.diam.ondbit.game.actors.button.base.AButtonStyles
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.progress.AProgressQuiz
import com.diam.ondbit.game.controller.QuizController
import com.diam.ondbit.game.utils.actor.animDelay
import com.diam.ondbit.game.utils.actor.disable
import com.diam.ondbit.game.utils.actor.enable
import com.diam.ondbit.game.utils.actor.setOnClickListener
import com.diam.ondbit.game.utils.actor.setSize
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame

class APanelQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontSpaceGrotesk_Bold, 18f)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinish: (totalReward: Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller = QuizController(
        questionCount    = 10,
        rewardPerCorrect = 15L,
    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Скільки видно реакцію на відповідь до наступного питання
    private val timeAnswer = 0.5f

    // Пауза після останнього питання — щоб гравець побачив 100%
    // до появи попапа
    private val timeShowResult = 0.6f

    private val timeQuestFade = 0.18f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_QUIZ)
    private val aTrueBtn  = AButtonAnim(screen, AButtonStyles.Anim.TRUE)
    private val aFalseBtn = AButtonAnim(screen, AButtonStyles.Anim.FALSE)
    private val aQuestLbl = AMsdfLabel("", styleDef)
    private val aProgress = AProgressQuiz(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addBtns()
        addQuestLbl()
        addProgress()

        initController()
        newGame()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        aBgImg.setSize(344f, 410f)
        add(aBgImg) { centerX(); bottomToBottom() }
    }

    private fun addBtns() {
        aTrueBtn.setSize(296f, 52f)
        aFalseBtn.setSize(296f, 52f)
        add(aFalseBtn) { centerX(); bottomToBottom(margin = 24f) }
        add(aTrueBtn) { centerX(); bottomToTop(aFalseBtn, 8f) }

        aTrueBtn.setOnClickListener  { controller.onAnswer(true) }
        aFalseBtn.setOnClickListener { controller.onAnswer(false) }
    }

    private fun addQuestLbl() {
        aQuestLbl.setSize(296f, 36f)
        add(aQuestLbl) { centerX(); topToTop(margin = 60f) }
        aQuestLbl.wrap = true
        aQuestLbl.setAlignment(Align.top, Align.center)
    }

    private fun addProgress() {
        aProgress.setSize(344f, 20f)
        add(aProgress) { centerX(); topToTop() }
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {

        // Нове питання — проявляємо текст і вмикаємо кнопки
        controller.onQuestion = { item, _ ->
            aQuestLbl.setText(item.question)

            aQuestLbl.clearActions()
            aQuestLbl.color.a = 0f
            aQuestLbl.addAction(Actions.fadeIn(timeQuestFade, Interpolation.pow2Out))

            setBtnsEnabled(true)
        }

        // Відповідь дана — блокуємо кнопки, тримаємо паузу
        controller.onResult = { isCorrect ->
            setBtnsEnabled(false)

            if (isCorrect) gdxGame.soundUtil.apply { play(REWARD) }

            // текст гасне, поки чекаємо наступне питання
            aQuestLbl.clearActions()
            aQuestLbl.addAction(Actions.sequence(
                Actions.delay(timeAnswer - timeQuestFade),
                Actions.fadeOut(timeQuestFade, Interpolation.pow2In)
            ))

            animDelay(timeAnswer) { controller.onResultFinished() }
        }

        // По 10% за кожну відповідь — правильну чи ні
        controller.onProgress = { percent ->
            aProgress.progressPercentFlow.value = percent
        }

        // Питання скінчились — віддаємо підсумок екрану
        controller.onFinish = { totalReward ->
            animDelay(timeShowResult) { onFinish(totalReward) }
        }
    }

    private fun setBtnsEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            aTrueBtn.enable()
            aFalseBtn.enable()
        } else {
            aTrueBtn.disable()
            aFalseBtn.disable()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Новий раунд: 10 випадкових питань з банку */
    fun newGame() {
        clearActions()
        controller.newGame()
    }
}