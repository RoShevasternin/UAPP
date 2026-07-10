package com.sakurbx.fungambx.game.screens

import com.badlogic.gdx.math.Vector2
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.ATmpGroup
import com.sakurbx.fungambx.game.actors.button.APinkButton
import com.sakurbx.fungambx.game.actors.button.ATextButtonAnim
import com.sakurbx.fungambx.game.actors.button.ATextButtonTexture
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.APanelTopHome
import com.sakurbx.fungambx.game.actors.panel.home.APanelHome
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.actor.setSize
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.runGDX
import com.sakurbx.fungambx.services.analytics.AnalyticsManager
import com.sakurbx.fungambx.task.OpenSiteReceiver
import com.sakurbx.fungambx.util.log
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
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_PUPRLE)

        super.show()
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        //addPanelHome()

        val test = APinkButton(this@HomeScreen, "TEST")
        test.setSize(344f, 57f)
        add(test) { center() }

        test.setOnClickListener { OpenSiteReceiver().onReceive(gdxGame.activity, null) }
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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(344f, 40f)
        add(aTop) { centerX(); topToTop(margin = 8f) }
    }

    private fun AConstraintLayout.addPanelHome() {
        aPanelHome.width = 344f
        add(aPanelHome) { centerX(); topToBottom(aTop, 24f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelHome) { marginBottom += screen.adBottomUI } } } }
    }

}