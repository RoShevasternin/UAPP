package com.diam.ondbit.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.actors.panel.quiz.APanelQuiz
import com.diam.ondbit.game.actors.popup.APopup
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.VERTICAL_BIAS
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animHideAndDisable
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.actor.animShowAndEnable
import com.diam.ondbit.game.utils.actor.setOnClickListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.overlay.OverlayManager

class QuizScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.2f
    private val timeHide = 0.2f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop  by lazy { APanelTop(this) }
    private val aDescImg   by lazy { Image(gdxGame.assetsAll.DESC_QUIZ) }
    private val aPanelQuiz by lazy { APanelQuiz(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_70)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addDescImg()
        addPanelPick()

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
        aPanelTop.setSize(344f, 32f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("ASTRO QUIZ")
    }

    private fun AConstraintLayout.addDescImg() {
        aDescImg.setSize(280f, 29f)
        add(aDescImg) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addPanelPick() {
        aPanelQuiz.setSize(344f, 446f)
        add(aPanelQuiz) { centerX(); topToBottom(aDescImg, 16f); }

        aPanelQuiz.onFinish = { totalReward ->
            aPopup.setReward(totalReward)
            overlayManager.show(Overlay.POPUP)
        }
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
        aPopup.setSize(344f, 410f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(Overlay.POPUP, OverlayManager.Config(
            showDim    = true,
            isClosable = false,
            onShow     = { aPopup.animShowAndEnable(timeShow) },
            onHide     = { aPopup.animHideAndDisable(timeHide) },
        ))

        aPopup.onClaim = {
            overlayManager.close()
            aPanelQuiz.newGame()
        }
    }

}