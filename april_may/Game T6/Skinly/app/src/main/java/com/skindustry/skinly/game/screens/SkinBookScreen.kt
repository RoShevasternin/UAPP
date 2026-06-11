package com.skindustry.skinly.game.screens

import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.ABottomPanelHome
import com.skindustry.skinly.game.actors.panel.APanelTopHome
import com.skindustry.skinly.game.actors.panel.skinBook.APanelSkinBook
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import com.skindustry.skinly.util.log
import kotlinx.coroutines.launch

class SkinBookScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aTop           by lazy { APanelTopHome(this) }
    private val aPanelSkinBook by lazy { APanelSkinBook(this) }
    private val aBottomPanel   by lazy { ABottomPanelHome(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addPanelSkinBook()
        addBottomPanel()
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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(WIDTH, 68f)
        add(aTop) { centerX(); topToTop() }
    }

    private fun AConstraintLayout.addPanelSkinBook() {
        val aVertical   = AAutoLayout(
            screen      = this@SkinBookScreen,
            direction   = AAutoLayout.Direction.VERTICAL,
            sizingH     = AAutoLayout.Sizing.HUG,
        )
        val aScrollPane = AScrollPane(aVertical)

        add(aScrollPane) {
            centerX(); topToBottom(aTop); bottomToTop(aBottomPanel);
            matchConstraint()
        }

        aVertical.setSize(aScrollPane.width, aScrollPane.height)
        aVertical.minH = aScrollPane.height

        aPanelSkinBook.width  = aVertical.width
        aPanelSkinBook.height = 572f

        aVertical.add(aPanelSkinBook)
    }

    private fun AConstraintLayout.addBottomPanel() {
        aBottomPanel.height = 74f
        add(aBottomPanel) {
            centerX(); bottomToBottom()
            matchWidth()
        }
        aBottomPanel.check(ABottomPanelHome.Type.SKIN_BOOK)

        aBottomPanel.onTabChanged = { type ->
            when(type) {
                ABottomPanelHome.Type.HOME      -> { animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, SkinBookScreen::class.java.name) } }
                ABottomPanelHome.Type.SKIN_BOOK -> { log("bottom: SKIN_BOOK") }
            }
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomPanel) { marginBottom += screen.adBottomUI } } }
        }
    }

}