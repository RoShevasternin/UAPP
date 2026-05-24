package com.rbuxrds.counter.game.manager

import com.badlogic.gdx.Gdx
import com.rbuxrds.counter.game.GDXGame
import com.rbuxrds.counter.game.screens.*
import com.rbuxrds.counter.game.screens.main.AccessoriesScreen
import com.rbuxrds.counter.game.screens.main.AllCharactersScreen
import com.rbuxrds.counter.game.screens.main.AnimationsScreen
import com.rbuxrds.counter.game.screens.main.ClothingScreen
import com.rbuxrds.counter.game.screens.main.DailyConverterScreen
import com.rbuxrds.counter.game.screens.main.DailyFreeRbxCalculatorScreen
import com.rbuxrds.counter.game.screens.main.HeadAndBodyScreen
import com.rbuxrds.counter.game.screens.main.LogicQuizTimeScreen
import com.rbuxrds.counter.game.screens.main.MemesForFunScreen
import com.rbuxrds.counter.game.screens.main.RedeemCoinScreen
import com.rbuxrds.counter.game.screens.main.ScratchScreen
import com.rbuxrds.counter.game.screens.main.SettingsScreen
import com.rbuxrds.counter.game.screens.main.SpinWheelScreen
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.runGDX

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
    }

    fun back(key: Int? = null) = runGDX {
        this.key = key

        if (isBackStackEmpty()) exit() else game.updateScreen(getScreenByName(backStack.removeAt(backStack.lastIndex)))
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