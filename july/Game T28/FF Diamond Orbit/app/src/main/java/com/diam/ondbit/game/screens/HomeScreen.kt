package com.diam.ondbit.game.screens

import com.diam.ondbit.adsmodule.AdSizeManager
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTopHome
import com.diam.ondbit.game.actors.panel.home.APanelHome
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import com.diam.ondbit.services.analytics.AnalyticsManager
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
        setBackground(gdxGame.assetsLoader.BACKGROUND)

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