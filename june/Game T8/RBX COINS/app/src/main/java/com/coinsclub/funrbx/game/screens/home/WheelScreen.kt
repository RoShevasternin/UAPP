package com.coinsclub.funrbx.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.button.AYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.popup.APopupCongratulations
import com.coinsclub.funrbx.game.actors.panel.wheel.AWheel
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.VERTICAL_BIAS
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animHideAndDisable
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.actor.animShowAndEnable
import com.coinsclub.funrbx.game.utils.actor.setOnClickListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.overlay.OverlayManager
import com.coinsclub.funrbx.game.utils.runGDX
import com.coinsclub.funrbx.util.log
import kotlinx.coroutines.launch

class WheelScreen: AdvancedScreen() {

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
    private val aWheel    by lazy { AWheel(this) }
    private val aDesc     by lazy { Image(gdxGame.assetsAll.WHEEL_DESC) }
    private val aSpinBtn  by lazy { AYellowButton(this, "SPIN NOW") }

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

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addDesc()
        addWheel()
        addSpinBtn()

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
        aPanelTop.height = 72f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("SPIN WHEEL")
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(345f, 86f)
        add(aDesc) { centerX(); topToBottom(aPanelTop, margin = 16f) }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(426f, 426f)
        add(aWheel) { centerX(); topToBottom(aDesc, -10f) }
    }

    private fun AConstraintLayout.addSpinBtn() {
        aSpinBtn.setSize(345f, 57f)
        add(aSpinBtn) { centerX(); bottomToBottom(margin = 30f) }

        aSpinBtn.setOnClickListener {
            aSpinBtn.disable()

            aWheel.spin { result ->
                log("result = $result")
                aSpinBtn.enable()
                gdxGame.modelPlayer.addRbx(result.sum)

                aPopup.setReward(result.sum)
                overlayManager.show(Overlay.POPUP)
            }
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aSpinBtn) { marginBottom += screen.adBottomUI } } }
        }

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
        aPopup.setSize(344f, 220f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        aPopup.onContinue = { overlayManager.close() }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))
    }

}