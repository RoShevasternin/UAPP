package com.redwave.downloader.game.manager

import com.redwave.downloader.game.GDXGame
import com.redwave.downloader.game.screens.AppScreen
import com.redwave.downloader.game.screens.LauncherScreen
import com.redwave.downloader.game.screens.OnboardingScreen
import com.redwave.downloader.game.screens.PlayerScreen
import com.redwave.downloader.game.screens.SplashScreen
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.runGDX

// ═════════════════════════════════════════════════════════════════════════════
//  NavigationManager — як у T35, але без реклами між екранами (onFront/onBack
//  Navigation прибрано) і exit() = moveTaskToBack: Redwave буває головним
//  екраном, а лаунчер закривати не можна.
// ═════════════════════════════════════════════════════════════════════════════
class NavigationManager(val game: GDXGame) {

    private val backStack = mutableListOf<String>()
    var key: Int? = null
        private set

    /** Ім'я екрана, з якого щойно прийшли. null — перший екран застосунку. */
    var fromScreenName: String? = null
        private set

    var currentScreenName: String? = null
        private set

    fun navigate(toScreenName: String, fromScreenName: String? = null, key: Int? = null) = runGDX {
        this.key = key

        this.fromScreenName = currentScreenName
        currentScreenName   = toScreenName

        game.updateScreen(getScreenByName(toScreenName))
        backStack.filter { name -> name == toScreenName }.onEach { name -> backStack.remove(name) }
        fromScreenName?.let { fromName ->
            backStack.filter { name -> name == fromName }.onEach { name -> backStack.remove(name) }
            backStack.add(fromName)
        }
    }

    /** Перехід з порожнім бекстеком (HOME → Launcher, Splash → App). */
    fun navigateRoot(toScreenName: String, key: Int? = null) = runGDX {
        backStack.clear()
        navigate(toScreenName, null, key)
    }

    fun back(key: Int? = null) = runGDX {
        this.key = key

        if (isBackStackEmpty()) {
            exit()
        } else {
            val target = backStack.removeAt(backStack.lastIndex)

            this.fromScreenName = currentScreenName
            currentScreenName   = target

            game.updateScreen(getScreenByName(target))
        }
    }

    fun exit() = runGDX { game.bridge.moveToBack() }

    fun isBackStackEmpty() = backStack.isEmpty()

    fun isCurrent(name: String) = currentScreenName == name

    private fun getScreenByName(name: String): AdvancedScreen = when(name) {
        SplashScreen    ::class.java.name -> SplashScreen()
        OnboardingScreen::class.java.name -> OnboardingScreen()
        AppScreen       ::class.java.name -> AppScreen()
        PlayerScreen    ::class.java.name -> PlayerScreen()
        LauncherScreen  ::class.java.name -> LauncherScreen()

        else -> AppScreen()
    }

}
