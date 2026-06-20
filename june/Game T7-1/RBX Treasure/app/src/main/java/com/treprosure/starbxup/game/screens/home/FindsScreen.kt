package com.treprosure.starbxup.game.screens.home

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.treprosure.starbxup.adsmodule.AdSizeManager
import com.treprosure.starbxup.game.actors.ATmpGroup
import com.treprosure.starbxup.game.actors.button.AImageYellowButton
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.actors.panel.APanelTop
import com.treprosure.starbxup.game.actors.panel.finds.APanelFinds
import com.treprosure.starbxup.game.actors.popup.APopupCongratulations
import com.treprosure.starbxup.game.utils.Block
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.TIME_ANIM_SCREEN
import com.treprosure.starbxup.game.utils.VERTICAL_BIAS
import com.treprosure.starbxup.game.utils.actor.animDelay
import com.treprosure.starbxup.game.utils.actor.animHide
import com.treprosure.starbxup.game.utils.actor.animHideAndDisable
import com.treprosure.starbxup.game.utils.actor.animShow
import com.treprosure.starbxup.game.utils.actor.animShowAndEnable
import com.treprosure.starbxup.game.utils.actor.setOnClickListener
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame
import com.treprosure.starbxup.game.utils.overlay.OverlayManager
import com.treprosure.starbxup.game.utils.runGDX
import com.treprosure.starbxup.util.log
import kotlinx.coroutines.launch

class FindsScreen: AdvancedScreen() {

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
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(10)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aPanelFinds by lazy { APanelFinds(this) }
    private val aDesc       by lazy { Image(gdxGame.assetsAll.DESC_FINDS) }
    private val aCardsLbl   by lazy { Label("3 cards left to pick", FontFactory.create(this, parameter, fontGenerator_Anton_Regular, GameColor.beige_63553C)) }
    private val aGetFreeBtn by lazy { AImageYellowButton(this, TextureRegionDrawable(gdxGame.assetsAll.GET_FREE_FINDS), Vector2(210f, 20f)) }

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
        addPanelFinds()
        addDesc()
        addCardsLbl()
        addGetFreeBtn()

        addDimImg()
        addPopup()

        aPanelFinds.initialize()   // ← старт гри, коли все підключено
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

        aPanelTop.setTitle("TREASURE FINDS")
    }

    private fun AConstraintLayout.addPanelFinds() {
        aPanelFinds.setSize(344f, 326f)
        add(aPanelFinds) { centerX(); topToBottom(aPanelTop, 80f) }

        aPanelFinds.onPicksChanged = { n ->
            aCardsLbl.setText("$n cards left to pick")
        }
        aPanelFinds.onReward = { reward ->
            gdxGame.modelPlayer.addRbx(reward)
        }
        aPanelFinds.onResult = { wins, reward ->
            showResult(wins, reward)
        }
        aPanelFinds.onGetFreeEnabled = { enabled ->
            if (enabled) aGetFreeBtn.enable() else aGetFreeBtn.disable()
        }
    }

    private fun AConstraintLayout.addDesc() {
        aDesc.setSize(344f, 48f)
        add(aDesc) { centerX(); topToBottom(aPanelFinds, 24f) }
    }

    private fun AConstraintLayout.addCardsLbl() {
        aCardsLbl.setSize(84f, 15f)
        add(aCardsLbl) { centerX(); topToBottom(aDesc, 4f) }
        aCardsLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addGetFreeBtn() {
        aGetFreeBtn.setSize(344f, 52f)
        add(aGetFreeBtn) { centerX(); bottomToBottom(margin = 24f) }

        aGetFreeBtn.setOnClickListener {
            if (!aPanelFinds.canGetFree()) return@setOnClickListener

            gdxGame.activity.showInterstitial { aPanelFinds.addFreePicks() }
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
        aPopup.setSize(312f, 225f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        //aPopup.onContinue = { overlayManager.close() }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false,
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))
    }

    // ------------------------------------------------------------------------
    // Result flow
    // ------------------------------------------------------------------------
    private var pendingReward = 0L

    private fun showResult(wins: Int, reward: Long) {
        pendingReward = reward

        // крок 1 — скільки виграшних карт
        aPopup.setCards(wins)
        aPopup.onContinue = { showRewardStep() }
        overlayManager.show(Overlay.POPUP)
    }

    private fun showRewardStep() {
        // крок 2 — скільки монет
        aPopup.setReward(pendingReward)
        aPopup.onContinue = {
            overlayManager.close()
            animHideScreen { gdxGame.navigationManager.back() }
        }
        // попап вже видно, просто оновили текст
    }

}