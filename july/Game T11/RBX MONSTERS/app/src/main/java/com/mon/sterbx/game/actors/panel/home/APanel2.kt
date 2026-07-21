package com.mon.sterbx.game.actors.panel.home

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.actor.setBounds
import com.mon.sterbx.game.utils.actor.setOnTouchListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame

class APanel2(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[3])
    private val listBtn     = List(2) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onChest = {}
    var onFree  = {}

    private val listBlock = listOf(
        ::onChest,
        ::onFree,
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addBtns()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addBtns() {
        val listBounds = listOf(
            Rectangle(0f, 0f, 168f, 129f),
            Rectangle(177f, 0f, 168f, 129f),
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}