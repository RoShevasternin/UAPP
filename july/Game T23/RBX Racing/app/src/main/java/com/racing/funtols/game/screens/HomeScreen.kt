package com.racing.funtols.game.screens

import com.racing.funtols.adsmodule.AdSizeManager
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTopHome
import com.racing.funtols.game.actors.panel.home.APanelHome
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.runGDX
import com.racing.funtols.services.analytics.AnalyticsManager
import kotlinx.coroutines.launch

class HomeScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop       by lazy { APanelTopHome(this) }
    private val aPanelHome by lazy { APanelHome(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f

        super.show()
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addPanelHome()
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
        aTop.setSize(344f, 50f)
        add(aTop) { centerX(); topToTop(margin = 16f) }
    }

    private fun AConstraintLayout.addPanelHome() {
        aPanelHome.width = 345f
        add(aPanelHome) { centerX(); topToBottom(aTop, 8f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelHome) { marginBottom = screen.adBottomUI + 36f } } } }
    }

}