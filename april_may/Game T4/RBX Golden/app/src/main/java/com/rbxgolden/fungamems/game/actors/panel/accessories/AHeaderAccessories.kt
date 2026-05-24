package com.rbxgolden.fungamems.game.actors.panel.accessories

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.layout.AlignV
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AHorizontalGroup
import com.rbxgolden.fungamems.game.screens.MainScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesAllScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesFaceScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesHeadScreen
import com.rbxgolden.fungamems.game.screens.main.accessories.AccessoriesNeckScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingAllScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingPantsScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingShirtsScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingShoesScreen
import com.rbxgolden.fungamems.game.screens.main.clothing.ClothingT_shirtsScreen
import com.rbxgolden.fungamems.game.utils.actor.addAndFillActor
import com.rbxgolden.fungamems.game.utils.actor.setBounds
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.actor.setOnTouchListener
import com.rbxgolden.fungamems.game.utils.actor.setSize
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class AHeaderAccessories(
    override val screen: AdvancedScreen,
    headerTexture: Texture,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aHorizontalGroup = AHorizontalGroup(screen, alignV = AlignV.CENTER, wrapWidth = true)
    private val aContentGroup    = ATmpGroup(screen)
    private val aContentImg      = Image(headerTexture)
    private val listBtn          = List(4) { Actor() }
    private val aScrollPane      = AScrollPane(aHorizontalGroup, scrollX = true, scrollY = false)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    // Content Group ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        aHorizontalGroup.setSize(1f, height)
        aHorizontalGroup.paddingLeft  = 16f
        aHorizontalGroup.paddingRight = 16f

        aContentGroup.setSize(305f, 40f)
        aHorizontalGroup.addActor(aContentGroup)

        aContentGroup.addAndFillActor(aContentImg)
        aContentGroup.addListBtn()
    }

    private fun AdvancedGroup.addListBtn() {
        val listScreenName = listOf(
            AccessoriesAllScreen    ::class.java.name,
            AccessoriesFaceScreen   ::class.java.name,
            AccessoriesHeadScreen   ::class.java.name,
            AccessoriesNeckScreen   ::class.java.name,
        )

        val listBounds = listOf(
            Rectangle(0f, 0f, 57f, 40f),
            Rectangle(65f, 0f, 73f, 40f),
            Rectangle(146f, 0f, 76f, 40f),
            Rectangle(230f, 0f, 75f, 40f),
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])
            btn.setOnTouchListener {
                screen.animHideScreen {
                    gdxGame.navigationManager.navigate(listScreenName[index])
                }
            }
        }

    }

}