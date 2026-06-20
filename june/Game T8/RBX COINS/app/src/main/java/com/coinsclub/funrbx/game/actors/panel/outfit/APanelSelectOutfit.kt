package com.coinsclub.funrbx.game.actors.panel.outfit

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.screens.home.outfit.AccessoriesScreen
import com.coinsclub.funrbx.game.screens.home.outfit.AnimationsScreen
import com.coinsclub.funrbx.game.screens.home.outfit.ClothingScreen
import com.coinsclub.funrbx.game.screens.home.outfit.HeadScreen
import com.coinsclub.funrbx.game.utils.actor.setOnClickListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelSelectOutfit(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg  = Image(gdxGame.assetsAll.PANEL_SELECT_OUTFIT)
    private val listBtn = List(4) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addListBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addListBtn() {
        val nx = 0f
        var ny = 269f

        val listScreenName = listOf(
            ClothingScreen  ::class.java.name, AccessoriesScreen::class.java.name,
            AnimationsScreen::class.java.name, HeadScreen       ::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 344f, 81f)
            ny -= 8f + 81f
//            if (index.inc() % 2 == 0) {
//                nx = 0f
//                ny -= 8f + 134f
//            }

            btn.setOnClickListener {
                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreenName[index], screen::class.java.name) }
            }
        }
    }

}