package com.treprosure.starbxup.game.manager

import com.badlogic.gdx.Gdx
import com.treprosure.starbxup.game.GDXGame
import com.treprosure.starbxup.game.screens.HomeScreen
import com.treprosure.starbxup.game.screens.LoaderScreen
import com.treprosure.starbxup.game.screens.SettingsScreen
import com.treprosure.starbxup.game.screens.home.DailyScreen
import com.treprosure.starbxup.game.screens.home.FindsScreen
import com.treprosure.starbxup.game.screens.home.GiftScreen
import com.treprosure.starbxup.game.screens.home.QuizScreen
import com.treprosure.starbxup.game.screens.home.ScratchScreen
import com.treprosure.starbxup.game.screens.home.WheelScreen
import com.treprosure.starbxup.game.screens.home.character.CharacterScreen
import com.treprosure.starbxup.game.screens.home.character.SelectCharacterScreen
import com.treprosure.starbxup.game.screens.home.converter.ConverterScreen
import com.treprosure.starbxup.game.screens.home.converter.SelectConverterScreen
import com.treprosure.starbxup.game.screens.home.outfit.AccessoriesScreen
import com.treprosure.starbxup.game.screens.home.outfit.AnimationsScreen
import com.treprosure.starbxup.game.screens.home.outfit.ClothingScreen
import com.treprosure.starbxup.game.screens.home.outfit.HeadScreen
import com.treprosure.starbxup.game.screens.home.outfit.SelectOutfitScreen
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.gdxGame
import com.treprosure.starbxup.game.utils.runGDX

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
            HomeScreen::class.java.name,
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