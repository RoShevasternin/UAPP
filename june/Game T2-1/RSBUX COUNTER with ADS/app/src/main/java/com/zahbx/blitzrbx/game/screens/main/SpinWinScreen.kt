package com.zahbx.blitzrbx.game.screens.main

import com.zahbx.blitzrbx.game.actors.AWheel
import com.zahbx.blitzrbx.game.actors.button.AGreenButton
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.panel.APanelRBX
import com.zahbx.blitzrbx.game.actors.panel.APanelTop
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.NumberFormatter
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.game.utils.runGDX
import com.zahbx.blitzrbx.util.log
import kotlinx.coroutines.launch

class SpinWinScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop   = APanelTop(this)
    private val aWheel      = AWheel(this)
    private val aPanelRBX   = APanelRBX(this)
    private val aGreenBtn   = AGreenButton(this, "Spin Now")

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
        addWheel()
        addPanelRBX()
        addGreenBtn()
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

        aPanelTop.setTitle("Spin & Win")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(405f, 405f)
        add(aWheel) {
            centerX()
            topToBottom(aPanelTop, 50f)
        }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(105f, 48f)
        add(aPanelRBX) {
            topToBottom(aPanelTop, 16f)
            endToEnd(margin = 16f)
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

    private fun AConstraintLayout.addGreenBtn() {
        aGreenBtn.setSize(344f, 60f)
        add(aGreenBtn) {
            centerX()
            topToBottom(aWheel, -7f)
        }

        aGreenBtn.setOnClickListener {
            aWheel.spin { result ->
                log("result = $result")
                gdxGame.modelPlayer.addRbx(result.sum.toLong())
            }
        }

    }

}