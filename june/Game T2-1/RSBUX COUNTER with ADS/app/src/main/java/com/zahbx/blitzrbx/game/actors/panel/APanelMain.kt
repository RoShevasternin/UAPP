package com.zahbx.blitzrbx.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.game.actors.AScrollPane
import com.zahbx.blitzrbx.game.actors.ATmpGroup
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.layout.linear.AVerticalGroup
import com.zahbx.blitzrbx.game.screens.MainScreen
import com.zahbx.blitzrbx.game.screens.main.BoostModeScreen
import com.zahbx.blitzrbx.game.screens.main.DailyRewardScreen
import com.zahbx.blitzrbx.game.screens.main.MiniGameWelcomeScreen
import com.zahbx.blitzrbx.game.screens.main.NtoRBXScreen
import com.zahbx.blitzrbx.game.screens.main.QuizTimeScreen
import com.zahbx.blitzrbx.game.screens.main.RBXCalculatorScreen
import com.zahbx.blitzrbx.game.screens.main.ReferralBonusScreen
import com.zahbx.blitzrbx.game.screens.main.ScratchScreen
import com.zahbx.blitzrbx.game.screens.main.SettingsScreen
import com.zahbx.blitzrbx.game.screens.main.SpinWinScreen
import com.zahbx.blitzrbx.game.utils.GLOBAL_SELECTED_RBX_CALCULATOR_TITLE
import com.zahbx.blitzrbx.game.utils.actor.setBounds
import com.zahbx.blitzrbx.game.utils.actor.setOnTouchListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedGroup
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.util.log

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
        if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
        log("APanelMain adBottomUI = ${screen.adBottomUI}")

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