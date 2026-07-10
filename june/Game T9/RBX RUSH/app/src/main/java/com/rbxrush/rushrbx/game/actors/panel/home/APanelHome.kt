package com.rbxrush.rushrbx.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.AScrollPane
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.actors.panel.daily.APanelDaily
import com.rbxrush.rushrbx.game.screens.home.GuessScreen
import com.rbxrush.rushrbx.game.screens.home.FreeScreen
import com.rbxrush.rushrbx.game.screens.home.QuizScreen
import com.rbxrush.rushrbx.game.screens.home.ScratchScreen
import com.rbxrush.rushrbx.game.screens.home.WheelScreen
import com.rbxrush.rushrbx.game.screens.home.character.SelectCharacterScreen
import com.rbxrush.rushrbx.game.screens.home.converter.SelectConverterScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.SelectOutfitScreen
import com.rbxrush.rushrbx.game.utils.actor.setOnTouchListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aDailyImg      = APanelDaily(screen)
    private val aPanel4        = APanel4(screen)
    private val aFreeImg       = Image(gdxGame.assetsAll.listHomeContent[2])
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
            addConverter()
            addDaily()
            addPanel4()
            addFreeImg()
            addPanel2()
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aVertical.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun setupVerticalGroup() {
        aVertical.width = width
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(344f, 132f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(SelectConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addDaily() {
        aDailyImg.setSize(344f, 214f)
        add(aDailyImg)
    }

    private fun AAutoLayout.addPanel4() {
        aPanel4.setSize(344f, 344f)
        add(aPanel4)

        aPanel4.apply {
            onWheel   = { navTo(WheelScreen::class.java.name) }
            onQuiz    = { navTo(QuizScreen::class.java.name) }
            onScratch = { navTo(ScratchScreen::class.java.name) }
            onGuess   = { navTo(GuessScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addFreeImg() {
        aFreeImg.setSize(344f, 132f)
        add(aFreeImg)

        aFreeImg.setOnTouchListener {
            aFreeImg.clickScale()
            navTo(FreeScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addPanel2() {
        aPanel2.setSize(344f, 145f)
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
                    Actions.scaleTo(0.99f, 0.99f, 0.05f, Interpolation.sine),
                    Actions.scaleTo(1.00f, 1.00f, 0.05f, Interpolation.sine),
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