package com.rsbuxs.rcounbux.game.screens

import com.badlogic.gdx.math.Vector2
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.layout.linear.AVerticalGroup
import com.rsbuxs.rcounbux.game.actors.panel.APanelMain
import com.rsbuxs.rcounbux.game.actors.panel.APanelRBX
import com.rsbuxs.rcounbux.game.actors.panel.APanelTopLogo
import com.rsbuxs.rcounbux.game.utils.Block
import com.rsbuxs.rcounbux.game.utils.TIME_ANIM_SCREEN
import com.rsbuxs.rcounbux.game.utils.actor.animDelay
import com.rsbuxs.rcounbux.game.utils.actor.animHide
import com.rsbuxs.rcounbux.game.utils.actor.animShow
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class MainScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTopLogo = APanelTopLogo(this)
    private val aPanelMain    = APanelMain(this)

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
        addPanelTopLogo()
        addPanelMain()
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

    private fun AConstraintLayout.addPanelTopLogo() {
        aPanelTopLogo.setSize(376f, 80f)
        add(aPanelTopLogo) {
            centerX()
            topToTop()
        }
    }

    private fun AConstraintLayout.addPanelMain() {
        aPanelMain.width = 376f
        add(aPanelMain) {
            centerX()
            topToBottom(aPanelTopLogo)
            bottomToBottom()

            matchHeight()
        }
    }

}