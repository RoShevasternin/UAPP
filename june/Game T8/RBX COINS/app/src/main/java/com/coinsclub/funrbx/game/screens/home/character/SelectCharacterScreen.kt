package com.coinsclub.funrbx.game.screens.home.character

import com.badlogic.gdx.math.Vector2
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.character.APanelSelectCharacter
import com.coinsclub.funrbx.game.actors.panel.converter.APanelSelectConverter
import com.coinsclub.funrbx.game.actors.panel.selector.data.CharacterData
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class SelectCharacterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop    by lazy { APanelTop(this) }
    private val aPanelSelect by lazy { APanelSelectCharacter(this, CharacterData.items()) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
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

        aPanelTop.setTitle("CHARACTERS")
    }

    private fun AConstraintLayout.addPanelSelect() {
        aPanelSelect.width = 346f
        add(aPanelSelect) { centerX(); topToBottom(aPanelTop, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelSelect) { marginBottom += screen.adBottomUI } } } }
    }

}