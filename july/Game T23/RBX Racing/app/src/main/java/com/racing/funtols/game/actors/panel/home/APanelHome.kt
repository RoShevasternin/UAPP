package com.racing.funtols.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.AScrollPane
import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.daily.APanelDaily
import com.racing.funtols.game.screens.home.BoostScreen
import com.racing.funtols.game.screens.home.ConverterScreen
import com.racing.funtols.game.screens.home.PickScreen
import com.racing.funtols.game.screens.home.PlateScreen
import com.racing.funtols.game.screens.home.TurboMatchScreen
import com.racing.funtols.game.screens.home.character.SelectCharactersScreen
import com.racing.funtols.game.screens.home.outfit.SelectOutfitScreen
import com.racing.funtols.game.utils.actor.setOnTouchListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelDaily    = APanelDaily(screen)
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aPanel2_1      = APanel2_1(screen)
    private val aPanel2_2      = APanel2_2(screen)
    private val aCharactersImg = Image(gdxGame.assetsAll.listHomeContent[3])
    private val aAnimationImg  = Image(gdxGame.assetsAll.listHomeContent[4])

    private val aVertical = AAutoLayout(
        screen,
        direction = AAutoLayout.Direction.VERTICAL,
        gapMain = 8f,
        sizingH = AAutoLayout.Sizing.HUG,
        alignCross = AAutoLayout.AlignCross.CENTER,
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
            addPanel2_1()
            addPanel2_2()
            addCharactersImg()
            addAnimationImg()
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
        aPanelDaily.setSize(344f, 206f)
        add(aPanelDaily)

        aPanelDaily.onGetReward = { }
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(344f, 126f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(ConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addPanel2_1() {
        aPanel2_1.setSize(344f, 128f)
        add(aPanel2_1)

        aPanel2_1.apply {
            onTurboMatch  = { navTo(TurboMatchScreen::class.java.name) }
            onPlatePuzzle = { navTo(PlateScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addPanel2_2() {
        aPanel2_2.setSize(344f, 128f)
        add(aPanel2_2)

        aPanel2_2.apply {
            onFuelPick  = { navTo(PickScreen::class.java.name) }
            onFuelBoost = { navTo(BoostScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addCharactersImg() {
        aCharactersImg.setSize(344f, 108f)
        add(aCharactersImg)

        aCharactersImg.setOnTouchListener {
            aCharactersImg.clickScale()
            navTo(SelectCharactersScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addAnimationImg() {
        aAnimationImg.setSize(344f, 128f)
        add(aAnimationImg)

        aAnimationImg.setOnTouchListener {
            aAnimationImg.clickScale()
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
                Actions.scaleTo(0.95f, 0.95f, 0.05f, Interpolation.sineOut),   // втиснулась
                Actions.scaleTo(1.00f, 1.00f, 0.07f, Interpolation.sineIn),    // осіла
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