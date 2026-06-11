package com.rbxtreasure.fungamers.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.adsmodule.AdSizeManager
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.actors.panel.APanelTop
import com.rbxtreasure.fungamers.game.actors.panel.scratch.APanelScratch
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
import com.rbxtreasure.fungamers.game.utils.runGDX
import com.rbxtreasure.fungamers.util.log
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
    private val aScratch  by lazy { APanelScratch(this) }
    private val aDesc     by lazy { Image(gdxGame.assetsAll.SCRATCH_DESC) }

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
        addScratch()
        addDesc()

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

        aPanelTop.setTitle("SCRATCH MAP")
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(344f, 322f)
        add(aScratch) { centerX(); topToBottom(aPanelTop, 99f)}

        aScratch.onResult = {
            gdxGame.modelPlayer.addRbx(it)
            aPopup.setReward(it)
            overlayManager.show(Overlay.POPUP)
        }
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(345f, 47f)
        add(aDesc) { centerX(); topToBottom(aScratch, 23f) }
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