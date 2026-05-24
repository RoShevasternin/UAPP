package com.bossrbx.rbxcalculator.game.screens.main

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelRS
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.actors.panel.scratch.APanelScratch
import com.bossrbx.rbxcalculator.game.actors.popup.APopup
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class ScratchScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelScratch   by lazy { APanelScratch(this) }
    private val aPanelRSBackImg by lazy { Image(drawerUtil.getTexture(GameColor.gray_171717)) }
    private val aPanelRBX       by lazy { APanelRS(this) }
    private val aClaimBtn       by lazy { ABlueButton(this, "Claim") }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_90)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------

    private var localReward = 0

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        gdxGame.activity.hideBanner()

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.showBanner()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelScratch()
        addPanelRBX()
        addClaimBtn()
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

        aPanelTop.setTitle("Scratch Card")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelScratch() {
        aPanelScratch.setSize(344f, 452f)
        add(aPanelScratch) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelScratch.onResultOnceScratchCard = { sum ->
            localReward += sum

            aClaimBtn.enable()
            aClaimBtn.label.setText("Claim $localReward RBX")
        }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRSBackImg.setSize(344f, 56f)
        add(aPanelRSBackImg) { centerX(); topToBottom(aPanelScratch, 54f) }

        aPanelRBX.setSize(64f, 32f)
        add(aPanelRBX) { center(aPanelRSBackImg) }
    }

    private fun AConstraintLayout.addClaimBtn() {
        aClaimBtn.setSize(344f, 64f)
        add(aClaimBtn) { centerX(); topToBottom(aPanelRSBackImg, 20f) }

        aClaimBtn.disable()

        aClaimBtn.setOnClickListener {
            gdxGame.modelPlayer.addRbx(localReward.toLong())
            showDialog(localReward.toLong())
        }

    }

    private fun AConstraintLayout.showDialog(reward: Long) {
        aPopup.onClaim = {
            aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            aPopup.animHideAndDisable(0.15f) { aPopup.isDisposeOnRemove = false; aPopup.remove() }

            animHideScreen { gdxGame.navigationManager.back() }
        }

        aDimImg.animHideAndDisable()
        aPopup.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aPopup.setSize(344f, 340f)
        add(aPopup) {
            center()
            verticalBias = 0.70f
        }

        aPopup.setReward(reward)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aPopup.animShowAndEnable(TIME_ANIM_SCREEN)
    }

}