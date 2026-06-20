package com.coinsclub.funrbx.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.AScrollPane
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.daily.APanelDaily
import com.coinsclub.funrbx.game.screens.home.GuessScreen
import com.coinsclub.funrbx.game.screens.home.FreeScreen
import com.coinsclub.funrbx.game.screens.home.QuizScreen
import com.coinsclub.funrbx.game.screens.home.ScratchScreen
import com.coinsclub.funrbx.game.screens.home.WheelScreen
import com.coinsclub.funrbx.game.screens.home.character.SelectCharacterScreen
import com.coinsclub.funrbx.game.screens.home.converter.SelectConverterScreen
import com.coinsclub.funrbx.game.screens.home.outfit.SelectOutfitScreen
import com.coinsclub.funrbx.game.utils.actor.setOnTouchListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelBalance  = APanelRBX(screen)
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[1])
    private val aDailyImg      = APanelDaily(screen)
    private val aPanel5        = APanel5(screen)
    private val aPanel2        = APanel2(screen)

    private val aVertical = AAutoLayout(screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        gapMain       = 8f,
        sizingH       = AAutoLayout.Sizing.HUG,
        alignCross    = AAutoLayout.AlignCross.CENTER,
        paddingBottom = 20f,
    )
    private val aScrollPane = AScrollPane(aVertical)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        setupVerticalGroup()

        with(aVertical) {
            addBalance()
            addConverter()
            addDaily()
            addPanel5()
            addPanel2()
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun setupVerticalGroup() {
        aVertical.setSize(width, 1f)
        aVertical.minH = height
    }

    private fun AAutoLayout.addBalance() {
        aPanelBalance.setSize(342f, 99f)
        add(aPanelBalance)
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(348f, 104f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(SelectConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addDaily() {
        aDailyImg.setSize(348f, 169f)
        add(aDailyImg)

    //    aDailyImg.setOnTouchListener { navTo(DailyScreen::class.java.name) }
    }

    private fun AAutoLayout.addPanel5() {
        aPanel5.setSize(346f, 495f)
        add(aPanel5)

        aPanel5.apply {
            onWheel   = { navTo(WheelScreen::class.java.name) }
            onScratch = { navTo(ScratchScreen::class.java.name) }
            onQuiz    = { navTo(QuizScreen::class.java.name) }
            onGuess   = { navTo(GuessScreen::class.java.name) }
            onFree    = { navTo(FreeScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addPanel2() {
        aPanel2.setSize(346f, 234f)
        add(aPanel2)

        aPanel2.apply {
            onCharacters = { navTo(SelectCharacterScreen::class.java.name) }
            onAnimations = { navTo(SelectOutfitScreen::class.java.name) }
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun Image.clickScale() {
        clearActions()
        setOrigin(Align.center)
            addAction(
                Actions.sequence(
                    Actions.scaleTo(0.98f, 0.98f, 0.07f, Interpolation.fastSlow),
                    Actions.scaleTo(1.00f, 1.00f, 0.05f, Interpolation.slowFast),
                )
            )
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun navTo(screenName: String) {
        screen.animHideScreen {
            gdxGame.navigationManager.navigate(screenName, screen::class.java.name)
        }
    }

}