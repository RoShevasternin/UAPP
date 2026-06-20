package com.coinsclub.funrbx.game.screens.home

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.button.AImageYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.guess.APanelGuess
import com.coinsclub.funrbx.game.actors.popup.APopupCongratulations
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
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.overlay.OverlayManager
import com.coinsclub.funrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class GuessScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.clearActions(); aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.clearActions(); aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)
        .setBorderAndShadow(border = 1f, shadowX = 2, shadowY = 1)


    private val lsDef by lazy { FontFactory.create(this, parameterDef, fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3) }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aPanelQuess by lazy { APanelGuess(this) }
    private val aDesc       by lazy { Image(gdxGame.assetsAll.DESC_QUESS) }
    private val aCardsLbl   by lazy { Label("3 PICKS LEFT", lsDef) }
    private val aGetFreeBtn by lazy { AImageYellowButton(this, TextureRegionDrawable(gdxGame.assetsAll.GET_FREE_GUESS), Vector2(158f, 20f)) }

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
        addPanelQuess()
        addCardsLbl()
        addGetFreeBtn()

        addDimImg()
        addPopup()

        aPanelQuess.initialize()   // ← старт гри, коли все підключено
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

        aPanelTop.setTitle("RBX GUESSER")
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(345f, 86f)
        add(aDesc) { centerX(); topToBottom(aPanelTop, 16f) }
    }

    private fun AConstraintLayout.addPanelQuess() {
        aPanelQuess.setSize(346f, 374f)
        add(aPanelQuess) { centerX(); topToBottom(aDesc, 16f) }

        aPanelQuess.onPicksChanged = { n ->
            aCardsLbl.setText("$n PICKS LEFT")
        }
        aPanelQuess.onReward = { reward ->
            gdxGame.modelPlayer.addRbx(reward)
        }
        aPanelQuess.onResult = { _, reward ->
            aPopup.setReward(reward)
            overlayManager.show(Overlay.POPUP)
        }
        aPanelQuess.onGetFreeEnabled = { enabled ->
            if (enabled) aGetFreeBtn.enable() else aGetFreeBtn.disable()
        }
    }

    private fun AConstraintLayout.addCardsLbl() {
        aCardsLbl.setSize(344f, 12f)
        add(aCardsLbl) { centerX(aPanelQuess); bottomToBottom(aPanelQuess) }
        aCardsLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addGetFreeBtn() {
        aGetFreeBtn.setSize(345f, 57f)
        add(aGetFreeBtn) { centerX(); bottomToBottom(margin = 24f) }

        aGetFreeBtn.setOnClickListener {
            if (!aPanelQuess.canGetFree()) return@setOnClickListener

            gdxGame.activity.showInterstitial { aPanelQuess.addFreePicks() }
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aGetFreeBtn) { marginBottom += screen.adBottomUI } } }
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