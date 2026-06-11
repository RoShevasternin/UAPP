package com.rbxtreasure.fungamers.game.manager

import com.badlogic.gdx.Gdx
import com.rbxtreasure.fungamers.game.GDXGame
import com.rbxtreasure.fungamers.game.screens.LoaderScreen
import com.rbxtreasure.fungamers.game.screens.HomeScreen
import com.rbxtreasure.fungamers.game.screens.OnboardingScreen
import com.rbxtreasure.fungamers.game.screens.SettingsScreen
import com.rbxtreasure.fungamers.game.screens.home.DailyScreen
import com.rbxtreasure.fungamers.game.screens.home.FindsScreen
import com.rbxtreasure.fungamers.game.screens.home.GiftScreen
import com.rbxtreasure.fungamers.game.screens.home.QuizScreen
import com.rbxtreasure.fungamers.game.screens.home.ScratchScreen
import com.rbxtreasure.fungamers.game.screens.home.WheelScreen
import com.rbxtreasure.fungamers.game.screens.home.character.CharacterScreen
import com.rbxtreasure.fungamers.game.screens.home.character.SelectCharacterScreen
import com.rbxtreasure.fungamers.game.screens.home.converter.ConverterScreen
import com.rbxtreasure.fungamers.game.screens.home.converter.SelectConverterScreen
import com.rbxtreasure.fungamers.game.screens.home.outfit.AccessoriesScreen
import com.rbxtreasure.fungamers.game.screens.home.outfit.AnimationsScreen
import com.rbxtreasure.fungamers.game.screens.home.outfit.ClothingScreen
import com.rbxtreasure.fungamers.game.screens.home.outfit.HeadScreen
import com.rbxtreasure.fungamers.game.screens.home.outfit.SelectOutfitScreen
import com.rbxtreasure.fungamers.game.screens.selector.Selector_1_Screen
import com.rbxtreasure.fungamers.game.screens.selector.Selector_2_Screen
import com.rbxtreasure.fungamers.game.screens.selector.Selector_3_Screen
import com.rbxtreasure.fungamers.game.screens.selector.Selector_4_Screen
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.runGDX

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
        DailyScreen          ::class.java.name -> DailyScreen()
        WheelScreen          ::class.java.name -> WheelScreen()
        ScratchScreen        ::class.java.name -> ScratchScreen()
        FindsScreen          ::class.java.name -> FindsScreen()
        QuizScreen           ::class.java.name -> QuizScreen()
        GiftScreen           ::class.java.name -> GiftScreen()
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