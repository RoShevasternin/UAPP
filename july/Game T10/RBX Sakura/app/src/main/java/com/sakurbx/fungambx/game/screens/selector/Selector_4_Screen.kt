package com.sakurbx.fungambx.game.screens.selector

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.button.AImagePinkButton
import com.sakurbx.fungambx.game.actors.button.APinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.selector.APanelSelectorGrid
import com.sakurbx.fungambx.game.actors.panel.selector.APanelSelectorVertical
import com.sakurbx.fungambx.game.actors.panel.selector.data.CharacterData
import com.sakurbx.fungambx.game.actors.panel.selector.data.NamesData
import com.sakurbx.fungambx.game.screens.HomeScreen
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.runGDX
import kotlinx.coroutines.launch

class Selector_4_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aSelector    by lazy { APanelSelectorVertical(this, NamesData.items()) }
    private val aContinueBtn by lazy { AImagePinkButton(this, TextureRegionDrawable(gdxGame.assetsAll.icon_btn_2), Vector2(82f, 25f)) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_PUPRLE)

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
    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 56f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }

        aPanelTop.setTitle("SELECT SKIN")
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 57f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 32f) }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom += screen.adBottomUI } } } }

        aContinueBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, Selector_4_Screen::class.java.name) } }
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.width = 344f
        add(aSelector) { centerX(); topToBottom(aPanelTop, 16f); bottomToTop(aContinueBtn, 8f); matchHeight() }
    }

}