package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxhubpro.rohumex.game.actors.scratch.AScratchCard
import com.rbxhubpro.rohumex.game.actors.button.ABlueButton
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelRBX
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.actors.scratch.AScratch
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.util.log

class ScratchScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop      = APanelTop(this)
    private val aScratch       = AScratch(this)
    private val aPanelRBX      = APanelRBX(this)
    private val aScratchNowBtn = ABlueButton(this, "Scratch Now")

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addScratch()
        addPanelRBX()
        addScratchNowBtn()

        animShowScreen()
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
        aPanelTop.setTitle("Scratch And Win")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addScratch() {
        aScratch.setSize(345f, 345f)
        addActorWithConstraints(aScratch) {
            startToStartOf = this@addScratch
            endToEndOf     = this@addScratch
            topToBottomOf  = aPanelTop

            marginTop = 80f
        }

        aScratch.onResult = { result -> aPanelRBX.setResult(result.sum) }
    }

    private fun Group.addPanelRBX() {
        aPanelRBX.setSize(105f, 48f)
        addActorWithConstraints(aPanelRBX) {
            startToStartOf = this@addPanelRBX
            endToEndOf     = this@addPanelRBX
            topToBottomOf  = aScratch

            marginTop = 48f
        }

    }

    private fun Group.addScratchNowBtn() {
        aScratchNowBtn.setSize(344f, 56f)
        addActorAligned(aScratchNowBtn, AlignH.CENTER, AlignV.BOTTOM)
        aScratchNowBtn.y += adBannerUI //20f

        aScratchNowBtn.onClick = { aScratch.regenerateScratch() }
    }

}