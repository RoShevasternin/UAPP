package com.diam.ondbit.game.actors.panel.outfit

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.screens.home.outfit.AccessoriesScreen
import com.diam.ondbit.game.screens.home.outfit.EmotesScreen
import com.diam.ondbit.game.screens.home.outfit.GearScreen
import com.diam.ondbit.game.screens.home.outfit.ClothingScreen
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.actor.setOnClickListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

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
        var ny = 177f

        val listScreenName = listOf(
            GearScreen        ::class.java.name,
            ClothingScreen    ::class.java.name,
            EmotesScreen      ::class.java.name,
            AccessoriesScreen ::class.java.name,
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 168f)
            nx += 8f + 168f
            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 8f + 168f
            }

            btn.setOnClickListener {
                screen.animHideScreen { gdxGame.navigationManager.navigate(listScreenName[index], screen::class.java.name) }
            }
        }
    }

}