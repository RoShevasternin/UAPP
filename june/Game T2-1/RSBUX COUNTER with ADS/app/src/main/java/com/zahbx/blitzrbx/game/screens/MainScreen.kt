package com.zahbx.blitzrbx.game.screens

import com.badlogic.gdx.math.Vector2
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.layout.linear.AVerticalGroup
import com.zahbx.blitzrbx.game.actors.panel.APanelMain
import com.zahbx.blitzrbx.game.actors.panel.APanelRBX
import com.zahbx.blitzrbx.game.actors.panel.APanelTopLogo
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.services.analytics.AnalyticsManager

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
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen { AnalyticsManager.openHomeScreen() }
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