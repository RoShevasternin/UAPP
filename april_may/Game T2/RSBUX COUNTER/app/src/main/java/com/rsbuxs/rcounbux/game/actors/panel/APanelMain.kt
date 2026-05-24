package com.rsbuxs.rcounbux.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rsbuxs.rcounbux.game.actors.AScrollPane
import com.rsbuxs.rcounbux.game.actors.ATmpGroup
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.layout.linear.AVerticalGroup
import com.rsbuxs.rcounbux.game.screens.MainScreen
import com.rsbuxs.rcounbux.game.screens.main.BoostModeScreen
import com.rsbuxs.rcounbux.game.screens.main.DailyRewardScreen
import com.rsbuxs.rcounbux.game.screens.main.MiniGameWelcomeScreen
import com.rsbuxs.rcounbux.game.screens.main.NtoRBXScreen
import com.rsbuxs.rcounbux.game.screens.main.QuizTimeScreen
import com.rsbuxs.rcounbux.game.screens.main.RBXCalculatorScreen
import com.rsbuxs.rcounbux.game.screens.main.ReferralBonusScreen
import com.rsbuxs.rcounbux.game.screens.main.ScratchScreen
import com.rsbuxs.rcounbux.game.screens.main.SettingsScreen
import com.rsbuxs.rcounbux.game.screens.main.SpinWinScreen
import com.rsbuxs.rcounbux.game.utils.GLOBAL_SELECTED_RBX_CALCULATOR_TITLE
import com.rsbuxs.rcounbux.game.utils.actor.setBounds
import com.rsbuxs.rcounbux.game.utils.actor.setOnTouchListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class APanelMain(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val aPanelMainImg  = Image(gdxGame.assetsAll.PANEL_MAIN)
    private val listBtn        = List(11) { Actor() }
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    // Content Group ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        aVerticalGroup.setSize(376f, 1840f)
        aContentGroup.setSize(376f, 840f)

        aVerticalGroup.addActor(aContentGroup)
        //if (isADS) aVerticalGroup.add(space)

        aContentGroup.also {
            it.addAndFillActor(aPanelMainImg)
            it.addListBtn()
        }

    }

    private fun AdvancedGroup.addListBtn() {
        val listBounds = listOf(
            Rectangle(16f, 736f, 344f, 88f),
            Rectangle(16f, 640f, 344f, 88f),
            Rectangle(16f, 508f, 168f, 124f), Rectangle(192f, 508f, 168f, 124f),
            Rectangle(16f, 376f, 168f, 124f), Rectangle(192f, 376f, 168f, 124f),
            Rectangle(16f, 244f, 168f, 124f), Rectangle(192f, 244f, 168f, 124f),
            Rectangle(16f, 112f, 168f, 124f), Rectangle(192f, 112f, 168f, 124f),
            Rectangle(16f, 16f, 344f, 88f),
        )
        val listScreen = listOf(
            RBXCalculatorScreen::class.java.name,
            MiniGameWelcomeScreen::class.java.name,
            QuizTimeScreen::class.java.name,          DailyRewardScreen::class.java.name,
            SpinWinScreen::class.java.name,           ScratchScreen::class.java.name,
            NtoRBXScreen::class.java.name,            NtoRBXScreen::class.java.name,
            ReferralBonusScreen::class.java.name,     BoostModeScreen::class.java.name,
            SettingsScreen::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener {
                when {
                    index.inc() == 7 -> GLOBAL_SELECTED_RBX_CALCULATOR_TITLE = "RBX to Dollar"
                    index.inc() == 8 -> GLOBAL_SELECTED_RBX_CALCULATOR_TITLE = "Dollar to RBX"
                }

                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreen[index], screen::class.java.name) }
            }
        }

    }

}