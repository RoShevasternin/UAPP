package com.rsbuxs.rcounbux.game.manager

import com.badlogic.gdx.Gdx
import com.rsbuxs.rcounbux.game.GDXGame
import com.rsbuxs.rcounbux.game.screens.*
import com.rsbuxs.rcounbux.game.screens.main.BoostModeScreen
import com.rsbuxs.rcounbux.game.screens.main.DailyRewardScreen
import com.rsbuxs.rcounbux.game.screens.main.MiniGameScreen
import com.rsbuxs.rcounbux.game.screens.main.MiniGameWelcomeScreen
import com.rsbuxs.rcounbux.game.screens.main.NtoRBXScreen
import com.rsbuxs.rcounbux.game.screens.main.QuizTimeScreen
import com.rsbuxs.rcounbux.game.screens.main.RBXCalculatorScreen
import com.rsbuxs.rcounbux.game.screens.main.ReferralBonusScreen
import com.rsbuxs.rcounbux.game.screens.main.ScratchScreen
import com.rsbuxs.rcounbux.game.screens.main.SettingsScreen
import com.rsbuxs.rcounbux.game.screens.main.SpinWinScreen
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import com.rsbuxs.rcounbux.game.utils.runGDX

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
        WelcomeScreen   ::class.java.name -> WelcomeScreen()
        MainScreen      ::class.java.name -> MainScreen()

        // Main
        RBXCalculatorScreen   ::class.java.name -> RBXCalculatorScreen()
        NtoRBXScreen          ::class.java.name -> NtoRBXScreen()
        BoostModeScreen       ::class.java.name -> BoostModeScreen()
        MiniGameWelcomeScreen ::class.java.name -> MiniGameWelcomeScreen()
        MiniGameScreen        ::class.java.name -> MiniGameScreen()
        QuizTimeScreen        ::class.java.name -> QuizTimeScreen()
        DailyRewardScreen     ::class.java.name -> DailyRewardScreen()
        SpinWinScreen         ::class.java.name -> SpinWinScreen()
        ScratchScreen         ::class.java.name -> ScratchScreen()
        ReferralBonusScreen   ::class.java.name -> ReferralBonusScreen()
        SettingsScreen        ::class.java.name -> SettingsScreen()

        else -> LanguageScreen()
    }

}