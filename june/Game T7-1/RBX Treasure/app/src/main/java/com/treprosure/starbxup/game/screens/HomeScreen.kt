package com.treprosure.starbxup.game.screens

import com.badlogic.gdx.math.Vector2
import com.treprosure.starbxup.adsmodule.AdSizeManager
import com.treprosure.starbxup.game.actors.ATmpGroup
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.actors.panel.APanelTop
import com.treprosure.starbxup.game.actors.panel.APanelTopHome
import com.treprosure.starbxup.game.actors.panel.home.APanelHome
import com.treprosure.starbxup.game.utils.Block
import com.treprosure.starbxup.game.utils.TIME_ANIM_SCREEN
import com.treprosure.starbxup.game.utils.actor.animDelay
import com.treprosure.starbxup.game.utils.actor.animHide
import com.treprosure.starbxup.game.utils.actor.animShow
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.gdxGame
import com.treprosure.starbxup.game.utils.runGDX
import com.treprosure.starbxup.services.analytics.AnalyticsManager
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
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
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
    }

}