package com.zahbx.blitzrbx.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.game.actors.ATimer
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.panel.miniGame.APanelMiniGameProcess
import com.zahbx.blitzrbx.game.actors.panel.APanelRBX
import com.zahbx.blitzrbx.game.actors.panel.APanelTop
import com.zahbx.blitzrbx.game.actors.panel.miniGame.ADialogResultMiniGame
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.GameColor
import com.zahbx.blitzrbx.game.utils.HEIGHT_UI
import com.zahbx.blitzrbx.game.utils.NumberFormatter
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.WIDTH_UI
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class MiniGameScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop = APanelTop(this)
    private val aMiniGame = APanelMiniGameProcess(this)
    private val aPanelRBX = APanelRBX(this)
    private val aTimer    = ATimer(this)

    private val aDimImg = Image(drawerUtil.getTexture(GameColor.black_80))
    private val aDialog = ADialogResultMiniGame(this)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var collectedRBX = 0

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen {
            aTimer.start(30)
            aMiniGame.start()
        }
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelMiniGame()
        addPanelRBX()
        addTimer()
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

        aPanelTop.setTitle("Mini Game")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelMiniGame() {
        aMiniGame.width = WIDTH_UI
        add(aMiniGame) {
            topToBottom(aPanelTop)
            bottomToBottom()

            matchHeight()
        }

        aMiniGame.onHit = {
            collectedRBX += 1
            gdxGame.modelPlayer.addRbx(1)
        }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(73f, 48f)
        add(aPanelRBX) {
            startToStart(margin = 16f)
            topToBottom(aPanelTop, 16f)
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

    private fun AConstraintLayout.addTimer() {
        aTimer.setSize(66f, 48f)
        add(aTimer) {
            endToEnd(margin = 16f)
            topToBottom(aPanelTop, 16f)
        }

        aTimer.onTimeout = {
            aMiniGame.stop()
            showDialog()
        }
    }

    // ------------------------------------------------------------------------
    // Dialog
    // ------------------------------------------------------------------------
    private fun showDialog() {
        rootConstraintLayout.add(aDimImg) { fillParent() }

        aDialog.setSize(344f, 330f)
        rootConstraintLayout.add(aDialog) {
            center()
            if (adBottomUI >= 0f) marginBottom = adBottomUI
        }

        aDialog.setResult(collectedRBX)

        // Анімація появи
        aDimImg.color.a = 0f
        aDialog.color.a = 0f
        aDimImg.animShow(0.3f)
        aDialog.animShow(0.3f)

        aDialog.onClaimReward = { animHideScreen { gdxGame.navigationManager.back() } }
    }

}