package com.rbuxdrop.cougame.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.adsmodule.AdSizeManager
import com.rbuxdrop.cougame.game.actors.AScrollPane
import com.rbuxdrop.cougame.game.actors.ATmpGroup
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.layout.linear.AVerticalGroup
import com.rbuxdrop.cougame.game.screens.MainScreen
import com.rbuxdrop.cougame.game.screens.main.DailyRewardScreen
import com.rbuxdrop.cougame.game.screens.main.FlipScreen
import com.rbuxdrop.cougame.game.screens.main.ScratchScreen
import com.rbuxdrop.cougame.game.screens.main.SelectConverterScreen
import com.rbuxdrop.cougame.game.screens.main.TipsScreen
import com.rbuxdrop.cougame.game.screens.main.WheelScreen
import com.rbuxdrop.cougame.game.screens.main.quiz.QuizScreen
import com.rbuxdrop.cougame.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbuxdrop.cougame.game.utils.WIDTH_UI
import com.rbuxdrop.cougame.game.utils.actor.setBounds
import com.rbuxdrop.cougame.game.utils.actor.setOnTouchListener
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedGroup
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.utils.runGDX
import com.rbuxdrop.cougame.util.log
import kotlinx.coroutines.launch

class APanelMain(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val aPanelMainImg  = Image(gdxGame.assetsAll.PANEL_MAIN)
    private val listBtn        = List(7) { Actor() }
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
        aVerticalGroup.setSize(376f, 654f)
        aContentGroup.setSize(376f, 654f)

        aVerticalGroup.addActor(aContentGroup)

        val space = aScrollPane.height - 654f
        if (space > 0) aVerticalGroup.paddingBottom += space

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.safeBannerUI}")
                }
            }
        }

        aContentGroup.also {
            it.addAndFillActor(aPanelMainImg)
            it.addListBtn()
        }

    }

    private fun AdvancedGroup.addListBtn() {
        val listBounds = listOf(
            Rectangle(16f, 486f, 344f, 168f),
            Rectangle(16f, 324f, 168f, 154f), Rectangle(192f, 324f, 168f, 154f),
            Rectangle(16f, 162f, 168f, 154f), Rectangle(192f, 162f, 168f, 154f),
            Rectangle(16f, 0f, 168f, 154f),   Rectangle(192f, 0f, 168f, 154f),
        )
        val listScreen = listOf(
            SelectConverterScreen::class.java.name,
            DailyRewardScreen::class.java.name    , WheelScreen::class.java.name,
            ScratchScreen::class.java.name        , QuizScreen::class.java.name,
            FlipScreen::class.java.name           , TipsScreen::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener {
                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreen[index], screen::class.java.name) }
            }
        }

    }

}