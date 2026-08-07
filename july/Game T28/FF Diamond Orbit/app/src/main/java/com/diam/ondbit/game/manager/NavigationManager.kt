package com.diam.ondbit.game.manager

import com.badlogic.gdx.Gdx
import com.diam.ondbit.game.screens.home.outfit.EmotesScreen
import com.diam.ondbit.game.screens.home.outfit.GearScreen
import com.diam.ondbit.game.screens.selector.Selector_2_Screen
import com.diam.ondbit.game.screens.selector.Selector_3_Screen
import com.diam.ondbit.game.GDXGame
import com.diam.ondbit.game.screens.HomeScreen
import com.diam.ondbit.game.screens.LoaderScreen
import com.diam.ondbit.game.screens.OnboardingScreen
import com.diam.ondbit.game.screens.SettingsScreen
import com.diam.ondbit.game.screens.home.BoostScreen
import com.diam.ondbit.game.screens.home.converter.ConverterScreen
import com.diam.ondbit.game.screens.home.QuizScreen
import com.diam.ondbit.game.screens.home.ScratchScreen
import com.diam.ondbit.game.screens.home.map.SelectMapScreen
import com.diam.ondbit.game.screens.home.character.CharacterScreen
import com.diam.ondbit.game.screens.home.character.SelectCharactersScreen
import com.diam.ondbit.game.screens.home.converter.SelectConverterScreen
import com.diam.ondbit.game.screens.home.map.MapScreen
import com.diam.ondbit.game.screens.home.outfit.AccessoriesScreen
import com.diam.ondbit.game.screens.home.outfit.ClothingScreen
import com.diam.ondbit.game.screens.home.outfit.SelectOutfitScreen
import com.diam.ondbit.game.screens.selector.Selector_1_Screen
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX

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
        HomeScreen           ::class.java.name -> HomeScreen()

        SelectConverterScreen::class.java.name -> SelectConverterScreen()
        ConverterScreen      ::class.java.name -> ConverterScreen()
        SelectMapScreen      ::class.java.name -> SelectMapScreen()
        MapScreen            ::class.java.name -> MapScreen()
        QuizScreen           ::class.java.name -> QuizScreen()
        ScratchScreen        ::class.java.name -> ScratchScreen()
        BoostScreen          ::class.java.name -> BoostScreen()

        SelectCharactersScreen::class.java.name -> SelectCharactersScreen()
        CharacterScreen       ::class.java.name -> CharacterScreen()

        SelectOutfitScreen::class.java.name -> SelectOutfitScreen()
        GearScreen    ::class.java.name -> GearScreen()
        AccessoriesScreen ::class.java.name -> AccessoriesScreen()
        EmotesScreen  ::class.java.name -> EmotesScreen()
        ClothingScreen        ::class.java.name -> ClothingScreen()

        SettingsScreen::class.java.name -> SettingsScreen()

        else -> HomeScreen()
    }

}