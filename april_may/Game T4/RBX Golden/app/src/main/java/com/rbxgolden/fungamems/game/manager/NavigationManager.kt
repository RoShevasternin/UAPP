package com.rbxgolden.fungamems.game.manager

import com.badlogic.gdx.Gdx
import com.rbxgolden.fungamems.game.GDXGame
import com.rbxgolden.fungamems.game.actors.panel.clothing.APanelClothingT_shirts
import com.rbxgolden.fungamems.game.screens.*
import com.rbxgolden.fungamems.game.screens.main.AllClothesAnimationsScreen
import com.rbxgolden.fungamems.game.screens.main.CharacterScreen
import com.rbxgolden.fungamems.game.screens.main.ConverterScreen
import com.rbxgolden.fungamems.game.screens.main.DailyRewardScreen
import com.rbxgolden.fungamems.game.screens.main.GiftScreen
import com.rbxgolden.fungamems.game.screens.main.MemesScreen
import com.rbxgolden.fungamems.game.screens.main.QuizScreen
import com.rbxgolden.fungamems.game.screens.main.ScratchScreen
import com.rbxgolden.fungamems.game.screens.main.SelectCharactersScreen
import com.rbxgolden.fungamems.game.screens.main.SelectConverterScreen
import com.rbxgolden.fungamems.game.screens.main.WheelScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesAllScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesFaceScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesHeadScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesNeckScreen
import com.rbxgolden.fungamems.game.screens.main.animations.AnimationsAllScreen
import com.rbxgolden.fungamems.game.screens.main.animations.AnimationsBundlesScreen
import com.rbxgolden.fungamems.game.screens.main.animations.AnimationsEmotesScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingAllScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingPantsScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingShirtsScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingShoesScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingT_shirtsScreen
import com.rbxgolden.fungamems.game.screens.main.head_body.Head_BodyAllScreen
import com.rbxgolden.fungamems.game.screens.main.head_body.Head_BodyBodyShapesScreen
import com.rbxgolden.fungamems.game.screens.main.head_body.Head_BodyFaceLookScreen
import com.rbxgolden.fungamems.game.screens.main.head_body.Head_BodyFaceShapesScreen
import com.rbxgolden.fungamems.game.screens.select.Select_1_Screen
import com.rbxgolden.fungamems.game.screens.select.Select_2_Screen
import com.rbxgolden.fungamems.game.screens.select.Select_3_Screen
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX

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
        LoaderScreen              ::class.java.name -> LoaderScreen()
        Select_1_Screen          ::class.java.name -> Select_1_Screen()
        Select_2_Screen          ::class.java.name -> Select_2_Screen()
        Select_3_Screen          ::class.java.name -> Select_3_Screen()
        SelectAnimationPackScreen::class.java.name -> SelectAnimationPackScreen()
        MainScreen               ::class.java.name -> MainScreen()

        // Main
        SettingsScreen            ::class.java.name -> SettingsScreen()
        DailyRewardScreen         ::class.java.name -> DailyRewardScreen()
        SelectConverterScreen     ::class.java.name -> SelectConverterScreen()
        ConverterScreen           ::class.java.name -> ConverterScreen()
        WheelScreen               ::class.java.name -> WheelScreen()
        ScratchScreen             ::class.java.name -> ScratchScreen()
        QuizScreen                ::class.java.name -> QuizScreen()
        GiftScreen                ::class.java.name -> GiftScreen()
        MemesScreen               ::class.java.name -> MemesScreen()
        SelectCharactersScreen    ::class.java.name -> SelectCharactersScreen()
        CharacterScreen           ::class.java.name -> CharacterScreen()

        AllClothesAnimationsScreen::class.java.name -> AllClothesAnimationsScreen()
        ClothingAllScreen         ::class.java.name -> ClothingAllScreen()
        ClothingT_shirtsScreen    ::class.java.name -> ClothingT_shirtsScreen()
        ClothingShirtsScreen      ::class.java.name -> ClothingShirtsScreen()
        ClothingPantsScreen       ::class.java.name -> ClothingPantsScreen()
        ClothingShoesScreen       ::class.java.name -> ClothingShoesScreen()

        AccessoriesAllScreen ::class.java.name -> AccessoriesAllScreen()
        AccessoriesFaceScreen::class.java.name -> AccessoriesFaceScreen()
        AccessoriesHeadScreen::class.java.name -> AccessoriesHeadScreen()
        AccessoriesNeckScreen::class.java.name -> AccessoriesNeckScreen()

        AnimationsAllScreen    ::class.java.name -> AnimationsAllScreen()
        AnimationsEmotesScreen ::class.java.name -> AnimationsEmotesScreen()
        AnimationsBundlesScreen::class.java.name -> AnimationsBundlesScreen()

        Head_BodyAllScreen       ::class.java.name -> Head_BodyAllScreen()
        Head_BodyFaceShapesScreen::class.java.name -> Head_BodyFaceShapesScreen()
        Head_BodyFaceLookScreen  ::class.java.name -> Head_BodyFaceLookScreen()
        Head_BodyBodyShapesScreen::class.java.name -> Head_BodyBodyShapesScreen()

        else -> MainScreen()
    }

}