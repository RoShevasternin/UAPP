package com.bossrbx.rbxcalculator.game.screens.main

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.businesModule.backend.Bt
import com.bossrbx.rbxcalculator.businesModule.backend.Events
import com.bossrbx.rbxcalculator.businesModule.economy.Econ
import com.bossrbx.rbxcalculator.businesModule.economy.Wallet
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelRS
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.actors.panel.wheel.APanelSpin
import com.bossrbx.rbxcalculator.game.actors.panel.wheel.AWheel
import com.bossrbx.rbxcalculator.game.actors.popup.APopup
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.actor.disable
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.util.log

class WheelScreen: AdvancedScreen() {

    // правка 4: розріз аналітики цього екрана. block після релізу НЕ
    // перейменовується — старі дані лишились би під старим ім'ям.
    override val analyticsBt    = Bt.SPIN
    override val analyticsBlock = "wheel_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop   by lazy { APanelTop(this) }
    private val aWheel      by lazy { AWheel(this) }
    private val aPanelSpin  by lazy { APanelSpin(this) }
    private val aPanelRBX   by lazy { APanelRS(this) }
    private val aSpinBtn    by lazy { ABlueButton(this, "Spin Now") }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_90)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        gdxGame.activity.hideBanner()

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.showBanner()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addWheel()
        addPanelSpin()
        addPanelRBX()
        addSpinBtn()
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
        aPanelTop.setSize(WIDTH, 64f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Lucky Wheel")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(412f, 418f)
        add(aWheel) { centerX(); topToBottom(aPanelTop, -14f) }
    }

    private fun AConstraintLayout.addPanelSpin() {
        aPanelSpin.setSize(344f, 180f)
        add(aPanelSpin) { centerX(); topToBottom(aWheel, -7f) }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(61f, 32f)
        add(aPanelRBX) { centerX(aPanelSpin);bottomToBottom(aPanelSpin, 12f) }
    }

    private fun AConstraintLayout.addSpinBtn() {
        aSpinBtn.setSize(344f, 64f)
        add(aSpinBtn) { centerX(); topToBottom(aPanelSpin, 20f) }

        aSpinBtn.setOnClickListener {
            if (!aPanelSpin.isSpin) return@setOnClickListener

            // Ціна спроби. Дефолт 0 = сьогоднішня поведінка (спін безкоштовний,
            // ліміт тримає aPanelSpin). Число в картці вмикає платний спін без релізу.
            val price = Econ.price(analyticsBlock, PRICE_DEF)
            if (!Wallet.spend(price, bt = analyticsBt, block = analyticsBlock)) {
                gdxGame.activity.showToast("Not enough coins — you need $price")
                return@setOnClickListener
            }

            aPanelSpin.markSpin()
            aSpinBtn.disable()

            aWheel.spin { result ->
                log("result = $result")
                if (aPanelSpin.isSpin) aSpinBtn.enable()

                // ⚠️ Суму дає aWheel.payout, а НЕ result.sum: номінали секторів
                // їдуть списком economy.rewards_list.spin_wheel. Показуємо і
                // нараховуємо ОДНЕ І ТЕ САМЕ число.
                // coins_earned шле сам Wallet.add — другий виклик поруч був би дублем.
                val win = aWheel.payout(result)
                Wallet.add(win, bt = analyticsBt, block = analyticsBlock)

                // правка 4: ігровий цикл завершено, amount = виграш
                Events.featureComplete(bt = analyticsBt, block = analyticsBlock, amount = win)

                showDialog(win.toLong())
            }
        }

    }

    companion object {
        // Фолбек = сьогоднішня поведінка апки. Спін безкоштовний, тому 0 —
        // чесний дефолт: порожня картка нічого не змінює.
        private const val PRICE_DEF = 0
    }

    private fun AConstraintLayout.showDialog(reward: Long) {
        aPopup.onClaim = {
            aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            aPopup.animHideAndDisable(0.15f) { aPopup.isDisposeOnRemove = false; aPopup.remove() }
        }

        aDimImg.animHideAndDisable()
        aPopup.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aPopup.setSize(344f, 340f)
        add(aPopup) {
            center()
            verticalBias = 0.70f
        }

        aPopup.setReward(reward)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aPopup.animShowAndEnable(TIME_ANIM_SCREEN)

        gdxGame.soundUtil.apply { play(WIN) }
    }

}