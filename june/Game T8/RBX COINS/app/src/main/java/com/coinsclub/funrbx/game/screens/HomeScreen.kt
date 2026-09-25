package com.coinsclub.funrbx.game.screens

import com.coinsclub.funrbx.businesModule.backend.Bt
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.ATmpGroup
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.APanelTopHome
import com.coinsclub.funrbx.game.actors.panel.home.APanelHome
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.Onboarding
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.runGDX
import com.coinsclub.funrbx.services.analytics.AnalyticsManager
import com.coinsclub.funrbx.util.log
import kotlinx.coroutines.launch

class HomeScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.HUB
    override val analyticsBlock = "home_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop       by lazy { APanelTopHome(this) }
    private val aPanelHome by lazy { APanelHome(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        // Нативки в меню немає навмисно: вона перекриває плитки механік
        // (рішення користувача). Банер лишається.
        stageUI.root.color.a = 0f
        super.show()
        // Дійшли до меню — онбординг пройдено, наступні запуски стартують звідси
        Onboarding.markDone()
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
        aTop.height = 72f
        add(aTop) { centerX(); topToTop(); matchWidth() }
    }

    private fun AConstraintLayout.addPanelHome() {
        aPanelHome.width = 344f
        add(aPanelHome) { centerX(); topToBottom(aTop); bottomToBottom(); matchHeight() }

        // «=», не «+=»: adBottomFlow — StateFlow, шле на кожну зміну — інакше накопичується
        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelHome) { marginBottom = screen.adBottomUI.coerceAtLeast(0f) } } } }
    }

}