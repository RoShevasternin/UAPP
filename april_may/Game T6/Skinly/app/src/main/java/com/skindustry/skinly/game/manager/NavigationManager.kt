package com.skindustry.skinly.game.manager

import com.badlogic.gdx.Gdx
import com.skindustry.skinly.game.GDXGame
import com.skindustry.skinly.game.screens.LoaderScreen
import com.skindustry.skinly.game.screens.HomeScreen
import com.skindustry.skinly.game.screens.HomeSelectScreen
import com.skindustry.skinly.game.screens.OnboardingScreen
import com.skindustry.skinly.game.screens.PersonalizationScreen
import com.skindustry.skinly.game.screens.SettingsScreen
import com.skindustry.skinly.game.screens.ShareScreen
import com.skindustry.skinly.game.screens.SkinBookScreen
import com.skindustry.skinly.game.screens.selector.Selector_1_Screen
import com.skindustry.skinly.game.screens.selector.Selector_2_Screen
import com.skindustry.skinly.game.screens.selector.Selector_3_Screen
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX

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
        HomeSelectScreen     ::class.java.name -> HomeSelectScreen()
        PersonalizationScreen::class.java.name -> PersonalizationScreen()
        ShareScreen          ::class.java.name -> ShareScreen()
        SkinBookScreen       ::class.java.name -> SkinBookScreen()
        SettingsScreen       ::class.java.name -> SettingsScreen()

        else -> HomeScreen()
    }

}