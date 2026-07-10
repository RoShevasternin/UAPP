package com.rbxrush.rushrbx.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.actor.setOnTouchListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame

class APanel2(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[3])
    private val listBtn     = List(2) { Actor() }

    private val aContainer = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 8f,
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onCharacters = {}
    var onAnimations = {}

    private val listBlock = listOf(
        ::onCharacters,
        ::onAnimations,
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addContainer()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addContainer() {
        add(aContainer) { fillParent() }

        listBtn.forEachIndexed { index, btn ->
            btn.setSize(168f, 145f)
            aContainer.add(btn)

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}