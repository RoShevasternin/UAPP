package com.fimer.skintool.game.manager

import com.badlogic.gdx.Gdx
import com.fimer.skintool.game.GDXGame
import com.fimer.skintool.game.screens.HomeScreen
import com.fimer.skintool.game.screens.LoaderScreen
import com.fimer.skintool.game.screens.OnboardingScreen
import com.fimer.skintool.game.screens.SettingsScreen
import com.fimer.skintool.game.screens.home.FreeScreen
import com.fimer.skintool.game.screens.home.calculator.CalculatorScreen
import com.fimer.skintool.game.screens.home.calculator.ResultScreen
import com.fimer.skintool.game.screens.home.calculator.SelectCalculatorScreen
import com.fimer.skintool.game.screens.home.selectors.bundles.BundlesScreen
import com.fimer.skintool.game.screens.home.selectors.bundles.SelectBundlesScreen
import com.fimer.skintool.game.screens.home.selectors.char.CharScreen
import com.fimer.skintool.game.screens.home.selectors.char.SelectCharScreen
import com.fimer.skintool.game.screens.home.selectors.emotes.EmotesScreen
import com.fimer.skintool.game.screens.home.selectors.emotes.SelectEmotesScreen
import com.fimer.skintool.game.screens.home.selectors.parachutes.ParachutesScreen
import com.fimer.skintool.game.screens.home.selectors.parachutes.SelectParachutesScreen
import com.fimer.skintool.game.screens.home.selectors.pets.PetsScreen
import com.fimer.skintool.game.screens.home.selectors.pets.SelectPetsScreen
import com.fimer.skintool.game.screens.home.select.SelectCategoryScreen
import com.fimer.skintool.game.screens.home.select.SelectIdScreen
import com.fimer.skintool.game.screens.home.select.SelectRankScreen
import com.fimer.skintool.game.screens.home.select.SelectSkinScreen
import com.fimer.skintool.game.screens.home.selectors.vehicles.SelectVehiclesScreen
import com.fimer.skintool.game.screens.home.selectors.vehicles.VehiclesScreen
import com.fimer.skintool.game.screens.home.selectors.weapon.SelectWeaponScreen
import com.fimer.skintool.game.screens.home.selectors.weapon.WeaponScreen
import com.fimer.skintool.game.screens.home.tips.SelectTipsScreen
import com.fimer.skintool.game.screens.home.tips.TipsScreen
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.runGDX

class NavigationManager(val game: GDXGame) {

    private val backStack = mutableListOf<String>()
    var key: Int? = null
        private set

    fun navigate(toScreenName: String, fromScreenName: String? = null, key: Int? = null) = runGDX {
        this.key = key

        game.updateScreen(getScreenByName(toScreenName))
        backStack.filter { name -> name == toScreenName }.onEach { name -> backStack.remove(name) }
        fromScreenName?.let { fromName ->
            backStack.filter { name -> name == fromName }.onEach { name -> backStack.remove(name) }
            backStack.add(fromName)
        }

        // Не показуємо рекламу при переходах на стартові екрани
        val noAdScreens = listOf(
            LoaderScreen::class.java.name,
            OnboardingScreen::class.java.name,
        )
        if (toScreenName in noAdScreens) return@runGDX

        gdxGame.activity.onFrontNavigation()
    }

    fun back(key: Int? = null) = runGDX {
        this.key = key

        if (isBackStackEmpty()) exit() else game.updateScreen(getScreenByName(backStack.removeAt(backStack.lastIndex)))

        gdxGame.activity.onBackNavigation()
    }


    fun exit() = runGDX { Gdx.app.exit() }


    fun isBackStackEmpty() = backStack.isEmpty()

    private fun getScreenByName(name: String): AdvancedScreen = when(name) {
        LoaderScreen         ::class.java.name -> LoaderScreen()
        OnboardingScreen     ::class.java.name -> OnboardingScreen()
        HomeScreen           ::class.java.name -> HomeScreen()
        SettingsScreen       ::class.java.name -> SettingsScreen()

        SelectIdScreen      ::class.java.name -> SelectIdScreen()
        SelectRankScreen    ::class.java.name -> SelectRankScreen()
        SelectCategoryScreen::class.java.name -> SelectCategoryScreen()
        SelectSkinScreen    ::class.java.name -> SelectSkinScreen()

        SelectEmotesScreen::class.java.name -> SelectEmotesScreen()
        EmotesScreen      ::class.java.name -> EmotesScreen()

        SelectWeaponScreen::class.java.name -> SelectWeaponScreen()
        WeaponScreen      ::class.java.name -> WeaponScreen()

        SelectParachutesScreen::class.java.name -> SelectParachutesScreen()
        ParachutesScreen      ::class.java.name -> ParachutesScreen()

        SelectVehiclesScreen::class.java.name -> SelectVehiclesScreen()
        VehiclesScreen      ::class.java.name -> VehiclesScreen()

        SelectBundlesScreen::class.java.name -> SelectBundlesScreen()
        BundlesScreen      ::class.java.name -> BundlesScreen()

        SelectPetsScreen::class.java.name -> SelectPetsScreen()
        PetsScreen      ::class.java.name -> PetsScreen()

        SelectCharScreen::class.java.name -> SelectCharScreen()
        CharScreen      ::class.java.name -> CharScreen()

        SelectTipsScreen::class.java.name -> SelectTipsScreen()
        TipsScreen      ::class.java.name -> TipsScreen()

        SelectCalculatorScreen::class.java.name -> SelectCalculatorScreen()
        CalculatorScreen      ::class.java.name -> CalculatorScreen()
        ResultScreen          ::class.java.name -> ResultScreen()

        FreeScreen::class.java.name -> FreeScreen()

        else -> HomeScreen()
    }

}