package com.bossrbx.rbxcalculator.game.screens.main

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelRS
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.actors.panel.dailyReward.APanelDailyReward
import com.bossrbx.rbxcalculator.game.actors.popup.APopup
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.actor.setSize
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class DailyRewardScreen: AdvancedScreen() {

    private val text = "Login 7 consecutive days for the jackpot reward! Missing a day resets your streak."

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(text)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop         by lazy { APanelTop(this) }
    private val aPanelDailyReward by lazy { APanelDailyReward(this) }
    private val aPanelRSBackImg   by lazy { Image(drawerUtil.getTexture(GameColor.gray_171717)) }
    private val aPanelRS          by lazy { APanelRS(this) }
    private val aLogin7Lbl        by lazy { Label(text, FontFactory.create(this, parameter, fontGenerator_Light, GameColor.gray_808080)) }
    private val aComeBackImg      by lazy { Image(gdxGame.assetsAll.COME_BACK) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_90)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelDailyReward()
        addPanelRS()
        addTextLbl()
        addComeBackImg()
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
        aPanelTop.setSize(WIDTH, 64f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Daily Robux")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelDailyReward() {
        aPanelDailyReward.setSize(344f, 224f)
        add(aPanelDailyReward) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelDailyReward.onGetReward = { reward -> rootConstraintLayout.showDialog(reward) }
    }

    private fun AConstraintLayout.addPanelRS() {
        aPanelRSBackImg.setSize(344f, 56f)
        add(aPanelRSBackImg) { centerX(); topToBottom(aPanelDailyReward, 24f) }

        aPanelRS.setSize(64f, 32f)
        add(aPanelRS) { center(aPanelRSBackImg) }
    }

    private fun AConstraintLayout.addTextLbl() {
        aLogin7Lbl.setSize(310f, 48f)
        add(aLogin7Lbl) { centerX(); topToBottom(aPanelRSBackImg, 24f) }

        aLogin7Lbl.setAlignment(Align.center)
        aLogin7Lbl.wrap = true
    }

    private fun AConstraintLayout.addComeBackImg() {
        aComeBackImg.setSize(344f, 88f)
        add(aComeBackImg) { centerX(); topToBottom(aLogin7Lbl, 48f) }

        updateComeBackState()
    }

    private fun AConstraintLayout.showDialog(reward: Long) {
        aPopup.onClaim = {
            aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            aPopup.animHideAndDisable(0.15f) { aPopup.isDisposeOnRemove = false; aPopup.remove() }
            aComeBackImg.animShowAndEnable(0.15f)
        }

        aDimImg.animHideAndDisable()
        aPopup.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aPopup.setSize(344f, 340f)
        add(aPopup) {
            center()
            verticalBias = 0.7f
        }

        aPopup.setReward(reward)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aPopup.animShowAndEnable(TIME_ANIM_SCREEN)

        gdxGame.soundUtil.apply { play(WIN) }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun updateComeBackState() {
        if (gdxGame.modelPlayer.canClaimDailyReward()) {
            aComeBackImg.animHideAndDisable(0.15f)
        } else {
            aComeBackImg.animShowAndEnable(0.15f)
        }
    }


}