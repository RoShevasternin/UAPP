package com.racing.funtols.game.manager

import com.badlogic.gdx.Gdx
import com.racing.funtols.game.screens.home.outfit.AnimationsScreen
import com.racing.funtols.game.screens.home.outfit.ClothingScreen
import com.racing.funtols.game.screens.selector.Selector_2_Screen
import com.racing.funtols.game.screens.selector.Selector_3_Screen
import com.racing.funtols.game.screens.selector.Selector_4_Screen
import com.racing.funtols.game.GDXGame
import com.racing.funtols.game.screens.HomeScreen
import com.racing.funtols.game.screens.LoaderScreen
import com.racing.funtols.game.screens.OnboardingScreen
import com.racing.funtols.game.screens.SettingsScreen
import com.racing.funtols.game.screens.home.BoostScreen
import com.racing.funtols.game.screens.home.ConverterScreen
import com.racing.funtols.game.screens.home.PickScreen
import com.racing.funtols.game.screens.home.PlateScreen
import com.racing.funtols.game.screens.home.TurboMatchScreen
import com.racing.funtols.game.screens.home.character.CharacterScreen
import com.racing.funtols.game.screens.home.character.SelectCharactersScreen
import com.racing.funtols.game.screens.home.outfit.AccessoriesScreen
import com.racing.funtols.game.screens.home.outfit.HeadScreen
import com.racing.funtols.game.screens.home.outfit.SelectOutfitScreen
import com.racing.funtols.game.screens.selector.Selector_1_Screen
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.runGDX

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
        Selector_1_Screen    ::class.java.name -> Selector_1_Screen()
        Selector_2_Screen    ::class.java.name -> Selector_2_Screen()
        Selector_3_Screen    ::class.java.name -> Selector_3_Screen()
        Selector_4_Screen    ::class.java.name -> Selector_4_Screen()
        HomeScreen           ::class.java.name -> HomeScreen()

        ConverterScreen      ::class.java.name -> ConverterScreen()
        TurboMatchScreen     ::class.java.name -> TurboMatchScreen()
        PlateScreen          ::class.java.name -> PlateScreen()
        PickScreen           ::class.java.name -> PickScreen()
        BoostScreen          ::class.java.name -> BoostScreen()

        SelectCharactersScreen::class.java.name -> SelectCharactersScreen()
        CharacterScreen       ::class.java.name -> CharacterScreen()

        SelectOutfitScreen::class.java.name -> SelectOutfitScreen()
        ClothingScreen    ::class.java.name -> ClothingScreen()
        AccessoriesScreen ::class.java.name -> AccessoriesScreen()
        AnimationsScreen  ::class.java.name -> AnimationsScreen()
        HeadScreen        ::class.java.name -> HeadScreen()

        SettingsScreen::class.java.name -> SettingsScreen()

        else -> HomeScreen()
    }

}