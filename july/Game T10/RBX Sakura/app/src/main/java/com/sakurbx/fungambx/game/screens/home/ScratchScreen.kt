package com.sakurbx.fungambx.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelRBX
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.scratch.APanelScratch
import com.sakurbx.fungambx.game.actors.popup.APopupSakura
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.VERTICAL_BIAS
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animHideAndDisable
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.actor.animShowAndEnable
import com.sakurbx.fungambx.game.utils.actor.setOnClickListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.overlay.OverlayManager

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

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_60)) }
    private val aPopup  by lazy { APopupSakura(this) }

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
        setBackground(gdxGame.assetsAll.BACKGROUND_PUPRLE)

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
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 56f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }

        aPanelTop.setTitle("SCRATCH CARD")
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(344f, 344f)
        add(aScratch) { center() }

        aScratch.onResult = {
            gdxGame.modelPlayer.addRbx(it)
            aPopup.setReward(it)
            overlayManager.show(Overlay.POPUP)
        }
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(261f, 20f)
        add(aDesc) { centerX(); bottomToTop(aScratch, 16f) }
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
        aPopup.setSize(344f, 374f)
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