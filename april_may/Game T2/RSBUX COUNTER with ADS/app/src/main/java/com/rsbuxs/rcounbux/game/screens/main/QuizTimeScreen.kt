package com.rsbuxs.rcounbux.game.screens.main

import com.rsbuxs.rcounbux.businesModule.economy.Wallet
import com.rsbuxs.rcounbux.businesModule.economy.Econ
import com.rsbuxs.rcounbux.businesModule.backend.Bt
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.panel.APanelTop
import com.rsbuxs.rcounbux.game.actors.panel.quiz.APanelQuiz
import com.rsbuxs.rcounbux.game.utils.Block
import com.rsbuxs.rcounbux.game.utils.TIME_ANIM_SCREEN
import com.rsbuxs.rcounbux.game.utils.actor.animDelay
import com.rsbuxs.rcounbux.game.utils.actor.animHide
import com.rsbuxs.rcounbux.game.utils.actor.animShow
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class QuizTimeScreen: AdvancedScreen() {

    companion object { private const val PRICE_DEF = 0 }

    override val analyticsBt    = Bt.QUIZ
    override val analyticsBlock = "quiz_time_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop      = APanelTop(this)
    private val aPanelQuiz     = APanelQuiz(this)

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
    // Один прохід квізу на візит — «спроба» це вхід на екран.
    // Дефолт 0: Wallet.spend(0) повертає true одразу і подій не шле — сьогодні
    // поведінка не змінюється, але сервер може увімкнути ціну через
    // economy.prices без релізу.
    private fun chargeEntryPrice() {
        val price = Econ.price(analyticsBlock!!, PRICE_DEF)
        if (!Wallet.spend(price, bt = analyticsBt!!, block = analyticsBlock!!)) {
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
        aPanelTop.setSize(376f, 56f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }

        aPanelTop.setTitle("Quiz Time")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelQuiz() {
        aPanelQuiz.setSize(376f, 308f)
        add(aPanelQuiz) {
            centerX()
            topToBottom(aPanelTop)
        }
    }

}