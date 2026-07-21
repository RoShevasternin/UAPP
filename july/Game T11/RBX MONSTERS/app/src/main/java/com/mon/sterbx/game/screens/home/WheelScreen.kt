package com.mon.sterbx.game.screens.home

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.button.AOrangeButton
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelRBX
import com.mon.sterbx.game.actors.panel.APanelTop
import com.mon.sterbx.game.actors.popup.APopup
import com.mon.sterbx.game.actors.panel.wheel.AWheel
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.VERTICAL_BIAS
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animHideAndDisable
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.actor.animShowAndEnable
import com.mon.sterbx.game.utils.actor.setOnClickListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.overlay.OverlayManager
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.launch

class WheelScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val lsDef by lazy { FontFactory.create(this, parameterDef, fontGenerator_BeVietnamPro_MediumItalic, Color.BLACK) }

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
    private val aPanelTop     by lazy { APanelTop(this) }
    private val aWheel        by lazy { AWheel(this) }
    private val aPanelRBX     by lazy { APanelRBX(this) }
    private val aSpinBtn      by lazy { AOrangeButton(this, "SPIN") }
    private val aSpinPriceLbl by lazy { Label("SPIN PRICE: 100", lsDef) }


    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_75)) }
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
        setBackground(gdxGame.assetsAll.BACKGROUND_YELLOW)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addWheel()
        addPanelRBX()
        addSpinBtn()

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
        aPanelTop.setSize(WIDTH, 42f)
        add(aPanelTop) { centerX(); topToTop(margin = 12f) }

        aPanelTop.setTitle("LUCKY WHEEL")
    }

    private fun AConstraintLayout.addSpinBtn() {
        aSpinBtn.setSize(344f, 64f)
        add(aSpinBtn) { centerX(); bottomToBottom(margin = 36f) }

        aSpinBtn.setOnClickListener {
            aSpinBtn.disable()

            aWheel.spin { result ->
                aSpinBtn.enable()
                gdxGame.modelPlayer.addRbx(result.sum)

                aPopup.setReward(result.sum)
                overlayManager.show(Overlay.POPUP)
            }
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aSpinBtn) { marginBottom = screen.adBottomUI + 36f } } } }
    }

    private fun AConstraintLayout.addWheel() {
        aWheel.setSize(344f, 344f)
        add(aWheel) { centerX(); bottomToTop(aSpinBtn, 114f) }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(187f, 47f)
        add(aPanelRBX) { centerX(); topToBottom(aWheel, margin = 6f) }

        aSpinPriceLbl.setSize(183f, 15f)
        add(aSpinPriceLbl) { centerX(aPanelRBX); topToBottom(aPanelRBX, margin = 6f) }
        aSpinPriceLbl.setAlignment(Align.center)
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
        aPopup.setSize(344f, 368f)
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