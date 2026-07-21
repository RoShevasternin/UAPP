package com.mon.sterbx.game.screens.home

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.button.AOrangeButton
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelTop
import com.mon.sterbx.game.controller.ChestController
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.launch

class ChestScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val lsDef by lazy { FontFactory.create(this, parameterDef, fontGenerator_BeVietnamPro_MediumItalic, Color.BLACK) }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateGuess = gdxGame.assetsAll.GUESS
    private val stateWin   = gdxGame.assetsAll.WIN
    private val stateLose  = gdxGame.assetsAll.FAIL

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop   by lazy { APanelTop(this) }
    private val aPanelQuess by lazy { Image(stateGuess) }
    private val aOpenBtn    by lazy { AOrangeButton(this, "OPEN") }
    private val aLeftLbl    by lazy { Label("ATTEMPTS LEFT: 3", lsDef) }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy { ChestController(coroutine) }

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
        addOpenBtn()
        addLeftLbl()
        addPanelQuess()

        initController()
    }

    private fun initController() {
        controller.onChestState = { state ->
            aPanelQuess.drawable = TextureRegionDrawable(
                when (state) {
                    ChestController.State.CLOSED -> stateGuess
                    ChestController.State.WIN    -> stateWin
                    ChestController.State.LOSE   -> stateLose
                }
            )
        }

        controller.onAttemptsChanged = { left ->
            aLeftLbl.setText("ATTEMPTS LEFT: $left")
        }

        controller.onButtonState = { text, enabled ->
            aOpenBtn.label.setText(text)
            if (enabled) aOpenBtn.enable() else aOpenBtn.disable()
        }

        controller.onReward = { reward ->
            gdxGame.modelPlayer.addRbx(reward)
        }

        controller.onFinished = {
            // спроби скінчились — тут можеш показати попап або вийти
        }

        controller.initialize()
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

        aPanelTop.setTitle("MONSTER CHEST")
    }

    private fun AConstraintLayout.addOpenBtn() {
        aOpenBtn.setSize(344f, 64f)
        add(aOpenBtn) { centerX(); bottomToBottom(margin = 36f) }

        aOpenBtn.setOnClickListener { controller.onButtonClick() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aOpenBtn) { marginBottom = screen.adBottomUI + 36f } } } }

    }

    private fun AConstraintLayout.addLeftLbl() {
        aLeftLbl.setSize(131f, 15f)
        add(aLeftLbl) { centerX(); bottomToTop(aOpenBtn, 8f) }
        aLeftLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addPanelQuess() {
        aPanelQuess.setSize(467f, 465f)
        add(aPanelQuess) { centerX(); topToBottom(aPanelTop); bottomToTop(aOpenBtn) }
    }

}