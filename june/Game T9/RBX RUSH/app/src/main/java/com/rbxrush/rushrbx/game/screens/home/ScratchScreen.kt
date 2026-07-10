package com.rbxrush.rushrbx.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.adsmodule.AdSizeManager
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.actors.panel.APanelRBX
import com.rbxrush.rushrbx.game.actors.panel.APanelTop
import com.rbxrush.rushrbx.game.actors.panel.scratch.APanelScratch
import com.rbxrush.rushrbx.game.actors.popup.APopupCongratulations
import com.rbxrush.rushrbx.game.utils.Block
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.TIME_ANIM_SCREEN
import com.rbxrush.rushrbx.game.utils.VERTICAL_BIAS
import com.rbxrush.rushrbx.game.utils.actor.animDelay
import com.rbxrush.rushrbx.game.utils.actor.animHide
import com.rbxrush.rushrbx.game.utils.actor.animHideAndDisable
import com.rbxrush.rushrbx.game.utils.actor.animShow
import com.rbxrush.rushrbx.game.utils.actor.animShowAndEnable
import com.rbxrush.rushrbx.game.utils.actor.setOnClickListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.overlay.OverlayManager
import com.rbxrush.rushrbx.game.utils.runGDX
import com.rbxrush.rushrbx.util.log
import kotlinx.coroutines.launch

class ScratchScreen: AdvancedScreen() {

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
    private val aDesc     by lazy { Image(gdxGame.assetsAll.SCRATCH_DESC) }
    private val aScratch  by lazy { APanelScratch(this) }
    private val aPanelRBX by lazy { APanelRBX(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_60)) }
    private val aPopup  by lazy { APopupCongratulations(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.22f
    private val timeHide = 0.22f

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
        addScratch()
        addDesc()
        addPanelRBX()

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

        aPanelTop.setTitle("Scratch Card")
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(344f, 349f)
        add(aScratch) { centerX(); topToBottom(aPanelTop); bottomToBottom(); verticalBias = 0.65f}

        aScratch.onResult = {
            gdxGame.modelPlayer.addRbx(it)
            aPopup.setReward(it)
            overlayManager.show(Overlay.POPUP)
        }
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(344f, 19f)
        add(aDesc) { centerX(); bottomToTop(aScratch, 12f) }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(56f, 32f)
        add(aPanelRBX) { centerX(); topToBottom(aScratch, margin = 12f) }
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
        aPopup.setSize(312f, 253f)
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