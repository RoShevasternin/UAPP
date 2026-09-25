package com.sakurbx.fungambx.game.screens.home

import com.sakurbx.fungambx.businesModule.economy.Econ
import com.sakurbx.fungambx.businesModule.backend.Events
import com.sakurbx.fungambx.businesModule.economy.Wallet
import com.sakurbx.fungambx.businesModule.backend.Bt
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.button.APinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.popup.APopupSakura
import com.sakurbx.fungambx.game.actors.panel.wheel.AWheel
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.VERTICAL_BIAS
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animHideAndDisable
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.actor.animShowAndEnable
import com.sakurbx.fungambx.game.utils.actor.setOnClickListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.overlay.OverlayManager
import com.sakurbx.fungambx.game.utils.runGDX
import com.sakurbx.fungambx.util.log
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
    private val aSpinBtn  by lazy { APinkButton(this, "SPIN NOW") }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_60)) }
    private val aPopup  by lazy { APopupSakura(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.2f
    private val timeHide = 0.2f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_PUPRLE)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addWheel()
        addDesc()
        addSpinBtn()

        addDimImg()
        addPopup()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 56f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }

        aPanelTop.setTitle("LUCKY WHEEL")
    }

    private fun AConstraintLayout.addSpinBtn() {
        aSpinBtn.setSize(344f, 57f)
        add(aSpinBtn) { centerX(); bottomToBottom(margin = 32f) }

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
            AdSizeManager.adBottomFlow.collect { runGDX { update(aSpinBtn) { marginBottom = 32f + screen.adBottomUI.coerceAtLeast(0f) } } }
        }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(344f, 344f)
        add(aWheel) { centerX(); topToBottom(aPanelTop); bottomToTop(aSpinBtn) }
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(266f, 20f)
        add(aDesc) { centerX(); bottomToTop(aWheel, margin = 16f) }
    }

    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) {
            matchConstraint()
            centerX(); bottomToBottom(); topToTop(margin = -safeStatusBarUI)
        }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.animHideAndDisable()
        aPopup.setSize(344f, 374f)
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