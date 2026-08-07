package com.fimer.skintool.game.actors.panel.home

import com.fimer.skintool.game.actors.AScrollPane
import com.fimer.skintool.game.actors.layout.autoLayout.AAutoLayout
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.daily.APanelDaily
import com.fimer.skintool.game.screens.HomeScreen
import com.fimer.skintool.game.screens.home.FreeScreen
import com.fimer.skintool.game.screens.home.calculator.SelectCalculatorScreen
import com.fimer.skintool.game.screens.home.select.SelectIdScreen
import com.fimer.skintool.game.screens.home.selectors.bundles.SelectBundlesScreen
import com.fimer.skintool.game.screens.home.selectors.char.SelectCharScreen
import com.fimer.skintool.game.screens.home.selectors.emotes.SelectEmotesScreen
import com.fimer.skintool.game.screens.home.selectors.parachutes.SelectParachutesScreen
import com.fimer.skintool.game.screens.home.selectors.pets.SelectPetsScreen
import com.fimer.skintool.game.screens.home.selectors.vehicles.SelectVehiclesScreen
import com.fimer.skintool.game.screens.home.selectors.weapon.SelectWeaponScreen
import com.fimer.skintool.game.screens.home.tips.SelectTipsScreen
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_SCREEN_NAME

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelDaily    = APanelDaily(screen)
    private val aPanelContent1 = APanelContent1(screen)
    private val aPanelContent2 = APanelContent2(screen)

    private val aVertical = AAutoLayout(
        screen,
        direction = AAutoLayout.Direction.VERTICAL,
        gapMain = 16f,
        sizingH = AAutoLayout.Sizing.HUG,
        alignCross = AAutoLayout.AlignCross.CENTER,
        paddingBottom = 20f,
    )
    private val aScrollPane = AScrollPane(aVertical)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        setupVerticalGroup()

        with(aVertical) {
            addDaily()
            addPanelContent1()
            addPanelContent2()
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aVertical.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun setupVerticalGroup() {
        aVertical.width = width
    }

    private fun AAutoLayout.addDaily() {
        aPanelDaily.setSize(344f, 216f)
        add(aPanelDaily)

        aPanelDaily.onGetReward = { }
    }

    private fun AAutoLayout.addPanelContent1() {
        aPanelContent1.setSize(346f, 644f)
        add(aPanelContent1)

        aPanelContent1.apply {
            onEmotes    = { navTo(SelectEmotesScreen::class.java.name) }
            onWeapon    = { navTo(SelectWeaponScreen::class.java.name) }
            onVehicles  = { navTo(SelectVehiclesScreen::class.java.name) }
            onParachute = { navTo(SelectParachutesScreen::class.java.name) }
            onBundles   = { navTo(SelectBundlesScreen::class.java.name) }
            onPets      = { navTo(SelectPetsScreen::class.java.name) }
            onCharacter = { navTo(SelectCharScreen::class.java.name) }
        }
    }

    private fun AAutoLayout.addPanelContent2() {
        aPanelContent2.setSize(346f, 514f)
        add(aPanelContent2)

        aPanelContent2.apply {
            onCalculator = { navTo(SelectCalculatorScreen::class.java.name) }
            onTips       = { navTo(SelectTipsScreen::class.java.name) }
            onFree       = { navTo(FreeScreen::class.java.name) }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun navTo(screenName: String) {
        GLOBAL_SCREEN_NAME = screenName
        screen.animHideScreen {
            gdxGame.navigationManager.navigate(SelectIdScreen::class.java.name, screen::class.java.name)
        }
    }

}