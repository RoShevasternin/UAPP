package com.zahbx.blitzrbx.game.screens.main

import com.zahbx.blitzrbx.businesModule.economy.Econ
import com.zahbx.blitzrbx.businesModule.backend.Events
import com.zahbx.blitzrbx.businesModule.economy.Wallet
import com.zahbx.blitzrbx.businesModule.backend.Bt
import com.zahbx.blitzrbx.game.actors.AWheel
import com.zahbx.blitzrbx.game.actors.button.AGreenButton
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.panel.APanelRBX
import com.zahbx.blitzrbx.game.actors.panel.APanelTop
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.NumberFormatter
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.game.utils.runGDX
import com.zahbx.blitzrbx.util.log
import kotlinx.coroutines.launch

class SpinWinScreen: AdvancedScreen() {

    companion object { private const val PRICE_DEF = 0 }

    override val analyticsBt    = Bt.SPIN
    override val analyticsBlock = "spin_win_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop   = APanelTop(this)
    private val aWheel      = AWheel(this)
    private val aPanelRBX   = APanelRBX(this)
    private val aGreenBtn   = AGreenButton(this, "Spin Now")

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addWheel()
        addPanelRBX()
        addGreenBtn()
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

        aPanelTop.setTitle("Spin & Win")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(405f, 405f)
        add(aWheel) {
            centerX()
            topToBottom(aPanelTop, 50f)
        }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(105f, 48f)
        add(aPanelRBX) {
            topToBottom(aPanelTop, 16f)
            endToEnd(margin = 16f)
        }

        coroutine?.launch {
            Wallet.balanceFlow.collect { rbx ->
                runGDX {
                    val rbxFormat = NumberFormatter.format(rbx)
                    aPanelRBX.setText(rbxFormat)
                }
            }
        }
    }

    private fun AConstraintLayout.addGreenBtn() {
        aGreenBtn.setSize(344f, 60f)
        add(aGreenBtn) {
            centerX()
            topToBottom(aWheel, -7f)
        }

        aGreenBtn.setOnClickListener {
            // Ціна спроби з конфігу (economy.prices.spin_win_screen).
            // Дефолт 0 = спін безкоштовний сьогодні; це ручка, не «вимкнено».
            val price = Econ.price(analyticsBlock!!, PRICE_DEF)
            if (!Wallet.spend(price, bt = analyticsBt!!, block = analyticsBlock!!)) {
                gdxGame.activity.showToast("Not enough coins — you need $price")
                return@setOnClickListener
            }

            aWheel.spin { result ->
                log("result = $result")
                // ⚠️ Суму дає aWheel.payout, а НЕ result.sum: номінали секторів
                // їдуть списком economy.rewards_list.spin. boost ×2 — своя механіка
                // апки (BoostModeScreen), застосовується ДО нарахування.
                val win = gdxGame.modelPlayer.boosted(aWheel.payout(result))
                Wallet.add(win, bt = analyticsBt!!, block = analyticsBlock!!)
                Events.featureComplete(bt = analyticsBt!!, block = analyticsBlock!!, amount = win)
            }
        }

    }

}