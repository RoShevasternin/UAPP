package com.racing.funtols.game.screens.selector

import com.badlogic.gdx.math.Vector2
import com.racing.funtols.adsmodule.AdSizeManager
import com.racing.funtols.game.actors.button.ARedButton
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTop
import com.racing.funtols.game.actors.panel.selector.APanelSelectorGrid
import com.racing.funtols.game.actors.panel.selector.APanelSelectorVertical
import com.racing.funtols.game.actors.panel.selector.data.CharacterData
import com.racing.funtols.game.actors.panel.selector.data.NamesData
import com.racing.funtols.game.screens.HomeScreen
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.runGDX
import kotlinx.coroutines.launch

class Selector_4_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aSelector    by lazy { APanelSelectorVertical(this, NamesData.items()) }
    private val aContinueBtn by lazy { ARedButton(this, "CONTINUE") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f


        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addContinueBtn()
        addSelector()
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
        aPanelTop.setSize(344f, 32f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("SELECT FAVORITE PACK")
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 52f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 42f) }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom = screen.adBottomUI + 42f } } } }

        aContinueBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, Selector_4_Screen::class.java.name) } }
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.width = 344f
        add(aSelector) { centerX(); topToBottom(aPanelTop, 24f); bottomToTop(aContinueBtn, 8f); matchHeight() }
    }

}