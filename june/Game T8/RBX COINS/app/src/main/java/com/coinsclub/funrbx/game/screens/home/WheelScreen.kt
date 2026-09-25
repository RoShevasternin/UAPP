package com.coinsclub.funrbx.game.screens.home

import com.coinsclub.funrbx.businesModule.economy.Econ
import com.coinsclub.funrbx.businesModule.backend.Events
import com.coinsclub.funrbx.businesModule.economy.Wallet
import com.coinsclub.funrbx.businesModule.backend.Bt
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.button.AYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.popup.APopupCongratulations
import com.coinsclub.funrbx.game.actors.panel.wheel.AWheel
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.VERTICAL_BIAS
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animHideAndDisable
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.actor.animShowAndEnable
import com.coinsclub.funrbx.game.utils.actor.setOnClickListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.overlay.OverlayManager
import com.coinsclub.funrbx.game.utils.runGDX
import com.coinsclub.funrbx.util.log
import kotlinx.coroutines.launch

class WheelScreen: AdvancedScreen() {

    companion object { private const val PRICE_DEF = 0 }

    override val analyticsBt    = Bt.SPIN
    override val analyticsBlock = "wheel_screen"

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.clearActions(); aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.clearActions(); aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop by lazy { APanelTop(this) }
    private val aWheel    by lazy { AWheel(this) }
    private val aDesc     by lazy { Image(gdxGame.assetsAll.WHEEL_DESC) }
    private val aSpinBtn  by lazy { AYellowButton(this, "SPIN NOW") }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_60)) }
    private val aPopup  by lazy { APopupCongratulations(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.25f
    private val timeHide = 0.20f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addDesc()
        addWheel()
        addSpinBtn()

        addDimImg()
        addPopup()
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
        aPanelTop.height = 72f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("SPIN WHEEL")
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(345f, 86f)
        add(aDesc) { centerX(); topToBottom(aPanelTop, margin = 16f) }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(426f, 426f)
        add(aWheel) { centerX(); topToBottom(aDesc, -10f) }
    }

    private fun AConstraintLayout.addSpinBtn() {
        aSpinBtn.setSize(345f, 57f)
        add(aSpinBtn) { centerX(); bottomToBottom(margin = 30f) }

        aSpinBtn.setOnClickListener {
            // Ціна спроби з конфігу. Дефолт 0 = спін безкоштовний сьогодні;
            // ключ economy.prices.wheel_screen — ручка без релізу.
            val price = Econ.price(analyticsBlock!!, PRICE_DEF)
            if (!Wallet.spend(price, bt = analyticsBt!!, block = analyticsBlock!!)) {
                gdxGame.activity.showToast("Not enough coins — you need $price")
                return@setOnClickListener
            }

            aSpinBtn.disable()

            aWheel.spin { result ->
                log("result = $result")
                aSpinBtn.enable()
                // ⚠️ Суму дає aWheel.payout, а НЕ result.sum: номінали секторів
                // їдуть списком economy.rewards_list.wheel. Показуємо й нараховуємо
                // ОДНЕ число. coins_earned шле сам Wallet.
                val win = aWheel.payout(result)
                Wallet.add(win.toInt(), bt = analyticsBt!!, block = analyticsBlock!!)
                Events.featureComplete(bt = analyticsBt!!, block = analyticsBlock!!, amount = win.toInt())

                aPopup.setReward(win)
                overlayManager.show(Overlay.POPUP)
            }
        }

        // «=», не «+=»: adBottomFlow — StateFlow, шле на кожну зміну — інакше накопичується
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aSpinBtn) { marginBottom = 30f + screen.adBottomUI.coerceAtLeast(0f) } } }
        }

    }

    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) { fillParent() }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.animHideAndDisable()
        aPopup.setSize(344f, 220f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        aPopup.onContinue = { overlayManager.close() }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))
    }

}