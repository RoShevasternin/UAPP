package com.rbxtreasure.fungamers.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.layout.AScrollLayout
import com.rbxtreasure.fungamers.game.actors.layout.autoLayout.AAutoLayout
import com.rbxtreasure.fungamers.game.screens.home.DailyScreen
import com.rbxtreasure.fungamers.game.screens.home.FindsScreen
import com.rbxtreasure.fungamers.game.screens.home.GiftScreen
import com.rbxtreasure.fungamers.game.screens.home.QuizScreen
import com.rbxtreasure.fungamers.game.screens.home.ScratchScreen
import com.rbxtreasure.fungamers.game.screens.home.WheelScreen
import com.rbxtreasure.fungamers.game.screens.home.character.SelectCharacterScreen
import com.rbxtreasure.fungamers.game.screens.home.converter.SelectConverterScreen
import com.rbxtreasure.fungamers.game.screens.home.outfit.SelectOutfitScreen
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.actor.setOnTouchListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.util.log

class APanelHome(screen: AdvancedScreen): AScrollLayout(screen, 8f, 20f) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelBalance  = APanelRBX(screen)
    private val aDailyImg      = Image(gdxGame.assetsAll.listHomeContent[1])
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[2])
    private val aPanelTrio     = APanelTrio(screen)
    private val aPanelDuo      = APanelDuo(screen)
    private val aCharactersImg = Image(gdxGame.assetsAll.listHomeContent[5])
    private val aOutfitsImg    = Image(gdxGame.assetsAll.listHomeContent[6])

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun AAutoLayout.addContent() {
        addBalance()
        addDaily()
        addConverter()
        addTrio()
        addDuo()
        addCharacters()
        addOutfits()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AAutoLayout.addBalance() {
        aPanelBalance.setSize(344f, 123f)
        add(aPanelBalance)
    }

    private fun AAutoLayout.addDaily() {
        aDailyImg.setSize(344f, 120f)
        add(aDailyImg)

        aDailyImg.setOnTouchListener {
            aDailyImg.clickScale()
            navTo(DailyScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(344f, 133f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(SelectConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addTrio() {
        aPanelTrio.setSize(344f, 166f)
        add(aPanelTrio)

        aPanelTrio.apply {
            onScratch = { navTo(ScratchScreen::class.java.name) }
            onWheel   = { navTo(WheelScreen::class.java.name) }
            onFinds   = { navTo(FindsScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addDuo() {
        aPanelDuo.setSize(344f, 127f)
        add(aPanelDuo)

        aPanelDuo.apply {
            onQuiz = { navTo(QuizScreen::class.java.name) }
            onGift = { navTo(GiftScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addCharacters() {
        aCharactersImg.setSize(344f, 127f)
        add(aCharactersImg)

        aCharactersImg.setOnTouchListener {
            aCharactersImg.clickScale()
            navTo(SelectCharacterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addOutfits() {
        aOutfitsImg.setSize(344f, 127f)
        add(aOutfitsImg)

        aOutfitsImg.setOnTouchListener {
            aOutfitsImg.clickScale()
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
                    Actions.scaleTo(0.97f, 0.97f, 0.08f, Interpolation.sineOut),
                    Actions.scaleTo(1.00f, 1.00f, 0.10f, Interpolation.sineIn),
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