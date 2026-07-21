package com.mon.sterbx.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelTopHome
import com.mon.sterbx.game.actors.panel.home.APanelHome
import com.mon.sterbx.game.actors.popup.daily.APopupDaily
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
import com.mon.sterbx.services.analytics.AnalyticsManager
import kotlinx.coroutines.launch

class HomeScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop       by lazy { APanelTopHome(this) }
    private val aPanelHome by lazy { APanelHome(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_75)) }
    private val aPopup  by lazy { APopupDaily(this) }

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
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addPanelHome()

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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(350f, 47f)
        add(aTop) { centerX(); topToTop(margin = 8f) }
    }

    private fun AConstraintLayout.addPanelHome() {
        aPanelHome.width = 345f
        add(aPanelHome) { centerX(); topToBottom(aTop, 8f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelHome) { marginBottom = screen.adBottomUI + 36f } } } }

        aPanelHome.onDaily = { overlayManager.show(Overlay.POPUP) }
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
        aPopup.setSize(349f, 340f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        aPopup.onClose = { overlayManager.close() }

        overlayManager.register(Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = true,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))
    }

}