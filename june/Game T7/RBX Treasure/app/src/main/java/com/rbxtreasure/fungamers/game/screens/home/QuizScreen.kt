package com.rbxtreasure.fungamers.game.screens.home

import com.rbxtreasure.fungamers.businesModule.backend.Events
import com.rbxtreasure.fungamers.businesModule.economy.Econ
import com.rbxtreasure.fungamers.businesModule.economy.Wallet
import com.rbxtreasure.fungamers.businesModule.backend.Bt
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.actors.panel.APanelTop
import com.rbxtreasure.fungamers.game.actors.panel.quiz.APanelQuiz
import com.rbxtreasure.fungamers.game.actors.popup.APopupCongratulations
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.TIME_ANIM_SCREEN
import com.rbxtreasure.fungamers.game.utils.VERTICAL_BIAS
import com.rbxtreasure.fungamers.game.utils.actor.animDelay
import com.rbxtreasure.fungamers.game.utils.actor.animHide
import com.rbxtreasure.fungamers.game.utils.actor.animHideAndDisable
import com.rbxtreasure.fungamers.game.utils.actor.animShow
import com.rbxtreasure.fungamers.game.utils.actor.animShowAndEnable
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.overlay.OverlayManager

class QuizScreen: AdvancedScreen() {

    companion object { private const val PRICE_DEF = 0 }

    override val analyticsBt    = Bt.QUIZ
    override val analyticsBlock = "quiz_screen"

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
    private val aPanelTop by lazy { APanelTop(this) }
    private val aQuiz     by lazy { APanelQuiz(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_60)) }
    private val aPopup  by lazy { APopupCongratulations(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.25f
    private val timeHide = 0.20f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
        chargeEntryPrice()
    }
    // ── Ціна спроби ───────────────────────────────────────────────────────────
    // Один прохід квізу на візит (controller.initialize при створенні) —
    // «спроба» це вхід на екран. Дефолт 0: spend(0) віддає true одразу.
    private fun chargeEntryPrice() {
        val price = Econ.price(analyticsBlock!!, PRICE_DEF)
        if (!Wallet.spend(price, bt = analyticsBt!!, block = analyticsBlock!!)) {
            gdxGame.activity.showToast("Not enough coins — you need $price")
            animHideScreen { gdxGame.navigationManager.back() }
        }
    }


    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addQuiz()

        addDimImg()
        addPopup()
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
        aPanelTop.height = 56f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("QUIZ ISLAND")
    }

    private fun AConstraintLayout.addQuiz() {
        aQuiz.setSize(344f, 359f)
        add(aQuiz) { centerX(); topToBottom(aPanelTop, margin = 50f) }

        aQuiz.onWrong = { penalty ->
            // spend не пускає баланс у мінус і при 0 нічого не робить
            Wallet.spend(penalty.toInt(), bt = analyticsBt!!, block = analyticsBlock!!)
        }
        aQuiz.onCorrect = { reward ->
            Wallet.add(reward.toInt(), bt = analyticsBt!!, block = analyticsBlock!!)
        }
        aQuiz.onFinished = { _, totalReward ->
            aPopup.setReward(totalReward)
            overlayManager.show(Overlay.POPUP)
        }
        aQuiz.initialize()
    }

    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) { fillParent() }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.animHideAndDisable()
        aPopup.setSize(312f, 225f)
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