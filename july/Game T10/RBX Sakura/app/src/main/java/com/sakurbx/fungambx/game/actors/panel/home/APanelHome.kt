package com.sakurbx.fungambx.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.AScrollPane
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.daily.APanelDaily
import com.sakurbx.fungambx.game.screens.home.GuessScreen
import com.sakurbx.fungambx.game.screens.home.FreeScreen
import com.sakurbx.fungambx.game.screens.home.QuizScreen
import com.sakurbx.fungambx.game.screens.home.ScratchScreen
import com.sakurbx.fungambx.game.screens.home.WheelScreen
import com.sakurbx.fungambx.game.screens.home.character.CharacterScreen
import com.sakurbx.fungambx.game.screens.home.character.SelectCharacterScreen
import com.sakurbx.fungambx.game.screens.home.converter.SelectConverterScreen
import com.sakurbx.fungambx.game.screens.home.outfit.SelectOutfitScreen
import com.sakurbx.fungambx.game.utils.actor.setOnTouchListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDailyImg      = APanelDaily(screen)
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aPanel4        = APanel4(screen)
    private val aFreeImg       = Image(gdxGame.assetsAll.listHomeContent[2])
    private val aCharacterImg  = Image(gdxGame.assetsAll.listHomeContent[3])
    private val aOutfitImg     = Image(gdxGame.assetsAll.listHomeContent[4])

    private val aVertical   = AAutoLayout(screen,
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
            addDaily()
            addConverter()
            addPanel4()
            addFreeImg()
            addCharacterImg()
            addOutfitImg()
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

    private fun AAutoLayout.addDaily() {
        aDailyImg.setSize(344f, 167f)
        add(aDailyImg)
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(344f, 97f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(SelectConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addPanel4() {
        aPanel4.setSize(344f, 269f)
        add(aPanel4)

        aPanel4.apply {
            onWheel   = { navTo(WheelScreen  ::class.java.name) }
            onScratch = { navTo(ScratchScreen::class.java.name) }
            onQuiz    = { navTo(QuizScreen   ::class.java.name) }
            onGuess   = { navTo(GuessScreen  ::class.java.name) }
        }
    }

    private fun AAutoLayout.addFreeImg() {
        aFreeImg.setSize(344f, 130f)
        add(aFreeImg)

        aFreeImg.setOnTouchListener {
            aFreeImg.clickScale()
            navTo(FreeScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addCharacterImg() {
        aCharacterImg.setSize(344f, 97f)
        add(aCharacterImg)

        aCharacterImg.setOnTouchListener {
            aCharacterImg.clickScale()
            navTo(SelectCharacterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addOutfitImg() {
        aOutfitImg.setSize(344f, 97f)
        add(aOutfitImg)

        aOutfitImg.setOnTouchListener {
            aOutfitImg.clickScale()
            navTo(SelectOutfitScreen::class.java.name)
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
                Actions.scaleTo(0.90f, 0.90f, 0.07f, Interpolation.sineOut),   // втиснулась
                Actions.scaleTo(1.06f, 1.06f, 0.10f, Interpolation.sineOut),   // відскок з перельотом
                Actions.scaleTo(1.00f, 1.00f, 0.08f, Interpolation.sineIn),    // осіла
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