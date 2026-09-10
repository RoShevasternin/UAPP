package com.rsbuxs.rcounbux.game.screens.main

import com.rsbuxs.rcounbux.businesModule.economy.Econ
import com.rsbuxs.rcounbux.businesModule.backend.Events
import com.rsbuxs.rcounbux.businesModule.economy.Wallet
import com.rsbuxs.rcounbux.businesModule.backend.Bt
import com.badlogic.gdx.math.Vector2
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.panel.APanelTop
import com.rsbuxs.rcounbux.game.actors.scratch.AScratch
import com.rsbuxs.rcounbux.game.utils.Block
import com.rsbuxs.rcounbux.game.utils.TIME_ANIM_SCREEN
import com.rsbuxs.rcounbux.game.utils.actor.animDelay
import com.rsbuxs.rcounbux.game.utils.actor.animHide
import com.rsbuxs.rcounbux.game.utils.actor.animShow
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import com.rsbuxs.rcounbux.util.log

class ScratchScreen: AdvancedScreen() {

    companion object { private const val PRICE_DEF = 0 }

    override val analyticsBt    = Bt.GRID
    override val analyticsBlock = "scratch_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop = APanelTop(this)
    private val aScratch  = AScratch(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
        chargeEntryPrice()
    }

    // ── Ціна спроби ───────────────────────────────────────────────────────────
    // Картка одна на візит (regenerateScratch ніде не зветься) — «спроба» це вхід.
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


    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addScratch()
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

        aPanelTop.setTitle("Lucky Scratch")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(345f, 345f)
        add(aScratch) {
            centerX()
            topToBottom(aPanelTop, 16f)
        }

        aScratch.onResult = { result ->
            log("aScratch result: $result")
            // ⚠️ Суму дає aScratch.payout — рівно те число, що на стертій картці
            val win = gdxGame.modelPlayer.boosted(aScratch.payout(result))
            Wallet.add(win, bt = analyticsBt!!, block = analyticsBlock!!)
            Events.featureComplete(bt = analyticsBt!!, block = analyticsBlock!!, amount = win)
        }
    }

}