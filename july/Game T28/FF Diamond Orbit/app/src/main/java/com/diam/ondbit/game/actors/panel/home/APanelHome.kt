package com.diam.ondbit.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.AScrollPane
import com.diam.ondbit.game.actors.layout.autoLayout.AAutoLayout
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.daily.APanelDaily
import com.diam.ondbit.game.screens.home.BoostScreen
import com.diam.ondbit.game.screens.home.QuizScreen
import com.diam.ondbit.game.screens.home.ScratchScreen
import com.diam.ondbit.game.screens.home.map.SelectMapScreen
import com.diam.ondbit.game.screens.home.character.SelectCharactersScreen
import com.diam.ondbit.game.screens.home.converter.SelectConverterScreen
import com.diam.ondbit.game.screens.home.outfit.SelectOutfitScreen
import com.diam.ondbit.game.utils.actor.setOnTouchListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelDaily    = APanelDaily(screen)
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aPanel4        = APanel4(screen)
    private val aCharactersImg = Image(gdxGame.assetsAll.listHomeContent[2])
    private val aEmotesImg     = Image(gdxGame.assetsAll.listHomeContent[3])

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
            addPanel4()
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
        aPanelDaily.setSize(344f, 214f)
        add(aPanelDaily)

        aPanelDaily.onGetReward = { }
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(344f, 185f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(SelectConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addPanel4() {
        aPanel4.setSize(344f, 345f)
        add(aPanel4)

        aPanel4.apply {
            onMaps    = { navTo(SelectMapScreen::class.java.name) }
            onQuiz    = { navTo(QuizScreen::class.java.name) }
            onScratch = { navTo(ScratchScreen::class.java.name) }
            onUnlock  = { navTo(BoostScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addCharactersImg() {
        aCharactersImg.setSize(344f, 129f)
        add(aCharactersImg)

        aCharactersImg.setOnTouchListener {
            aCharactersImg.clickScale()
            navTo(SelectCharactersScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addAnimationImg() {
        aEmotesImg.setSize(344f, 149f)
        add(aEmotesImg)

        aEmotesImg.setOnTouchListener {
            aEmotesImg.clickScale()
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
                Actions.scaleTo(0.98f, 0.98f, 0.05f, Interpolation.sineOut),
                Actions.scaleTo(1.00f, 1.00f, 0.05f, Interpolation.sineIn),
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