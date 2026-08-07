package com.diam.ondbit.game.screens.selector

import com.badlogic.gdx.math.Vector2
import com.diam.ondbit.adsmodule.AdSizeManager
import com.diam.ondbit.game.actors.button.AYellowButton
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.actors.panel.selector.APanelSelectorGrid
import com.diam.ondbit.game.actors.panel.selector.data.AnimationData
import com.diam.ondbit.game.actors.panel.selector.data.CharacterData
import com.diam.ondbit.game.actors.panel.selector.data.PetsData
import com.diam.ondbit.game.screens.HomeScreen
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.launch

class Selector_3_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aSelector    by lazy { APanelSelectorGrid(this, AnimationData.items()) }
    private val aContinueBtn by lazy { AYellowButton(this, "CONTINUE") }

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
        aPanelTop.setSize(344f, 40f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("SELECT EMOTES")
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(345f, 62f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 40f) }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom = screen.adBottomUI + 40f } } } }

        aContinueBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, Selector_3_Screen::class.java.name) } }
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.width = 344f
        add(aSelector) { centerX(); topToBottom(aPanelTop, 24f); bottomToTop(aContinueBtn); matchHeight() }
    }

}