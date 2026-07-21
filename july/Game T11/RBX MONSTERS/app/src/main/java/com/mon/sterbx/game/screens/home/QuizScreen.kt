package com.mon.sterbx.game.screens.home

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.video.VideoPlayer
import com.badlogic.gdx.video.VideoPlayerCreator
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.AVideoActor
import com.mon.sterbx.game.actors.button.base.AButtonAnim
import com.mon.sterbx.game.actors.button.base.AButtonStyles
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelTop
import com.mon.sterbx.game.actors.panel.quiz.APanelQuiz
import com.mon.sterbx.game.actors.popup.APopup
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.VERTICAL_BIAS
import com.mon.sterbx.game.utils.actor.animDelay
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animHideAndDisable
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.actor.animShowAndEnable
import com.mon.sterbx.game.utils.actor.setOnClickListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.overlay.OverlayManager
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.launch

class QuizScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.clearActions(); aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.clearActions(); aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aVideoActor by lazy { Image(gdxGame.assetsAll.MONSTER) }
    private val aQuiz       by lazy { APanelQuiz(this) }
    private val aFalseBtn   by lazy { AButtonAnim(this, AButtonStyles.Anim.FALSE) }
    private val aTrueBtn    by lazy { AButtonAnim(this, AButtonStyles.Anim.TRUE) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_75)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.2f
    private val timeHide = 0.2f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_YELLOW)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addContentVideoActor()
        addQuiz()
        addBtns()

        addDimImg()
        addPopup()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 42f)
        add(aPanelTop) { centerX(); topToTop(margin = 12f) }

        aPanelTop.setTitle("MONSTER QUIZ")
    }

    private fun AConstraintLayout.addContentVideoActor() {
        aVideoActor.setSize(336f, 581f)
        add(aVideoActor) { startToStart(); bottomToBottom(margin = -76f) }
    }

    private fun AConstraintLayout.addQuiz() {
        aQuiz.setSize(375f, 218f)
        add(aQuiz) { endToEnd(aVideoActor, -40f); bottomToTop(aVideoActor, -60f); }

        aQuiz.onCorrect = { reward ->
            gdxGame.modelPlayer.addRbx(reward)
        }
        aQuiz.onFinished = { _, totalReward ->
            aPopup.setReward(totalReward)
            overlayManager.show(Overlay.POPUP)
        }
        aQuiz.initialize()
    }

    private fun AConstraintLayout.addBtns() {
        aFalseBtn.setSize(344f, 64f)
        add(aFalseBtn) { centerX(); bottomToBottom(margin = 36f) }
        aTrueBtn.setSize(344f, 64f)
        add(aTrueBtn) { centerX(); bottomToTop(aFalseBtn, 8f) }

        aFalseBtn.setOnClickListener { aQuiz.controller.answer(false) }
        aTrueBtn.setOnClickListener  { aQuiz.controller.answer(true) }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aFalseBtn) { marginBottom = screen.adBottomUI + 36f } } } }
    }

    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) {
            matchConstraint()
            centerX(); bottomToBottom(); topToTop(margin = -safeStatusBarUI)
        }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.animHideAndDisable()
        aPopup.setSize(344f, 368f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        aPopup.onContinue = {
            overlayManager.close()
            animHideScreen { gdxGame.navigationManager.back() }
        }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))
    }

}