package com.diam.ondbit.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
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
import com.diam.ondbit.game.actors.panel.scratch.APanelScratch

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

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_70)) }
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
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addScratch()

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

        aPanelTop.setTitle("CRYSTAL SCRATCH")
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(344f, 536f)
        add(aScratch) { centerX(); topToBottom(aPanelTop, 24f) }

        aScratch.onResult = {
            aPopup.setReward(it)
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
        aPopup.setSize(344f, 374f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
        ))
        aPopup.onClaim = {
            overlayManager.close()
            animHideScreen { gdxGame.navigationManager.back() }
        }
    }

}