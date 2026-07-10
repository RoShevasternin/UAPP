package com.rbxrush.rushrbx.game.manager

import com.badlogic.gdx.Gdx
import com.rbxrush.rushrbx.game.GDXGame
import com.rbxrush.rushrbx.game.screens.HomeScreen
import com.rbxrush.rushrbx.game.screens.LoaderScreen
import com.rbxrush.rushrbx.game.screens.OnboardingScreen
import com.rbxrush.rushrbx.game.screens.SettingsScreen
import com.rbxrush.rushrbx.game.screens.home.GuessScreen
import com.rbxrush.rushrbx.game.screens.home.FreeScreen
import com.rbxrush.rushrbx.game.screens.home.QuizScreen
import com.rbxrush.rushrbx.game.screens.home.ScratchScreen
import com.rbxrush.rushrbx.game.screens.home.WheelScreen
import com.rbxrush.rushrbx.game.screens.home.character.CharacterScreen
import com.rbxrush.rushrbx.game.screens.home.character.SelectCharacterScreen
import com.rbxrush.rushrbx.game.screens.home.converter.ConverterScreen
import com.rbxrush.rushrbx.game.screens.home.converter.SelectConverterScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.AccessoriesScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.AnimationsScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.ClothingScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.HeadScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.SelectOutfitScreen
import com.rbxrush.rushrbx.game.screens.selector.Selector_1_Screen
import com.rbxrush.rushrbx.game.screens.selector.Selector_2_Screen
import com.rbxrush.rushrbx.game.screens.selector.Selector_3_Screen
import com.rbxrush.rushrbx.game.screens.selector.Selector_4_Screen
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.runGDX

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

        SelectConverterScreen::class.java.name -> SelectConverterScreen()
        ConverterScreen      ::class.java.name -> ConverterScreen()
        WheelScreen          ::class.java.name -> WheelScreen()
        ScratchScreen        ::class.java.name -> ScratchScreen()
        GuessScreen          ::class.java.name -> GuessScreen()
        QuizScreen           ::class.java.name -> QuizScreen()
        FreeScreen           ::class.java.name -> FreeScreen()
        SelectCharacterScreen::class.java.name -> SelectCharacterScreen()
        CharacterScreen      ::class.java.name -> CharacterScreen()

        SelectOutfitScreen   ::class.java.name -> SelectOutfitScreen()
        ClothingScreen       ::class.java.name -> ClothingScreen()
        AccessoriesScreen    ::class.java.name -> AccessoriesScreen()
        AnimationsScreen     ::class.java.name -> AnimationsScreen()
        HeadScreen           ::class.java.name -> HeadScreen()

        SettingsScreen       ::class.java.name -> SettingsScreen()

        else -> HomeScreen()
    }

}