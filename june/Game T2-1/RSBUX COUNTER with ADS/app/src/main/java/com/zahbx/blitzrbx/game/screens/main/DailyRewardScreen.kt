package com.zahbx.blitzrbx.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.panel.APanelTop
import com.zahbx.blitzrbx.game.actors.panel.dailyReward.APanelDailyReward
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.actor.setBounds
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame

class DailyRewardScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop         = APanelTop(this)
    private val aPanelDailyReward = APanelDailyReward(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
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
        addPanelDailyReward()
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
        aPanelTop.setSize(376f, 56f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }

        aPanelTop.setTitle("Daily Reward")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelDailyReward() {
        aPanelDailyReward.setSize(376f, 411f)
        add(aPanelDailyReward) {
            centerX()
            topToBottom(aPanelTop)
        }
    }

}