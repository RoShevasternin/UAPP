package com.zahbx.blitzrbx.game.actors.panel

import com.zahbx.blitzrbx.game.actors.layout.AlignH
import com.zahbx.blitzrbx.adsmodule.AdSizeManager
import com.zahbx.blitzrbx.game.utils.runGDX
import kotlinx.coroutines.launch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.game.actors.AScrollPane
import com.zahbx.blitzrbx.game.actors.ATmpGroup
import com.zahbx.blitzrbx.game.actors.button.AGreenButton
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

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    // ⚠️ alignH = CENTER обов'язково: дефолт у AVerticalGroup — LEFT, і кнопка
    // шириною 344 ставала в x=0, тоді як плитки намальовані в PANEL_MAIN.png
    // з відступом 16 — кнопка візуально з'їжджала вліво.
    private val aVerticalGroup  = AVerticalGroup(screen, gap = GAP, alignH = AlignH.CENTER, wrap = true)
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
        aVerticalGroup.setSize(376f, 1840f)
        aContentGroup.setSize(376f, GRID_HEIGHT)

        addFreeRewardsBtn()
        aVerticalGroup.addActor(aContentGroup)

        // Скільки треба добити знизу, щоб короткий контент заповнив ScrollPane
        basePaddingBottom = (aScrollPane.height - (GRID_HEIGHT + GAP + FREE_BTN_HEIGHT)).coerceAtLeast(0f)
        aVerticalGroup.paddingBottom = basePaddingBottom

        // ⚠️ Два «не так», на які легко наступити:
        // 1. Саме «=», а не «+=»: adBottomFlow це StateFlow — віддає поточне
        //    значення одразу і далі КОЖНУ зміну; з «+=» відступ накопичувався б.
        // 2. maxOf, а не сума: base добиває короткий контент до висоти pane,
        //    adBottom ховає його за рекламою — це ОДНА й та сама дірка знизу.
        // Раніше тут узагалі не було підписки — висота реклами читалась один раз
        // на старті, коли банера ще немає (тобто майже завжди 0).
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    val adBottom = screen.adBottomUI.coerceAtLeast(0f)
                    aVerticalGroup.paddingBottom = maxOf(basePaddingBottom, adBottom)
                    log("APanelMain adBottomUI = ${screen.adBottomUI}")
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
    // Стиль — наявний AGreenButton апки: вся палітра тут зелена, друга зелена
    // кнопка іншого відтінку виглядала б чужою.
    private fun addFreeRewardsBtn() {
        aFreeRewardsBtn.setSize(344f, FREE_BTN_HEIGHT)
        aVerticalGroup.addActor(aFreeRewardsBtn)

        aFreeRewardsBtn.setOnClickListener {
            gdxGame.activity.showInterstitial()
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

    companion object {
        private const val GRID_HEIGHT     = 840f   // висота PANEL_MAIN.png
        private const val GAP             = 16f
        private const val FREE_BTN_HEIGHT = 56f
    }

}