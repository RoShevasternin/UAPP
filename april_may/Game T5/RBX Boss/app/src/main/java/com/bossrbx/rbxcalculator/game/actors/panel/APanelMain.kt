package com.bossrbx.rbxcalculator.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.ATmpGroup
import com.bossrbx.rbxcalculator.game.actors.layout.AScrollLayout
import com.bossrbx.rbxcalculator.game.actors.layout.linear.AVerticalGroup
import com.bossrbx.rbxcalculator.game.screens.main.DailyRewardScreen
import com.bossrbx.rbxcalculator.game.screens.main.flipCard.FlipCardScreen
import com.bossrbx.rbxcalculator.game.screens.main.converter.SelectConverterScreen
import com.bossrbx.rbxcalculator.game.screens.main.ScratchScreen
import com.bossrbx.rbxcalculator.game.screens.main.WheelScreen
import com.bossrbx.rbxcalculator.game.screens.main.quiz.QuizPlayScreen
import com.bossrbx.rbxcalculator.game.utils.actor.setBounds
import com.bossrbx.rbxcalculator.game.utils.actor.setOnTouchListener
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APanelMain(screen: AdvancedScreen): AScrollLayout(screen) {

    override val contentHeight = 596f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentGroup  = ATmpGroup(screen)
    private val aContentImg    = Image(gdxGame.assetsAll.PANEL_MAIN)
    private val listBtn        = List(6) { Actor() }
    private val aPanelRS       = APanelRS(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun AVerticalGroup.addContent() {
        addContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    // Content Group start ------------------------------------------------------------------------

    private fun AVerticalGroup.addContentGroup() {
        aContentGroup.setSize(344f, contentHeight)
        addActor(aContentGroup)

        aContentGroup.also {
            it.addAndFillActor(aContentImg)
            it.addListBtn()
            it.addPanelRS()
        }
    }

    // Content Group end ------------------------------------------------------------------------

    private fun AdvancedGroup.addListBtn() {
        val listBounds = listOf(
            Rectangle(176f, 504f, 168f, 92f),
            Rectangle(0f, 352f, 344f, 144f),
            Rectangle(0f, 176f, 168f, 168f), Rectangle(176f, 176f, 168f, 168f),
            Rectangle(0f, 0f, 168f, 168f), Rectangle(176f, 0f, 168f, 168f),
        )
        val listScreen = listOf(
            DailyRewardScreen    ::class.java.name,
            SelectConverterScreen::class.java.name,
            WheelScreen          ::class.java.name, ScratchScreen::class.java.name,
            FlipCardScreen       ::class.java.name, QuizPlayScreen::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener {
                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreen[index], screen::class.java.name) }
            }
        }

    }

    private fun AdvancedGroup.addPanelRS() {
        addActor(aPanelRS)
        aPanelRS.setBounds(12f, 520f, 64f, 32f)
    }

}