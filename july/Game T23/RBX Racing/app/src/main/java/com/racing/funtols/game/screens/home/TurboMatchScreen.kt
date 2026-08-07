package com.racing.funtols.game.screens.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTop
import com.racing.funtols.game.actors.panel.match.APanelMatch
import com.racing.funtols.game.actors.popup.APopup
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.VERTICAL_BIAS
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animHideAndDisable
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.actor.animShowAndEnable
import com.racing.funtols.game.utils.actor.setOnClickListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.overlay.OverlayManager

class TurboMatchScreen: AdvancedScreen() {

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

    // Нагорода за зібрані 6 пар
    private val rewardRbx = 20L

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aDescImg    by lazy { Image(gdxGame.assetsAll.TURBO_DESC) }
    private val aPanelMatch by lazy { APanelMatch(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_70)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addDescImg()
        addPanelMatch()

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

        aPanelTop.setTitle("TURBO MATCH")
    }

    private fun AConstraintLayout.addDescImg() {
        aDescImg.setSize(257f, 20f)
        add(aDescImg) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addPanelMatch() {
        aPanelMatch.setSize(344f, 460f)
        add(aPanelMatch) { centerX(); topToBottom(aDescImg, 16f) }

        // Усі 6 пар зібрано → показуємо попап з нагородою
        aPanelMatch.onWin = {
            aPopup.setReward(rewardRbx)
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
        aPopup.setSize(344f, 294f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(Overlay.POPUP, OverlayManager.Config(
            showDim    = true,
            isClosable = false, // поки не забрав нагороду — не закривати кліком по фону
            onShow     = { aPopup.animShowAndEnable(timeShow) },
            onHide     = { aPopup.animHideAndDisable(timeHide) },
        ))

        aPopup.onClaim = {
            overlayManager.close()
            aPanelMatch.newGame()
        }
    }

}