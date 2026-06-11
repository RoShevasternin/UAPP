package com.rbxtreasure.fungamers.game.screens.home.converter

import com.badlogic.gdx.math.Vector2
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.actors.panel.APanelTop
import com.rbxtreasure.fungamers.game.actors.panel.converter.APanelSelectConverter
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.TIME_ANIM_SCREEN
import com.rbxtreasure.fungamers.game.utils.actor.animDelay
import com.rbxtreasure.fungamers.game.utils.actor.animHide
import com.rbxtreasure.fungamers.game.utils.actor.animShow
import com.rbxtreasure.fungamers.game.utils.actor.setSize
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame

class SelectConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop    by lazy { APanelTop(this) }
    private val aPanelSelect by lazy { APanelSelectConverter(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        //val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        //gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        //gdxGame.activity.hideNative()
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
        aPanelTop.height = 56f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("ROBUX CONVERTER")
    }

    private fun AConstraintLayout.addPanelSelect() {
        aPanelSelect.setSize(344f, 418f)
        add(aPanelSelect) { centerX(); topToBottom(aPanelTop, 16f) }
    }

}