package com.fimer.skintool.game.screens.home.selectors.vehicles

import com.badlogic.gdx.math.Vector2
import com.fimer.skintool.adsmodule.AdSizeManager
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelTop
import com.fimer.skintool.game.actors.panel.selectors.vehicles.APanelSelectVehicles
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.runGDX
import kotlinx.coroutines.launch

class SelectVehiclesScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop    by lazy { APanelTop(this) }
    private val aSelect by lazy { APanelSelectVehicles(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addSelect()
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
        aTop.setSize(344f, 32f)
        add(aTop) { centerX(); topToTop(margin = 16f) }

        aTop.setTitle("VEHICLES")
    }

    private fun AConstraintLayout.addSelect() {
        aSelect.width = 344f
        add(aSelect) { centerX(); topToBottom(aTop, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aSelect) { marginBottom = screen.adBottomUI + 30f } } } }
    }

}