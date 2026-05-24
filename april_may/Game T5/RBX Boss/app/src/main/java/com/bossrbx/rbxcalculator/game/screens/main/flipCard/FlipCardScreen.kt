package com.bossrbx.rbxcalculator.game.screens.main.flipCard

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.ACardReward
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.ACircleCard
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.APanelCongratulations
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.APanelFlipCard
import com.bossrbx.rbxcalculator.game.screens.main.flipCard.state.StateCardBoss
import com.bossrbx.rbxcalculator.game.screens.main.flipCard.state.StateCongratulation
import com.bossrbx.rbxcalculator.game.screens.main.flipCard.state.StateFlipCard
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.actor.disable
import com.bossrbx.rbxcalculator.game.utils.actor.setOnTouchListener
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenStateMachine

class FlipCardScreen: AdvancedScreen() {

    private val text = """Tap on the card
        |to flip it""".trimMargin()

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(text)
        .setSize(12)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop             by lazy { APanelTop(this) }
    private val aCircleCard           by lazy { ACircleCard(this) }
    private val aShadowImg            by lazy { Image(gdxGame.assetsAll.SHADOW_FLIP) }
    private val aPanelFlipCard        by lazy { APanelFlipCard(this) }
    private val aPanelCongratulations by lazy { APanelCongratulations(this) }
    private val aTextLbl              by lazy { Label(text, FontFactory.create(this, parameter, fontGenerator_Light, GameColor.gray_808080)) }
    private val aCardBossImg          by lazy { Image(gdxGame.assetsAll.CARD_BOSS) }
    private val aCardReward           by lazy { ACardReward(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_90)) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val randomReward = listOf(15, 25, 50, 75, 100).random()

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateMachine = ScreenStateMachine()

    private val stateFlipCard       by lazy { StateFlipCard(stateMachine, aPanelFlipCard) }
    private val stateCardBoss       by lazy { StateCardBoss(stateMachine, aDimImg, aCardBossImg) { goToStateCongratulation() } }
    private val stateCongratulation by lazy { StateCongratulation(stateMachine, aDimImg, aPanelCongratulations, aCardReward, aPanelFlipCard) }

    // Переходи — викликаєш з будь-якого місця
    private fun goToStateFlipCard() {
        stateMachine.setState(stateFlipCard)
    }
    private fun goToStateCardBoss() {
        stateMachine.setState(stateCardBoss)
    }
    private fun goToStateCongratulation() {
        stateMachine.setState(stateCongratulation)
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        gdxGame.activity.hideBanner()

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen {
            aCircleCard.animRotateToStart()
        }
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.showBanner()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addCircleCard()
        addShadowImg()
        addPanelFlipCard()
        addPanelCongratulations()
        addCardBossImg()
        addCardReward()

        addDimImg()

        goToStateFlipCard()
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
        aPanelTop.setSize(WIDTH, 64f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Flip Card")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addCircleCard() {
        aCircleCard.setSize(906f, 906f)
        add(aCircleCard) { centerX(); topToBottom(aPanelTop, -3f) }

        aTextLbl.setSize(90f, 40f)
        add(aTextLbl) { centerX(); topToTop(aCircleCard, 377f) }

        aTextLbl.setAlignment(Align.center)
        aTextLbl.disable()

        aCircleCard.setOnTouchListener {
            aCircleCard.disable()
            goToStateCardBoss()
        }
    }

    private fun AConstraintLayout.addShadowImg() {
        aShadowImg.height = 245f
        add(aShadowImg) {
            centerX(); bottomToBottom()
            matchWidth()
        }
    }

    private fun AConstraintLayout.addPanelFlipCard() {
        aPanelFlipCard.setSize(344f, 196f)
        add(aPanelFlipCard) { centerX(); bottomToBottom(aShadowImg, 37f) }

        aPanelFlipCard.animHideAndDisable()
    }

    private fun AConstraintLayout.addPanelCongratulations() {
        aPanelCongratulations.setSize(344f, 176f)
        add(aPanelCongratulations) { centerX(); bottomToBottom(aShadowImg, 37f) }

        aPanelCongratulations.animHideAndDisable()
        aPanelCongratulations.setReward(randomReward)

        aPanelCongratulations.onGood = {
            aPanelCongratulations.disable()
            gdxGame.modelPlayer.addRbx(randomReward.toLong())
            animHideScreen { gdxGame.navigationManager.back() }
        }
    }

    private fun AConstraintLayout.addCardBossImg() {
        aCardBossImg.setSize(240f, 335f)
        add(aCardBossImg) {
            center()
            verticalBias = 0.8f
        }

        aCardBossImg.animHideAndDisable()
    }

    private fun AConstraintLayout.addCardReward() {
        aCardReward.setSize(240f, 335f)
        add(aCardReward) {
            center()
            verticalBias = 0.8f
        }

        aCardReward.animHideAndDisable()
        aCardReward.setReward(randomReward)
    }



    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) { fillParent() }
    }

}