package com.rbuxrds.counterds.game.manager

import com.badlogic.gdx.Gdx
import com.rbuxrds.counterds.game.GDXGame
import com.rbuxrds.counterds.game.screens.*
import com.rbuxrds.counterds.game.screens.main.AccessoriesScreen
import com.rbuxrds.counterds.game.screens.main.AllCharactersScreen
import com.rbuxrds.counterds.game.screens.main.AnimationsScreen
import com.rbuxrds.counterds.game.screens.main.ClothingScreen
import com.rbuxrds.counterds.game.screens.main.DailyConverterScreen
import com.rbuxrds.counterds.game.screens.main.DailyFreeRbxCalculatorScreen
import com.rbuxrds.counterds.game.screens.main.HeadAndBodyScreen
import com.rbuxrds.counterds.game.screens.main.LogicQuizTimeScreen
import com.rbuxrds.counterds.game.screens.main.MemesForFunScreen
import com.rbuxrds.counterds.game.screens.main.RedeemCoinScreen
import com.rbuxrds.counterds.game.screens.main.ScratchScreen
import com.rbuxrds.counterds.game.screens.main.SettingsScreen
import com.rbuxrds.counterds.game.screens.main.SpinWheelScreen
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.runGDX

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
            Select_1_Screen::class.java.name,
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
        LoaderScreen     ::class.java.name -> LoaderScreen()
        Select_1_Screen  ::class.java.name -> Select_1_Screen()
        MainScreen       ::class.java.name -> MainScreen()

        DailyConverterScreen        ::class.java.name -> DailyConverterScreen()
        DailyFreeRbxCalculatorScreen::class.java.name -> DailyFreeRbxCalculatorScreen()
        SpinWheelScreen             ::class.java.name -> SpinWheelScreen()
        ScratchScreen               ::class.java.name -> ScratchScreen()
        LogicQuizTimeScreen         ::class.java.name -> LogicQuizTimeScreen()
        RedeemCoinScreen            ::class.java.name -> RedeemCoinScreen()
        MemesForFunScreen           ::class.java.name -> MemesForFunScreen()
        AllCharactersScreen         ::class.java.name -> AllCharactersScreen()
        AnimationsScreen            ::class.java.name -> AnimationsScreen()
        AccessoriesScreen           ::class.java.name -> AccessoriesScreen()
        ClothingScreen              ::class.java.name -> ClothingScreen()
        HeadAndBodyScreen           ::class.java.name -> HeadAndBodyScreen()
        SettingsScreen              ::class.java.name -> SettingsScreen()

        else -> Select_1_Screen()
    }

}