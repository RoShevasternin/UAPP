package com.coinsclub.funrbx.game.screens.home.outfit

import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.outfit.APanelSelectOutfit
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class SelectOutfitScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop    by lazy { APanelTop(this) }
    private val aPanelSelect by lazy { APanelSelectOutfit(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelSelect()
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

        aPanelTop.setTitle("CLOTHES & ANIMATION")
    }

    private fun AConstraintLayout.addPanelSelect() {
        aPanelSelect.setSize(346f, 350f)
        add(aPanelSelect) { centerX(); topToBottom(aPanelTop, 16f) }
    }

}