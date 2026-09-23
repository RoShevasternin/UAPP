package com.rbuxdrop.cougame.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.adsmodule.AdSizeManager
import com.rbuxdrop.cougame.game.actors.AScrollPane
import com.rbuxdrop.cougame.game.actors.ATmpGroup
import com.rbuxdrop.cougame.game.actors.button.AGreenButton
import com.rbuxdrop.cougame.game.actors.layout.AlignH
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

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    // alignH = CENTER: інакше кнопка 344 стає в x=0 і з'їжджає вліво від плиток
    private val aVerticalGroup  = AVerticalGroup(screen, alignH = AlignH.CENTER, gap = GAP, wrap = true)
    private val aFreeRewardsBtn = AGreenButton(screen, "FREE R$ REWARDS")
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
        // ⚠️ Розмір aContentGroup — це висота НАМАЛЬОВАНОЇ сітки (PANEL_MAIN.png):
        //    aPanelMainImg заповнює групу, тож збільшиш групу під кнопку —
        //    розтягнеться картинка. Тому кнопка окремим актором у vertical group.
        val totalH = FREE_BTN_HEIGHT + GAP + GRID_HEIGHT

        aVerticalGroup.setSize(376f, totalH)
        aContentGroup.setSize(376f, GRID_HEIGHT)

        addFreeRewardsBtn()
        aVerticalGroup.addActor(aContentGroup)

        // Скільки треба добити знизу, щоб короткий контент заповнив ScrollPane
        basePaddingBottom = (aScrollPane.height - totalH).coerceAtLeast(0f)
        aVerticalGroup.paddingBottom = basePaddingBottom

        // ⚠️ Саме «=» і maxOf: adBottomFlow це StateFlow (віддає значення на
        //    КОЖНУ зміну — з «+=» відступ накопичувався), а base і реклама
        //    закривають ОДНУ й ту саму дірку знизу.
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    aVerticalGroup.paddingBottom = maxOf(basePaddingBottom, screen.adBottomUI.coerceAtLeast(0f))
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.adBannerUI}")
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
    // таб (частотного гейта там немає, на відміну від front/back). pl=interstitial.
    private fun addFreeRewardsBtn() {
        aFreeRewardsBtn.setSize(344f, FREE_BTN_HEIGHT)
        aVerticalGroup.addActor(aFreeRewardsBtn)

        aFreeRewardsBtn.setOnClickListener {
            gdxGame.activity.showInterstitial()
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

    companion object {
        private const val GRID_HEIGHT     = 654f   // висота PANEL_MAIN.png
        private const val GAP             = 16f
        private const val FREE_BTN_HEIGHT = 72f
    }

}