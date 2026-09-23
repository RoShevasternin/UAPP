package com.rbuxdrop.cougame.game.screens.main.quiz

import com.rbuxdrop.cougame.businesModule.backend.Bt
import com.rbuxdrop.cougame.businesModule.backend.Events
import com.rbuxdrop.cougame.businesModule.economy.Econ
import com.rbuxdrop.cougame.businesModule.economy.Wallet
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.APanelTop
import com.rbuxdrop.cougame.game.actors.panel.quiz.APanelPlayQuiz
import com.rbuxdrop.cougame.game.actors.panel.quiz.APanelStartQuiz
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class QuizStartScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.QUIZ
    override val analyticsBlock = "quiz_start_screen"

    companion object {
        // Ціна гри: сьогодні безкоштовно — ручка для сервера
        private const val PRICE_DEF   = 0
        // За правильну відповідь — як сьогодні
        private const val REWARD_DEF  = 5
        // Штраф за неправильну: сьогодні 0 (гілка холоста), але ручка є
        private const val PENALTY_DEF = 0
    }

    private var earned = 0

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)
    private val aPanelQuiz = APanelStartQuiz(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
        chargeEntryPrice()
    }

    // ── Ціна спроби ───────────────────────────────────────────────────────────
    // Один прохід квізу на візит (скидання немає) — «спроба» це вхід на екран.
    private fun chargeEntryPrice() {
        val price = Econ.price(analyticsBlock, PRICE_DEF)
        if (!Wallet.spend(price, bt = analyticsBt, block = analyticsBlock)) {
            gdxGame.activity.showToast("Not enough coins — you need $price")
            animHideScreen { gdxGame.navigationManager.back() }
        }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelQuiz()
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

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Quiz")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelQuiz() {
        aPanelQuiz.setSize(WIDTH, 446f)
        add(aPanelQuiz) { centerX(); topToBottom(aPanelTop) }

        aPanelQuiz.onAnswer = { isCorrect ->
            if (isCorrect) {
                val reward = Econ.reward(analyticsBlock, REWARD_DEF)
                Wallet.add(reward, bt = analyticsBt, block = analyticsBlock)
                earned += reward
            } else {
                Wallet.spend(Econ.penalty(analyticsBlock, PENALTY_DEF), bt = analyticsBt, block = analyticsBlock)
            }
        }
        aPanelQuiz.onFinish = {
            Events.featureComplete(bt = analyticsBt, block = analyticsBlock, amount = earned)
        }
    }

}