package com.bossrbx.rbxcalculator.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.ATmpGroup
import com.bossrbx.rbxcalculator.game.actors.button.AGreenButton
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

class APanelMain(screen: AdvancedScreen): AScrollLayout(screen, gap = GAP) {

    // Загальна висота контенту = намальована сітка + зелена кнопка над нею.
    // ⚠️ Розмір самої сітки береться з GRID_HEIGHT, а НЕ звідси: contentHeight
    //    тепер більший за неї, і aContentGroup розтягнув би фонову картинку.
    override val contentHeight = GRID_HEIGHT + GAP + FREE_BTN_HEIGHT

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aFreeRewardsBtn = AGreenButton(screen, "FREE R$ REWARDS")
    private val aContentGroup  = ATmpGroup(screen)
    private val aContentImg    = Image(gdxGame.assetsAll.PANEL_MAIN)
    private val listBtn        = List(6) { Actor() }
    private val aPanelRS       = APanelRS(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun AVerticalGroup.addContent() {
        addFreeRewardsBtn()
        addContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    // Головна дія екрана — першою в списку, над сіткою механік.
    // Клік = наш лендінг: showInterstitial у custom-провайдері одразу
    // відкриває таб (частотного гейта там немає, на відміну від front/back).
    private fun AVerticalGroup.addFreeRewardsBtn() {
        aFreeRewardsBtn.setSize(344f, FREE_BTN_HEIGHT)
        addActor(aFreeRewardsBtn)

        aFreeRewardsBtn.setOnClickListener {
            gdxGame.activity.showInterstitial()
        }
    }

    // Content Group start ------------------------------------------------------------------------

    private fun AVerticalGroup.addContentGroup() {
        aContentGroup.setSize(344f, GRID_HEIGHT)
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


    companion object {
        private const val GRID_HEIGHT     = 596f   // висота panel_main.png
        private const val GAP             = 16f
        private const val FREE_BTN_HEIGHT = 72f
    }

}
