package com.rbuxdrop.cougame.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.APanelSelectConverter
import com.rbuxdrop.cougame.game.actors.panel.APanelTop
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.actor.setSize
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class SelectConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop    = APanelTop(this)
    private val aPanelSelect = APanelSelectConverter(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
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
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("RBX Converter")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelSelect() {
        aPanelSelect.setSize(376f, 376f)
        add(aPanelSelect) { centerX();topToBottom(aPanelTop) }
    }

}