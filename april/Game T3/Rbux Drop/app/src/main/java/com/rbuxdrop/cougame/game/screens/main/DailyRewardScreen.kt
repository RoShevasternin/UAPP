package com.rbuxdrop.cougame.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.adsmodule.AdSizeManager
import com.rbuxdrop.cougame.game.actors.AScrollPane
import com.rbuxdrop.cougame.game.actors.layout.AlignH
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.layout.linear.AVerticalGroup
import com.rbuxdrop.cougame.game.actors.panel.APanelTop
import com.rbuxdrop.cougame.game.actors.panel.dailyReward.ADialog
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.actors.panel.dailyReward.APanelDailyReward
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.WIDTH_UI
import com.rbuxdrop.cougame.game.utils.actor.animHideAndDisable
import com.rbuxdrop.cougame.game.utils.actor.animShowAndEnable
import com.rbuxdrop.cougame.game.utils.runGDX
import com.rbuxdrop.cougame.util.log
import kotlinx.coroutines.launch

class DailyRewardScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop         = APanelTop(this)
    private val aPanelDailyReward = APanelDailyReward(this)

    private val aVerticalGroup    = AVerticalGroup(this, alignH = AlignH.CENTER, wrap = true)
    private val aScrollPane       = AScrollPane(aVerticalGroup)

    private val aDimImg = Image(drawerUtil.getTexture(GameColor.background_80))
    private val aDialog = ADialog(this)

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
        addScrollPane()
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
        aPanelTop.setSize(WIDTH_UI, 56f)
        add(aPanelTop) { centerX(); topToTop() }
        aPanelTop.setTitle("Daily RBX")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addScrollPane() {
        aScrollPane.width = WIDTH_UI
        add(aScrollPane) {
            centerX()
            topToBottom(aPanelTop)
            bottomToBottom()

            matchHeight()
        }

        setUpVerticalGroup()
    }

    private fun setUpVerticalGroup() {
        aVerticalGroup.width = WIDTH_UI
        aVerticalGroup.paddingTop = 12f

        aPanelDailyReward.setSize(344f, 552f)
        aVerticalGroup.addActor(aPanelDailyReward)

        val space = aScrollPane.height - 552f
        if (space > 0) aVerticalGroup.paddingBottom += space

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { if (adBottomUI >= 0f) aVerticalGroup.paddingBottom += adBottomUI } }
        }

        aPanelDailyReward.onGetReward = { reward -> rootConstraintLayout.showDialog(reward) }
    }

    private fun AConstraintLayout.showDialog(reward: Long) {
        aDialog.onClaim = {
            aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            aDialog.animHideAndDisable(0.15f) { aDialog.isDisposeOnRemove = false; aDialog.remove() }
        }

        aDimImg.animHideAndDisable()
        aDialog.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aDialog.setSize(344f, 284f)
        add(aDialog) {
            centerX()
            centerY()

            verticalBias = 0.65f
        }

        aDialog.setReward(reward)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aDialog.animShowAndEnable(TIME_ANIM_SCREEN)
    }




}