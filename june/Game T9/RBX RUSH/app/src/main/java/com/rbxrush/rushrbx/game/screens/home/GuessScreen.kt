package com.rbxrush.rushrbx.game.screens.home

import com.rbxrush.rushrbx.businesModule.economy.Econ
import com.rbxrush.rushrbx.businesModule.backend.Events
import com.rbxrush.rushrbx.businesModule.economy.Wallet
import com.rbxrush.rushrbx.businesModule.backend.Bt
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.adsmodule.AdSizeManager
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.actors.panel.APanelTop
import com.rbxrush.rushrbx.game.actors.panel.guess.APanelGuess
import com.rbxrush.rushrbx.game.actors.popup.APopupCongratulations
import com.rbxrush.rushrbx.game.actors.popup.APopupGuess
import com.rbxrush.rushrbx.game.utils.Block
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.TIME_ANIM_SCREEN
import com.rbxrush.rushrbx.game.utils.VERTICAL_BIAS
import com.rbxrush.rushrbx.game.utils.actor.animDelay
import com.rbxrush.rushrbx.game.utils.actor.animHide
import com.rbxrush.rushrbx.game.utils.actor.animHideAndDisable
import com.rbxrush.rushrbx.game.utils.actor.animShow
import com.rbxrush.rushrbx.game.utils.actor.animShowAndEnable
import com.rbxrush.rushrbx.game.utils.actor.disable
import com.rbxrush.rushrbx.game.utils.actor.enable
import com.rbxrush.rushrbx.game.utils.actor.setOnClickListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.overlay.OverlayManager
import com.rbxrush.rushrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class GuessScreen: AdvancedScreen() {

    companion object { private const val PRICE_DEF = 0 }

    override val analyticsBt    = Bt.REVEAL
    override val analyticsBlock = "guess_screen"

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
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aPanelQuess by lazy { APanelGuess(this) }
    private val aGetFreeBtn by lazy { Image(gdxGame.assetsAll.GUESS_MORE) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_60)) }
    private val aPopup  by lazy { APopupGuess(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.22f
    private val timeHide = 0.22f

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
    // Одна гра на візит (controller.initialize при створенні), тому «спроба» — вхід.
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
        addPanelQuess()
        addGetFreeBtn()

        addDimImg()
        addPopup()

        aPanelQuess.initialize()   // ← старт гри, коли все підключено
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

        aPanelTop.setTitle("RBX Finds")
    }

    private fun AConstraintLayout.addPanelQuess() {
        aPanelQuess.setSize(344f, 436f)
        add(aPanelQuess) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelQuess.onReward = { reward ->
            Wallet.add(reward.toInt(), bt = analyticsBt!!, block = analyticsBlock!!)
        }
        aPanelQuess.onResult = { wins, reward ->
            Events.featureComplete(bt = analyticsBt!!, block = analyticsBlock!!, amount = wins)
            aPopup.setReward(reward)
            aPopup.setMoreVisible(aPanelQuess.hasAdsLeft())   // ховаємо MORE якщо реклами скінчились
            overlayManager.show(Overlay.POPUP)
        }
        aPanelQuess.onGetFreeEnabled = { enabled ->
            if (enabled) aGetFreeBtn.enable() else aGetFreeBtn.disable()
        }
    }

    private fun AConstraintLayout.addGetFreeBtn() {
        aGetFreeBtn.setSize(344f, 132f)
        add(aGetFreeBtn) { centerX(); bottomToBottom(margin = 24f) }

        aGetFreeBtn.setOnClickListener {
            if (!aPanelQuess.canGetFree()) return@setOnClickListener

            gdxGame.activity.showInterstitial { aPanelQuess.addFreePicks() }
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aGetFreeBtn) { marginBottom += screen.adBottomUI } } } }

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
        aPopup.setSize(312f, 317f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        aPopup.onContinue = {
            overlayManager.close()
            animHideScreen { gdxGame.navigationManager.back() }
        }
        aPopup.onMore = {
            if (aPanelQuess.hasAdsLeft()) {
                overlayManager.close()
                gdxGame.activity.showInterstitial { aPanelQuess.addFreePicks() }
            }
        }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))
    }

}