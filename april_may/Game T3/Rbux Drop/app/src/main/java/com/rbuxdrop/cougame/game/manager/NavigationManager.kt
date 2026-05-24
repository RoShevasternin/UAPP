package com.rbuxdrop.cougame.game.manager

import com.badlogic.gdx.Gdx
import com.rbuxdrop.cougame.game.GDXGame
import com.rbuxdrop.cougame.game.screens.*
import com.rbuxdrop.cougame.game.screens.main.ConverterScreen
import com.rbuxdrop.cougame.game.screens.main.DailyRewardScreen
import com.rbuxdrop.cougame.game.screens.main.FlipScreen
import com.rbuxdrop.cougame.game.screens.main.ScratchScreen
import com.rbuxdrop.cougame.game.screens.main.SelectConverterScreen
import com.rbuxdrop.cougame.game.screens.main.TipsScreen
import com.rbuxdrop.cougame.game.screens.main.TipsSelectedScreen
import com.rbuxdrop.cougame.game.screens.main.WheelScreen
import com.rbuxdrop.cougame.game.screens.main.quiz.QuizHowScreen
import com.rbuxdrop.cougame.game.screens.main.quiz.QuizScreen
import com.rbuxdrop.cougame.game.screens.main.quiz.QuizStartScreen
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.utils.runGDX

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
        LoaderScreen    ::class.java.name -> LoaderScreen()
        LanguageScreen  ::class.java.name -> LanguageScreen()
        OnboardingScreen::class.java.name -> OnboardingScreen()
        MainScreen      ::class.java.name -> MainScreen()

        // Main
        SelectConverterScreen::class.java.name -> SelectConverterScreen()
        ConverterScreen      ::class.java.name -> ConverterScreen()
        DailyRewardScreen    ::class.java.name -> DailyRewardScreen()
        WheelScreen          ::class.java.name -> WheelScreen()
        ScratchScreen        ::class.java.name -> ScratchScreen()
        QuizScreen           ::class.java.name -> QuizScreen()
        QuizHowScreen        ::class.java.name -> QuizHowScreen()
        QuizStartScreen      ::class.java.name -> QuizStartScreen()
        FlipScreen           ::class.java.name -> FlipScreen()
        TipsScreen           ::class.java.name -> TipsScreen()
        TipsSelectedScreen   ::class.java.name -> TipsSelectedScreen()
        SettingsScreen       ::class.java.name -> SettingsScreen()

        else -> LanguageScreen()
    }

}