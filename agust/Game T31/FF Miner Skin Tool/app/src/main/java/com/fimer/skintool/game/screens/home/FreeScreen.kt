package com.fimer.skintool.game.screens.home

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelTop
import com.fimer.skintool.game.actors.popup.APopup
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.GameColor
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.VERTICAL_BIAS
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animHideAndDisable
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.actor.animShowAndEnable
import com.fimer.skintool.game.utils.actor.setOnClickListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.overlay.OverlayManager
import com.fimer.skintool.game.utils.runGDX

class FreeScreen: AdvancedScreen() {

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
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aBoostImg   by lazy { Image(gdxGame.assetsAll.FREE) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_85)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addBoostImg()

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

        aPanelTop.setTitle("EARN FREE DIAMONDS")
    }

    private fun AConstraintLayout.addBoostImg() {
        aBoostImg.setSize(344f, 376f)
        add(aBoostImg) { centerX(); topToBottom(aPanelTop, 24f) }

        aBoostImg.setOnClickListener {
            gdxGame.activity.showInterstitial {
                runGDX {
                    aPopup.setReward(listOf(55, 75, 100).random().toLong())
                    overlayManager.show(Overlay.POPUP)
                }
            }
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
        aPopup.setSize(296f, 200f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(Overlay.POPUP, OverlayManager.Config(
            showDim    = true,
            isClosable = false, // поки не забрав нагороду — не закривати кліком по фону
            onShow     = { aPopup.animShowAndEnable(timeShow) },
            onHide     = { aPopup.animHideAndDisable(timeHide) },
        ))

        aPopup.onClaim = { overlayManager.close() }
    }

}