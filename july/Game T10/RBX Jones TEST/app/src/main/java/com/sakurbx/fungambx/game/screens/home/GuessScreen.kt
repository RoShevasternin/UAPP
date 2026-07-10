package com.sakurbx.fungambx.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.guess.APanelGuess
import com.sakurbx.fungambx.game.actors.popup.APopupCongratulations
import com.sakurbx.fungambx.game.actors.popup.APopupGuess
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.VERTICAL_BIAS
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animHideAndDisable
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.actor.animShowAndEnable
import com.sakurbx.fungambx.game.utils.actor.disable
import com.sakurbx.fungambx.game.utils.actor.enable
import com.sakurbx.fungambx.game.utils.actor.setOnClickListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.overlay.OverlayManager
import com.sakurbx.fungambx.game.utils.runGDX
import kotlinx.coroutines.launch

class GuessScreen: AdvancedScreen() {

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
            gdxGame.modelPlayer.addRbx(reward)
        }
        aPanelQuess.onResult = { _, reward ->
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