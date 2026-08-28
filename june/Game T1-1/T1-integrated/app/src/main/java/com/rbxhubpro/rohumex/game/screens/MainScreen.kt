package com.rbxhubpro.rohumex.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelMain
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.services.analytics.AnalyticsManager

class MainScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.CATALOG
    override val analyticsBlock = "main_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)
    private val aPanelMain = APanelMain(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addPanelMain()

        animShowScreen { AnalyticsManager.openHomeScreen() }
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

    private fun Group.addPanelTop() {
        aPanelTop.setSize(376f, 56f)
        addActorAligned(aPanelTop, AlignH.CENTER, AlignV.TOP)
        aPanelTop.setTitle("RBX Counter")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addPanelMain() {
        aPanelMain.setSize(376f, aPanelTop.y - adBannerUI)
        addActorWithConstraints(aPanelMain) {
            startToStartOf = this@addPanelMain
            endToEndOf     = this@addPanelMain
            topToBottomOf  = aPanelTop

            marginTop = 15f
        }
    }

}