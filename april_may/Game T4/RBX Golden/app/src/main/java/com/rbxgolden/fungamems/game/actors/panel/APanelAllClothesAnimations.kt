package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.screens.main.AllClothesAnimationsScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesAllScreen
import com.rbxgolden.fungamems.game.screens.main.animations.AnimationsAllScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingAllScreen
import com.rbxgolden.fungamems.game.screens.main.head_body.Head_BodyAllScreen
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class APanelAllClothesAnimations(
    override val screen: AdvancedScreen
) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_ALL_CLOTHES_ANIMATIONS)
    private val listBtn   = List(4) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelImg()
        addListBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelImg() {
        add(aPanelImg) { fillParent() }
    }

    private fun addListBtn() {
        val listScreenName = listOf(
            ClothingAllScreen::class.java.name  , AccessoriesAllScreen::class.java.name,
            AnimationsAllScreen::class.java.name, Head_BodyAllScreen::class.java.name,
        )

        var nx = 0f
        var ny = 197f

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 189f)

            nx += 8f + 168f
            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 8f + 189f
            }

            btn.setOnClickListener {
                screen.animHideScreen {
                    gdxGame.navigationManager.navigate(listScreenName[index], screen::class.java.name)
                }
            }
        }

    }

}