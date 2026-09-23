package com.skindustry.skinly.game.screens

import com.skindustry.skinly.businesModule.backend.Bt
import com.skindustry.skinly.game.actors.button.AGreenButton
import com.skindustry.skinly.game.utils.Onboarding
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

    override val analyticsBt    = Bt.HUB
    override val analyticsBlock = "home_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aTop               by lazy { APanelTopHome(this) }
    private val aFreeRewardsBtn    by lazy { AGreenButton(this, "FREE COINS") }
    private val aPanelSelectBlokcy by lazy { APanelSelectBlokcy(this) }
    private val aBottomPanel       by lazy { ABottomPanelHome(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        Onboarding.markDone()   // дійшов до головного екрана = онбординг пройдено
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addFreeRewardsBtn()
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

    // Головна дія екрана — першою, над каруселлю. Клік = наш лендінг:
    // showInterstitial у custom-провайдері одразу відкриває таб (частотного
    // гейта там немає, на відміну від front/back). pl=interstitial.
    private fun AConstraintLayout.addFreeRewardsBtn() {
        aFreeRewardsBtn.setSize(344f, 72f)
        add(aFreeRewardsBtn) { centerX(); topToBottom(aTop, 4f) }

        aFreeRewardsBtn.setOnClickListener { gdxGame.activity.showInterstitial() }
    }

    private fun AConstraintLayout.addPanelSelectBlokcy() {
        aPanelSelectBlokcy.height = 509f
        add(aPanelSelectBlokcy) {
            centerX(); topToBottom(aFreeRewardsBtn, 8f)
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
            // «=», не «+=»: adBottomFlow — StateFlow, шле на кожну зміну — інакше накопичується
            AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomPanel) { marginBottom = screen.adBottomUI.coerceAtLeast(0f) } } }
        }
    }

}