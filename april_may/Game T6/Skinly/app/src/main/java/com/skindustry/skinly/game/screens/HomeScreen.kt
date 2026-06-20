package com.skindustry.skinly.game.screens

import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.ABottomPanelHome
import com.skindustry.skinly.game.actors.panel.APanelTopHome
import com.skindustry.skinly.game.actors.panel.blokcy.APanelSelectBlokcy
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import com.skindustry.skinly.services.analytics.AnalyticsManager
import com.skindustry.skinly.util.log
import kotlinx.coroutines.launch

class HomeScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aTop               by lazy { APanelTopHome(this) }
    private val aPanelSelectBlokcy by lazy { APanelSelectBlokcy(this) }
    private val aBottomPanel       by lazy { ABottomPanelHome(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addPanelSelectBlokcy()
        addBottomPanel()
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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(WIDTH, 68f)
        add(aTop) { centerX(); topToTop() }
    }

    private fun AConstraintLayout.addPanelSelectBlokcy() {
        aPanelSelectBlokcy.height = 509f
        add(aPanelSelectBlokcy) {
            centerX(); topToBottom(aTop)
            matchWidth()
        }
    }

    private fun AConstraintLayout.addBottomPanel() {
        aBottomPanel.height = 74f
        add(aBottomPanel) {
            centerX(); bottomToBottom()
            matchWidth()
        }
        aBottomPanel.check(ABottomPanelHome.Type.HOME)

        aBottomPanel.onTabChanged = { type ->
            when(type) {
                ABottomPanelHome.Type.HOME      -> { log("bottom: HOME") }
                ABottomPanelHome.Type.SKIN_BOOK -> { animHideScreen { gdxGame.navigationManager.navigate(SkinBookScreen::class.java.name, HomeScreen::class.java.name) } }
            }
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomPanel) { marginBottom += screen.adBottomUI } } }
        }
    }

}