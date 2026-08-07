package com.racing.funtols.game.actors.panel.outfit

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.screens.home.outfit.AccessoriesScreen
import com.racing.funtols.game.screens.home.outfit.AnimationsScreen
import com.racing.funtols.game.screens.home.outfit.ClothingScreen
import com.racing.funtols.game.screens.home.outfit.HeadScreen
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.actor.setOnClickListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

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
        var ny = 198f

        val listScreenName = listOf(
            ClothingScreen    ::class.java.name,
            AccessoriesScreen ::class.java.name,
            AnimationsScreen  ::class.java.name,
            HeadScreen        ::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(0f, ny, 344f, 58f)
            ny -= 8f + 58f

            btn.setOnClickListener {
                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreenName[index], screen::class.java.name) }
            }
        }
    }

}