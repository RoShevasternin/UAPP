package com.rbxtreasure.fungamers.game.screens.selector

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.adsmodule.AdSizeManager
import com.rbxtreasure.fungamers.game.actors.button.AYellowButton
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.actors.panel.APanelTop
import com.rbxtreasure.fungamers.game.actors.panel.selector.APanelSelectorLeft
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.TIME_ANIM_SCREEN
import com.rbxtreasure.fungamers.game.utils.actor.animDelay
import com.rbxtreasure.fungamers.game.utils.actor.animHide
import com.rbxtreasure.fungamers.game.utils.actor.animShow
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.runGDX
import kotlinx.coroutines.launch

class Selector_3_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aSelector    by lazy { APanelSelectorLeft(this, gdxGame.assetsAll.listSelector[2]) }
    private val aBottomImg   by lazy { Image(gdxGame.assetsAll.BOTTOM_BROWN) }
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
        aPanelTop.height = 56f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("Select Your Favorite Skin")
    }

    private fun AConstraintLayout.addBottomImg() {
        aBottomImg.height = 146f
        add(aBottomImg) { centerX(); bottomToBottom(); matchWidth() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomImg) { marginBottom += screen.adBottomUI } } } }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 51f)
        add(aContinueBtn) { centerX(); bottomToBottom(aBottomImg, margin = 41f) }

        aContinueBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.navigate(Selector_4_Screen::class.java.name, Selector_3_Screen::class.java.name) } }
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.setSize(WIDTH, 1f)
        add(aSelector) { centerX(); topToBottom(aPanelTop, 16f); bottomToTop(aBottomImg, -38f); matchHeight() }
    }

}