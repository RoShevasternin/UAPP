package com.mon.sterbx.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.button.AOrangeButton
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelTop
import com.mon.sterbx.game.actors.popup.APopup
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.VERTICAL_BIAS
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animHideAndDisable
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.actor.animShowAndEnable
import com.mon.sterbx.game.utils.actor.setOnClickListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.overlay.OverlayManager
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.launch

class FreeScreen: AdvancedScreen() {

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
    private val aPanelTop  by lazy { APanelTop(this) }
    private val aPanelFree by lazy { Image(gdxGame.assetsAll.PANEL_FREE) }
    private val aFreeBtn   by lazy { AOrangeButton(this, "CLAIM") }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_75)) }
    private val aPopup  by lazy { APopup(this) }

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
        setBackground(gdxGame.assetsAll.BACKGROUND_YELLOW)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addFreeBtn()
        addPanelFree()

        addDimImg()
        addPopup()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 42f)
        add(aPanelTop) { centerX(); topToTop(margin = 12f) }

        aPanelTop.setTitle("FREE COINS")
    }

    private fun AConstraintLayout.addFreeBtn() {
        aFreeBtn.setSize(344f, 64f)
        add(aFreeBtn) { centerX(); bottomToBottom(margin = 36f) }

        aFreeBtn.setOnClickListener {
            gdxGame.activity.showInterstitial {
                gdxGame.modelPlayer.addRbx(200)
                aPopup.setReward(200)
                overlayManager.show(Overlay.POPUP)
            }
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aFreeBtn) { marginBottom = screen.adBottomUI + 36f } } } }

    }

    private fun AConstraintLayout.addPanelFree() {
        aPanelFree.setSize(351f, 451f)
        add(aPanelFree) { centerX(); topToBottom(aPanelTop); bottomToTop(aFreeBtn) }
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
        aPopup.setSize(344f, 368f)
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