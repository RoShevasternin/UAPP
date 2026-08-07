package com.diam.ondbit.game.screens.home

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.utils.Block
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
import com.diam.ondbit.game.utils.runGDX

class BoostScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
//    private enum class Overlay { POPUP }
//
//    private val overlayManager = OverlayManager(
//        onShowDim = { aDimImg.animShowAndEnable(timeShow) },
//        onHideDim = { aDimImg.animHideAndDisable(timeHide) },
//    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
//    private val timeShow = 0.2f
//    private val timeHide = 0.2f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop  by lazy { APanelTop(this) }
    private val aBoostImg   by lazy { Image(gdxGame.assetsAll.BOOST) }
    //private val aRewardBtn by lazy { AYellowButton(this, "CLAIM") }

    //private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_70)) }
    //private val aPopup  by lazy { APopup(this) }

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
        //addClaimBtn()

        //addDimImg()
        //addPopup()
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

        aPanelTop.setTitle("UNLOCK COSMIC\nDIAMONDS")
    }

    private fun AConstraintLayout.addBoostImg() {
        aBoostImg.setSize(344f, 410f)
        add(aBoostImg) { centerX(); topToBottom(aPanelTop, 24f) }

        aBoostImg.setOnClickListener {
            gdxGame.activity.showInterstitial {
                runGDX {
                    gdxGame.soundUtil.apply { play(REWARD) }
                    gdxGame.modelPlayer.addRbx(25)
                    //aPopup.setReward(50)
                    //overlayManager.show(Overlay.POPUP)
                }
            }
        }
    }

    private fun AConstraintLayout.addClaimBtn() {
//        aRewardBtn.setSize(312f, 40f)
//        add(aRewardBtn) { centerX(aBoostImg); bottomToBottom(aBoostImg, 16f) }
    }

//    private fun AConstraintLayout.addDimImg() {
//        aDimImg.animHideAndDisable()
//        add(aDimImg) {
//            matchConstraint()
//            centerX(); bottomToBottom(); topToTop(margin = -safeStatusBarUI)
//        }
//        aDimImg.setOnClickListener(null) {
//            if (overlayManager.isClosable) overlayManager.close()
//        }
//    }
//
//    private fun AConstraintLayout.addPopup() {
//        aPopup.animHideAndDisable()
//        aPopup.setSize(344f, 294f)
//        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }
//
//        overlayManager.register(Overlay.POPUP, OverlayManager.Config(
//            showDim    = true,
//            isClosable = false, // поки не забрав нагороду — не закривати кліком по фону
//            onShow     = { aPopup.animShowAndEnable(timeShow) },
//            onHide     = { aPopup.animHideAndDisable(timeHide) },
//        ))
//
//        aPopup.onClaim = { overlayManager.close() }
//    }

}