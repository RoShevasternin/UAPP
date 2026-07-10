package com.sakurbx.fungambx.game.screens.home

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.button.AImagePinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.guess.APanelGuess
import com.sakurbx.fungambx.game.actors.popup.APopupSakura
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
    private val aGetFreeBtn by lazy { AImagePinkButton(this, TextureRegionDrawable(gdxGame.assetsAll.icon_btn_3), Vector2(167f, 25f)) }
    private val aDescImg    by lazy { Image(gdxGame.assetsAll.PANEL_QUESS) }

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
        addGetFreeBtn()
        addPanelQuess()
        addDesc()

        addDimImg()
        addPopup()

        aPanelQuess.initialize()
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

        aPanelTop.setTitle("RBX GUESSER")
    }

    private fun AConstraintLayout.addGetFreeBtn() {
        aGetFreeBtn.setSize(344f, 57f)
        add(aGetFreeBtn) { centerX(); bottomToBottom(margin = 32f) }

        aGetFreeBtn.setOnClickListener {
            if (!aPanelQuess.canGetFree()) return@setOnClickListener

            gdxGame.activity.showInterstitial { aPanelQuess.addFreePicks() }
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aGetFreeBtn) { marginBottom += screen.adBottomUI } } } }

    }

    private fun AConstraintLayout.addPanelQuess() {
        aPanelQuess.setSize(344f, 344f)
        add(aPanelQuess) { centerX(); topToBottom(aPanelTop); bottomToTop(aGetFreeBtn) }

        aPanelQuess.onReward = { reward ->
            gdxGame.modelPlayer.addRbx(reward)
        }
        aPanelQuess.onResult = { _, reward ->
            aPopup.setReward(reward)
            overlayManager.show(Overlay.POPUP)
        }
    }

    private fun AConstraintLayout.addDesc() {
        aDescImg.setSize(344f, 40f)
        add(aDescImg) { centerX(); bottomToTop(aPanelQuess, margin = 16f) }
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

        aPopup.onContinue = {
            overlayManager.close()
            animHideScreen { gdxGame.navigationManager.back() }
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