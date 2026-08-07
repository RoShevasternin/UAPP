package com.racing.funtols.game.screens.home

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.adsmodule.AdSizeManager
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTop
import com.racing.funtols.game.actors.panel.plate.APanelPlate
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
import com.racing.funtols.game.utils.runGDX
import kotlinx.coroutines.launch

class PlateScreen: AdvancedScreen() {

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

    // Нагорода за зібрані 4 таблички
    private val rewardRbx = 30L

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aDescImg    by lazy { Image(gdxGame.assetsAll.PLATE_DESC) }
    private val aPanelPlate by lazy { APanelPlate(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_70)) }
    private val aPopup  by lazy { APopup(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f

        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addDescImg()
        addPanelPlate()

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

        aPanelTop.setTitle("LICENSE PLATE PUZZLE")
    }

    private fun AConstraintLayout.addDescImg() {
        aDescImg.setSize(300f, 40f)
        add(aDescImg) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addPanelPlate() {
        aPanelPlate.setSize(344f, 308f)
        add(aPanelPlate) { centerX(); topToBottom(aDescImg); bottomToBottom() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelPlate) { marginBottom = screen.adBottomUI + 36f } } } }

        // Усі 4 таблички зібрано → попап з нагородою
        aPanelPlate.onWin = {
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
            aPanelPlate.newGame()
        }
    }

}