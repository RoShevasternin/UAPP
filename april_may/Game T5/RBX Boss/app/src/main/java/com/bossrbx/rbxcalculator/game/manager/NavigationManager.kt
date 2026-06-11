package com.bossrbx.rbxcalculator.game.manager

import com.badlogic.gdx.Gdx
import com.bossrbx.rbxcalculator.game.GDXGame
import com.bossrbx.rbxcalculator.game.screens.LanguageScreen
import com.bossrbx.rbxcalculator.game.screens.LoaderScreen
import com.bossrbx.rbxcalculator.game.screens.MainScreen
import com.bossrbx.rbxcalculator.game.screens.SettingsScreen
import com.bossrbx.rbxcalculator.game.screens.main.DailyRewardScreen
import com.bossrbx.rbxcalculator.game.screens.main.flipCard.FlipCardScreen
import com.bossrbx.rbxcalculator.game.screens.main.ScratchScreen
import com.bossrbx.rbxcalculator.game.screens.main.WheelScreen
import com.bossrbx.rbxcalculator.game.screens.main.converter.ConverterScreen
import com.bossrbx.rbxcalculator.game.screens.main.converter.SelectConverterScreen
import com.bossrbx.rbxcalculator.game.screens.main.quiz.QuizGameScreen
import com.bossrbx.rbxcalculator.game.screens.main.quiz.QuizPlayScreen
import com.bossrbx.rbxcalculator.game.screens.onboarding.Onboarding_1_Screen
import com.bossrbx.rbxcalculator.game.screens.onboarding.Onboarding_2_Screen
import com.bossrbx.rbxcalculator.game.screens.onboarding.Onboarding_3_Screen
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.runGDX

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
            LanguageScreen::class.java.name,
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
        LoaderScreen       ::class.java.name -> LoaderScreen()
        LanguageScreen     ::class.java.name -> LanguageScreen()
        Onboarding_1_Screen::class.java.name -> Onboarding_1_Screen()
        Onboarding_2_Screen::class.java.name -> Onboarding_2_Screen()
        Onboarding_3_Screen::class.java.name -> Onboarding_3_Screen()
        MainScreen         ::class.java.name -> MainScreen()

        // Main
        SettingsScreen       ::class.java.name -> SettingsScreen()
        DailyRewardScreen    ::class.java.name -> DailyRewardScreen()
        SelectConverterScreen::class.java.name -> SelectConverterScreen()
        ConverterScreen      ::class.java.name -> ConverterScreen()
        WheelScreen          ::class.java.name -> WheelScreen()
        ScratchScreen        ::class.java.name -> ScratchScreen()
        FlipCardScreen       ::class.java.name -> FlipCardScreen()
        QuizPlayScreen       ::class.java.name -> QuizPlayScreen()
        QuizGameScreen       ::class.java.name -> QuizGameScreen()

        else -> MainScreen()
    }

}