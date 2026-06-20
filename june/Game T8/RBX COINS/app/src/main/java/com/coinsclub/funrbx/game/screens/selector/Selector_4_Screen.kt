package com.coinsclub.funrbx.game.screens.selector

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.button.AYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.selector.APanelSelectorVertical
import com.coinsclub.funrbx.game.actors.panel.selector.data.NamesData
import com.coinsclub.funrbx.game.screens.HomeScreen
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class Selector_4_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aSelector    by lazy { APanelSelectorVertical(this, NamesData.items()) }
    private val aBottomImg   by lazy { Image(drawerUtil.getTexture(GameColor.purple_421870)) }
    private val aContinueBtn by lazy { AYellowButton(this, "CONTINUE") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

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

        aPanelTop.setTitle("SELECT ANIMATION PACK")
    }

    private fun AConstraintLayout.addBottomImg() {
        aBottomImg.height = 115f
        add(aBottomImg) { centerX(); bottomToBottom(); matchWidth() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomImg) { marginBottom += screen.adBottomUI } } } }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(345f, 58f)
        add(aContinueBtn) { centerX(); topToTop(aBottomImg, margin = 15f) }

        aContinueBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, Selector_4_Screen::class.java.name) } }
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.width = 346f
        add(aSelector) { centerX(); topToBottom(aPanelTop, 16f); bottomToTop(aBottomImg, -20f); matchHeight() }
    }

}