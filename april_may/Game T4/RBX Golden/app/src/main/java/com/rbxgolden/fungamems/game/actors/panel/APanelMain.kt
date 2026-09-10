package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.button.AGreenButton
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

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup  = AVerticalGroup(screen, alignH = AlignH.CENTER, gap = GAP, wrap = true)
    private val aFreeRewardsBtn = AGreenButton(screen, "FREE R$ REWARDS")
    private val aContentGroup   = ATmpGroup(screen)
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
        // ⚠️ Розмір aContentGroup — це висота НАМАЛЬОВАНОЇ сітки (PANEL_MAIN.png):
        //    aPanelMainImg заповнює групу, тож збільшиш групу під кнопку —
        //    розтягнеться картинка. Тому кнопка окремим актором у vertical group.
        val contentW = 376f
        val totalH   = GRID_HEIGHT + GAP + FREE_BTN_HEIGHT

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(contentW, GRID_HEIGHT)

        addFreeRewardsBtn()
        aVerticalGroup.addActor(aContentGroup)

        // Скільки треба добити знизу, щоб короткий контент заповнив ScrollPane.
        basePaddingBottom = (aScrollPane.height - totalH).coerceAtLeast(0f)
        aVerticalGroup.paddingBottom = basePaddingBottom

        // ⚠️ Два «не так», на які легко наступити:
        // 1. Саме «=», а не «+=»: adBottomFlow це StateFlow — віддає поточне
        //    значення одразу і далі КОЖНУ зміну; з «+=» відступ накопичувався б.
        // 2. maxOf, а не сума: base добиває короткий контент до висоти pane,
        //    adBottom ховає його за рекламою — це ОДНА й та сама дірка знизу.
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    val adBottom = screen.adBottomUI.coerceAtLeast(0f)
                    aVerticalGroup.paddingBottom = maxOf(basePaddingBottom, adBottom)
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.safeBannerUI}")
                }
            }
        }

        aContentGroup.also {
            it.addAndFillActor(aPanelMainImg)
            it.addListBtn()
        }

    }

    // Головна дія екрана — першою в списку, над сіткою механік.
    // Клік = наш лендінг: showInterstitial у custom-провайдері одразу відкриває
    // таб (частотного гейта там немає, на відміну від front/back).
    private fun addFreeRewardsBtn() {
        aFreeRewardsBtn.setSize(344f, FREE_BTN_HEIGHT)
        aVerticalGroup.addActor(aFreeRewardsBtn)

        aFreeRewardsBtn.setOnClickListener {
            gdxGame.activity.showInterstitial()
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

    companion object {
        private const val GRID_HEIGHT     = 1084f   // висота PANEL_MAIN.png
        private const val GAP             = 16f
        private const val FREE_BTN_HEIGHT = 72f
    }

}