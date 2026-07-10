package com.rbxrush.rushrbx.game.actors.panel.outfit

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.screens.home.outfit.AccessoriesScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.AnimationsScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.ClothingScreen
import com.rbxrush.rushrbx.game.screens.home.outfit.HeadScreen
import com.rbxrush.rushrbx.game.utils.actor.setOnClickListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame

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
        var nx = 0f
        var ny = 127f

        val listScreenName = listOf(
            ClothingScreen  ::class.java.name, AccessoriesScreen::class.java.name,
            AnimationsScreen::class.java.name, HeadScreen       ::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 119f)
            nx += 8f + 168f

            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 8f + 119f
            }

            btn.setOnClickListener {
                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreenName[index], screen::class.java.name) }
            }
        }
    }

}