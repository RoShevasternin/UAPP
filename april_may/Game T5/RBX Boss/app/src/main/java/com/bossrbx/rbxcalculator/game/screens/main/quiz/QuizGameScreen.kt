package com.bossrbx.rbxcalculator.game.screens.main.quiz

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.adsmodule.AdSizeManager
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelRS
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.actors.panel.quiz.APanelQuiz
import com.bossrbx.rbxcalculator.game.actors.popup.APopup
import com.bossrbx.rbxcalculator.game.actors.popup.APopupQuiz
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
import com.bossrbx.rbxcalculator.game.utils.runGDX
import kotlinx.coroutines.launch

class QuizGameScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelQuiz      by lazy { APanelQuiz(this) }
    private val aPanelRSBackImg by lazy { Image(drawerUtil.getTexture(GameColor.gray_171717)) }
    private val aPanelRBX       by lazy { APanelRS(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_90)) }
    private val aPopup  by lazy { APopupQuiz(this) }

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
        addPanelQuiz()
        addPanelRS()
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

        aPanelTop.setTitle("Quiz")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addPanelQuiz() {
        aPanelQuiz.setSize(WIDTH, 452f)
        add(aPanelQuiz) { centerX(); topToBottom(aPanelTop) }

        aPanelQuiz.onAnswer = { isWin -> showDialog(10, isWin) }
    }

    private fun AConstraintLayout.addPanelRS() {
        aPanelRSBackImg.setSize(344f, 56f)
        add(aPanelRSBackImg) { centerX(); bottomToBottom(margin = 24f) }

        aPanelRBX.setSize(64f, 32f)
        add(aPanelRBX) { center(aPanelRSBackImg) }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    val adBottom = screen.adBottomUI.coerceAtLeast(0f)
                    if (adBottom > 0f) this@addPanelRS.update(aPanelRSBackImg) {
                        marginBottom += adBottom
                    }
                }
            }
        }
    }

    private fun AConstraintLayout.showDialog(reward: Long, isWin: Boolean) {
        aPopup.onClaim = {
            aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            aPopup.animHideAndDisable(0.15f) { aPopup.isDisposeOnRemove = false; aPopup.remove() }

            if (isWin) gdxGame.modelPlayer.addRbx(reward) else gdxGame.modelPlayer.spendRbx(reward)
        }

        aDimImg.animHideAndDisable()
        aPopup.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aPopup.setSize(344f, 352f)
        add(aPopup) {
            center()
            verticalBias = 0.70f
        }

        aPopup.setReward(reward, isWin)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aPopup.animShowAndEnable(TIME_ANIM_SCREEN)

        if (isWin) gdxGame.soundUtil.apply { play(WIN) } else gdxGame.soundUtil.apply { play(FAIL) }

    }

}