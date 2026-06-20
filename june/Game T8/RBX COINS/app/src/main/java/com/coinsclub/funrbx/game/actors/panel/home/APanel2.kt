package com.coinsclub.funrbx.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.actor.setOnTouchListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanel2(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[3])
    private val listBtn     = List(2) { Actor() }

    private val aVetical = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.VERTICAL,
        gapMain   = 6f,
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
        addVertical()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addVertical() {
        aVetical.setSize(308f, 172f)
        add(aVetical) { centerX(); bottomToBottom(margin = 19f) }

        listBtn.forEachIndexed { index, btn ->
            btn.setSize(308f, 83f)
            aVetical.add(btn)

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}