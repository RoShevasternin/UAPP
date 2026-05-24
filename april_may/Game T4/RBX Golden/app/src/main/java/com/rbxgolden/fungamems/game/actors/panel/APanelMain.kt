package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.screens.MainScreen
import com.rbxgolden.fungamems.game.screens.main.AllClothesAnimationsScreen
import com.rbxgolden.fungamems.game.screens.main.ConverterScreen
import com.rbxgolden.fungamems.game.screens.main.DailyRewardScreen
import com.rbxgolden.fungamems.game.screens.main.GiftScreen
import com.rbxgolden.fungamems.game.screens.main.MemesScreen
import com.rbxgolden.fungamems.game.screens.main.QuizScreen
import com.rbxgolden.fungamems.game.screens.main.ScratchScreen
import com.rbxgolden.fungamems.game.screens.main.SelectCharactersScreen
import com.rbxgolden.fungamems.game.screens.main.SelectConverterScreen
import com.rbxgolden.fungamems.game.screens.main.WheelScreen
import com.rbxgolden.fungamems.game.utils.ConverterType
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxgolden.fungamems.game.utils.actor.setBounds
import com.rbxgolden.fungamems.game.utils.actor.setOnTouchListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelMain(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
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
        val contentW = 376f
        val contentH = 1084f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(contentW, contentH)

        aVerticalGroup.addActor(aContentGroup)

        val space = aScrollPane.height - contentH
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
            Rectangle(16f, 906f, 344f, 162f),
            Rectangle(16f, 768f, 168f, 122f), Rectangle(192f, 768f, 168f, 122f),
            Rectangle(16f, 638f, 168f, 122f), Rectangle(192f, 638f, 168f, 122f),
            Rectangle(16f, 460f, 344f, 162f),
            Rectangle(16f, 322f, 168f, 122f), Rectangle(192f, 322f, 168f, 122f),
            Rectangle(16f, 194f, 168f, 122f), Rectangle(192f, 194f, 168f, 122f),
            Rectangle(16f, 16f, 344f, 162f),
        )
        val listScreen = listOf(
            SelectConverterScreen::class.java.name      ,

            ScratchScreen::class.java.name              , WheelScreen::class.java.name,
            MemesScreen::class.java.name                , DailyRewardScreen::class.java.name,

            SelectCharactersScreen::class.java.name     ,

            QuizScreen::class.java.name                 , GiftScreen::class.java.name,
            ConverterScreen::class.java.name            , ConverterScreen::class.java.name,

            AllClothesAnimationsScreen::class.java.name ,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener {
                when(index.inc()) {
                    9  -> GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.RBX_TO_DOLLAR
                    10 -> GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.DOLLAR_TO_RBX
                }

                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreen[index], screen::class.java.name) }
            }
        }

    }

}