package com.rbuxdrop.cougame.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.APanelRBX
import com.rbuxdrop.cougame.game.actors.panel.APanelTop
import com.rbuxdrop.cougame.game.actors.panel.scratch.ADialog
import com.rbuxdrop.cougame.game.actors.panel.scratch.AScratch
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.NumberFormatter
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animHideAndDisable
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.actor.animShowAndEnable
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.utils.runGDX
import com.rbuxdrop.cougame.util.log
import kotlinx.coroutines.launch

class ScratchScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop = APanelTop(this)
    private val aPanelRBX = APanelRBX(this)
    private val aScratch  = AScratch(this)

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
        addPanelRBX()
        addScratch()
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
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Scratch Card")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(110f, 48f)
        add(aPanelRBX) {
            endToEnd(margin = 16f)
            topToTop(aPanelTop)
            bottomToBottom(aPanelTop)
        }

        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX {
                    val rbxFormat = NumberFormatter.format(rbx)
                    aPanelRBX.setText(rbxFormat)
                }
            }
        }
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(344f, 272f)
        add(aScratch) { centerX(); topToBottom(aPanelTop, 16f) }

        aScratch.onResult = { result ->
            log("aScratch result: $result")
            gdxGame.modelPlayer.addRbx(result.sum.toLong())

            showDialog(result.sum.toLong())
        }
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