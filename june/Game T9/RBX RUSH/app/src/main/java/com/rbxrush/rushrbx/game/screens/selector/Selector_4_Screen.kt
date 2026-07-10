package com.rbxrush.rushrbx.game.screens.selector

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.adsmodule.AdSizeManager
import com.rbxrush.rushrbx.game.actors.button.AYellowButton
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.actors.panel.APanelTop
import com.rbxrush.rushrbx.game.actors.panel.selector.APanelSelectorVertical
import com.rbxrush.rushrbx.game.actors.panel.selector.data.NamesData
import com.rbxrush.rushrbx.game.screens.HomeScreen
import com.rbxrush.rushrbx.game.utils.Block
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.TIME_ANIM_SCREEN
import com.rbxrush.rushrbx.game.utils.actor.animDelay
import com.rbxrush.rushrbx.game.utils.actor.animHide
import com.rbxrush.rushrbx.game.utils.actor.animShow
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class Selector_4_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aSelector    by lazy { APanelSelectorVertical(this, NamesData.items()) }
    private val aBottomImg   by lazy { Image(drawerUtil.getTexture(GameColor.background)) }
    private val aContinueBtn by lazy { AYellowButton(this, "NEXT") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addBottomImg()
        addContinueBtn()
        addSelector()

        aSelector.toBack()
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
    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.height = 72f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("Animation Pack")
    }

    private fun AConstraintLayout.addBottomImg() {
        aBottomImg.height = 108f
        add(aBottomImg) { centerX(); bottomToBottom(margin = 0f); matchWidth() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomImg) { marginBottom += screen.adBottomUI } } } }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 56f)
        add(aContinueBtn) { centerX(); topToTop(aBottomImg, margin = 16f) }

        aContinueBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, Selector_4_Screen::class.java.name) } }
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.width = 344f
        add(aSelector) { centerX(); topToBottom(aPanelTop, 16f); bottomToTop(aBottomImg, -20f); matchHeight() }
    }

}